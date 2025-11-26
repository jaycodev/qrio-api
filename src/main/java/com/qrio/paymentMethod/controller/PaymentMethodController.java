package com.qrio.paymentMethod.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.qrio.paymentMethod.dto.request.CreatePaymentMethodRequest;
import com.qrio.paymentMethod.dto.request.UpdatePaymentMethodRequest;
import com.qrio.paymentMethod.dto.response.PaymentMethodResponse;
import com.qrio.paymentMethod.service.PaymentMethodService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/payment-methods")
@RequiredArgsConstructor
public class PaymentMethodController {

    private final PaymentMethodService paymentMethodService;

    @PostMapping
    @Operation(summary = "Create a new payment method")
    public ResponseEntity<PaymentMethodResponse> createPaymentMethod(
            @RequestBody CreatePaymentMethodRequest request) {
        PaymentMethodResponse created = paymentMethodService.createPaymentMethod(request);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update payment method by ID")
    public ResponseEntity<PaymentMethodResponse> updatePaymentMethod(
            @PathVariable Long id,
            @RequestBody UpdatePaymentMethodRequest request) {
        PaymentMethodResponse updated = paymentMethodService.updatePaymentMethod(id, request);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get payment method by ID")
    public ResponseEntity<PaymentMethodResponse> getPaymentMethodById(@PathVariable Long id) {
        PaymentMethodResponse pm = paymentMethodService.getPaymentMethodById(id);
        return ResponseEntity.ok(pm);
    }

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Get all payment methods for a customer")
    public ResponseEntity<List<PaymentMethodResponse>> getAllByCustomer(@PathVariable Long customerId) {
        List<PaymentMethodResponse> methods = paymentMethodService.getAllByCustomer(customerId);
        return ResponseEntity.ok(methods);
    }

}
