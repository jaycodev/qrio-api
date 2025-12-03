package com.qrio.category.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.qrio.category.dto.request.CreateCategoryRequest;
import com.qrio.category.dto.request.UpdateCategoryRequest;
import com.qrio.category.dto.response.CategoryDetailResponse;
import com.qrio.category.dto.response.CategoryListResponse;
import com.qrio.category.model.Category;
import com.qrio.category.repository.CategoryRepository;
import com.qrio.restaurant.model.Restaurant;
import com.qrio.restaurant.repository.RestaurantRepository;
import com.qrio.shared.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Validated
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final RestaurantRepository restaurantRepository;

    @Transactional(readOnly = true)
    public List<CategoryListResponse> getList(Long restaurantId) {
        return categoryRepository.findList(restaurantId);
    }

    @Transactional(readOnly = true)
    public CategoryDetailResponse getDetailById(Long id) {
        return categoryRepository.findDetailById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
    }

    @Transactional
    public CategoryListResponse create(CreateCategoryRequest request) {
        Restaurant restaurant = restaurantRepository.findById(request.restaurantId())
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));

        if (categoryRepository.existsByRestaurantIdAndNameIgnoreCase(request.restaurantId(), request.name())) {
            throw new IllegalArgumentException("Category name already exists for this restaurant");
        }

        Category category = new Category();
        category.setName(request.name());
        category.setRestaurant(restaurant);

        Category saved = categoryRepository.save(category);
        return toListResponse(saved);
    }

    @Transactional
    public CategoryListResponse update(Long id, UpdateCategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        boolean nameExistsForOther = categoryRepository
                .findByRestaurantIdAndNameIgnoreCase(category.getRestaurant().getId(), request.name())
                .filter(c -> !c.getId().equals(id))
                .isPresent();

        if (nameExistsForOther) {
            throw new IllegalArgumentException("Category name already exists for this restaurant");
        }

        category.setName(request.name());

        Category updated = categoryRepository.save(category);
        return toListResponse(updated);
    }

    private CategoryListResponse toListResponse(Category category) {
        return new CategoryListResponse(
                category.getId(),
                category.getRestaurant().getId(),
                category.getName());
    }
}