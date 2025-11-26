package com.qrio.user.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.qrio.user.dto.request.CreateUserRequest;
import com.qrio.user.dto.response.UserResponse;
import com.qrio.user.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    @Operation(summary = "Create a new user")
    public ResponseEntity<UserResponse> createUser(@RequestBody CreateUserRequest request) {
        UserResponse userResponse = userService.createUser(request);
        return ResponseEntity.ok(userResponse);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        UserResponse userResponse = userService.getUserById(id);
        return ResponseEntity.ok(userResponse);
    }

    @GetMapping("/firebase/{uid}")
    @Operation(summary = "Get user by Firebase UID")
    public ResponseEntity<UserResponse> getUserByFirebaseUid(@PathVariable String uid) {
        UserResponse userResponse = userService.getUserByFirebaseUid(uid);
        return ResponseEntity.ok(userResponse);
    }

    @GetMapping
    @Operation(summary = "Get list of all users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user by ID")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @RequestBody CreateUserRequest request) {
        UserResponse updatedUser = userService.updateUser(id, request);
        return ResponseEntity.ok(updatedUser);
    }

    @PatchMapping("/{id}/deactivate")
    @Operation(summary = "Deactivate user by ID")
    public ResponseEntity<UserResponse> deactivateUser(@PathVariable Long id) {
        UserResponse deactivatedUser = userService.deactivateUser(id);
        return ResponseEntity.ok(deactivatedUser);
    }
}
