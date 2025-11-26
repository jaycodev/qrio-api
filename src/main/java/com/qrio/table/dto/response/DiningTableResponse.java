package com.qrio.table.dto.response;

import com.qrio.table.model.DiningTable;

public class DiningTableResponse {
    private Long id;
    private Long branchId;
    private Integer tableNumber;
    private String qrCode;

    public static DiningTableResponse from(DiningTable t) {
        DiningTableResponse resp = new DiningTableResponse();
        resp.id = t.getId();
        resp.branchId = t.getBranchId();
        resp.tableNumber = t.getTableNumber();
        resp.qrCode = t.getQrCode();
        return resp;
    }

    public Long getId() {
        return id;
    }

    public Long getBranchId() {
        return branchId;
    }

    public Integer getTableNumber() {
        return tableNumber;
    }

    public String getQrCode() {
        return qrCode;
    }
}
