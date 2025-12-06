package com.qrio.user.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.qrio.user.model.type.UserRole;
import com.qrio.shared.type.Status;

public record EmployeeDetailResponse(
    Long id,
    Long restaurantId,
    Long branchId,
    String name,
    String email,
    String phone,
    UserRole role,
    Status status,
    LocalDateTime createdAt,
    
    List<EmployeePermissionItemResponse> permissions
) {

    public EmployeeDetailResponse withPermissions(List<EmployeePermissionItemResponse> permissions) {
        return new EmployeeDetailResponse(id, restaurantId, branchId, name, email, phone, role, status, createdAt, permissions);
    }
}
