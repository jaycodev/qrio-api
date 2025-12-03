package com.qrio.category.dto.response;

public record CategoryDetailResponse(
    Long id,
    Long restaurantId,
    String name
) {}
