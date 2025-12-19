package com.qrio.table.controller;

import com.qrio.shared.api.ApiSuccess;
import com.qrio.shared.validation.ValidationMessages;
import com.qrio.table.dto.request.CreateDiningTableRequest;
import com.qrio.table.dto.request.UpdateDiningTableRequest;
import com.qrio.table.dto.request.UpdateDiningTableFloorRequest;
import com.qrio.table.dto.response.DiningTableDetailResponse;
import com.qrio.table.dto.response.DiningTableListResponse;
import com.qrio.table.service.DiningTableService;
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
@RequestMapping("/tables")
@RequiredArgsConstructor
@Validated
@Tag(name = "Dining Tables", description = "Operations related to dining tables")
public class DiningTableController {
    private final DiningTableService diningTableService;

    @GetMapping
    @Operation(summary = "List dining tables by branch")
    public ResponseEntity<ApiSuccess<List<DiningTableListResponse>>> list(@RequestParam Long branchId) {
        List<DiningTableListResponse> tables = diningTableService.getList(branchId);
        ApiSuccess<List<DiningTableListResponse>> response = new ApiSuccess<>(
                tables.isEmpty() ? "No tables found" : "Tables listed successfully",
                tables);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a dining table by ID")
    public ResponseEntity<ApiSuccess<DiningTableDetailResponse>> get(
            @PathVariable @Min(value = 1, message = ValidationMessages.ID_MIN_VALUE) Long id) {
        DiningTableDetailResponse table = diningTableService.getDetailById(id);
        return ResponseEntity.ok(new ApiSuccess<>("Table found", table));
    }

    @PostMapping
    @Operation(summary = "Create a new dining table")
    public ResponseEntity<ApiSuccess<DiningTableListResponse>> create(
            @Valid @RequestBody CreateDiningTableRequest request) {
        DiningTableListResponse created = diningTableService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiSuccess<>("Table created successfully", created));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a dining table by ID")
    public ResponseEntity<ApiSuccess<DiningTableListResponse>> update(
            @PathVariable @Min(value = 1, message = ValidationMessages.ID_MIN_VALUE) Long id,
            @Valid @RequestBody UpdateDiningTableRequest request) {
        DiningTableListResponse result = diningTableService.update(id, request);
        return ResponseEntity.ok(new ApiSuccess<>("Table updated successfully", result));
    }

    @PatchMapping("/{id}/floor")
    @Operation(summary = "Update dining table floor by ID")
    public ResponseEntity<ApiSuccess<DiningTableListResponse>> updateFloor(
            @PathVariable @Min(value = 1, message = ValidationMessages.ID_MIN_VALUE) Long id,
            @Valid @RequestBody UpdateDiningTableFloorRequest request) {
        DiningTableListResponse result = diningTableService.updateFloor(id, request.floor());
        return ResponseEntity.ok(new ApiSuccess<>("Table floor updated successfully", result));
    }
}
