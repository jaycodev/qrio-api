package com.qrio.order.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.qrio.order.dto.response.OrderDetailResponse;
import com.qrio.order.dto.response.OrderListResponse;
import com.qrio.order.model.Order;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends CrudRepository<Order, Long> {
    @Query("""
        SELECT 
            o.id AS id,
            o.tableId AS tableId,
            o.customerId AS customerId,
            o.status AS status,
            o.total AS total,
            o.people AS people,
            
            COALESCE(COUNT(oi.id), 0) AS itemCount
        FROM Order o
        LEFT JOIN o.orderItems oi
        GROUP BY o.id, o.tableId, o.customerId, o.status, o.total, o.people
        ORDER BY o.id DESC
    """)
    List<OrderListResponse> findList();

    @Query("""
        SELECT 
            o.id AS id,
            o.tableId AS tableId,
            o.customerId AS customerId,
            o.status AS status,
            o.total AS total,
            o.people AS people,
            
            NULL AS items
        FROM Order o
        WHERE o.id = :id
    """)
    Optional<OrderDetailResponse> findDetailById(Long id);
}
