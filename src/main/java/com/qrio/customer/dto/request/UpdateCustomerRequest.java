package com.qrio.customer.dto.request;

import com.qrio.customer.model.type.CustomerStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateCustomerRequest(

        @NotBlank(message = "Firebase UID is required") @Size(max = 255, message = "Firebase UID cannot exceed 255 characters") String firebaseUid,

        @NotBlank(message = "Name is required") @Size(max = 100, message = "Name cannot exceed 100 characters") String name,

        @NotBlank(message = "Email is required") @Email(message = "Invalid email format") String email,

        @Size(max = 20, message = "Phone cannot exceed 20 characters") String phone,

        CustomerStatus status // opcional
) {
}
