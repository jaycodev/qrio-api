package com.qrio.user.dto.request;

import com.qrio.user.model.type.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(

        @NotBlank(message = "Firebase UID is required") @Size(max = 255, message = "Firebase UID cannot exceed 255 characters") String firebaseUid,

        @NotBlank(message = "Name is required") @Size(max = 100) String name,

        @NotBlank(message = "Email is required") @Email(message = "Invalid email format") String email,

        @Size(max = 20) String phone,

        UserStatus status

) {
}
