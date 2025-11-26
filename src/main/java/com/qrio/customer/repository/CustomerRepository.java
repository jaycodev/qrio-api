package com.qrio.customer.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.qrio.customer.model.Customer;;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByEmail(String email);

    boolean existsByUidFirebase(String firebaseUid);

    Optional<Customer> findByFirebaseUid(String firebaseUid);

}
