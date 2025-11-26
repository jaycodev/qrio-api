package com.qrio.paymentMethod.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.qrio.customer.repository.CustomerRepository;
import com.qrio.paymentMethod.dto.request.CreatePaymentMethodRequest;
import com.qrio.paymentMethod.dto.request.UpdatePaymentMethodRequest;
import com.qrio.paymentMethod.dto.response.PaymentMethodResponse;
import com.qrio.paymentMethod.model.PaymentMethod;
import com.qrio.paymentMethod.repository.PaymentMethodRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentMethodService {

    private final PaymentMethodRepository paymentMethodRepository;
    private final CustomerRepository customerRepository;

    @Transactional
    public PaymentMethodResponse createPaymentMethod(CreatePaymentMethodRequest request) {
        if (paymentMethodRepository.existsByPaymentToken(request.paymentToken())) {
            throw new IllegalArgumentException("Payment token already exists");
        }

        var customer = customerRepository.findById(request.customerId())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        PaymentMethod pm = new PaymentMethod();
        pm.setCustomer(customer);
        pm.setType(request.type());
        pm.setPaymentToken(request.paymentToken());
        pm.setLast4(request.last4());
        pm.setBrand(request.brand());
        pm.setCreatedAt(LocalDateTime.now());

        PaymentMethod saved = paymentMethodRepository.save(pm);

        return mapToResponse(saved);
    }

    @Transactional
    public PaymentMethodResponse updatePaymentMethod(Long id, UpdatePaymentMethodRequest request) {
        PaymentMethod pm = paymentMethodRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Payment method not found"));

        pm.setType(request.type());
        pm.setLast4(request.last4());
        pm.setBrand(request.brand());

        PaymentMethod updated = paymentMethodRepository.save(pm);

        return mapToResponse(updated);
    }

    @Transactional(readOnly = true)
    public PaymentMethodResponse getPaymentMethodById(Long id) {
        PaymentMethod pm = paymentMethodRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Payment method not found"));
        return mapToResponse(pm);
    }

    @Transactional(readOnly = true)
    public List<PaymentMethodResponse> getAllByCustomer(Long customerId) {
        List<PaymentMethod> methods = paymentMethodRepository.findByCustomerId(customerId);
        return methods.stream()
                .map(this::mapToResponse)
                .toList();
    }

    private PaymentMethodResponse mapToResponse(PaymentMethod pm) {
        return new PaymentMethodResponse(
                pm.getId(),
                pm.getCustomer().getId(),
                pm.getType(),
                pm.getLast4(),
                pm.getBrand(),
                pm.getCreatedAt());
    }
}
