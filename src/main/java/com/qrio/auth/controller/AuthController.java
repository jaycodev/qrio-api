package com.qrio.auth.controller;

import com.qrio.shared.config.security.JwtService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.qrio.appAdmin.repository.AppAdminRepository;
import com.qrio.auth.dto.web.UserBranchResponse;
import com.qrio.branch.repository.BranchRepository;

import com.qrio.customer.service.CustomerService;
import com.qrio.user.model.User;
import com.qrio.user.repository.UserRepository;
import com.qrio.shared.api.ApiError;
import com.qrio.auth.dto.mobile.LoginRequest;
import com.qrio.auth.dto.mobile.LoginResponse;
import com.qrio.auth.dto.mobile.MeResponse;
import com.qrio.auth.dto.mobile.TokenInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.http.ResponseCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.core.env.Environment;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final AppAdminRepository appAdminRepository;
    private final BranchRepository branchRepository;
    private final Environment environment;

    private final CustomerService customerService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        User user = userRepository.findByEmail(request.email()).orElseThrow();
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole().name());
        claims.put("restaurantId", user.getRestaurant() != null ? user.getRestaurant().getId() : null);
        claims.put("branchId", user.getBranch() != null ? user.getBranch().getId() : null);
        claims.put("name", user.getName());
        String accessToken = jwtService.generateToken(user.getEmail(), claims);
        String refreshToken = jwtService.generateToken(user.getEmail(), claims);

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

    @PostMapping("/admin/login")
    public ResponseEntity<?> adminLogin(@Valid @RequestBody LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        var appAdminOpt = appAdminRepository.findByEmail(request.email());
        if (appAdminOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        var admin = appAdminOpt.get();
        Map<String, Object> claims = new HashMap<>();
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
                    user.getBranch() != null ? user.getBranch().getId() : null));
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
                    null));
        }
        return ResponseEntity.status(401).build();
    }

    @GetMapping("/branches")
    public ResponseEntity<?> branches(@AuthenticationPrincipal UserDetails principal) {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }

        var userOpt = userRepository.findByEmail(principal.getUsername());
        if (userOpt.isEmpty()) {
            var adminOpt = appAdminRepository.findByEmail(principal.getUsername());
            if (adminOpt.isPresent()) {
                return ResponseEntity.ok(List.of());
            }
            return ResponseEntity.status(401).build();
        }

        var user = userOpt.get();
        List<UserBranchResponse> list;
        if ("DUEÑO".equals(user.getRole().name())) {
            list = branchRepository.findBranchesByUserId(user.getId());
        } else {
            list = branchRepository.findBranchesByEmployeeId(user.getId());
        }

        return ResponseEntity.ok(list);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(jakarta.servlet.http.HttpServletRequest request) {
        jakarta.servlet.http.Cookie[] cookies = request.getCookies();
        if (cookies == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        String refreshToken = null;
        for (jakarta.servlet.http.Cookie c : cookies) {
            if ("refresh_token".equals(c.getName())) {
                refreshToken = c.getValue();
                break;
            }
        }
        if (refreshToken == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        String email;
        try {
            email = jwtService.validateAndGetSubject(refreshToken);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole().name());
        claims.put("restaurantId", user.getRestaurant() != null ? user.getRestaurant().getId() : null);
        claims.put("branchId", user.getBranch() != null ? user.getBranch().getId() : null);
        claims.put("name", user.getName());

        String newAccess = jwtService.generateToken(email, claims);
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

    @PostMapping("/logout")
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


    @PostMapping("/firebase")
    public ResponseEntity<?> firebaseAuth(@RequestBody(required = false) FirebaseLoginRequest request,
                                          HttpServletRequest httpReq) {
        String idToken = request != null ? request.idToken() : null;
        if (idToken == null || idToken.isBlank()) {
            // Fallbacks de compatibilidad para la expo: header o parámetro
            String headerToken = httpReq.getHeader("X-Firebase-IdToken");
            if (headerToken == null || headerToken.isBlank()) {
                String authHeader = httpReq.getHeader("Authorization");
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    headerToken = authHeader.substring(7);
                }
            }
            if (headerToken == null || headerToken.isBlank()) {
                headerToken = httpReq.getParameter("idToken");
            }
            idToken = headerToken;
        }
        if (idToken == null || idToken.isBlank()) {
            return ResponseEntity.badRequest().body("idToken requerido");
        }

        FirebaseToken decoded;
        try {
            decoded = firebaseTokenVerifier.verify(idToken);
        } catch (IllegalStateException ise) {
            // Configuración de credenciales Firebase ausente o inválida
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ise.getMessage());
        } catch (Exception e) {
            // idToken inválido o verificación fallida
            String msg = e.getMessage() != null ? e.getMessage() : e.toString();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido: " + msg);
        }

        String uid = decoded.getUid();
        String email = decoded.getEmail();

        Map<String, Object> cust = customerService.firebaseAuth(uid, email);
        Long customerId = (Long) cust.get("customerId");
        String name = (String) cust.get("name");

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "CUSTOMER");
        claims.put("customerId", customerId);
        claims.put("email", email);
        claims.put("name", name);

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
    }


    @GetMapping("/token-info")
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

}