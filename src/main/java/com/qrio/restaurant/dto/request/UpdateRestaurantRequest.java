package com.qrio.restaurant.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateRestaurantRequest(
    @NotNull(message = "Admin ID is required")
    Long adminId,

    @NotBlank(message = "Name is required")
    @Size(max = 150, message = "Name must not exceed 150 characters")
    String name,

    String description,
    
    String logoUrl,

    Boolean isActive
) {}
