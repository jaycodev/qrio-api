package com.qrio.categories.dto.response;

public record CategorieResponse(
        Long id,
        Long restaurantId,
        String name
) {
}
