package com.qrio.order.dto.response;

import java.math.BigDecimal;

import com.qrio.order.model.type.OrderStatus;

public record OrderListResponse(
    Long id,
    Long tableId,
    Long customerId,
    OrderStatus status,
    BigDecimal total,
    Integer people,
    
    Long itemCount
) {}
