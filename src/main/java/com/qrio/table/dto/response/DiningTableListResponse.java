package com.qrio.table.dto.response;

public record DiningTableListResponse(
        Long id,
        Long branchId,
        Integer tableNumber,
        String qrCode
) {
}
