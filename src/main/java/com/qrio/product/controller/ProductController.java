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
    @Operation(summary = "List products by category")
    public ResponseEntity<ApiSuccess<List<ProductListResponse>>> list(
            @RequestParam(required = false) Long categoryId) {
        List<ProductListResponse> products = productService.getList(categoryId);
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
    public ResponseEntity<ApiSuccess<ProductListResponse>> create(@Valid @RequestBody CreateProductRequest request) {
        ProductListResponse created = productService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiSuccess<>("Product created successfully", created));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a product by ID")
    public ResponseEntity<ApiSuccess<ProductListResponse>> update(
            @PathVariable @Min(value = 1, message = ValidationMessages.ID_MIN_VALUE) Long id,
            @Valid @RequestBody UpdateProductRequest request) {
        ProductListResponse result = productService.update(id, request);
        return ResponseEntity.ok(new ApiSuccess<>("Product updated successfully", result));
    }
}