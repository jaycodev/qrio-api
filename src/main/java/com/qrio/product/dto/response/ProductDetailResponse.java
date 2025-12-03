package com.qrio.product.dto.response;

import java.math.BigDecimal;

public record ProductDetailResponse(
    Long id,
    Long categoryId,
    String name,
    String description,
    BigDecimal price,
    String imageUrl
) {}
