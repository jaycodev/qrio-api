package com.qrio.paymentMethod.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.qrio.paymentMethod.dto.request.CreatePaymentMethodRequest;
import com.qrio.paymentMethod.dto.request.UpdatePaymentMethodRequest;
import com.qrio.paymentMethod.dto.response.PaymentMethodDetailResponse;
import com.qrio.paymentMethod.dto.response.PaymentMethodListResponse;
import com.qrio.paymentMethod.service.PaymentMethodService;
import com.qrio.shared.api.ApiSuccess;
import com.qrio.shared.validation.ValidationMessages;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/payment-methods")
@RequiredArgsConstructor
@Validated
@Tag(name = "Payment Methods", description = "Operations related to payment methods")
public class PaymentMethodController {

    private final PaymentMethodService paymentMethodService;

    @GetMapping
    @Operation(summary = "List payment methods by customer")
    public ResponseEntity<ApiSuccess<List<PaymentMethodListResponse>>> list(
            @RequestParam(required = false) Long customerId) {
        List<PaymentMethodListResponse> methods = paymentMethodService.getList(customerId);
        ApiSuccess<List<PaymentMethodListResponse>> response = new ApiSuccess<>(
                methods.isEmpty() ? "No payment methods found" : "Payment methods listed successfully",
                methods);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a payment method by ID")
    public ResponseEntity<ApiSuccess<PaymentMethodDetailResponse>> get(
            @PathVariable @Min(value = 1, message = ValidationMessages.ID_MIN_VALUE) Long id) {
        PaymentMethodDetailResponse method = paymentMethodService.getDetailById(id);
        return ResponseEntity.ok(new ApiSuccess<>("Payment method found", method));
    }

    @PostMapping
    @Operation(summary = "Create a new payment method")
    public ResponseEntity<ApiSuccess<PaymentMethodListResponse>> create(
            @Valid @RequestBody CreatePaymentMethodRequest request) {
        PaymentMethodListResponse created = paymentMethodService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiSuccess<>("Payment method created successfully", created));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a payment method by ID")
    public ResponseEntity<ApiSuccess<PaymentMethodListResponse>> update(
            @PathVariable @Min(value = 1, message = ValidationMessages.ID_MIN_VALUE) Long id,
            @Valid @RequestBody UpdatePaymentMethodRequest request) {
        PaymentMethodListResponse result = paymentMethodService.update(id, request);
        return ResponseEntity.ok(new ApiSuccess<>("Payment method updated successfully", result));
    }
}
