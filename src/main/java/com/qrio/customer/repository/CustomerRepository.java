package com.qrio.customer.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.qrio.customer.model.Customer;
import com.qrio.shared.response.OptionResponse;;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
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
