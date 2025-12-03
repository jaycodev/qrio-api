package com.qrio.offer.dto.request;

import java.math.BigDecimal;
import java.util.List;

import com.qrio.shared.validation.NoNullElements;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record UpdateOfferRequest(
    @NotBlank(message = "Title is required")
    String title,

    String description,

    @DecimalMin(value = "0.00", message = "Offer discount percentage must be at least 0.00")
    @Digits(integer = 3, fraction = 2, message = "Offer discount percentage must have at most 3 integer digits and 2 fractional digits")
    BigDecimal offerDiscountPercentage,

    @NotNull(message = "Active status is required")
    Boolean active,

    @NotNull(message = "Products are required")
    @NotEmpty(message = "Products cannot be empty")
    @NoNullElements(message = "Products cannot contain null elements")
    @Valid
    List<UpdateOfferProductRequest> products
) {}
