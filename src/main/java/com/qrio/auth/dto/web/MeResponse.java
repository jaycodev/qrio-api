package com.qrio.auth.dto.mobile;

public record MeResponse(
    Long id,
    String email,
    String name,
    String role,
    Long restaurantId,
    Long branchId
) {}
