package com.qrio.restaurant.dto.response;

import java.time.LocalDateTime;

public record RestaurantDetailResponse(
    Long id,
    String code,
    Long userId,
    String name,
    String description,
    String logoUrl,
    Boolean isActive,
    LocalDateTime createdAt
) {}
