package com.qrio.paymentMethod.dto.response;

import java.time.LocalDateTime;

public record PaymentMethodResponse(

        Long id,
        Long customerId,
        String type,
        String last4,
        String brand,
        LocalDateTime createdAt

) {
}
