package com.qrio.user.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.qrio.user.dto.request.CreateUserRequest;
import com.qrio.user.dto.response.UserResponse;
import com.qrio.user.model.User;
import com.qrio.user.model.type.UserStatus;
import com.qrio.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByUidFirebase(request.firebaseUid())) {
            throw new IllegalArgumentException("User with this Firebase UID already exists");
        }

        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("Email is already in use");
        }

        User user = new User();
        user.setFirebaseUid(request.firebaseUid());
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPhone(request.phone());
        user.setStatus(request.status() != null ? request.status() : UserStatus.ACTIVE);

        User saved = userRepository.save(user);

        return mapToResponse(saved);
    }

    @Transactional
    public UserResponse updateUser(Long id, CreateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));

        userRepository.findByEmail(request.email())
                .filter(u -> !u.getId().equals(id))
                .ifPresent(u -> {
                    throw new IllegalArgumentException("Email is already in use by another user");
                });

        user.setName(request.name());
        user.setEmail(request.email());
        user.setPhone(request.phone());
        user.setStatus(request.status() != null ? request.status() : user.getStatus());

        User updated = userRepository.save(user);

        return mapToResponse(updated);
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
        return mapToResponse(user);
    }

    @Transactional(readOnly = true)
    public UserResponse getUserByFirebaseUid(String firebaseUid) {
        User user = userRepository.findByFirebaseUid(firebaseUid)
                .orElseThrow(() -> new IllegalArgumentException("User not found with Firebase UID: " + firebaseUid));
        return mapToResponse(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public UserResponse deactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));

        user.setStatus(UserStatus.INACTIVE);

        User updated = userRepository.save(user);

        return mapToResponse(updated);
    }

    private UserResponse mapToResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getFirebaseUid(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getStatus());
    }
}
