package com.qrio.restaurant.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.qrio.restaurant.dto.response.RestaurantDetailResponse;
import com.qrio.restaurant.dto.response.RestaurantListResponse;
import com.qrio.restaurant.model.Restaurant;
import com.qrio.shared.response.OptionResponse;

import java.util.List;
import java.util.Optional;

public interface RestaurantRepository extends CrudRepository<Restaurant, Long> {
    @Query("""
        SELECT 
            r.id AS id,
            r.code AS code,
            r.admin.id AS adminId,
            r.name AS name,
            r.logoUrl AS logoUrl,
            r.isActive AS isActive
        FROM Restaurant r
        ORDER BY r.id DESC
    """)
    List<RestaurantListResponse> findList();

    @Query("""
        SELECT 
            r.id AS id,
            r.code AS code,
            r.admin.id AS adminId,
            r.name AS name,
            r.description AS description,
            r.logoUrl AS logoUrl,
            r.isActive AS isActive,
            r.createdAt AS createdAt
        FROM Restaurant r
        WHERE r.id = :id
    """)
    Optional<RestaurantDetailResponse> findDetailById(Long id);

    @Query("""
        SELECT 
            r.id AS value,
            r.name AS label
        FROM Restaurant r
        WHERE r.isActive = true
        ORDER BY r.name
    """)
    List<OptionResponse> findForOptions();
}
