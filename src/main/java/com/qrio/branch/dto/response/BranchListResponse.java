package com.qrio.branch.dto.response;

import java.time.LocalDateTime;

public record BranchListResponse(
    Long id,
    String code,
    Long restaurantId,
    String name,
    String address,
    String phone,
    String schedule,
    LocalDateTime createdAt
) {}
