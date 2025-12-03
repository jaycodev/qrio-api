package com.qrio.category.dto.response;

public record CategoryListResponse(
    Long id,
    Long restaurantId,
    String name
) {}
