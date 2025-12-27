package com.qrio.shared.config.security;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Verificador de tokens de Firebase configurable por properties.
 * Prioriza la propiedad Spring "security.firebase.credentials-path";
 * si no está, usa la variable de entorno GOOGLE_APPLICATION_CREDENTIALS.
 */
@Component
public class FirebaseTokenVerifier {
    private final String credentialsPath;
    private final ResourceLoader resourceLoader;
    private volatile boolean initialized = false;

    public FirebaseTokenVerifier(
            @Value("${security.firebase.credentials-path:}") String credentialsPath,
            ResourceLoader resourceLoader) {
        this.credentialsPath = credentialsPath;
        this.resourceLoader = resourceLoader;
    }

    private synchronized void initIfNeeded() throws IOException {
        if (initialized) return;
        if (FirebaseApp.getApps().isEmpty()) {
            InputStream credStream = null;
            String path = (credentialsPath != null && !credentialsPath.isBlank())
                    ? credentialsPath
                    : System.getenv("GOOGLE_APPLICATION_CREDENTIALS");

            if (path == null || path.isBlank()) {
                throw new IllegalStateException("Configure security.firebase.credentials-path o GOOGLE_APPLICATION_CREDENTIALS");
            }

            // Soporte de credenciales inline en properties
            if (path.trim().startsWith("{")) {
                // Detectar si es Service Account o google-services.json de cliente
                String json = path.trim();
                ObjectMapper mapper = new ObjectMapper();
                JsonNode node = mapper.readTree(json);
                boolean isServiceAccount = node.has("type") && "service_account".equals(node.get("type").asText());
                boolean isClientConfig = node.has("project_info");

                if (isServiceAccount) {
                    // Usar credenciales del Service Account
                    credStream = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
                } else if (isClientConfig) {
                    // Inicializar solo con projectId para verificar tokens (no requiere claves privadas)
                    String projectId = node.path("project_info").path("project_id").asText(null);
                    if (projectId == null || projectId.isBlank()) {
                        throw new IllegalStateException("project_id faltante en google-services.json");
                    }
                    FirebaseOptions options = FirebaseOptions.builder()
                            .setProjectId(projectId)
                            .build();
                    FirebaseApp.initializeApp(options);
                    initialized = true;
                    return;
                } else {
                    throw new IllegalStateException("Credenciales Firebase inline no reconocidas: use Service Account o google-services.json");
                }
            } else if (path.startsWith("classpath:")) {
                Resource res = resourceLoader.getResource(path);
                credStream = res.getInputStream();
            } else {
                credStream = new FileInputStream(path);
            }

            try (InputStream is = credStream) {
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(is))
                        .build();
                FirebaseApp.initializeApp(options);
            }
        }
        initialized = true;
    }

    public FirebaseToken verify(String idToken) throws Exception {
        initIfNeeded();
        return FirebaseAuth.getInstance().verifyIdToken(idToken);
    }
}
