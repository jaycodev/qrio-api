package com.qrio.shared.config.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

public class JwtService {
    private final SecretKey key;
    private final long expirationMs;

    public JwtService(String secret, long expirationMs) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.expirationMs = expirationMs;
    }

    public String generateToken(String subject) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + expirationMs);
        return Jwts.builder()
            .subject(subject)
            .issuedAt(now)
            .expiration(exp)
            .signWith(key)
            .compact();
    }

    public String generateToken(String subject, Map<String, Object> claims) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + expirationMs);
        return Jwts.builder()
            .subject(subject)
            .claims(claims)
            .issuedAt(now)
            .expiration(exp)
            .signWith(key)
            .compact();
    }

    public String validateAndGetSubject(String token) {
        return Jwts.parser().verifyWith(key).build()
            .parseSignedClaims(token)
            .getPayload()
            .getSubject();
    }

    public long getExpirationSeconds() {
        return expirationMs / 1000;
    }
}