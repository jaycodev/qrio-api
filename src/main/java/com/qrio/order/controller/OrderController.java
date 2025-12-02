package com.qrio.order.controller;

import com.qrio.order.dto.request.CreateOrderRequest;
import com.qrio.order.dto.request.UpdateOrderRequest;
import com.qrio.order.dto.response.OrderDetailResponse;
import com.qrio.order.dto.response.OrderFilterOptionsResponse;
import com.qrio.order.dto.response.OrderListResponse;
import com.qrio.order.service.OrderService;
import com.qrio.shared.api.ApiSuccess;
import com.qrio.shared.validation.ValidationMessages;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Validated
@Tag(name = "Orders", description = "Operations related to orders")
public class OrderController {
    private final OrderService orderService;

    @GetMapping
    @Operation(summary = "List orders by restaurant and branch")
    public ResponseEntity<ApiSuccess<List<OrderListResponse>>> list(
            @RequestParam Long restaurantId,
            @RequestParam(required = false) Long branchId) {
        List<OrderListResponse> orders = orderService.getList(restaurantId, branchId);
        ApiSuccess<List<OrderListResponse>> response = new ApiSuccess<>(
                orders.isEmpty() ? "No orders found" : "Orders listed successfully",
                orders);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/filter-options")
    @Operation(summary = "Get filter options for orders")
    public ResponseEntity<ApiSuccess<OrderFilterOptionsResponse>> filterOptions(
            @RequestParam Long branchId) {
        OrderFilterOptionsResponse options = orderService.getFilterOptions(branchId);

        boolean hasOptions = !options.tables().isEmpty() || !options.customers().isEmpty();

        ApiSuccess<OrderFilterOptionsResponse> response = new ApiSuccess<>(
                hasOptions ? "Filter options retrieved successfully" : "No filter options found",
                options);

        HttpStatus status = hasOptions ? HttpStatus.OK : HttpStatus.NO_CONTENT;
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get an order by ID")
    public ResponseEntity<ApiSuccess<OrderDetailResponse>> get(
            @PathVariable @Min(value = 1, message = ValidationMessages.ID_MIN_VALUE) Long id) {
        OrderDetailResponse order = orderService.getDetailById(id);
        return ResponseEntity.ok(new ApiSuccess<>("Order found", order));
    }

    @PostMapping
    @Operation(summary = "Create a new order")
    public ResponseEntity<ApiSuccess<OrderListResponse>> create(@Valid @RequestBody CreateOrderRequest request) {
        OrderListResponse created = orderService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiSuccess<>("Order created successfully", created));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an order by ID")
    public ResponseEntity<ApiSuccess<OrderListResponse>> update(
            @PathVariable @Min(value = 1, message = ValidationMessages.ID_MIN_VALUE) Long id,
            @Valid @RequestBody UpdateOrderRequest request) {
        OrderListResponse result = orderService.update(id, request);
        return ResponseEntity.ok(new ApiSuccess<>("Order updated successfully", result));
    }
}
