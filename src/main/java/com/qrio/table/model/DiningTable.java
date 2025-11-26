package com.qrio.table.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "dining_tables")
public class DiningTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "branch_id", nullable = false)
    private Long branchId;

    @Column(name = "table_number", nullable = false)
    private Integer tableNumber;

    @Column(name = "qr_code", nullable = false, length = 255, unique = true)
    private String qrCode;

}
