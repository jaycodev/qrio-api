package com.qrio.restaurantActivation.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.qrio.admin.model.Admin;
import com.qrio.admin.repository.AdminRepository;
import com.qrio.restaurant.model.Restaurant;
import com.qrio.restaurant.repository.RestaurantRepository;
import com.qrio.restaurantActivation.dto.request.CreateRestaurantActivationRequest;
import com.qrio.restaurantActivation.dto.request.UpdateRestaurantActivationRequest;
import com.qrio.restaurantActivation.dto.response.RestaurantActivationDetailResponse;
import com.qrio.restaurantActivation.dto.response.RestaurantActivationListResponse;
import com.qrio.restaurantActivation.model.RestaurantActivationRequest;
import com.qrio.restaurantActivation.model.type.ActivationStatus;
import com.qrio.restaurantActivation.repository.RestaurantActivationRepository;
import com.qrio.shared.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Validated
public class RestaurantActivationService {
    private final RestaurantActivationRepository restaurantActivationRepository;
    private final RestaurantRepository restaurantRepository;
    private final AdminRepository adminRepository;

    public List<RestaurantActivationListResponse> getList(Long restaurantId) {
        return restaurantActivationRepository.findList(restaurantId);
    }

    public RestaurantActivationDetailResponse getDetailById(Long id) {
        return restaurantActivationRepository.findDetailById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Restaurant activation request not found with ID: " + id));
    }

    @Transactional
    public RestaurantActivationListResponse create(CreateRestaurantActivationRequest request) {
        Restaurant restaurant = restaurantRepository.findById(request.restaurantId())
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));
        Admin admin = adminRepository.findById(request.adminId())
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));

        RestaurantActivationRequest activation = new RestaurantActivationRequest();
        activation.setRestaurant(restaurant);
        activation.setAdmin(admin);
        activation.setStatus(
                request.status() != null ? ActivationStatus.valueOf(request.status()) : ActivationStatus.PENDIENTE);
        activation.setComment(request.comment());

        RestaurantActivationRequest saved = restaurantActivationRepository.save(activation);
        return toListResponse(saved);
    }

    @Transactional
    public RestaurantActivationListResponse update(Long id, UpdateRestaurantActivationRequest request) {
        RestaurantActivationRequest activation = restaurantActivationRepository.findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Restaurant activation request not found with ID: " + id));

        activation.setStatus(ActivationStatus.valueOf(request.status()));
        activation.setComment(request.comment());
        activation.setReviewedAt(LocalDateTime.now());

        RestaurantActivationRequest updated = restaurantActivationRepository.save(activation);
        return toListResponse(updated);
    }

    private RestaurantActivationListResponse toListResponse(RestaurantActivationRequest activation) {
        return new RestaurantActivationListResponse(
                activation.getId(),
                activation.getRestaurant().getId(),
                activation.getAdmin().getId(),
                activation.getStatus(),
                activation.getComment(),
                activation.getCreatedAt(),
                activation.getReviewedAt());
    }
}
