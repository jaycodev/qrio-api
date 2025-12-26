package com.qrio.auth.dto.mobile;

import com.qrio.customer.model.Customer;

public record FirebaseAuthResponse(
        Customer customer,
        boolean isNew) {
}
