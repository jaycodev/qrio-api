package com.qrio.user.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.qrio.shared.response.OptionResponse;
import com.qrio.user.dto.response.EmployeeDetailResponse;
import com.qrio.user.dto.response.EmployeeListResponse;
import com.qrio.user.dto.response.OwnerListResponse;
import com.qrio.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {
    @Query("""
        SELECT 
            u.id AS id,
            u.restaurant.id AS restaurantId,
            u.branch.id AS branchId,
            u.name AS name,
            u.email AS email,
            u.phone AS phone,
            u.role AS role,
            u.status AS status,
            u.createdAt AS createdAt
        FROM User u
        WHERE u.role = 'EMPLEADO'
            AND u.branch.id = :branchId
        ORDER BY u.createdAt DESC
        """)
    List<EmployeeListResponse> findListEmployee(Long branchId);

    @Query("""
        SELECT 
            u.id AS id,
            u.restaurant.id AS restaurantId,
            u.branch.id AS branchId,
            u.name AS name,
            u.email AS email,
            u.phone AS phone,
            u.role AS role,
            u.status AS status,
            u.createdAt AS createdAt,
            
            NULL AS permissions
        FROM User u
        WHERE u.id = :id AND u.role = 'EMPLEADO'
        """)
    Optional<EmployeeDetailResponse> findDetailEmployee(Long id);

    @Query("""
        SELECT 
            u.id AS value,
            u.name AS label
        FROM User u
        WHERE u.status = 'ACTIVO'
            AND u.role = 'EMPLEADO'
            AND u.restaurant.id = :restaurantId
        ORDER BY u.name
        """)
    List<OptionResponse> findForOptions(Long restaurantId);

    @Query("""
        SELECT 
            u.id AS id,
            u.restaurant.id AS restaurantId,
            u.name AS name,
            u.email AS email,
            u.phone AS phone,
            u.role AS role,
            u.status AS status,
            u.createdAt AS createdAt
        FROM User u
        WHERE u.id = :id AND u.role = 'DUEÑO'
        """)
    Optional<OwnerListResponse> findDetailOwner(Long id);

    Optional<User> findByEmail(String email);
}
