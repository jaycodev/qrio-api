package com.qrio.order.dto.response;

import java.math.BigDecimal;
import java.util.List;

import com.qrio.order.model.type.OrderStatus;

public record OrderDetailResponse(
    Long id,
    Long tableId,
    Long customerId,
    OrderStatus status,
    BigDecimal total,
    Integer people,

    List<OrderItem> items
) {

    public OrderDetailResponse withItems(List<OrderItem> items) {
        return new OrderDetailResponse(id, tableId, customerId, status, total, people, items);
    }

    public record OrderItem(
        Long dishId,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal subtotal
    ) {}
}
