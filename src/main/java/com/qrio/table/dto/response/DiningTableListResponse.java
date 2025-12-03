package com.qrio.table.dto.response;

public record DiningTableListResponse(
    Long id,
    Integer tableNumber,
    Integer floor,
    String qrCode
) {}
