package com.qrio.product.model;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductBranchAvailabilityId implements Serializable {
    @Column(name = "product_id")
    private Long productId;

    @Column(name = "branch_id")
    private Long branchId;
}