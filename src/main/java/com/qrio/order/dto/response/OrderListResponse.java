package com.qrio.order.dto.response;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.qrio.order.model.type.OrderStatus;

@JsonPropertyOrder({ "id", "table", "customer", "status", "total", "people", "itemCount" })
public record OrderListResponse(
    Long id,

    @JsonIgnore Long tableId,
    @JsonIgnore Integer tableNumber,

    @JsonIgnore Long customerId,
    @JsonIgnore String customerName,

    OrderStatus status,
    BigDecimal total,
    Integer people,
    Long itemCount
) {

    @JsonGetter("table")
    public Table getTable() {
        return new Table(tableId, tableNumber);
    }

    public record Table(
        Long id,
        Integer number
    ) {}

    @JsonGetter("customer")
    public Customer getCustomer() {
        return new Customer(customerId, customerName);
    }

    public record Customer(
        Long id,
        String name
    ) {}
}
