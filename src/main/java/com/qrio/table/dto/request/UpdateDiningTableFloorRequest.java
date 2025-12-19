package com.qrio.table.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record UpdateDiningTableFloorRequest(
        @NotNull(message = "Floor is required")
        @Min(value = 0, message = "Floor must be at least 0")
        Integer floor
) {}
