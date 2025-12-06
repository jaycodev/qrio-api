package com.qrio.user.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.qrio.branch.model.Branch;
import com.qrio.branch.repository.BranchRepository;
import com.qrio.restaurant.model.Restaurant;
import com.qrio.restaurant.repository.RestaurantRepository;
import com.qrio.shared.exception.ResourceNotFoundException;
import com.qrio.shared.type.Status;
import com.qrio.user.dto.request.CreateEmployeeRequest;
import com.qrio.user.dto.request.UpdateEmployeeRequest;
import com.qrio.user.dto.response.EmployeeDetailResponse;
import com.qrio.user.dto.response.EmployeeListResponse;
import com.qrio.user.model.EmployeePermission;
import com.qrio.user.model.User;
import com.qrio.user.model.type.UserRole;
import com.qrio.user.repository.EmployeePermissionRepository;
import com.qrio.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Validated
public class EmployeeService {
    private final UserRepository userRepository;
    private final EmployeePermissionRepository employeePermissionRepository;
    private final RestaurantRepository restaurantRepository;
    private final BranchRepository branchRepository;

    public List<EmployeeListResponse> getList(Long branchId) {
        return userRepository.findListEmployee(branchId);
    }

    public EmployeeDetailResponse getDetailById(Long id) {
        EmployeeDetailResponse base = userRepository.findDetailEmployee(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + id));

        return base.withPermissions(employeePermissionRepository.findPermissionsByUserId(id));
    }

    @Transactional
    public EmployeeListResponse create(CreateEmployeeRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("Email already in use");
        }

        Restaurant restaurant = restaurantRepository.findById(request.restaurantId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Restaurant not found with ID: " + request.restaurantId()));

        Branch branch = null;
        if (request.branchId() != null) {
            branch = branchRepository.findById(request.branchId())
                    .orElseThrow(
                            () -> new ResourceNotFoundException("Branch not found with ID: " + request.branchId()));
        }

        User user = new User();
        user.setEmail(request.email());
        user.setPassword(request.password());
        user.setName(request.name());
        user.setPhone(request.phone());
        user.setRole(UserRole.valueOf(request.role()));
        user.setRestaurant(restaurant);
        user.setBranch(branch);
        user.setStatus(request.status() != null ? Status.valueOf(request.status()) : Status.ACTIVO);

        User saved = userRepository.save(user);

        if (request.permissions() != null && !request.permissions().isEmpty()) {
            for (var permDto : request.permissions()) {
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
                permission.setUser(saved);
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
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + id));

        Branch branch = null;
        if (request.branchId() != null) {
            branch = branchRepository.findById(request.branchId())
                    .orElseThrow(
                            () -> new ResourceNotFoundException("Branch not found with ID: " + request.branchId()));
        }

        user.setName(request.name());
        user.setPhone(request.phone());
        user.setBranch(branch);
        user.setRole(UserRole.valueOf(request.role()));
        user.setStatus(request.status() != null ? Status.valueOf(request.status()) : Status.ACTIVO);

        User updated = userRepository.save(user);

        employeePermissionRepository.deleteAllByUser(updated);

        if (request.permissions() != null && !request.permissions().isEmpty()) {
            for (var permDto : request.permissions()) {
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
                permission.setUser(updated);
                permission.setRestaurant(permRestaurant);
                permission.setBranch(permBranch);
                permission.setPermission(permDto.permission());

                employeePermissionRepository.save(permission);
            }
        }

        return toListResponse(updated);
    }

    private EmployeeListResponse toListResponse(User user) {
        return new EmployeeListResponse(
                user.getId(),
                user.getRestaurant().getId(),
                user.getBranch() != null ? user.getBranch().getId() : null,
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getRole(),
                user.getStatus(),
                user.getCreatedAt());
    }
}
