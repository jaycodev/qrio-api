package com.qrio.offer.dto.response;

import java.math.BigDecimal;

public record OfferListResponse(
    Long id,
    Long restaurantId,
    String title,
    String description,
    BigDecimal offerPrice,
    Boolean active
) {}
