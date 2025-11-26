package com.qrio.categories.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateCategorieRequest(

        @NotNull(message = "Restaurant ID is required") Long restaurantId,

        @NotBlank(message = "Name is required") @Size(max = 100, message = "Name cannot exceed 100 characters") String name
) {
}