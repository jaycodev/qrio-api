package com.qrio.user.controller;

import com.qrio.shared.api.ApiSuccess;
import com.qrio.user.dto.request.CreateEmployeeRequest;
import com.qrio.user.dto.request.CreateOwnerRequest;
import com.qrio.user.dto.request.UpdateEmployeeRequest;
import com.qrio.user.dto.request.UpdateOwnerRequest;
import com.qrio.user.dto.response.EmployeeDetailResponse;
import com.qrio.user.dto.response.EmployeeListResponse;
import com.qrio.user.dto.response.OwnerListResponse;
import com.qrio.user.service.EmployeeService;
import com.qrio.user.service.OwnerService;
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
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
@Tag(name = "Users", description = "Operations related to users (employees and owners)")
public class UserController {
    private final EmployeeService employeeService;
    private final OwnerService ownerService;

    @GetMapping("/employees")
    @Operation(summary = "List employees by branch")
    public ResponseEntity<ApiSuccess<List<EmployeeListResponse>>> listEmployees(
            @RequestParam @Min(value = 1, message = ValidationMessages.ID_MIN_VALUE) Long branchId) {
        List<EmployeeListResponse> responses = employeeService.getList(branchId);
        ApiSuccess<List<EmployeeListResponse>> response = new ApiSuccess<>(
                responses.isEmpty() ? "No employees found" : "Employees listed successfully",
                responses);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/employees/{id}")
    @Operation(summary = "Get employee details by ID")
    public ResponseEntity<ApiSuccess<EmployeeDetailResponse>> getEmployeeDetail(
            @PathVariable @Min(value = 1, message = ValidationMessages.ID_MIN_VALUE) Long id) {
        EmployeeDetailResponse response = employeeService.getDetailById(id);
        return ResponseEntity.ok(new ApiSuccess<>("Employee found", response));
    }

    @PostMapping("/employees")
    @Operation(summary = "Create a new employee")
    public ResponseEntity<ApiSuccess<EmployeeListResponse>> createEmployee(
            @Valid @RequestBody CreateEmployeeRequest request) {
        EmployeeListResponse response = employeeService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiSuccess<>("Employee created successfully", response));
    }

    @PutMapping("/employees/{id}")
    @Operation(summary = "Update an employee")
    public ResponseEntity<ApiSuccess<EmployeeListResponse>> updateEmployee(
            @PathVariable @Min(value = 1, message = ValidationMessages.ID_MIN_VALUE) Long id,
            @Valid @RequestBody UpdateEmployeeRequest request) {
        EmployeeListResponse response = employeeService.update(id, request);
        return ResponseEntity.ok(new ApiSuccess<>("Employee updated successfully", response));
    }

    @PostMapping("/owners")
    @Operation(summary = "Create a new owner")
    public ResponseEntity<ApiSuccess<OwnerListResponse>> createOwner(
            @Valid @RequestBody CreateOwnerRequest request) {
        OwnerListResponse response = ownerService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiSuccess<>("Owner created successfully", response));
    }

    @PutMapping("/owners/{id}")
    @Operation(summary = "Update an owner")
    public ResponseEntity<ApiSuccess<OwnerListResponse>> updateOwner(
            @PathVariable @Min(value = 1, message = ValidationMessages.ID_MIN_VALUE) Long id,
            @Valid @RequestBody UpdateOwnerRequest request) {
        OwnerListResponse response = ownerService.update(id, request);
        return ResponseEntity.ok(new ApiSuccess<>("Owner updated successfully", response));
    }
}
