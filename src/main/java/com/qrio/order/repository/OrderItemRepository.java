package com.qrio.order.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.qrio.order.dto.response.OrderDetailResponse;
import com.qrio.order.model.Order;
import com.qrio.order.model.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    @Query("""
        SELECT 
            li.product.id AS productId,
            li.quantity AS quantity,
            li.unitPrice AS unitPrice,
            li.subtotal AS subtotal
        FROM OrderItem li
        WHERE li.order.id = :id
    """)
    List<OrderDetailResponse.OrderItem> findOrderItemsByOrderId(Long id);

    void deleteAllByOrder(Order order);
}
