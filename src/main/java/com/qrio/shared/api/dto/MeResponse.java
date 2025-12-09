package com.qrio.shared.api.dto;

import lombok.Data;

@Data
public class MeResponse {
    private final Long id;
    private final String email;
    private final String name;
    private final String role;
    private final Long restaurantId;
    private final Long branchId;
}
