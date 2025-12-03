package com.qrio.appAdmin.controller;

import com.qrio.appAdmin.dto.request.CreateAppAdminRequest;
import com.qrio.appAdmin.dto.request.UpdateAppAdminRequest;
import com.qrio.appAdmin.dto.response.AppAdminDetailResponse;
import com.qrio.appAdmin.dto.response.AppAdminListResponse;
import com.qrio.appAdmin.service.AppAdminService;
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
@RequestMapping("/app-admins")
@RequiredArgsConstructor
@Validated
@Tag(name = "AppAdmins", description = "Operations related to app admins")
public class AppAdminController {
    private final AppAdminService appAdminService;

    @GetMapping
    @Operation(summary = "List all app admins")
    public ResponseEntity<ApiSuccess<List<AppAdminListResponse>>> list() {
        List<AppAdminListResponse> appAdmins = appAdminService.getList();
        ApiSuccess<List<AppAdminListResponse>> response = new ApiSuccess<>(
                appAdmins.isEmpty() ? "No app admins found" : "App admins listed successfully",
                appAdmins);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get an app admin by ID")
    public ResponseEntity<ApiSuccess<AppAdminDetailResponse>> get(
            @PathVariable @Min(value = 1, message = ValidationMessages.ID_MIN_VALUE) Long id) {
        AppAdminDetailResponse appAdmin = appAdminService.getDetailById(id);
        return ResponseEntity.ok(new ApiSuccess<>("App admin found", appAdmin));
    }

    @PostMapping
    @Operation(summary = "Create a new app admin")
    public ResponseEntity<ApiSuccess<AppAdminListResponse>> create(@Valid @RequestBody CreateAppAdminRequest request) {
        AppAdminListResponse created = appAdminService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiSuccess<>("App admin created successfully", created));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an app admin by ID")
    public ResponseEntity<ApiSuccess<AppAdminListResponse>> update(
            @PathVariable @Min(value = 1, message = ValidationMessages.ID_MIN_VALUE) Long id,
            @Valid @RequestBody UpdateAppAdminRequest request) {
        AppAdminListResponse result = appAdminService.update(id, request);
        return ResponseEntity.ok(new ApiSuccess<>("App admin updated successfully", result));
    }
}
