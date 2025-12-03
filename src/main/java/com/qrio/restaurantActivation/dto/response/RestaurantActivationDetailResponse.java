package com.qrio.restaurantActivation.dto.response;

import java.time.LocalDateTime;

import com.qrio.restaurantActivation.model.type.ActivationStatus;

public record RestaurantActivationDetailResponse(
    Long id,
    Long restaurantId,
    Long adminId,
    ActivationStatus status,
    String comment,
    LocalDateTime createdAt,
    LocalDateTime reviewedAt
) {}
