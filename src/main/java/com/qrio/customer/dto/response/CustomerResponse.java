package com.qrio.customer.dto.response;

import com.qrio.customer.model.type.CustomerStatus;

import java.time.LocalDateTime;

public record CustomerResponse(

        Long id,
        String uidFirebase,
        String name,
        String email,
        String phone,
        CustomerStatus status,
        LocalDateTime createdAt

) {
}
