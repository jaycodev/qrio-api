package com.qrio.offer.controller;

import com.qrio.offer.dto.request.CreateOfferRequest;
import com.qrio.offer.dto.request.UpdateOfferRequest;
import com.qrio.offer.dto.response.OfferDetailResponse;
import com.qrio.offer.dto.response.OfferListResponse;
import com.qrio.offer.service.OfferService;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/offers")
@RequiredArgsConstructor
@Validated
@Tag(name = "Offers", description = "Operations related to offers")
public class OfferController {
    private final OfferService offerService;

    @GetMapping
    @Operation(summary = "List all offers")
    public ResponseEntity<ApiSuccess<List<OfferListResponse>>> list() {
        List<OfferListResponse> offers = offerService.getList();
        ApiSuccess<List<OfferListResponse>> response = new ApiSuccess<>(
                offers.isEmpty() ? "No offers found" : "Offers listed successfully",
                offers);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get an offer by ID")
    public ResponseEntity<ApiSuccess<OfferDetailResponse>> get(
            @PathVariable @Min(value = 1, message = ValidationMessages.ID_MIN_VALUE) Long id) {
        OfferDetailResponse offer = offerService.getDetailById(id);
        return ResponseEntity.ok(new ApiSuccess<>("Offer found", offer));
    }

    @PatchMapping("/{id}/active")
    @Operation(summary = "Set or toggle offer active state")
    public ResponseEntity<ApiSuccess<Boolean>> setActive(
            @PathVariable @Min(value = 1, message = ValidationMessages.ID_MIN_VALUE) Long id,
            @RequestBody(required = false) java.util.Map<String, Boolean> body) {
        Boolean result;
        if (body == null || !body.containsKey("active")) {
            result = offerService.toggleActive(id);
        } else {
            Boolean active = body.getOrDefault("active", false);
            result = offerService.setActive(id, active);
        }
        return ResponseEntity.ok(new ApiSuccess<>("Offer active state updated", result));
    }

    @PostMapping
    @Operation(summary = "Create a new offer")
    public ResponseEntity<ApiSuccess<OfferListResponse>> create(@Valid @RequestBody CreateOfferRequest request) {
        OfferListResponse created = offerService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiSuccess<>("Offer created successfully", created));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an offer by ID")
    public ResponseEntity<ApiSuccess<OfferListResponse>> update(
            @PathVariable @Min(value = 1, message = ValidationMessages.ID_MIN_VALUE) Long id,
            @Valid @RequestBody UpdateOfferRequest request) {
        OfferListResponse result = offerService.update(id, request);
        return ResponseEntity.ok(new ApiSuccess<>("Offer updated successfully", result));
    }

    @io.swagger.v3.oas.annotations.Operation(summary = "Delete an offer by ID")
    @org.springframework.web.bind.annotation.DeleteMapping("/{id}")
    public ResponseEntity<ApiSuccess<Boolean>> delete(
            @PathVariable @Min(value = 1, message = ValidationMessages.ID_MIN_VALUE) Long id) {
        offerService.delete(id);
        return ResponseEntity.ok(new ApiSuccess<>("Offer deleted successfully", true));
    }
}
