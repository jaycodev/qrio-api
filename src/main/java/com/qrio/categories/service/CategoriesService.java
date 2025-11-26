package com.qrio.categories.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.qrio.categories.dto.request.CreateCategorieRequest;
import com.qrio.categories.dto.request.UpdateCategorieRequest;
import com.qrio.categories.dto.response.CategorieResponse;
import com.qrio.categories.model.Categories;
import com.qrio.categories.repository.CategoriesRepository;
import com.qrio.restaurant.model.Restaurant; 
import com.qrio.restaurant.repository.RestaurantRepository; 

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoriesService {

    private final CategoriesRepository categoriesRepository;
    private final RestaurantRepository restaurantRepository;

    @Transactional
    public CategorieResponse createCategory(CreateCategorieRequest request) {
    	
        Restaurant restaurant = restaurantRepository.findById(request.restaurantId())
                .orElseThrow(() -> new IllegalArgumentException("Restaurant not found with id: " + request.restaurantId()));

        if (categoriesRepository.existsByRestaurantIdAndNameIgnoreCase(request.restaurantId(), request.name())) {
            throw new IllegalArgumentException("Category name already exists for this restaurant");
        }

        Categories category = new Categories();
        category.setName(request.name());
        category.setRestaurant(restaurant);

        Categories saved = categoriesRepository.save(category);
        return mapToResponse(saved);
    }

    @Transactional
    public CategorieResponse updateCategory(Long id, UpdateCategorieRequest request) {
        Categories category = categoriesRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + id));

        boolean nameExistsForOther = categoriesRepository.findByRestaurantIdAndNameIgnoreCase(category.getRestaurant().getId(), request.name())
                .filter(c -> !c.getId().equals(id))
                .isPresent();
        
        if (nameExistsForOther) {
            throw new IllegalArgumentException("Category name already exists for this restaurant");
        }

        category.setName(request.name());

        Categories updated = categoriesRepository.save(category);
        return mapToResponse(updated);
    }

    @Transactional(readOnly = true)
    public CategorieResponse getCategoryById(Long id) {
        Categories category = categoriesRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + id));
        return mapToResponse(category);
    }

    @Transactional(readOnly = true)
    public List<CategorieResponse> getCategoriesByRestaurantId(Long restaurantId) {
        return categoriesRepository.findByRestaurantId(restaurantId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public void deleteCategory(Long id) {
        if (!categoriesRepository.existsById(id)) {
            throw new IllegalArgumentException("Category not found with id: " + id);
        }
        categoriesRepository.deleteById(id);
    }

    private CategorieResponse mapToResponse(Categories category) {
        return new CategorieResponse(
                category.getId(),
                category.getRestaurant().getId(),
                category.getName());
    }
}