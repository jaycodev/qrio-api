package com.qrio.order.dto.request;

import java.math.BigDecimal;
import java.util.List;

import com.qrio.order.model.type.OrderStatus;
import com.qrio.shared.validation.ValidEnum;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record CreateOrderRequest(
    @NotNull(message = "Table ID is required")
    @Min(value = 1, message = "Table ID must be at least 1")
    Long tableId,

    @NotNull(message = "Customer ID is required")
    @Min(value = 1, message = "Customer ID must be at least 1")
    Long customerId,

    @NotBlank(message = "Status is required")
    @ValidEnum(enumClass = OrderStatus.class, message = "Invalid status")
    String status,

    @NotNull(message = "Total is required")
    @DecimalMin(value = "0.00", message = "Total must be at least 0.00")
    @Digits(integer = 8, fraction = 2, message = "Total must have at most 8 integer digits and 2 fractional digits")
    BigDecimal total,

    @NotNull(message = "People is required")
    @Min(value = 1, message = "People must be at least 1")
    Integer people,

    @NotNull(message = "Items are required")
    @NotEmpty(message = "Items cannot be empty")
    List<CreateOrderItemRequest> items
) {}
