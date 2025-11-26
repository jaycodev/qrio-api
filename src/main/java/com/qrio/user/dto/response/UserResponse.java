package com.qrio.user.dto.response;

import com.qrio.user.model.type.UserStatus;

public record UserResponse(
        Long id,
        String firebaseUid,
        String name,
        String email,
        String phone,
        UserStatus status) {
}
