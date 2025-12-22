package com.qrio.branch.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.qrio.auth.dto.UserBranchResponse;
import com.qrio.branch.dto.response.BranchDetailResponse;
import com.qrio.branch.dto.response.BranchListResponse;
import com.qrio.branch.model.Branch;
import com.qrio.shared.response.OptionResponse;

import java.util.List;
import java.util.Optional;

public interface BranchRepository extends CrudRepository<Branch, Long> {
    @Query("""
        SELECT 
            b.id AS id,
            b.code AS code,
            b.restaurant.id AS restaurantId,
            b.name AS name,
            b.address AS address,
            b.phone AS phone,
            b.schedule AS schedule,
            b.createdAt AS createdAt
        FROM Branch b
        WHERE (:restaurantId IS NULL OR b.restaurant.id = :restaurantId)
        ORDER BY b.id DESC
    """)
    List<BranchListResponse> findList(Long restaurantId);

    @Query("""
        SELECT 
            b.id AS id,
            b.code AS code,
            b.restaurant.id AS restaurantId,
            b.name AS name,
            b.address AS address,
            b.phone AS phone,
            b.schedule AS schedule,
            b.createdAt AS createdAt
        FROM Branch b
        WHERE b.id = :id
    """)
    Optional<BranchDetailResponse> findDetailById(Long id);

    @Query("""
        SELECT
            b.id AS id,
            b.restaurant.name AS restaurantName,
            b.name AS branchName
        FROM Branch b
        WHERE b.restaurant.user.id = :userId
        ORDER BY b.restaurant.id DESC
    """)
    List<UserBranchResponse> findBranchesByUserId(Long userId);

    @Query("""
        SELECT
            b.id AS id,
            b.restaurant.name AS restaurantName,
            b.name AS branchName
        FROM Branch b
        WHERE EXISTS (
            SELECT ep.id FROM EmployeePermission ep
            WHERE ep.user.id = :userId
              AND (ep.branch.id = b.id OR ep.restaurant.id = b.restaurant.id)
        )
        ORDER BY b.restaurant.id DESC
    """)
    List<UserBranchResponse> findBranchesByEmployeeId(Long userId);

    @Query("""
        SELECT 
            b.id AS value,
            b.name AS label
        FROM Branch b
        WHERE b.restaurant.id = :restaurantId
        ORDER BY b.name
    """)
    List<OptionResponse> findForOptions(Long restaurantId);
}
