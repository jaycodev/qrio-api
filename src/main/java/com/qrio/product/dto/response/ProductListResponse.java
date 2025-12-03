package com.qrio.product.dto.response;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "id", "imageUrl", "name", "description", "price", "category", "available" })
public record ProductListResponse(
    Long id,

    String imageUrl,
    String name,
    String description,
    BigDecimal price,

    @JsonIgnore Long categoryId,
    @JsonIgnore String categoryName,

    Boolean available
) {

    @JsonGetter("category")
    public Category getCategory() {
        return new Category(categoryId, categoryName);
    }

    public record Category(
        Long id,
        String name
    ) {}
}
