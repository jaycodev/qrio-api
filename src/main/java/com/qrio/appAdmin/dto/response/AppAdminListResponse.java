package com.qrio.appAdmin.dto.response;

import java.time.LocalDateTime;

public record AppAdminListResponse(
    Long id,
    String name,
    String email,
    LocalDateTime createdAt
) {}
