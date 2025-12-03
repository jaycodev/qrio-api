package com.qrio.offer.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record OfferDetailResponse(
    Long id,
    String code,
    Long restaurantId,
    String title,
    String description,
    BigDecimal offerDiscountPercentage,
    Boolean active,
    
    List<OfferProductItem> products
) {

    public OfferDetailResponse withProducts(List<OfferProductItem> products) {
        return new OfferDetailResponse(id, code, restaurantId, title, description, offerDiscountPercentage, active, products);
    }

    public record OfferProductItem(
        Long productId,
        Integer quantity
    ) {}
}
