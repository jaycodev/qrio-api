package com.qrio.branch.repository;

import com.qrio.branch.model.Branch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BranchRepository extends JpaRepository<Branch, Long> {
    List<Branch> findByRestaurantId(Long restaurantId);
}
