package com.qrio.restaurant.controller;

import com.qrio.restaurant.dto.request.CreateRestaurantRequest;
import com.qrio.restaurant.dto.request.UpdateRestaurantRequest;
import com.qrio.restaurant.dto.response.RestaurantDetailResponse;
import com.qrio.restaurant.dto.response.RestaurantListResponse;
import com.qrio.restaurant.service.RestaurantService;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/restaurants")
@RequiredArgsConstructor
@Validated
@Tag(name = "Restaurants", description = "Operations related to restaurants")
public class RestaurantController {
    private final RestaurantService restaurantService;

    @GetMapping
    @Operation(summary = "List all restaurants")
    public ResponseEntity<ApiSuccess<List<RestaurantListResponse>>> list() {
        List<RestaurantListResponse> restaurants = restaurantService.getList();
        ApiSuccess<List<RestaurantListResponse>> response = new ApiSuccess<>(
                restaurants.isEmpty() ? "No restaurants found" : "Restaurants listed successfully",
                restaurants);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/owner/{ownerId}")
    @Operation(summary = "List restaurants by owner ID")
    public ResponseEntity<ApiSuccess<List<RestaurantListResponse>>> listByOwner(
            @PathVariable @Min(value = 1, message = ValidationMessages.ID_MIN_VALUE) Long ownerId) {
        List<RestaurantListResponse> restaurants = restaurantService.getListByOwnerId(ownerId);
        ApiSuccess<List<RestaurantListResponse>> response = new ApiSuccess<>(
                restaurants.isEmpty() ? "No restaurants found for owner" : "Restaurants listed successfully",
                restaurants);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a restaurant by ID")
    public ResponseEntity<ApiSuccess<RestaurantDetailResponse>> get(
            @PathVariable @Min(value = 1, message = ValidationMessages.ID_MIN_VALUE) Long id) {
        RestaurantDetailResponse restaurant = restaurantService.getDetailById(id);
        return ResponseEntity.ok(new ApiSuccess<>("Restaurant found", restaurant));
    }

    @PostMapping
    @Operation(summary = "Create a new restaurant")
    public ResponseEntity<ApiSuccess<RestaurantListResponse>> create(
            @Valid @RequestBody CreateRestaurantRequest request) {
        RestaurantListResponse created = restaurantService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiSuccess<>("Restaurant created successfully", created));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a restaurant by ID")
    public ResponseEntity<ApiSuccess<RestaurantListResponse>> update(
            @PathVariable @Min(value = 1, message = ValidationMessages.ID_MIN_VALUE) Long id,
            @Valid @RequestBody UpdateRestaurantRequest request) {
        RestaurantListResponse result = restaurantService.update(id, request);
        return ResponseEntity.ok(new ApiSuccess<>("Restaurant updated successfully", result));
    }
}
