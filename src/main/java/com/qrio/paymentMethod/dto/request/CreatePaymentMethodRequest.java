package com.qrio.paymentMethod.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreatePaymentMethodRequest(

        @NotNull(message = "Customer ID is required") Long customerId,

        @NotBlank(message = "Payment type is required") String type,

        @NotBlank(message = "Payment token is required") String paymentToken,

        String last4, // opcional, últimos 4 dígitos de la tarjeta
        String brand // opcional, marca de la tarjeta (Visa, MasterCard, etc.)

) {
}
