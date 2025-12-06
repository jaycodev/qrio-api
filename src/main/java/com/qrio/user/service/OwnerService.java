package com.qrio.user.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.qrio.restaurant.model.Restaurant;
import com.qrio.restaurant.repository.RestaurantRepository;
import com.qrio.shared.exception.ResourceNotFoundException;
import com.qrio.shared.type.Status;
import com.qrio.user.dto.request.CreateOwnerRequest;
import com.qrio.user.dto.request.UpdateOwnerRequest;
import com.qrio.user.dto.response.OwnerListResponse;
import com.qrio.user.model.User;
import com.qrio.user.model.type.UserRole;
import com.qrio.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Validated
public class OwnerService {
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;

    @Transactional
    public OwnerListResponse create(CreateOwnerRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("Email already in use");
        }

        Restaurant restaurant = restaurantRepository.findById(request.restaurantId())
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with ID: " + request.restaurantId()));

        User owner = new User();
        owner.setEmail(request.email());
        owner.setPassword(request.password());
        owner.setName(request.name());
        owner.setPhone(request.phone());
        owner.setRole(UserRole.DUEÑO);
        owner.setRestaurant(restaurant);
        owner.setBranch(null);
        owner.setStatus(request.status() != null ? Status.valueOf(request.status()) : Status.ACTIVO);

        User saved = userRepository.save(owner);

        return toListResponse(saved);
    }

    @Transactional
    public OwnerListResponse update(Long id, UpdateOwnerRequest request) {
        User owner = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found with ID: " + id));

        owner.setName(request.name());
        owner.setPhone(request.phone());
        owner.setStatus(request.status() != null ? Status.valueOf(request.status()) : Status.ACTIVO);

        User updated = userRepository.save(owner);

        return toListResponse(updated);
    }

    private OwnerListResponse toListResponse(User user) {
        return new OwnerListResponse(
                user.getId(),
                user.getRestaurant().getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getRole(),
                user.getStatus(),
                user.getCreatedAt());
    }
}
