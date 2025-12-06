package com.qrio.user.dto.response;

import java.time.LocalDateTime;

import com.qrio.shared.type.Status;
import com.qrio.user.model.type.UserRole;

public record EmployeeListResponse(
    Long id,
    Long restaurantId,
    Long branchId,
    String name,
    String email,
    String phone,
    UserRole role,
    Status status,
    LocalDateTime createdAt
) {}
