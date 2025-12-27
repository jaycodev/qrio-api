package com.qrio.customer.dto.response;

import com.qrio.shared.type.Status;

import java.time.LocalDateTime;

public record CustomerDetailResponse(
        Long id,
        String name,
        String email,
        String phone,
        Status status,
        LocalDateTime createdAt
) {
}
