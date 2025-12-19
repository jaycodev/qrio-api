package com.qrio.product.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.qrio.product.dto.request.CreateProductRequest;
import com.qrio.product.dto.request.UpdateProductRequest;
import com.qrio.product.dto.response.ProductDetailResponse;
import com.qrio.product.dto.response.ProductListResponse;
import com.qrio.product.service.ProductService;
import com.qrio.shared.api.ApiSuccess;
import com.qrio.shared.validation.ValidationMessages;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Validated
@Tag(name = "Products", description = "Operations related to products")
public class ProductController {
    private final ProductService productService;

    @GetMapping
    @Operation(summary = "List products by branch")
    public ResponseEntity<ApiSuccess<List<ProductListResponse>>> list(@RequestParam Long branchId) {
        List<ProductListResponse> products = productService.getList(branchId);
        ApiSuccess<List<ProductListResponse>> response = new ApiSuccess<>(
                products.isEmpty() ? "No products found" : "Products listed successfully",
                products);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a product by ID")
    public ResponseEntity<ApiSuccess<ProductDetailResponse>> get(
            @PathVariable @Min(value = 1, message = ValidationMessages.ID_MIN_VALUE) Long id) {
        ProductDetailResponse product = productService.getDetailById(id);
        return ResponseEntity.ok(new ApiSuccess<>("Product found", product));
    }

    @PostMapping
    @Operation(summary = "Create a new product")
    public ResponseEntity<ApiSuccess<ProductListResponse>> create(
            @RequestParam Long branchId,
            @Valid @RequestBody CreateProductRequest request) {
        ProductListResponse created = productService.create(request, branchId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiSuccess<>("Product created successfully", created));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a product by ID")
    public ResponseEntity<ApiSuccess<ProductListResponse>> update(
            @PathVariable @Min(value = 1, message = ValidationMessages.ID_MIN_VALUE) Long id,
            @RequestParam Long branchId,
            @Valid @RequestBody UpdateProductRequest request) {
        ProductListResponse result = productService.update(id, request, branchId);
        return ResponseEntity.ok(new ApiSuccess<>("Product updated successfully", result));
    }

    @PatchMapping("/{id}/availability")
    @Operation(summary = "Set product availability for a branch")
    public ResponseEntity<ApiSuccess<Boolean>> setAvailability(
            @PathVariable @Min(value = 1, message = ValidationMessages.ID_MIN_VALUE) Long id,
            @RequestParam Long branchId,
            @RequestBody java.util.Map<String, Boolean> body) {
        Boolean available = body.getOrDefault("available", false);
        Boolean result = productService.setAvailability(id, branchId, available);
        return ResponseEntity.ok(new ApiSuccess<>("Availability updated", result));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a product by ID")
    public ResponseEntity<ApiSuccess<Boolean>> delete(
            @PathVariable @Min(value = 1, message = ValidationMessages.ID_MIN_VALUE) Long id) {
        productService.delete(id);
        return ResponseEntity.ok(new ApiSuccess<>("Product deleted successfully", true));
    }
}