package com.qrio.auth.dto;

public record UserBranchResponse(
    Long id,
    String restaurantName,
    String branchName
) {}
