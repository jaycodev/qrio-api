package com.qrio.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.qrio.user.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByUidFirebase(String firebaseUid);

    Optional<User> findByFirebaseUid(String firebaseUid);

    // List<User> findByStatus(UserStatus status);

}
