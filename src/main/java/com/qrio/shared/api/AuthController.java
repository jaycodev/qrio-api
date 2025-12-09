package com.qrio.shared.api;

import com.qrio.shared.config.security.JwtService;
import com.qrio.user.model.User;
import com.qrio.user.repository.UserRepository;
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
import com.qrio.shared.api.dto.LoginRequest;
import com.qrio.shared.api.dto.LoginResponse;
import com.qrio.shared.api.dto.MeResponse;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final Environment environment;

    @PostMapping("/login")
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

    @GetMapping("/me")
    public ResponseEntity<?> me(@AuthenticationPrincipal UserDetails principal) {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }
        User user = userRepository.findByEmail(principal.getUsername()).orElseThrow();
        return ResponseEntity.ok(new MeResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getRole().name(),
                user.getRestaurant() != null ? user.getRestaurant().getId() : null,
                user.getBranch() != null ? user.getBranch().getId() : null
        ));
    }

    @PostMapping("/refresh")
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
        boolean isProd = Arrays.asList(environment.getActiveProfiles()).contains("prod");
        boolean secure = isProd;
        String sameSite = isProd ? "None" : "Lax";

        ResponseCookie accessCookie = ResponseCookie.from("access_token", newAccess)
            .httpOnly(true).secure(secure).sameSite(sameSite).path("/")
            .maxAge(jwtService.getExpirationSeconds()).build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .body(new LoginResponse(newAccess));
    }
}