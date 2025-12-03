package com.qrio.admin.dto.response;

import com.qrio.shared.type.Status;

public record AdminDetailResponse(
    Long id,
    String name,
    String email,
    String phone,
    Status status
) {}
