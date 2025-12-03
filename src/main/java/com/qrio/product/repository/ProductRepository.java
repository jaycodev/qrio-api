package com.qrio.product.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.qrio.product.dto.response.ProductDetailResponse;
import com.qrio.product.dto.response.ProductListResponse;
import com.qrio.product.model.Product;

public interface ProductRepository extends CrudRepository<Product, Long> {

    @Query("""
        SELECT 
            d.id AS id,
            d.category.id AS categoryId,
            d.name AS name,
            d.price AS price,
            d.imageUrl AS imageUrl,
            d.available AS available
        FROM Product d
        WHERE (:categoryId IS NULL OR d.category.id = :categoryId)
        ORDER BY d.name ASC
    """)
    List<ProductListResponse> findList(Long categoryId);

    @Query("""
        SELECT 
            d.id AS id,
            d.category.id AS categoryId,
            d.name AS name,
            d.description AS description,
            d.price AS price,
            d.imageUrl AS imageUrl,
            d.available AS available
        FROM Product d
        WHERE d.id = :id
    """)
    Optional<ProductDetailResponse> findDetailById(Long id);

    boolean existsByCategoryIdAndNameIgnoreCase(Long categoryId, String name);

    Optional<Product> findByCategoryIdAndNameIgnoreCase(Long categoryId, String name);
}