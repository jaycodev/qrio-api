package com.qrio.branch.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.qrio.branch.dto.request.CreateBranchRequest;
import com.qrio.branch.dto.request.UpdateBranchRequest;
import com.qrio.branch.dto.response.BranchDetailResponse;
import com.qrio.branch.dto.response.BranchListResponse;
import com.qrio.branch.model.Branch;
import com.qrio.branch.repository.BranchRepository;
import com.qrio.restaurant.model.Restaurant;
import com.qrio.restaurant.repository.RestaurantRepository;
import com.qrio.shared.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Validated
public class BranchService {
    private final BranchRepository branchRepository;
    private final RestaurantRepository restaurantRepository;

    public List<BranchListResponse> getList(Long restaurantId) {
        return branchRepository.findList(restaurantId);
    }

    public BranchDetailResponse getDetailById(Long id) {
        return branchRepository.findDetailById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found with ID: " + id));
    }

    @Transactional
    public BranchListResponse create(CreateBranchRequest request) {
        Restaurant restaurant = restaurantRepository.findById(request.restaurantId())
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));

        Branch branch = new Branch();
        branch.setRestaurant(restaurant);
        branch.setName(request.name());
        branch.setAddress(request.address());
        branch.setPhone(request.phone());
        branch.setSchedule(request.schedule());
        branch.setCreatedAt(LocalDateTime.now());

        Branch saved = branchRepository.save(branch);
        return toListResponse(saved);
    }

    @Transactional
    public BranchListResponse update(Long id, UpdateBranchRequest request) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found with ID: " + id));

        Restaurant restaurant = restaurantRepository.findById(request.restaurantId())
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));

        branch.setRestaurant(restaurant);
        branch.setName(request.name());
        branch.setAddress(request.address());
        branch.setPhone(request.phone());
        branch.setSchedule(request.schedule());

        Branch updated = branchRepository.save(branch);
        return toListResponse(updated);
    }

    private BranchListResponse toListResponse(Branch branch) {
        return new BranchListResponse(
                branch.getId(),
                branch.getCode(),
                branch.getRestaurant().getId(),
                branch.getName(),
                branch.getAddress(),
                branch.getPhone(),
                branch.getSchedule(),
                branch.getCreatedAt());
    }
}
