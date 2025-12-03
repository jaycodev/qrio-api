package com.qrio.offer.dto.response;

import java.math.BigDecimal;

public record OfferListResponse(
    Long id,
    String code,
    Long restaurantId,
    String title,
    String description,
    BigDecimal offerDiscountPercentage,
    Boolean active
) {}
