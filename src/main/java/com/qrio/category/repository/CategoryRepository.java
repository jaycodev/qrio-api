package com.qrio.category.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.qrio.category.dto.response.CategoryDetailResponse;
import com.qrio.category.dto.response.CategoryListResponse;
import com.qrio.category.model.Category;

public interface CategoryRepository extends CrudRepository<Category, Long> {

    @Query("""
        SELECT
            c.id AS id,
            c.restaurant.id AS restaurantId,
            c.name AS name
        FROM Category c
        WHERE (:restaurantId IS NULL OR c.restaurant.id = :restaurantId)
        ORDER BY c.name ASC
    """)
    List<CategoryListResponse> findList(Long restaurantId);

    @Query("""
        SELECT
            c.id AS id,
            c.restaurant.id AS restaurantId,
            c.name AS name
        FROM Category c
        WHERE c.id = :id
    """)
    Optional<CategoryDetailResponse> findDetailById(Long id);

    boolean existsByRestaurantIdAndNameIgnoreCase(Long restaurantId, String name);

    Optional<Category> findByRestaurantIdAndNameIgnoreCase(Long restaurantId, String name);
}