package com.qrio.auth.dto.web;

public class MeResponse {
    private final Long id;
    private final String email;
    private final String name;
    private final String role;
    private final Long restaurantId;
    private final Long branchId;

    public MeResponse(Long id,
                      String email,
                      String name,
                      String role,
                      Long restaurantId,
                      Long branchId) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.role = role;
        this.restaurantId = restaurantId;
        this.branchId = branchId;
    }

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getName() { return name; }
    public String getRole() { return role; }
    public Long getRestaurantId() { return restaurantId; }
    public Long getBranchId() { return branchId; }
}
