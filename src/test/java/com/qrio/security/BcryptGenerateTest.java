package com.qrio.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BcryptGenerateTest {
    @Test
    void generateHash() {
        String plain = "Qrio1234!";
        BCryptPasswordEncoder enc = new BCryptPasswordEncoder(10);
        String hash = enc.encode(plain);
        System.out.println("PLAINTEXT=" + plain);
        System.out.println("BCRYPT=" + hash);
    }
}
