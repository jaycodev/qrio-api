package com.qrio.product.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.qrio.product.model.ProductBranchAvailability;
import com.qrio.product.model.ProductBranchAvailabilityId;

public interface ProductBranchAvailabilityRepository extends CrudRepository<ProductBranchAvailability, ProductBranchAvailabilityId> {
    
    @Query("""
        SELECT pba.available
        FROM ProductBranchAvailability pba
        WHERE pba.product.id = :productId AND pba.branch.id = :branchId
    """)
    Optional<Boolean> findAvailabilityByProductAndBranch(Long productId, Long branchId);
}
