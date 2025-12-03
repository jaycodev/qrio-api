package com.qrio.table.dto.response;

public record DiningTableDetailResponse(
    Long id,
    Integer tableNumber,
    Integer floor,
    String qrCode
) {}
