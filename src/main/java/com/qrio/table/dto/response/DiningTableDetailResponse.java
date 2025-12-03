package com.qrio.table.dto.response;

public record DiningTableDetailResponse(
        Long id,
        Long branchId,
        Integer tableNumber,
        String qrCode
) {
}
