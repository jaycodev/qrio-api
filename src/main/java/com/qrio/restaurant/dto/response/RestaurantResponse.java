package com.qrio.restaurant.dto.response;

import com.qrio.restaurant.model.Restaurant;

import java.time.LocalDateTime;

public class RestaurantResponse {
    private Long id;
    private Long userId;
    private String name;
    private String description;
    private String logoUrl;
    private LocalDateTime createdAt;

    public static RestaurantResponse from(Restaurant r) {
        RestaurantResponse resp = new RestaurantResponse();
        resp.id = r.getId();
        resp.userId = r.getUserId();
        resp.name = r.getName();
        resp.description = r.getDescription();
        resp.logoUrl = r.getLogoUrl();
        resp.createdAt = r.getCreatedAt();
        return resp;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
