package com.qrio.user.dto.request;

import com.qrio.shared.validation.ValidEnum;
import com.qrio.user.model.type.UserStatus;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UpdateUserRequest(

        @Size(max = 100, message = "Name cannot exceed 100 characters") String name,

        @Email(message = "Invalid email format") @Size(max = 150, message = "Email cannot exceed 150 characters") String email,

        @Size(max = 20, message = "Phone cannot exceed 20 characters") String phone,

        @ValidEnum(enumClass = UserStatus.class, message = "Invalid status") String status) {
}
