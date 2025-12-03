package com.qrio.product.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateProductRequest(

        @NotNull(message = "Category ID is required") Long categoryId,

        @NotBlank(message = "Name is required") @Size(max = 150, message = "Name cannot exceed 150 characters") String name,

        String description,

        @NotNull(message = "Price is required") @DecimalMin(value = "0.01", message = "Price must be positive") BigDecimal price,

        String imageUrl,

        @NotNull(message = "Availability status is required") Boolean available) {
}