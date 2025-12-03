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
            p.id AS id,
            p.imageUrl AS imageUrl,
            p.name AS name,
            p.description AS description,
            p.price AS price,
            
            p.category.id AS categoryId,
            p.category.name AS categoryName,

            ba.available AS available
        FROM Product p
        JOIN p.branchAvailabilities ba ON ba.branch.id = :branchId
        ORDER BY p.name ASC
    """)
    List<ProductListResponse> findList(Long branchId);

    @Query("""
        SELECT
            p.id AS id,
            p.category.id AS categoryId,
            p.name AS name,
            p.description AS description,
            p.price AS price,
            p.imageUrl AS imageUrl
        FROM Product p
        WHERE p.id = :id
    """)
    Optional<ProductDetailResponse> findDetailById(Long id);

    boolean existsByCategoryIdAndNameIgnoreCase(Long categoryId, String name);

    Optional<Product> findByCategoryIdAndNameIgnoreCase(Long categoryId, String name);
}