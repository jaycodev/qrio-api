package com.qrio.customer.dto.request;

import com.qrio.shared.type.Status;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateCustomerRequest(

        @NotBlank(message = "Name is required") @Size(max = 100, message = "Name cannot exceed 100 characters") String name,

        @Size(max = 20, message = "Phone cannot exceed 20 characters") String phone

) {
}