package com.qrio.auth.dto.mobile;

import java.util.Date;

public record TokenInfoResponse(
    String subject,
    String role,
    Long customerId,
    String email,
    String name,
    Date issuedAt,
    Date expiration
) {}
