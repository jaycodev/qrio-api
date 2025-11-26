package com.qrio.branch.dto.response;

import com.qrio.branch.model.Branch;

import java.time.LocalDateTime;

public class BranchResponse {
    private Long id;
    private Long restaurantId;
    private String name;
    private String address;
    private String phone;
    private String schedule;
    private LocalDateTime createdAt;

    public static BranchResponse from(Branch b) {
        BranchResponse resp = new BranchResponse();
        resp.id = b.getId();
        resp.restaurantId = b.getRestaurantId();
        resp.name = b.getName();
        resp.address = b.getAddress();
        resp.phone = b.getPhone();
        resp.schedule = b.getSchedule();
        resp.createdAt = b.getCreatedAt();
        return resp;
    }

    public Long getId() {
        return id;
    }

    public Long getRestaurantId() {
        return restaurantId;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public String getSchedule() {
        return schedule;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
