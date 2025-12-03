package com.qrio.table.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateDiningTableRequest(
        @NotNull(message = "Branch ID is required") Long branchId,
        
        @NotNull(message = "Table number is required") 
        @Min(value = 1, message = "Table number must be at least 1") 
        Integer tableNumber,
        
        @NotBlank(message = "QR code is required") String qrCode
) {
}

