package com.qrio.appAdmin.dto.response;

import java.time.LocalDateTime;

import com.qrio.appAdmin.model.type.AppAdminRole;

public record AppAdminListResponse(
    Long id,
    String name,
    String email,
    AppAdminRole role,
    LocalDateTime createdAt
) {}
