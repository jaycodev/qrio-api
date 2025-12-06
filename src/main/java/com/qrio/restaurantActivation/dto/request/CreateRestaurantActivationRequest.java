package com.qrio.restaurantActivation.dto.request;

import com.qrio.restaurantActivation.model.type.ActivationStatus;
import com.qrio.shared.validation.ValidEnum;

import jakarta.validation.constraints.NotNull;

public record CreateRestaurantActivationRequest(
    @NotNull(message = "Restaurant ID is required")
    Long restaurantId,

    @NotNull(message = "Admin ID is required")
    Long userId,

    @ValidEnum(enumClass = ActivationStatus.class, message = "Invalid status")
    String status,

    String comment
) {}
