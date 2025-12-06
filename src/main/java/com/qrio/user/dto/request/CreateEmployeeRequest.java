package com.qrio.user.dto.request;

import java.util.List;

import com.qrio.user.model.type.UserRole;
import com.qrio.shared.type.Status;
import com.qrio.shared.validation.NoNullElements;
import com.qrio.shared.validation.ValidEnum;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateEmployeeRequest(
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 150, message = "Email must not exceed 150 characters")
    String email,

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 255, message = "Password must be between 6 and 255 characters")
    String password,

    @NotBlank(message = "Name is required")
    @Size(max = 150, message = "Name must not exceed 150 characters")
    String name,

    @Size(max = 20, message = "Phone must not exceed 20 characters")
    String phone,

    @NotNull(message = "Restaurant ID is required")
    Long restaurantId,

    Long branchId,

    @NotBlank(message = "Role is required")
    @ValidEnum(enumClass = UserRole.class, message = "Invalid role")
    String role,

    @ValidEnum(enumClass = Status.class, message = "Invalid status")
    String status,

    @NoNullElements(message = "Permissions cannot contain null elements")
    @Valid
    List<CreateEmployeePermissionRequest> permissions
) {}
