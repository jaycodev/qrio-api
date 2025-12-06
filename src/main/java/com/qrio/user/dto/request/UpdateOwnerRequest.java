package com.qrio.user.dto.request;

import com.qrio.shared.type.Status;
import com.qrio.shared.validation.ValidEnum;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateOwnerRequest(
    @NotBlank(message = "Name is required")
    @Size(max = 150, message = "Name must not exceed 150 characters")
    String name,

    @Size(max = 20, message = "Phone must not exceed 20 characters")
    String phone,

    @NotBlank(message = "Status is required")
    @ValidEnum(enumClass = Status.class, message = "Invalid status")
    String status
) {}
