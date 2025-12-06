package com.qrio.restaurant.dto.response;

public record RestaurantListResponse(
    Long id,
    String code,
    Long userId,
    String name,
    String logoUrl,
    Boolean isActive
) {}
