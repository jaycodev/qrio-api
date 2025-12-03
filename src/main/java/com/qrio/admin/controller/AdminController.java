package com.qrio.admin.controller;

import com.qrio.admin.dto.request.CreateAdminRequest;
import com.qrio.admin.dto.request.UpdateAdminRequest;
import com.qrio.admin.dto.response.AdminDetailResponse;
import com.qrio.admin.dto.response.AdminListResponse;
import com.qrio.admin.service.AdminService;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admins")
@RequiredArgsConstructor
@Validated
@Tag(name = "Admins", description = "Operations related to admins")
public class AdminController {
    private final AdminService adminService;

    @GetMapping
    @Operation(summary = "List all admins")
    public ResponseEntity<ApiSuccess<List<AdminListResponse>>> list() {
        List<AdminListResponse> admins = adminService.getList();
        ApiSuccess<List<AdminListResponse>> response = new ApiSuccess<>(
                admins.isEmpty() ? "No admins found" : "Admins listed successfully",
                admins);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get an admin by ID")
    public ResponseEntity<ApiSuccess<AdminDetailResponse>> get(
            @PathVariable @Min(value = 1, message = ValidationMessages.ID_MIN_VALUE) Long id) {
        AdminDetailResponse admin = adminService.getDetailById(id);
        return ResponseEntity.ok(new ApiSuccess<>("Admin found", admin));
    }

    @PostMapping
    @Operation(summary = "Create a new admin")
    public ResponseEntity<ApiSuccess<AdminListResponse>> create(@Valid @RequestBody CreateAdminRequest request) {
        AdminListResponse created = adminService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiSuccess<>("Admin created successfully", created));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an admin by ID")
    public ResponseEntity<ApiSuccess<AdminListResponse>> update(
            @PathVariable @Min(value = 1, message = ValidationMessages.ID_MIN_VALUE) Long id,
            @Valid @RequestBody UpdateAdminRequest request) {
        AdminListResponse result = adminService.update(id, request);
        return ResponseEntity.ok(new ApiSuccess<>("Admin updated successfully", result));
    }
}
