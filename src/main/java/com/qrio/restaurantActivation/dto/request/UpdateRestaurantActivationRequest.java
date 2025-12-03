package com.qrio.restaurantActivation.dto.request;

import com.qrio.restaurantActivation.model.type.ActivationStatus;
import com.qrio.shared.validation.ValidEnum;

import jakarta.validation.constraints.NotBlank;

public record UpdateRestaurantActivationRequest(
    @NotBlank(message = "Status is required")
    @ValidEnum(enumClass = ActivationStatus.class, message = "Invalid status")
    String status,

    String comment
) {}
