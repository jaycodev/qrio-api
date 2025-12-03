package com.qrio.paymentMethod.dto.response;

import java.time.LocalDateTime;

public record PaymentMethodDetailResponse(
        Long id,
        Long customerId,
        String type,
        String paymentToken,
        String last4,
        String brand,
        LocalDateTime createdAt
) {
}
