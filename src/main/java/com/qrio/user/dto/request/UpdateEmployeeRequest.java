package com.qrio.user.dto.request;

import java.util.List;

import com.qrio.user.model.type.UserRole;
import com.qrio.shared.type.Status;
import com.qrio.shared.validation.NoNullElements;
import com.qrio.shared.validation.ValidEnum;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateEmployeeRequest(
    @NotBlank(message = "Name is required")
    @Size(max = 150, message = "Name must not exceed 150 characters")
    String name,

    @Size(max = 20, message = "Phone must not exceed 20 characters")
    String phone,

    Long branchId,

    @NotBlank(message = "Role is required")
    @ValidEnum(enumClass = UserRole.class, message = "Invalid role")
    String role,

    @NotBlank(message = "Status is required")
    @ValidEnum(enumClass = Status.class, message = "Invalid status")
    String status,

    @NoNullElements(message = "Permissions cannot contain null elements")
    @Valid
    List<UpdateEmployeePermissionRequest> permissions
) {}
