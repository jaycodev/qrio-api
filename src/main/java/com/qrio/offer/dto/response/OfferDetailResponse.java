package com.qrio.offer.dto.response;

import java.math.BigDecimal;

public record OfferDetailResponse(
    Long id,
    Long restaurantId,
    String title,
    String description,
    BigDecimal offerPrice,
    Boolean active
) {}
