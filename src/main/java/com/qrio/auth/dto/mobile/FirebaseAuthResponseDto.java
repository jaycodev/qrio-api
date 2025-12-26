package com.qrio.auth.dto.mobile;

public record FirebaseAuthResponseDto(
        String token,
        Long customerId,
        String email,
        String name,
        boolean isNew) {
}
