package com.qrio.shared.api;

import com.qrio.shared.config.security.JwtService;
import com.qrio.shared.config.security.FirebaseTokenVerifier;
import com.qrio.appAdmin.repository.AppAdminRepository;
import com.qrio.user.model.User;
import com.qrio.user.repository.UserRepository;
import com.qrio.customer.repository.CustomerRepository;
import com.qrio.customer.service.CustomerService;
import com.qrio.customer.dto.request.CreateCustomerRequest;
import com.qrio.shared.type.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.http.ResponseCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.core.env.Environment;
import java.util.Arrays;
import java.util.Optional;
import jakarta.servlet.http.HttpServletRequest;
import io.jsonwebtoken.Claims;
import com.qrio.shared.api.dto.LoginRequest;
import com.qrio.shared.api.dto.LoginResponse;
import com.qrio.shared.api.dto.MeResponse;
import com.qrio.shared.api.dto.FirebaseLoginRequest;
import com.qrio.shared.api.dto.TokenInfoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Authentication and token management endpoints")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final AppAdminRepository appAdminRepository;
    private final CustomerRepository customerRepository;
    private final Environment environment;
    private final FirebaseTokenVerifier firebaseTokenVerifier;
    private final CustomerService customerService;

    @PostMapping("/login")
    @Operation(summary = "Login for restaurant employees/owners")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        java.util.Map<String, Object> claims = new java.util.HashMap<>();
        claims.put("role", user.getRole().name());
        claims.put("restaurantId", user.getRestaurant() != null ? user.getRestaurant().getId() : null);
        claims.put("branchId", user.getBranch() != null ? user.getBranch().getId() : null);
        claims.put("name", user.getName());
        String accessToken = jwtService.generateToken(user.getEmail(), claims);
        // Refresh token: mismo sujeto, mayor expiración (doble). Aquí generamos uno simple; en producción usa lista de revocación/rotación
        String refreshToken = jwtService.generateToken(user.getEmail(), claims);

        boolean isProd = Arrays.asList(environment.getActiveProfiles()).contains("prod");
        boolean secure = isProd; // En prod debe ser true (HTTPS)
        String sameSite = isProd ? "None" : "Lax"; // None para cross-site en prod

        ResponseCookie accessCookie = ResponseCookie.from("access_token", accessToken)
            .httpOnly(true)
            .secure(secure)
            .sameSite(sameSite)
            .path("/")
            .maxAge(jwtService.getExpirationSeconds())
            .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", refreshToken)
            .httpOnly(true)
            .secure(secure)
            .sameSite(sameSite)
            .path("/auth")
            .maxAge(jwtService.getExpirationSeconds())
            .build();

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
            .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
            .body(new LoginResponse(accessToken));
    }

    @PostMapping("/admin/login")
    @Operation(summary = "Login for app administrators")
    public ResponseEntity<?> adminLogin(@RequestBody LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        var appAdminOpt = appAdminRepository.findByEmail(request.getEmail());
        if (appAdminOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        var admin = appAdminOpt.get();
        java.util.Map<String, Object> claims = new java.util.HashMap<>();
        claims.put("role", "APP_ADMIN");
        claims.put("restaurantId", null);
        claims.put("branchId", null);
        claims.put("name", admin.getName());
        String accessToken = jwtService.generateToken(admin.getEmail(), claims);
        String refreshToken = jwtService.generateToken(admin.getEmail(), claims);

        boolean isProd = Arrays.asList(environment.getActiveProfiles()).contains("prod");
        boolean secure = isProd;
        String sameSite = isProd ? "None" : "Lax";

        ResponseCookie accessCookie = ResponseCookie.from("access_token", accessToken)
            .httpOnly(true)
            .secure(secure)
            .sameSite(sameSite)
            .path("/")
            .maxAge(jwtService.getExpirationSeconds())
            .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", refreshToken)
            .httpOnly(true)
            .secure(secure)
            .sameSite(sameSite)
            .path("/auth")
            .maxAge(jwtService.getExpirationSeconds())
            .build();

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
            .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
            .body(new LoginResponse(accessToken));
    }

    @GetMapping("/me")
    @Operation(summary = "Get current authenticated principal profile")
    public ResponseEntity<?> me(@AuthenticationPrincipal UserDetails principal) {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }
        var userOpt = userRepository.findByEmail(principal.getUsername());
        if (userOpt.isPresent()) {
            var user = userOpt.get();
            return ResponseEntity.ok(new MeResponse(
                    user.getId(),
                    user.getEmail(),
                    user.getName(),
                    user.getRole().name(),
                    user.getRestaurant() != null ? user.getRestaurant().getId() : null,
                    user.getBranch() != null ? user.getBranch().getId() : null
            ));
        }

        var adminOpt = appAdminRepository.findByEmail(principal.getUsername());
        if (adminOpt.isPresent()) {
            var admin = adminOpt.get();
            return ResponseEntity.ok(new MeResponse(
                    admin.getId(),
                    admin.getEmail(),
                    admin.getName(),
                    "APP_ADMIN",
                    null,
                    null
            ));
        }
        return ResponseEntity.status(401).build();
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token using refresh cookie")
    public ResponseEntity<?> refresh(jakarta.servlet.http.HttpServletRequest request) {
        // Leer refresh_token de cookie
        jakarta.servlet.http.Cookie[] cookies = request.getCookies();
        if (cookies == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        String refreshToken = null;
        for (jakarta.servlet.http.Cookie c : cookies) {
            if ("refresh_token".equals(c.getName())) { refreshToken = c.getValue(); break; }
        }
        if (refreshToken == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        String email;
        try { email = jwtService.validateAndGetSubject(refreshToken); }
        catch (Exception ex) { return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); }

        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        java.util.Map<String, Object> claims = new java.util.HashMap<>();
        claims.put("role", user.getRole().name());
        claims.put("restaurantId", user.getRestaurant() != null ? user.getRestaurant().getId() : null);
        claims.put("branchId", user.getBranch() != null ? user.getBranch().getId() : null);
        claims.put("name", user.getName());

        String newAccess = jwtService.generateToken(email, claims);
        // Rotar refresh token: emitir uno nuevo junto con el nuevo access token
        String newRefresh = jwtService.generateToken(email, claims);
        boolean isProd = Arrays.asList(environment.getActiveProfiles()).contains("prod");
        boolean secure = isProd;
        String sameSite = isProd ? "None" : "Lax";

        ResponseCookie accessCookie = ResponseCookie.from("access_token", newAccess)
            .httpOnly(true).secure(secure).sameSite(sameSite).path("/")
            .maxAge(jwtService.getExpirationSeconds()).build();
        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", newRefresh)
            .httpOnly(true).secure(secure).sameSite(sameSite).path("/auth")
            .maxAge(jwtService.getExpirationSeconds()).build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(new LoginResponse(newAccess));
    }

    @PostMapping("/firebase")
    @Operation(summary = "Exchange Firebase idToken for backend JWT and auto-onboard customer")
    public ResponseEntity<?> firebaseLogin(@RequestBody FirebaseLoginRequest request) {
        try {
            var decoded = firebaseTokenVerifier.verify(request.getIdToken());
            String uid = decoded.getUid();
            String email = decoded.getEmail();
            String name = decoded.getName();

            java.util.Map<String, Object> claims = new java.util.HashMap<>();
            claims.put("role", "CUSTOMER");

            Optional<com.qrio.customer.model.Customer> customerOpt = customerRepository.findByFirebaseUid(uid);
            if (customerOpt.isPresent()) {
                claims.put("customerId", customerOpt.get().getId());
            } else {
                // Auto-onboard: crear Customer si no existe
                String safeName = (name != null && !name.isBlank()) ? name : "Cliente";
                String safeEmail = (email != null && !email.isBlank()) ? email : (uid + "@firebase.local");
                var created = customerService.create(new CreateCustomerRequest(uid, safeName, safeEmail, null, Status.ACTIVO));
                claims.put("customerId", created.id());
            }
            if (email != null) claims.put("email", email);
            if (name != null) claims.put("name", name);

            String accessToken = jwtService.generateToken(uid, claims);
            String refreshToken = jwtService.generateToken(uid, claims);

            boolean isProd = Arrays.asList(environment.getActiveProfiles()).contains("prod");
            boolean secure = isProd;
            String sameSite = isProd ? "None" : "Lax";

            ResponseCookie accessCookie = ResponseCookie.from("access_token", accessToken)
                .httpOnly(true)
                .secure(secure)
                .sameSite(sameSite)
                .path("/")
                .maxAge(jwtService.getExpirationSeconds())
                .build();

            ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(secure)
                .sameSite(sameSite)
                .path("/auth")
                .maxAge(jwtService.getExpirationSeconds())
                .build();

            return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(new LoginResponse(accessToken));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiError(401, "Invalid Firebase token", "/auth/firebase"));
        }
    }

    @GetMapping("/token-info")
    @Operation(summary = "Return parsed claims of current access token")
    public ResponseEntity<?> tokenInfo(HttpServletRequest request) {
        String token = null;
        String auth = request.getHeader("Authorization");
        if (auth != null && auth.startsWith("Bearer ")) {
            token = auth.substring(7);
        } else if (request.getCookies() != null) {
            for (jakarta.servlet.http.Cookie c : request.getCookies()) {
                if ("access_token".equals(c.getName())) { token = c.getValue(); break; }
            }
        }
        if (token == null || token.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiError(401, "Missing token", "/auth/token-info"));
        }
        try {
            Claims claims = jwtService.parseClaims(token);
            String subject = claims.getSubject();
            String role = claims.get("role", String.class);
            Number custNum = claims.get("customerId", Number.class);
            Long customerId = custNum != null ? custNum.longValue() : null;
            String email = claims.get("email", String.class);
            String name = claims.get("name", String.class);
            return ResponseEntity.ok(new TokenInfoResponse(subject, role, customerId, email, name, claims.getIssuedAt(), claims.getExpiration()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiError(401, "Invalid token", "/auth/token-info"));
        }
    }

        @PostMapping("/logout")
        @Operation(summary = "Logout and clear auth cookies")
        public ResponseEntity<?> logout() {
        boolean isProd = Arrays.asList(environment.getActiveProfiles()).contains("prod");
        boolean secure = isProd;
        String sameSite = isProd ? "None" : "Lax";

        ResponseCookie clearAccess = ResponseCookie.from("access_token", "")
            .httpOnly(true)
            .secure(secure)
            .sameSite(sameSite)
            .path("/")
            .maxAge(0)
            .build();

        ResponseCookie clearRefresh = ResponseCookie.from("refresh_token", "")
            .httpOnly(true)
            .secure(secure)
            .sameSite(sameSite)
            .path("/auth")
            .maxAge(0)
            .build();

        return ResponseEntity.noContent()
            .header(HttpHeaders.SET_COOKIE, clearAccess.toString())
            .header(HttpHeaders.SET_COOKIE, clearRefresh.toString())
            .build();
        }
}