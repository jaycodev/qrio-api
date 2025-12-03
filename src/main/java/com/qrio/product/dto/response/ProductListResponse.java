package com.qrio.product.dto.response;

import java.math.BigDecimal;

public record ProductListResponse(
        Long id,
        Long categoryId,
        String name,
        BigDecimal price,
        String imageUrl,
        Boolean available) {
}
