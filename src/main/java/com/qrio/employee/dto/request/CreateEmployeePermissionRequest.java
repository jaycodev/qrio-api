package com.qrio.employee.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateEmployeePermissionRequest(
    @NotNull(message = "Restaurant ID is required")
    @Min(value = 1, message = "Restaurant ID must be at least 1")
    Long restaurantId,

    Long branchId,

    @NotBlank(message = "Permission is required")
    @Size(max = 100, message = "Permission must not exceed 100 characters")
    String permission
) {}
