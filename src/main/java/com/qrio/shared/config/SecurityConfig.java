package com.qrio.shared.config;

import com.qrio.shared.config.security.JwtAuthFilter;
import com.qrio.shared.config.security.JwtService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
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

import java.util.List;

@Configuration
public class SecurityConfig {

    @Bean
    public JwtService jwtService(Environment env) {
        String secret = env.getRequiredProperty("security.jwt.secret");
        String expMsProp = env.getProperty("security.jwt.expMs");
        long expMs = (expMsProp == null || expMsProp.isBlank()) ? 3600000L : Long.parseLong(expMsProp);
        return new JwtService(secret, expMs);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtService jwtService, UserDetailsService uds,
            Environment env)
            throws Exception {
        boolean isLocal = java.util.Arrays.asList(env.getActiveProfiles()).contains("local");
        http.csrf(csrf -> csrf.disable())
                .cors(cors -> {
                })
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> {
                    auth
                            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                            .requestMatchers(
                                    "/",
                                    "/docs.html",
                                        "/auth/login",
                                        "/auth/customer/login",
                                        "/auth/admin/login",
                                            "/auth/refresh",
                                            "/auth/logout",
                                    "/v3/api-docs/**",
                                    "/swagger-ui/**",
                                    "/swagger-ui.html",
                                    "/swagger-resources/**",
                                    "/webjars/**",
                                    "/favicon.ico")
                            .permitAll();

                    // Open catalog endpoints for mobile (public GET)
                    auth
                        .requestMatchers(HttpMethod.GET,
                            "/restaurants/**",
                            "/branches/**",
                            "/products/**",
                            "/offers/**")
                        .permitAll();

                    if (isLocal) {
                        auth.anyRequest().permitAll();
                    } else {
                        auth.anyRequest().authenticated();
                    }
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
    public CorsConfigurationSource corsConfigurationSource(Environment env) {
        CorsConfiguration config = new CorsConfiguration();
        boolean isLocal = java.util.Arrays.asList(env.getActiveProfiles()).contains("local");
        String originsProp = env.getProperty("security.cors.allowed-origins", "");

        if (isLocal) {
            config.setAllowedOriginPatterns(List.of("*"));
        } else {
            if (originsProp == null || originsProp.isBlank()) {
                throw new IllegalStateException(
                        "security.cors.allowed-origins must be configured for non-local profiles");
            }
            config.setAllowedOrigins(List.of(originsProp.split(",")));
        }

          config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With"));
        config.setAllowCredentials(true);
        config.setExposedHeaders(List.of("Authorization", "Set-Cookie"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}