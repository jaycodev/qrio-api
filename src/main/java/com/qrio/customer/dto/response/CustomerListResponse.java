package com.qrio.customer.dto.response;

import com.qrio.shared.type.Status;

import java.time.LocalDateTime;

public record CustomerListResponse(
        Long id,
        String name,
        String email,
        Status status,
        LocalDateTime createdAt
) {
}
