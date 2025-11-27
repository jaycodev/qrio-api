package com.qrio.dishes.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.qrio.categories.model.Categories;
import com.qrio.categories.repository.CategoriesRepository;
import com.qrio.dishes.dto.request.CreateDishRequest;
import com.qrio.dishes.dto.request.UpdateDishRequest;
import com.qrio.dishes.dto.response.DishResponse;
import com.qrio.dishes.model.Dish;
import com.qrio.dishes.repository.DishRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DishService {

    private final DishRepository dishRepository;
    private final CategoriesRepository categoryRepository; 


    @Transactional
    public DishResponse createDish(CreateDishRequest request) {
    	Categories category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + request.categoryId()));

        if (dishRepository.existsByCategoryIdAndNameIgnoreCase(request.categoryId(), request.name())) {
            throw new IllegalArgumentException("Dish name already exists in this category");
        }

        Dish dish = new Dish();
        dish.setCategory(category);
        dish.setName(request.name());
        dish.setDescription(request.description());
        dish.setPrice(request.price());
        dish.setImageUrl(request.imageUrl());
        dish.setAvailable(request.available() != null ? request.available() : true);

        Dish saved = dishRepository.save(dish);
        return mapToResponse(saved);
    }

    @Transactional
    public DishResponse updateDish(Long id, UpdateDishRequest request) {
        Dish dish = dishRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Dish not found with id: " + id));

        Categories newCategory = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + request.categoryId()));

        boolean nameExistsForOther = dishRepository.findByCategoryIdAndNameIgnoreCase(request.categoryId(), request.name())
                .filter(d -> !d.getId().equals(id))
                .isPresent();
        
        if (nameExistsForOther) {
            throw new IllegalArgumentException("Dish name already exists in the target category");
        }

        dish.setCategory(newCategory);
        dish.setName(request.name());
        dish.setDescription(request.description());
        dish.setPrice(request.price());
        dish.setImageUrl(request.imageUrl());
        dish.setAvailable(request.available());

        Dish updated = dishRepository.save(dish);
        return mapToResponse(updated);
    }

    @Transactional(readOnly = true)
    public DishResponse getDishById(Long id) {
        Dish dish = dishRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Dish not found with id: " + id));
        return mapToResponse(dish);
    }

    @Transactional(readOnly = true)
    public List<DishResponse> getDishesByCategoryId(Long categoryId, boolean onlyAvailable) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new IllegalArgumentException("Category not found with id: " + categoryId);
        }

        List<Dish> dishes;
        if (onlyAvailable) {
            dishes = dishRepository.findByCategoryIdAndAvailableTrue(categoryId);
        } else {
            dishes = dishRepository.findByCategoryId(categoryId);
        }
        
        return dishes.stream()
                .map(this::mapToResponse)
                .toList();
    }
    
    @Transactional
    public DishResponse setDishAvailability(Long id, boolean available) {
        Dish dish = dishRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Dish not found with id: " + id));
        
        dish.setAvailable(available);
        Dish updated = dishRepository.save(dish);
        return mapToResponse(updated);
    }

    @Transactional
    public void deleteDish(Long id) {
        if (!dishRepository.existsById(id)) {
            throw new IllegalArgumentException("Dish not found with id: " + id);
        }
        dishRepository.deleteById(id);
    }

    private DishResponse mapToResponse(Dish dish) {
        return new DishResponse(
                dish.getId(),
                dish.getCategory().getId(),
                dish.getName(),
                dish.getDescription(),
                dish.getPrice(),
                dish.getImageUrl(),
                dish.getAvailable());
    }
}