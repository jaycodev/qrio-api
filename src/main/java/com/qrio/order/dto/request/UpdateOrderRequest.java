package com.qrio.order.dto.request;

import java.util.List;

import com.qrio.order.model.type.OrderStatus;
import com.qrio.shared.validation.ValidEnum;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record UpdateOrderRequest(
    @NotNull(message = "Customer ID is required")
    @Min(value = 1, message = "Customer ID must be at least 1")
    Long customerId,

    @NotBlank(message = "Status is required")
    @ValidEnum(enumClass = OrderStatus.class, message = "Invalid status")
    String status,

    @NotNull(message = "People is required")
    @Min(value = 1, message = "People must be at least 1")
    Integer people,

    @NotNull(message = "Items are required")
    @NotEmpty(message = "Items cannot be empty")
    List<UpdateOrderItemRequest> items
) {}