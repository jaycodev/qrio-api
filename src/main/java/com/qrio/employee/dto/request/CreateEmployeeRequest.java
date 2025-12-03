package com.qrio.employee.dto.request;

import java.util.List;

import com.qrio.employee.model.type.EmployeeRole;
import com.qrio.shared.type.Status;
import com.qrio.shared.validation.NoNullElements;
import com.qrio.shared.validation.ValidEnum;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateEmployeeRequest(
    @NotNull(message = "Admin ID is required")
    Long adminId,

    @NotNull(message = "Restaurant ID is required")
    Long restaurantId,

    Long branchId,

    @NotBlank(message = "Name is required")
    @Size(max = 150, message = "Name must not exceed 150 characters")
    String name,

    @Email(message = "Email must be valid")
    @Size(max = 150, message = "Email must not exceed 150 characters")
    String email,

    @Size(max = 20, message = "Phone must not exceed 20 characters")
    String phone,

    @NotBlank(message = "Role is required")
    @ValidEnum(enumClass = EmployeeRole.class, message = "Invalid role")
    String role,

    @ValidEnum(enumClass = Status.class, message = "Invalid status")
    String status,

    @NoNullElements(message = "Permissions cannot contain null elements")
    @Valid
    List<CreateEmployeePermissionRequest> permissions
) {}
