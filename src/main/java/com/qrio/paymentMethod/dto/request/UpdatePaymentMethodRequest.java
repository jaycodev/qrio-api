package com.qrio.paymentMethod.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UpdatePaymentMethodRequest(

        @NotBlank(message = "Payment type is required") String type,

        String last4,
        String brand

) {
}
