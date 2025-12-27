package com.qrio.offer.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.qrio.offer.dto.response.OfferDetailResponse;
import com.qrio.offer.dto.response.OfferListResponse;
import com.qrio.offer.model.Offer;

import java.util.List;
import java.util.Optional;

public interface OfferRepository extends CrudRepository<Offer, Long> {
    @Query("""
        SELECT 
            o.id AS id,
            o.code AS code,
            o.restaurant.id AS restaurantId,
            o.title AS title,
            o.description AS description,
            o.offerDiscountPercentage AS offerDiscountPercentage,
            o.active AS active
        FROM Offer o
        ORDER BY o.id DESC
    """)
    List<OfferListResponse> findList();

    @Query("""
        SELECT 
            o.id AS id,
            o.code AS code,
            o.restaurant.id AS restaurantId,
            o.title AS title,
            o.description AS description,
            o.offerDiscountPercentage AS offerDiscountPercentage,
            o.active AS active
        FROM Offer o
        WHERE o.restaurant.id = :restaurantId
        ORDER BY o.id DESC
    """)
    List<OfferListResponse> findListByRestaurantId(Long restaurantId);

    @Query("""
        SELECT 
            o.id AS id,
            o.code AS code,
            o.restaurant.id AS restaurantId,
            o.title AS title,
            o.description AS description,
            o.offerDiscountPercentage AS offerDiscountPercentage,
            o.active AS active,
            
            NULL AS products
        FROM Offer o
        WHERE o.id = :id
    """)
    Optional<OfferDetailResponse> findDetailById(Long id);
}
