package com.qrio.employee.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.qrio.employee.dto.response.EmployeeDetailResponse;
import com.qrio.employee.model.Employee;
import com.qrio.employee.model.EmployeePermission;

import java.util.List;

public interface EmployeePermissionRepository extends CrudRepository<EmployeePermission, Long> {
    @Query("""
        SELECT 
            ep.restaurant.id AS restaurantId,
            ep.branch.id AS branchId,
            ep.permission AS permission
        FROM EmployeePermission ep
        WHERE ep.employee.id = :employeeId
        ORDER BY ep.id
    """)
    List<EmployeeDetailResponse.EmployeePermissionItem> findPermissionsByEmployeeId(Long employeeId);

    void deleteAllByEmployee(Employee employee);
}
