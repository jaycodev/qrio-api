package com.qrio.auth.dto;

public record MeResponse(
    Long id,
    String email,
    String name,
    String role,
    Long restaurantId,
    Long branchId
) {}
