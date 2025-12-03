package com.qrio.restaurantActivation.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.qrio.restaurantActivation.dto.response.RestaurantActivationDetailResponse;
import com.qrio.restaurantActivation.dto.response.RestaurantActivationListResponse;
import com.qrio.restaurantActivation.model.RestaurantActivationRequest;

import java.util.List;
import java.util.Optional;

public interface RestaurantActivationRepository extends CrudRepository<RestaurantActivationRequest, Long> {
    @Query("""
        SELECT 
            r.id AS id,
            r.restaurant.id AS restaurantId,
            r.admin.id AS adminId,
            r.status AS status,
            r.comment AS comment,
            r.createdAt AS createdAt,
            r.reviewedAt AS reviewedAt
        FROM RestaurantActivationRequest r
        WHERE (:restaurantId IS NULL OR r.restaurant.id = :restaurantId)
        ORDER BY r.id DESC
    """)
    List<RestaurantActivationListResponse> findList(Long restaurantId);

    @Query("""
        SELECT 
            r.id AS id,
            r.restaurant.id AS restaurantId,
            r.admin.id AS adminId,
            r.status AS status,
            r.comment AS comment,
            r.createdAt AS createdAt,
            r.reviewedAt AS reviewedAt
        FROM RestaurantActivationRequest r
        WHERE r.id = :id
    """)
    Optional<RestaurantActivationDetailResponse> findDetailById(Long id);
}
