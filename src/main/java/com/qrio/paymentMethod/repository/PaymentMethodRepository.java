package com.qrio.paymentMethod.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.qrio.paymentMethod.dto.response.PaymentMethodDetailResponse;
import com.qrio.paymentMethod.dto.response.PaymentMethodListResponse;
import com.qrio.paymentMethod.model.PaymentMethod;

public interface PaymentMethodRepository extends CrudRepository<PaymentMethod, Long> {

    @Query("""
        SELECT 
            pm.id AS id,
            pm.customer.id AS customerId,
            pm.type AS type,
            pm.last4 AS last4,
            pm.brand AS brand,
            pm.createdAt AS createdAt
        FROM PaymentMethod pm
        WHERE (:customerId IS NULL OR pm.customer.id = :customerId)
        ORDER BY pm.createdAt DESC
    """)
    List<PaymentMethodListResponse> findList(Long customerId);

    @Query("""
        SELECT 
            pm.id AS id,
            pm.customer.id AS customerId,
            pm.type AS type,
            pm.paymentToken AS paymentToken,
            pm.last4 AS last4,
            pm.brand AS brand,
            pm.createdAt AS createdAt
        FROM PaymentMethod pm
        WHERE pm.id = :id
    """)
    Optional<PaymentMethodDetailResponse> findDetailById(@Param("id") Long id);

    boolean existsByPaymentToken(String paymentToken);
}
