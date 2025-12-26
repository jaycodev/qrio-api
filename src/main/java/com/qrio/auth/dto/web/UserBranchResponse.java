package com.qrio.auth.dto.web;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "restaurant", "branch" })
public record UserBranchResponse(
    @JsonIgnore Long restaurantId,
    @JsonIgnore String restaurantName,

    @JsonIgnore Long branchId,
    @JsonIgnore String branchName
) {

    @JsonGetter("restaurant")
    public Restaurant getRestaurant() {
        return new Restaurant(restaurantId, restaurantName);
    }

    @JsonGetter("branch")
    public Branch getBranch() {
        return new Branch(branchId, branchName);
    }

    public record Restaurant(
        Long id,
        String name
    ) {}

    public record Branch(
        Long id,
        String name
    ) {}
}
