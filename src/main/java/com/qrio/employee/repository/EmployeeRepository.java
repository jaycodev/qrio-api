package com.qrio.employee.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.qrio.employee.dto.response.EmployeeDetailResponse;
import com.qrio.employee.dto.response.EmployeeListResponse;
import com.qrio.employee.model.Employee;
import com.qrio.shared.response.OptionResponse;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends CrudRepository<Employee, Long> {
    @Query("""
        SELECT 
            e.id AS id,
            e.admin.id AS adminId,
            e.restaurant.id AS restaurantId,
            e.branch.id AS branchId,
            e.name AS name,
            e.email AS email,
            e.phone AS phone,
            e.role AS role,
            e.status AS status,
            e.createdAt AS createdAt
        FROM Employee e
        WHERE e.restaurant.id = :restaurantId
        AND (:branchId IS NULL OR e.branch.id = :branchId)
        ORDER BY e.id DESC
    """)
    List<EmployeeListResponse> findList(Long restaurantId, Long branchId);

    @Query("""
        SELECT 
            e.id AS id,
            e.admin.id AS adminId,
            e.restaurant.id AS restaurantId,
            e.branch.id AS branchId,
            e.name AS name,
            e.email AS email,
            e.phone AS phone,
            e.role AS role,
            e.status AS status,
            e.createdAt AS createdAt,
            
            NULL AS permissions
        FROM Employee e
        WHERE e.id = :id
    """)
    Optional<EmployeeDetailResponse> findDetailById(Long id);

    @Query("""
        SELECT 
            e.id AS value,
            e.name AS label
        FROM Employee e
        WHERE e.status = 'ACTIVO'
        AND e.restaurant.id = :restaurantId
        ORDER BY e.name
    """)
    List<OptionResponse> findForOptions(Long restaurantId);
}
