package com.qrio.restaurantActivation.controller;

import com.qrio.restaurantActivation.dto.request.CreateRestaurantActivationRequest;
import com.qrio.restaurantActivation.dto.request.UpdateRestaurantActivationRequest;
import com.qrio.restaurantActivation.dto.response.RestaurantActivationDetailResponse;
import com.qrio.restaurantActivation.dto.response.RestaurantActivationListResponse;
import com.qrio.restaurantActivation.service.RestaurantActivationService;
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
@RequestMapping("/restaurant-activations")
@RequiredArgsConstructor
@Validated
@Tag(name = "Restaurant Activations", description = "Operations related to restaurant activation requests")
public class RestaurantActivationController {
    private final RestaurantActivationService restaurantActivationService;

    @GetMapping
    @Operation(summary = "List restaurant activation requests")
    public ResponseEntity<ApiSuccess<List<RestaurantActivationListResponse>>> list(
            @RequestParam(required = false) Long restaurantId) {
        List<RestaurantActivationListResponse> activations = restaurantActivationService.getList(restaurantId);
        ApiSuccess<List<RestaurantActivationListResponse>> response = new ApiSuccess<>(
                activations.isEmpty() ? "No activation requests found" : "Activation requests listed successfully",
                activations);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get an activation request by ID")
    public ResponseEntity<ApiSuccess<RestaurantActivationDetailResponse>> get(
            @PathVariable @Min(value = 1, message = ValidationMessages.ID_MIN_VALUE) Long id) {
        RestaurantActivationDetailResponse activation = restaurantActivationService.getDetailById(id);
        return ResponseEntity.ok(new ApiSuccess<>("Activation request found", activation));
    }

    @PostMapping
    @Operation(summary = "Create a new activation request")
    public ResponseEntity<ApiSuccess<RestaurantActivationListResponse>> create(@Valid @RequestBody CreateRestaurantActivationRequest request) {
        RestaurantActivationListResponse created = restaurantActivationService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiSuccess<>("Activation request created successfully", created));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an activation request by ID")
    public ResponseEntity<ApiSuccess<RestaurantActivationListResponse>> update(
            @PathVariable @Min(value = 1, message = ValidationMessages.ID_MIN_VALUE) Long id,
            @Valid @RequestBody UpdateRestaurantActivationRequest request) {
        RestaurantActivationListResponse result = restaurantActivationService.update(id, request);
        return ResponseEntity.ok(new ApiSuccess<>("Activation request updated successfully", result));
    }
}
