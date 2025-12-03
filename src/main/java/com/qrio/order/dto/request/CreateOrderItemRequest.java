package com.qrio.order.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CreateOrderItemRequest(
    @NotNull(message = "Order ID is required")
    @Min(value = 1, message = "Order ID must be at least 1")
    Long orderId,

    @NotNull(message = "Product ID is required")
    @Min(value = 1, message = "Product ID must be at least 1")
    Long productId,

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    Integer quantity,

    @NotNull(message = "Unit price is required")
    @DecimalMin(value = "0.00", message = "Unit price must be at least 0.00")
    @Digits(integer = 8, fraction = 2, message = "Unit price must have at most 8 integer digits and 2 fractional digits")
    BigDecimal unitPrice
) {}