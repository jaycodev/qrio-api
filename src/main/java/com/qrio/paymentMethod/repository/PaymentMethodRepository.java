package com.qrio.paymentMethod.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.qrio.paymentMethod.model.PaymentMethod;

public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Long> {

    List<PaymentMethod> findByCustomerId(Long customerId);

    boolean existsByPaymentToken(String paymentToken);

    Optional<PaymentMethod> findByCustomerIdAndType(Long customerId, String type);
}
