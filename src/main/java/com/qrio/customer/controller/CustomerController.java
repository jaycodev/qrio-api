package com.qrio.customer.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.qrio.customer.dto.request.CreateCustomerRequest;
import com.qrio.customer.dto.request.UpdateCustomerRequest;
import com.qrio.customer.dto.response.CustomerDetailResponse;
import com.qrio.customer.dto.response.CustomerListResponse;
import com.qrio.customer.service.CustomerService;
import com.qrio.shared.api.ApiSuccess;
import com.qrio.shared.validation.ValidationMessages;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
@Validated
@Tag(name = "Customers", description = "Operations related to customers")
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    @Operation(summary = "List all customers")
    public ResponseEntity<ApiSuccess<List<CustomerListResponse>>> list() {
        List<CustomerListResponse> customers = customerService.getList();
        ApiSuccess<List<CustomerListResponse>> response = new ApiSuccess<>(
                customers.isEmpty() ? "No customers found" : "Customers listed successfully",
                customers);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a customer by ID")
    public ResponseEntity<ApiSuccess<CustomerDetailResponse>> get(
            @PathVariable @Min(value = 1, message = ValidationMessages.ID_MIN_VALUE) Long id) {
        CustomerDetailResponse customer = customerService.getDetailById(id);
        return ResponseEntity.ok(new ApiSuccess<>("Customer found", customer));
    }

    // Endpoint por Firebase removido

    @PostMapping
    @Operation(summary = "Create a new customer")
    public ResponseEntity<ApiSuccess<CustomerListResponse>> create(@Valid @RequestBody CreateCustomerRequest request) {
        CustomerListResponse created = customerService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiSuccess<>("Customer created successfully", created));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a customer by ID")
    public ResponseEntity<ApiSuccess<CustomerListResponse>> update(
            @PathVariable @Min(value = 1, message = ValidationMessages.ID_MIN_VALUE) Long id,
            @Valid @RequestBody UpdateCustomerRequest request) {
        CustomerListResponse result = customerService.update(id, request);
        return ResponseEntity.ok(new ApiSuccess<>("Customer updated successfully", result));
    }
}