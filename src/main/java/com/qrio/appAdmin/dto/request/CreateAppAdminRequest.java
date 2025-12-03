package com.qrio.appAdmin.dto.request;

import com.qrio.appAdmin.model.type.AppAdminRole;
import com.qrio.shared.validation.ValidEnum;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateAppAdminRequest(
    @NotBlank(message = "Name is required")
    @Size(max = 150, message = "Name must not exceed 150 characters")
    String name,

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 150, message = "Email must not exceed 150 characters")
    String email,

    @ValidEnum(enumClass = AppAdminRole.class, message = "Invalid role")
    String role
) {}
