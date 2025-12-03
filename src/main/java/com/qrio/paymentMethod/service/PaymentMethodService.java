package com.qrio.paymentMethod.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.qrio.customer.model.Customer;
import com.qrio.customer.repository.CustomerRepository;
import com.qrio.paymentMethod.dto.request.CreatePaymentMethodRequest;
import com.qrio.paymentMethod.dto.request.UpdatePaymentMethodRequest;
import com.qrio.paymentMethod.dto.response.PaymentMethodDetailResponse;
import com.qrio.paymentMethod.dto.response.PaymentMethodListResponse;
import com.qrio.paymentMethod.model.PaymentMethod;
import com.qrio.paymentMethod.repository.PaymentMethodRepository;
import com.qrio.shared.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Validated
public class PaymentMethodService {

    private final PaymentMethodRepository paymentMethodRepository;
    private final CustomerRepository customerRepository;

    @Transactional(readOnly = true)
    public List<PaymentMethodListResponse> getList(Long customerId) {
        return paymentMethodRepository.findList(customerId);
    }

    @Transactional(readOnly = true)
    public PaymentMethodDetailResponse getDetailById(Long id) {
        return paymentMethodRepository.findDetailById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment method not found"));
    }

    @Transactional
    public PaymentMethodListResponse create(CreatePaymentMethodRequest request) {
        if (paymentMethodRepository.existsByPaymentToken(request.paymentToken())) {
            throw new IllegalArgumentException("Payment token already exists");
        }

        Customer customer = customerRepository.findById(request.customerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        PaymentMethod pm = new PaymentMethod();
        pm.setCustomer(customer);
        pm.setType(request.type());
        pm.setPaymentToken(request.paymentToken());
        pm.setLast4(request.last4());
        pm.setBrand(request.brand());
        pm.setCreatedAt(LocalDateTime.now());

        PaymentMethod saved = paymentMethodRepository.save(pm);
        return toListResponse(saved);
    }

    @Transactional
    public PaymentMethodListResponse update(Long id, UpdatePaymentMethodRequest request) {
        PaymentMethod pm = paymentMethodRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment method not found"));

        pm.setType(request.type());
        pm.setLast4(request.last4());
        pm.setBrand(request.brand());

        PaymentMethod updated = paymentMethodRepository.save(pm);
        return toListResponse(updated);
    }

    private PaymentMethodListResponse toListResponse(PaymentMethod pm) {
        return new PaymentMethodListResponse(
                pm.getId(),
                pm.getCustomer().getId(),
                pm.getType(),
                pm.getLast4(),
                pm.getBrand(),
                pm.getCreatedAt());
    }
}
