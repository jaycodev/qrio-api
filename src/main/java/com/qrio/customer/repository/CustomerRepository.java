package com.qrio.customer.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.qrio.customer.dto.response.CustomerDetailResponse;
import com.qrio.customer.dto.response.CustomerListResponse;
import com.qrio.customer.model.Customer;
import com.qrio.shared.response.OptionResponse;

public interface CustomerRepository extends CrudRepository<Customer, Long> {
    
    @Query("""
        SELECT 
            c.id AS id,
            c.firebaseUid AS firebaseUid,
            c.name AS name,
            c.email AS email,
            c.status AS status,
            c.createdAt AS createdAt
        FROM Customer c
        ORDER BY c.createdAt DESC
    """)
    List<CustomerListResponse> findList();

    @Query("""
        SELECT 
            c.id AS id,
            c.firebaseUid AS firebaseUid,
            c.name AS name,
            c.email AS email,
            c.phone AS phone,
            c.status AS status,
            c.createdAt AS createdAt
        FROM Customer c
        WHERE c.id = :id
    """)
    Optional<CustomerDetailResponse> findDetailById(Long id);

    Optional<Customer> findByEmail(String email);

    boolean existsByFirebaseUid(String firebaseUid);

    Optional<Customer> findByFirebaseUid(String firebaseUid);

    @Query("""
        SELECT 
            c.id AS value,
            c.name AS label
        FROM Customer c 
        ORDER BY c.name ASC
    """)
    List<OptionResponse> findForOptions();
}
