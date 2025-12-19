package com.qrio.shared.config;

import com.qrio.shared.config.security.JwtAuthFilter;
import com.qrio.shared.config.security.JwtService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import org.springframework.util.AntPathMatcher;

import java.util.List;

@Configuration
public class SecurityConfig {

    @Bean
    public JwtService jwtService(org.springframework.core.env.Environment env) {
        String secret = env.getProperty("security.jwt.secret", "defaultsecretdefaultsecretdefaultse");
        long expMs = Long.parseLong(env.getProperty("security.jwt.expMs", "3600000"));
        return new JwtService(secret, expMs);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtService jwtService, UserDetailsService uds,
            org.springframework.core.env.Environment env)
            throws Exception {
        boolean isLocal = java.util.Arrays.asList(env.getActiveProfiles()).contains("local");
        AntPathMatcher matcher = new AntPathMatcher();
        http.csrf(csrf -> csrf.disable())
                .cors(cors -> {
                })
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> {
                auth
                    // Permitir preflight CORS en todas las rutas
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    .requestMatchers(
                        "/",
                        "/auth/login",
                        "/auth/admin/login",
                        "/auth/refresh",
                        "/v3/api-docs/**"
                ).permitAll();

                        if (isLocal) {
                            // En local, permitir también métodos de escritura para endpoints seleccionados
                            String[] openPaths = new String[]{
                                    "/products", "/products/**",
                                    "/categories", "/categories/**",
                                    "/tables", "/tables/**",
                                    "/orders", "/orders/**",
                                    "/offers", "/offers/**",
                                    "/customers", "/customers/**"
                            };
                            auth.requestMatchers(HttpMethod.GET, openPaths).permitAll();
                            auth.requestMatchers(HttpMethod.POST, openPaths).permitAll();
                            auth.requestMatchers(HttpMethod.PUT, openPaths).permitAll();
                            auth.requestMatchers(HttpMethod.PATCH, openPaths).permitAll();
                            auth.requestMatchers(HttpMethod.DELETE, openPaths).permitAll();
                        }

                    auth.anyRequest().authenticated();
                })
                .addFilterBefore(new JwtAuthFilter(jwtService, uds), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(org.springframework.core.env.Environment env) {
        CorsConfiguration config = new CorsConfiguration();
        String originsProp = env.getProperty("security.cors.allowed-origins",
                "http://localhost:3000,http://localhost:4200");
        config.setAllowedOrigins(List.of(originsProp.split(",")));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With"));
        config.setAllowCredentials(true);
        config.setExposedHeaders(List.of("Authorization", "Set-Cookie"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}