package com.qrio.dishes.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.qrio.dishes.dto.request.CreateDishRequest;
import com.qrio.dishes.dto.request.UpdateDishRequest;
import com.qrio.dishes.dto.response.DishResponse;
import com.qrio.dishes.service.DishService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/dishes")
@RequiredArgsConstructor
public class DishController {

    private final DishService dishService;

    @Operation(summary = "Create a new dish")
    @PostMapping
    public ResponseEntity<DishResponse> createDish(@RequestBody CreateDishRequest request) {
        DishResponse response = dishService.createDish(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Update a dish by ID")
    @PutMapping("/{id}")
    public ResponseEntity<DishResponse> updateDish(
            @PathVariable Long id,
            @RequestBody UpdateDishRequest request) {
        DishResponse response = dishService.updateDish(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get a dish by ID")
    @GetMapping("/{id}")
    public ResponseEntity<DishResponse> getDishById(@PathVariable Long id) {
        DishResponse response = dishService.getDishById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get dishes by Category ID")
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<DishResponse>> getDishesByCategoryId(
        @PathVariable Long categoryId,
        @RequestParam(required = false, defaultValue = "false") boolean onlyAvailable) {
        
        // El parámetro 'onlyAvailable' filtra solo los que estan disponibles o que esten en stock(soy bravo)
        List<DishResponse> response = dishService.getDishesByCategoryId(categoryId, onlyAvailable);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Set availability status for a dish")
    @PatchMapping("/{id}/available/{status}")
    public ResponseEntity<DishResponse> setDishAvailability(
            @PathVariable Long id,
            @PathVariable boolean status) {
        DishResponse response = dishService.setDishAvailability(id, status);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete a dish by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDish(@PathVariable Long id) {
        dishService.deleteDish(id);
        return ResponseEntity.noContent().build();
    }
}