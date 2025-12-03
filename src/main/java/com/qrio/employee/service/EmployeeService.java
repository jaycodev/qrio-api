package com.qrio.employee.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.qrio.admin.model.Admin;
import com.qrio.admin.repository.AdminRepository;
import com.qrio.branch.model.Branch;
import com.qrio.branch.repository.BranchRepository;
import com.qrio.employee.dto.request.CreateEmployeePermissionRequest;
import com.qrio.employee.dto.request.CreateEmployeeRequest;
import com.qrio.employee.dto.request.UpdateEmployeePermissionRequest;
import com.qrio.employee.dto.request.UpdateEmployeeRequest;
import com.qrio.employee.dto.response.EmployeeDetailResponse;
import com.qrio.employee.dto.response.EmployeeListResponse;
import com.qrio.employee.model.Employee;
import com.qrio.employee.model.EmployeePermission;
import com.qrio.employee.model.type.EmployeeRole;
import com.qrio.shared.type.Status;
import com.qrio.employee.repository.EmployeePermissionRepository;
import com.qrio.employee.repository.EmployeeRepository;
import com.qrio.restaurant.model.Restaurant;
import com.qrio.restaurant.repository.RestaurantRepository;
import com.qrio.shared.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Validated
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final EmployeePermissionRepository employeePermissionRepository;
    private final AdminRepository adminRepository;
    private final RestaurantRepository restaurantRepository;
    private final BranchRepository branchRepository;

    public List<EmployeeListResponse> getList(Long restaurantId, Long branchId) {
        return employeeRepository.findList(restaurantId, branchId);
    }

    public EmployeeDetailResponse getDetailById(Long id) {
        EmployeeDetailResponse base = employeeRepository.findDetailById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + id));

        return base.withPermissions(employeePermissionRepository.findPermissionsByEmployeeId(id));
    }

    @Transactional
    public EmployeeListResponse create(CreateEmployeeRequest request) {
        Admin admin = adminRepository.findById(request.adminId())
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));
        Restaurant restaurant = restaurantRepository.findById(request.restaurantId())
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));

        Branch branch = null;
        if (request.branchId() != null) {
            branch = branchRepository.findById(request.branchId())
                    .orElseThrow(() -> new ResourceNotFoundException("Branch not found"));
        }

        Employee employee = new Employee();
        employee.setAdmin(admin);
        employee.setRestaurant(restaurant);
        employee.setBranch(branch);
        employee.setName(request.name());
        employee.setEmail(request.email());
        employee.setPhone(request.phone());
        employee.setRole(EmployeeRole.valueOf(request.role()));
        employee.setStatus(request.status() != null ? Status.valueOf(request.status()) : Status.ACTIVO);

        Employee saved = employeeRepository.save(employee);

        if (request.permissions() != null && !request.permissions().isEmpty()) {
            for (CreateEmployeePermissionRequest permDto : request.permissions()) {
                Restaurant permRestaurant = restaurantRepository.findById(permDto.restaurantId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Restaurant not found with ID: " + permDto.restaurantId()));

                Branch permBranch = null;
                if (permDto.branchId() != null) {
                    permBranch = branchRepository.findById(permDto.branchId())
                            .orElseThrow(() -> new ResourceNotFoundException(
                                    "Branch not found with ID: " + permDto.branchId()));
                }

                EmployeePermission permission = new EmployeePermission();
                permission.setEmployee(saved);
                permission.setRestaurant(permRestaurant);
                permission.setBranch(permBranch);
                permission.setPermission(permDto.permission());

                employeePermissionRepository.save(permission);
            }
        }

        return toListResponse(saved);
    }

    @Transactional
    public EmployeeListResponse update(Long id, UpdateEmployeeRequest request) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + id));

        Admin admin = adminRepository.findById(request.adminId())
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));
        Restaurant restaurant = restaurantRepository.findById(request.restaurantId())
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));

        Branch branch = null;
        if (request.branchId() != null) {
            branch = branchRepository.findById(request.branchId())
                    .orElseThrow(() -> new ResourceNotFoundException("Branch not found"));
        }

        employee.setAdmin(admin);
        employee.setRestaurant(restaurant);
        employee.setBranch(branch);
        employee.setName(request.name());
        employee.setEmail(request.email());
        employee.setPhone(request.phone());
        employee.setRole(EmployeeRole.valueOf(request.role()));
        employee.setStatus(Status.valueOf(request.status()));

        Employee updated = employeeRepository.save(employee);

        employeePermissionRepository.deleteAllByEmployee(updated);

        if (request.permissions() != null && !request.permissions().isEmpty()) {
            for (UpdateEmployeePermissionRequest permDto : request.permissions()) {
                Restaurant permRestaurant = restaurantRepository.findById(permDto.restaurantId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Restaurant not found with ID: " + permDto.restaurantId()));

                Branch permBranch = null;
                if (permDto.branchId() != null) {
                    permBranch = branchRepository.findById(permDto.branchId())
                            .orElseThrow(() -> new ResourceNotFoundException(
                                    "Branch not found with ID: " + permDto.branchId()));
                }

                EmployeePermission permission = new EmployeePermission();
                permission.setEmployee(updated);
                permission.setRestaurant(permRestaurant);
                permission.setBranch(permBranch);
                permission.setPermission(permDto.permission());

                employeePermissionRepository.save(permission);
            }
        }

        return toListResponse(updated);
    }

    private EmployeeListResponse toListResponse(Employee employee) {
        return new EmployeeListResponse(
                employee.getId(),
                employee.getAdmin().getId(),
                employee.getRestaurant().getId(),
                employee.getBranch() != null ? employee.getBranch().getId() : null,
                employee.getName(),
                employee.getEmail(),
                employee.getPhone(),
                employee.getRole(),
                employee.getStatus(),
                employee.getCreatedAt());
    }
}
