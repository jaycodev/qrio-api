package com.qrio.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class BcryptGuessTest {
    private static final String HASH = "$2a$10$slYQmyNdGzin7olVN3p5Be7DW5yIY666GB6h10pa.i7FC32aiUhSm";

    @Test
    void guessCommonPasswords() {
        BCryptPasswordEncoder enc = new BCryptPasswordEncoder(10);
        String[] candidates = new String[]{
                "123456", "password", "12345678", "qwerty", "abc123",
                "qrio123", "Qr1o!2025", "1234", "12345", "111111",
                "123123", "Password123", "admin", "user", "test123"
        };
        boolean any = false;
        for (String c : candidates) {
            if (enc.matches(c, HASH)) {
                System.out.println("MATCH:" + c);
                any = true;
            }
        }
        assertTrue(any, "No candidate matched the bcrypt hash");
    }
}
