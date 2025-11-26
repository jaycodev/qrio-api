package com.qrio.dishes.dto.response;

import java.math.BigDecimal;

public record DishResponse(
        Long id,
        Long categoryId,
        String name,
        String description,
        BigDecimal price,
        String imageUrl,
        Boolean available
) {
}