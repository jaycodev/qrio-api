package com.qrio.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class BcryptGuessTest {

    @Test
    void guessCommonPasswords() {
        BCryptPasswordEncoder enc = new BCryptPasswordEncoder(10);
        // Generamos un hash sobre una contraseña común incluida en candidatos
        String target = "Password123";
        String hash = enc.encode(target);

        String[] candidates = new String[]{
                "123456", "password", "12345678", "qwerty", "abc123",
                "qrio123", "Qr1o!2025", "1234", "12345", "111111",
                "123123", "Password123", "admin", "user", "test123"
        };
        boolean any = false;
        for (String c : candidates) {
            if (enc.matches(c, hash)) {
                System.out.println("MATCH:" + c);
                any = true;
            }
        }
        assertTrue(any, "No candidate matched the bcrypt hash");
    }
}
