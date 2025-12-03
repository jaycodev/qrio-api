package com.qrio.restaurant.service;

import com.qrio.admin.model.Admin;
import com.qrio.admin.repository.AdminRepository;
import com.qrio.restaurant.dto.request.CreateRestaurantRequest;
import com.qrio.restaurant.dto.request.UpdateRestaurantRequest;
import com.qrio.restaurant.dto.response.RestaurantDetailResponse;
import com.qrio.restaurant.dto.response.RestaurantListResponse;
import com.qrio.restaurant.model.Restaurant;
import com.qrio.restaurant.repository.RestaurantRepository;
import com.qrio.shared.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Validated
public class RestaurantService {
    private final RestaurantRepository restaurantRepository;
    private final AdminRepository adminRepository;

    public List<RestaurantListResponse> getList() {
        return restaurantRepository.findList();
    }

    public RestaurantDetailResponse getDetailById(Long id) {
        return restaurantRepository.findDetailById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with ID: " + id));
    }

    @Transactional
    public RestaurantListResponse create(CreateRestaurantRequest request) {
        Admin admin = adminRepository.findById(request.adminId())
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));

        Restaurant restaurant = new Restaurant();
        restaurant.setAdmin(admin);
        restaurant.setName(request.name());
        restaurant.setDescription(request.description());
        restaurant.setLogoUrl(request.logoUrl());
        restaurant.setIsActive(request.isActive() != null ? request.isActive() : false);
        restaurant.setCreatedAt(LocalDateTime.now());

        Restaurant saved = restaurantRepository.save(restaurant);
        return toListResponse(saved);
    }

    @Transactional
    public RestaurantListResponse update(Long id, UpdateRestaurantRequest request) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with ID: " + id));

        Admin admin = adminRepository.findById(request.adminId())
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));

        restaurant.setAdmin(admin);
        restaurant.setName(request.name());
        restaurant.setDescription(request.description());
        restaurant.setLogoUrl(request.logoUrl());
        restaurant.setIsActive(request.isActive() != null ? request.isActive() : restaurant.getIsActive());

        Restaurant updated = restaurantRepository.save(restaurant);
        return toListResponse(updated);
    }

    private RestaurantListResponse toListResponse(Restaurant restaurant) {
        return new RestaurantListResponse(
                restaurant.getId(),
                restaurant.getCode(),
                restaurant.getAdmin().getId(),
                restaurant.getName(),
                restaurant.getLogoUrl(),
                restaurant.getIsActive());
    }
}
