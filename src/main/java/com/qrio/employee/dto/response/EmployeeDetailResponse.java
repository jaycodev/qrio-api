package com.qrio.employee.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.qrio.employee.model.type.EmployeeRole;
import com.qrio.shared.type.Status;

public record EmployeeDetailResponse(
    Long id,
    Long adminId,
    Long restaurantId,
    Long branchId,
    String name,
    String email,
    String phone,
    EmployeeRole role,
    Status status,
    LocalDateTime createdAt,
    
    List<EmployeePermissionItem> permissions
) {

    public EmployeeDetailResponse withPermissions(List<EmployeePermissionItem> permissions) {
        return new EmployeeDetailResponse(id, adminId, restaurantId, branchId, name, email, phone, role, status, createdAt, permissions);
    }

    public record EmployeePermissionItem(
        Long restaurantId,
        Long branchId,
        String permission
    ) {}
}
