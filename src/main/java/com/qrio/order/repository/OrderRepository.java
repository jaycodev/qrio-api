package com.qrio.order.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.qrio.order.dto.response.OrderDetailResponse;
import com.qrio.order.dto.response.OrderListResponse;
import com.qrio.order.model.Order;
import com.qrio.order.model.type.OrderStatus;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends CrudRepository<Order, Long> {
    @Query("""
        SELECT 
            o.id AS id,
            o.code AS code,

            o.diningTable.id AS tableId,
            o.diningTable.tableNumber AS tableNumber,
            
            o.customer.id AS customerId,
            o.customer.code AS customerCode,
            o.customer.name AS customerName,
            
            o.status AS status,
            o.total AS total,
            o.people AS people,
            
            COALESCE(COUNT(oi.id), 0) AS itemCount
        FROM Order o
        LEFT JOIN o.orderItems oi
        JOIN o.diningTable dt
        WHERE dt.branch.id = :branchId
        GROUP BY o.id, o.code, o.diningTable.id, o.diningTable.tableNumber, o.customer.id, o.customer.code, o.customer.name, o.status, o.total, o.people
        ORDER BY o.id DESC
    """)
    List<OrderListResponse> findList(Long branchId);

    @Query("""
        SELECT 
            o.id AS id,
            o.code AS code,

            o.diningTable.id AS tableId,
            o.diningTable.tableNumber AS tableNumber,
            
            o.customer.id AS customerId,
            o.customer.code AS customerCode,
            o.customer.name AS customerName,
            
            o.status AS status,
            o.total AS total,
            o.people AS people,
            
            COALESCE(COUNT(oi.id), 0) AS itemCount
        FROM Order o
        LEFT JOIN o.orderItems oi
        JOIN o.diningTable dt
        WHERE dt.branch.id = :branchId AND o.status = :status
        GROUP BY o.id, o.code, o.diningTable.id, o.diningTable.tableNumber, o.customer.id, o.customer.code, o.customer.name, o.status, o.total, o.people
        ORDER BY o.id DESC
    """)
    List<OrderListResponse> findListByBranchAndStatus(Long branchId, OrderStatus status);

    @Query("""
        SELECT 
            o.id AS id,
            o.diningTable.id AS tableId,
            o.customer.id AS customerId,
            o.status AS status,
            o.total AS total,
            o.people AS people,
            
            NULL AS items
        FROM Order o
        WHERE o.id = :id
    """)
    Optional<OrderDetailResponse> findDetailById(Long id);
}
