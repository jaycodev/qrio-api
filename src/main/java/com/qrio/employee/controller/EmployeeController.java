package com.qrio.employee.controller;

import com.qrio.employee.dto.request.CreateEmployeeRequest;
import com.qrio.employee.dto.request.UpdateEmployeeRequest;
import com.qrio.employee.dto.response.EmployeeDetailResponse;
import com.qrio.employee.dto.response.EmployeeListResponse;
import com.qrio.employee.service.EmployeeService;
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
@RequestMapping("/employees")
@RequiredArgsConstructor
@Validated
@Tag(name = "Employees", description = "Operations related to employees")
public class EmployeeController {
    private final EmployeeService employeeService;

    @GetMapping
    @Operation(summary = "List employees by restaurant and branch")
    public ResponseEntity<ApiSuccess<List<EmployeeListResponse>>> list(
            @RequestParam Long restaurantId,
            @RequestParam(required = false) Long branchId) {
        List<EmployeeListResponse> employees = employeeService.getList(restaurantId, branchId);
        ApiSuccess<List<EmployeeListResponse>> response = new ApiSuccess<>(
                employees.isEmpty() ? "No employees found" : "Employees listed successfully",
                employees);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get an employee by ID")
    public ResponseEntity<ApiSuccess<EmployeeDetailResponse>> get(
            @PathVariable @Min(value = 1, message = ValidationMessages.ID_MIN_VALUE) Long id) {
        EmployeeDetailResponse employee = employeeService.getDetailById(id);
        return ResponseEntity.ok(new ApiSuccess<>("Employee found", employee));
    }

    @PostMapping
    @Operation(summary = "Create a new employee")
    public ResponseEntity<ApiSuccess<EmployeeListResponse>> create(@Valid @RequestBody CreateEmployeeRequest request) {
        EmployeeListResponse created = employeeService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiSuccess<>("Employee created successfully", created));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an employee by ID")
    public ResponseEntity<ApiSuccess<EmployeeListResponse>> update(
            @PathVariable @Min(value = 1, message = ValidationMessages.ID_MIN_VALUE) Long id,
            @Valid @RequestBody UpdateEmployeeRequest request) {
        EmployeeListResponse result = employeeService.update(id, request);
        return ResponseEntity.ok(new ApiSuccess<>("Employee updated successfully", result));
    }
}
