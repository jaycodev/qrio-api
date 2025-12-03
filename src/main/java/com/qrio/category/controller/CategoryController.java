package com.qrio.category.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.qrio.category.dto.request.CreateCategoryRequest;
import com.qrio.category.dto.request.UpdateCategoryRequest;
import com.qrio.category.dto.response.CategoryDetailResponse;
import com.qrio.category.dto.response.CategoryListResponse;
import com.qrio.category.service.CategoryService;
import com.qrio.shared.api.ApiSuccess;
import com.qrio.shared.validation.ValidationMessages;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Validated
@Tag(name = "Categories", description = "Operations related to categories")
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    @Operation(summary = "List categories by restaurant")
    public ResponseEntity<ApiSuccess<List<CategoryListResponse>>> list(
            @RequestParam(required = false) Long restaurantId) {
        List<CategoryListResponse> categories = categoryService.getList(restaurantId);
        ApiSuccess<List<CategoryListResponse>> response = new ApiSuccess<>(
                categories.isEmpty() ? "No categories found" : "Categories listed successfully",
                categories);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a category by ID")
    public ResponseEntity<ApiSuccess<CategoryDetailResponse>> get(
            @PathVariable @Min(value = 1, message = ValidationMessages.ID_MIN_VALUE) Long id) {
        CategoryDetailResponse category = categoryService.getDetailById(id);
        return ResponseEntity.ok(new ApiSuccess<>("Category found", category));
    }

    @PostMapping
    @Operation(summary = "Create a new category")
    public ResponseEntity<ApiSuccess<CategoryListResponse>> create(
            @Valid @RequestBody CreateCategoryRequest request) {
        CategoryListResponse created = categoryService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiSuccess<>("Category created successfully", created));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a category by ID")
    public ResponseEntity<ApiSuccess<CategoryListResponse>> update(
            @PathVariable @Min(value = 1, message = ValidationMessages.ID_MIN_VALUE) Long id,
            @Valid @RequestBody UpdateCategoryRequest request) {
        CategoryListResponse result = categoryService.update(id, request);
        return ResponseEntity.ok(new ApiSuccess<>("Category updated successfully", result));
    }
}