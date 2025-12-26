package com.qrio.order.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.qrio.customer.model.Customer;
import com.qrio.customer.repository.CustomerRepository;
import com.qrio.product.model.Product;
import com.qrio.product.repository.ProductRepository;
import com.qrio.table.model.DiningTable;
import com.qrio.table.repository.DiningTableRepository;
import com.qrio.order.dto.request.CreateOrderItemRequest;
import com.qrio.order.dto.request.CreateOrderRequest;
import com.qrio.order.dto.request.UpdateOrderItemRequest;
import com.qrio.order.dto.request.UpdateOrderRequest;
import com.qrio.order.dto.response.OrderDetailResponse;
import com.qrio.order.dto.response.OrderFilterOptionsResponse;
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
    private final CustomerRepository customerRepository;
    private final DiningTableRepository diningTableRepository;
    private final ProductRepository productRepository;

    public List<OrderListResponse> getList(Long branchId) {
        List<OrderListResponse> list = orderRepository.findList(branchId);
        return list.stream()
                .map(o -> new OrderListResponse(
                        o.id(),
                        o.code() != null ? o.code() : String.format("OR-%04d", o.id()),
                        o.getTable().id(),
                        o.getTable().number(),
                        o.getCustomer().id(),
                        o.getCustomer().code() != null ? o.getCustomer().code() : String.format("CU-%04d", o.getCustomer().id()),
                        o.getCustomer().name(),
                        o.status(),
                        o.total(),
                        o.people(),
                        o.itemCount()
                ))
                .toList();
    }

        public List<OrderListResponse> getListByStatus(Long branchId, String status) {
                com.qrio.order.model.type.OrderStatus orderStatus;
                try {
                        orderStatus = com.qrio.order.model.type.OrderStatus.valueOf(status);
                } catch (IllegalArgumentException ex) {
                        throw new IllegalArgumentException("Invalid order status: " + status);
                }

                List<OrderListResponse> list = orderRepository.findListByBranchAndStatus(branchId, orderStatus);
                return list.stream()
                                .map(o -> new OrderListResponse(
                                                o.id(),
                                                o.code() != null ? o.code() : String.format("OR-%04d", o.id()),
                                                o.getTable().id(),
                                                o.getTable().number(),
                                                o.getCustomer().id(),
                                                o.getCustomer().code() != null ? o.getCustomer().code() : String.format("CU-%04d", o.getCustomer().id()),
                                                o.getCustomer().name(),
                                                o.status(),
                                                o.total(),
                                                o.people(),
                                                o.itemCount()
                                ))
                                .toList();
        }

    public OrderFilterOptionsResponse getFilterOptions(Long branchId) {
        return new OrderFilterOptionsResponse(
                diningTableRepository.findForOptions(branchId),
                customerRepository.findForOptions());
    }

    public OrderDetailResponse getDetailById(Long id) {
        OrderDetailResponse base = orderRepository.findDetailById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + id));

        return base.withItems(orderItemRepository.findOrderItemsByOrderId(id));
    }

    @Transactional
    public OrderListResponse create(CreateOrderRequest request) {

        Customer customer = customerRepository.findById(request.customerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        DiningTable diningTable = diningTableRepository.findById(request.tableId())
                .orElseThrow(() -> new ResourceNotFoundException("DiningTable not found"));

        Order order = new Order();

        order.setDiningTable(diningTable);
        order.setCustomer(customer);
        order.setStatus(OrderStatus.valueOf(request.status()));
        order.setTotal(request.total());
        order.setPeople(request.people());
        order.setCreatedAt(LocalDateTime.now());

        Order saved = orderRepository.save(order);

        for (CreateOrderItemRequest itemDto : request.items()) {
            Product product = productRepository.findById(itemDto.productId())
                    .orElseThrow(
                            () -> new ResourceNotFoundException("Product not found with ID: " + itemDto.productId()));

            OrderItem item = new OrderItem();
            item.setOrder(saved);
            item.setProduct(product);
            item.setQuantity(itemDto.quantity());
            item.setUnitPrice(itemDto.unitPrice());
            item.setSubtotal(itemDto.unitPrice().multiply(BigDecimal.valueOf(itemDto.quantity())));

            orderItemRepository.save(item);
        }

        return toListResponse(saved);
    }

    @Transactional
    public OrderListResponse update(Long id, UpdateOrderRequest request) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + id));
        Customer customer = customerRepository.findById(request.customerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        order.setCustomer(customer);
        order.setStatus(OrderStatus.valueOf(request.status()));
        order.setPeople(request.people());

        Order updated = orderRepository.save(order);

        orderItemRepository.deleteAllByOrder(updated);

        for (UpdateOrderItemRequest itemDto : request.items()) {
            Product product = productRepository.findById(itemDto.productId())
                    .orElseThrow(
                            () -> new ResourceNotFoundException("Product not found with ID: " + itemDto.productId()));

            OrderItem item = new OrderItem();
            item.setOrder(updated);
            item.setProduct(product);
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
        String code = order.getCode() != null ? order.getCode() : String.format("OR-%04d", order.getId());
        String customerCode = order.getCustomer().getCode() != null
                ? order.getCustomer().getCode()
                : String.format("CU-%04d", order.getCustomer().getId());
        return new OrderListResponse(
                order.getId(),
                code,

                order.getDiningTable().getId(),
                order.getDiningTable().getTableNumber(),

                order.getCustomer().getId(),
                customerCode,
                order.getCustomer().getName(),

                order.getStatus(),
                order.getTotal(),
                order.getPeople(),

                (long) order.getOrderItems().size());
    }

        @Transactional
        public void delete(Long id) {
                Order order = orderRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + id));
                orderItemRepository.deleteAllByOrder(order);
                orderRepository.delete(order);
        }
}
