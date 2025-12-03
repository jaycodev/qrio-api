package com.qrio.branch.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateBranchRequest(
    @NotNull(message = "Restaurant ID is required")
    Long restaurantId,

    @NotBlank(message = "Name is required")
    @Size(max = 150, message = "Name must not exceed 150 characters")
    String name,

    @Size(max = 255, message = "Address must not exceed 255 characters")
    String address,

    @Size(max = 20, message = "Phone must not exceed 20 characters")
    String phone,

    String schedule
) {}
