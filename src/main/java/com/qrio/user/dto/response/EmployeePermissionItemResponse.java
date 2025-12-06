package com.qrio.user.dto.response;

public record EmployeePermissionItemResponse(
    Long restaurantId,
    Long branchId,
    String permission
) {}
