package com.qrio.shared.api.dto;

public class MeResponse {
    private Long id;
    private String email;
    private String name;
    private String role;
    private Long restaurantId;
    private Long branchId;

    public MeResponse() {}

    public MeResponse(Long id, String email, String name, String role, Long restaurantId, Long branchId) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.role = role;
        this.restaurantId = restaurantId;
        this.branchId = branchId;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public Long getRestaurantId() { return restaurantId; }
    public void setRestaurantId(Long restaurantId) { this.restaurantId = restaurantId; }
    public Long getBranchId() { return branchId; }
    public void setBranchId(Long branchId) { this.branchId = branchId; }
}
