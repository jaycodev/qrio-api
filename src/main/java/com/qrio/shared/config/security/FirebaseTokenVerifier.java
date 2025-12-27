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

            // Soporte de credenciales inline en properties: si parece JSON, úsalo directo
            if (path.trim().startsWith("{")) {
                credStream = new ByteArrayInputStream(path.getBytes(StandardCharsets.UTF_8));
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
