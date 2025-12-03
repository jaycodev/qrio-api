package com.qrio.branch.controller;

import com.qrio.branch.dto.request.CreateBranchRequest;
import com.qrio.branch.dto.request.UpdateBranchRequest;
import com.qrio.branch.dto.response.BranchDetailResponse;
import com.qrio.branch.dto.response.BranchListResponse;
import com.qrio.branch.service.BranchService;
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
@RequestMapping("/branches")
@RequiredArgsConstructor
@Validated
@Tag(name = "Branches", description = "Operations related to branches")
public class BranchController {
    private final BranchService branchService;

    @GetMapping
    @Operation(summary = "List branches by restaurant")
    public ResponseEntity<ApiSuccess<List<BranchListResponse>>> list(
            @RequestParam(required = false) Long restaurantId) {
        List<BranchListResponse> branches = branchService.getList(restaurantId);
        ApiSuccess<List<BranchListResponse>> response = new ApiSuccess<>(
                branches.isEmpty() ? "No branches found" : "Branches listed successfully",
                branches);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a branch by ID")
    public ResponseEntity<ApiSuccess<BranchDetailResponse>> get(
            @PathVariable @Min(value = 1, message = ValidationMessages.ID_MIN_VALUE) Long id) {
        BranchDetailResponse branch = branchService.getDetailById(id);
        return ResponseEntity.ok(new ApiSuccess<>("Branch found", branch));
    }

    @PostMapping
    @Operation(summary = "Create a new branch")
    public ResponseEntity<ApiSuccess<BranchListResponse>> create(@Valid @RequestBody CreateBranchRequest request) {
        BranchListResponse created = branchService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiSuccess<>("Branch created successfully", created));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a branch by ID")
    public ResponseEntity<ApiSuccess<BranchListResponse>> update(
            @PathVariable @Min(value = 1, message = ValidationMessages.ID_MIN_VALUE) Long id,
            @Valid @RequestBody UpdateBranchRequest request) {
        BranchListResponse result = branchService.update(id, request);
        return ResponseEntity.ok(new ApiSuccess<>("Branch updated successfully", result));
    }
}
