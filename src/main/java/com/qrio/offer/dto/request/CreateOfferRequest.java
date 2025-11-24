package com.qrio.offer.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateOfferRequest(
    @NotNull(message = "Restaurant ID is required")
    @Min(value = 1, message = "Restaurant ID must be at least 1")
    Long restaurantId,

    @NotBlank(message = "Title is required")
    String title,

    String description,

    @NotNull(message = "Offer price is required")
    @DecimalMin(value = "0.00", message = "Offer price must be at least 0.00")
    @Digits(integer = 8, fraction = 2, message = "Offer price must have at most 8 integer digits and 2 fractional digits")
    BigDecimal offerPrice,

    @NotNull(message = "Active status is required")
    Boolean active
) {}
