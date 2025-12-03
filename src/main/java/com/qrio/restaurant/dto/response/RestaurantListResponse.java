package com.qrio.restaurant.dto.response;

public record RestaurantListResponse(
    Long id,
    String code,
    Long adminId,
    String name,
    String logoUrl,
    Boolean isActive
) {}
