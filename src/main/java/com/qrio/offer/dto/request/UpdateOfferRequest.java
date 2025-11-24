package com.qrio.offer.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateOfferRequest(
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
