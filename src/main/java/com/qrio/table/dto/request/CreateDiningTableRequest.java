package com.qrio.table.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CreateDiningTableRequest(
        @NotNull(message = "Branch ID is required") Long branchId,
        
        // Optional: if not provided, server will auto-assign next number
        @Min(value = 1, message = "Table number must be at least 1") 
        Integer tableNumber,
        
        @NotNull(message = "Floor is required")
        @Min(value = 0, message = "Floor must be at least 0")
        Integer floor,
        
        // Optional: if not provided, server will generate a default QR code
        String qrCode
) {
}

