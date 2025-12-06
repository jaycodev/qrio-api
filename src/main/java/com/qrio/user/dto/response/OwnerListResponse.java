package com.qrio.user.dto.response;

import java.time.LocalDateTime;

import com.qrio.user.model.type.UserRole;
import com.qrio.shared.type.Status;

public record OwnerListResponse(
    Long id,
    Long restaurantId,
    String name,
    String email,
    String phone,
    UserRole role,
    Status status,
    LocalDateTime createdAt
) {}
