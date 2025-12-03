package com.qrio.employee.dto.response;

import java.time.LocalDateTime;

import com.qrio.employee.model.type.EmployeeRole;
import com.qrio.shared.type.Status;

public record EmployeeListResponse(
    Long id,
    Long adminId,
    Long restaurantId,
    Long branchId,
    String name,
    String email,
    String phone,
    EmployeeRole role,
    Status status,
    LocalDateTime createdAt
) {}
