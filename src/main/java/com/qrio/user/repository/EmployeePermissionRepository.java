package com.qrio.user.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.qrio.user.model.EmployeePermission;
import com.qrio.user.model.User;
import com.qrio.user.dto.response.EmployeePermissionItemResponse;

@Repository
public interface EmployeePermissionRepository extends JpaRepository<EmployeePermission, Long> {
    @Query("""
        SELECT 
            ep.restaurant.id AS restaurantId,
            ep.branch.id AS branchId,
            ep.permission AS permission
        FROM EmployeePermission ep
        WHERE ep.user.id = :userId
        ORDER BY ep.id DESC
    """)
    List<EmployeePermissionItemResponse> findPermissionsByUserId(Long userId);

    void deleteAllByUser(User user);
}
