package com.qrio.categories.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.qrio.categories.dto.request.CreateCategorieRequest;
import com.qrio.categories.dto.request.UpdateCategorieRequest;
import com.qrio.categories.dto.response.CategorieResponse;
import com.qrio.categories.service.CategoriesService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoriesController {

    private final CategoriesService categoriesService;

    @Operation(summary = "Create a new category for a restaurant")
    @PostMapping
    public ResponseEntity<CategorieResponse> createCategory(@RequestBody CreateCategorieRequest request) {
    	CategorieResponse response = categoriesService.createCategory(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Update a category by ID")
    @PutMapping("/{id}")
    public ResponseEntity<CategorieResponse> updateCategory(
            @PathVariable Long id,
            @RequestBody UpdateCategorieRequest request) {
    	CategorieResponse response = categoriesService.updateCategory(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get a category by ID")
    @GetMapping("/{id}")
    public ResponseEntity<CategorieResponse> getCategoryById(@PathVariable Long id) {
        CategorieResponse response = categoriesService.getCategoryById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all categories by Restaurant ID")
    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<CategorieResponse>> getCategoriesByRestaurantId(@PathVariable Long restaurantId) {
        List<CategorieResponse> response = categoriesService.getCategoriesByRestaurantId(restaurantId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete a category by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoriesService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}