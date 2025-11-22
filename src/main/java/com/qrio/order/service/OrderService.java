package com.qrio.order.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.qrio.order.dto.request.CreateOrderItemRequest;
import com.qrio.order.dto.request.CreateOrderRequest;
import com.qrio.order.dto.request.UpdateOrderItemRequest;
import com.qrio.order.dto.request.UpdateOrderRequest;
import com.qrio.order.dto.response.OrderDetailResponse;
import com.qrio.order.dto.response.OrderListResponse;
import com.qrio.order.model.Order;
import com.qrio.order.model.OrderItem;
import com.qrio.order.model.type.OrderStatus;
import com.qrio.order.repository.OrderItemRepository;
import com.qrio.order.repository.OrderRepository;
import com.qrio.shared.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Validated
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public List<OrderListResponse> getList() {
        return orderRepository.findList();
    }

    public OrderDetailResponse getDetailById(Long id) {
        OrderDetailResponse base = orderRepository.findDetailById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + id));

        return base.withItems(orderItemRepository.findOrderItemsByOrderId(id));
    }

    @Transactional
    public OrderListResponse create(CreateOrderRequest request) {
        Order order = new Order();
        order.setTableId(request.tableId());
        order.setCustomerId(request.customerId());
        order.setStatus(OrderStatus.valueOf(request.status()));
        order.setTotal(request.total());
        order.setPeople(request.people());
        order.setCreatedAt(LocalDateTime.now());

        Order saved = orderRepository.save(order);

        for (CreateOrderItemRequest itemDto : request.items()) {
            OrderItem item = new OrderItem();
            item.setOrder(saved);
            item.setDishId(itemDto.dishId());
            item.setQuantity(itemDto.quantity());
            item.setUnitPrice(itemDto.unitPrice());
            item.setSubtotal(itemDto.unitPrice().multiply(java.math.BigDecimal.valueOf(itemDto.quantity())));

            orderItemRepository.save(item);
        }

        return toListResponse(saved);
    }

    @Transactional
    public OrderListResponse update(Long id, UpdateOrderRequest request) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + id));

        order.setCustomerId(request.customerId());
        order.setStatus(OrderStatus.valueOf(request.status()));
        order.setPeople(request.people());

        Order updated = orderRepository.save(order);

        orderItemRepository.deleteAllByOrder(updated);

        for (UpdateOrderItemRequest itemDto : request.items()) {
            OrderItem item = new OrderItem();
            item.setOrder(updated);
            item.setDishId(itemDto.dishId());
            item.setQuantity(itemDto.quantity());
            item.setUnitPrice(itemDto.unitPrice());
            item.setSubtotal(itemDto.unitPrice().multiply(BigDecimal.valueOf(itemDto.quantity())));

            orderItemRepository.save(item);
        }

        BigDecimal total = updated.getOrderItems().stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        updated.setTotal(total);
        orderRepository.save(updated);

        return toListResponse(updated);
    }

    public OrderListResponse toListResponse(Order order) {
        return new OrderListResponse(
                order.getId(),
                order.getTableId(),
                order.getCustomerId(),
                order.getStatus(),
                order.getTotal(),
                order.getPeople(),
                
                (long) order.getOrderItems().size());
    }
}
