package com.project.razorpay.vault.dto.request;

import com.project.razorpay.vault.validation.ExpiryYear;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.LuhnCheck;

import java.util.UUID;

public record TokenizeRequest(
        @NotBlank(message = "PAN is required")
        @LuhnCheck(message = "Invalid card number")
        @Pattern(regexp = "^[0-9]{13,19}$", message = "PAN length is invalid")
        String pan,

        @NotBlank(message = "CVV is required")
        @Pattern(regexp = "^[0-9]{3,4}$", message = "CVV length is invalid")
        String cvv,

        @NotBlank(message = "Expiry month is required")
        @Min(value = 1, message = "Expiry month must be between 1 to 12")
        @Max(value = 12, message = "Expiry month must be between 1 to 12")
        Integer expiryMonth,

        @NotBlank(message = "Expiry year is required")
        @ExpiryYear
        Integer expiryYear,

        UUID customerId,

        @Size(min = 3, message = "CardHolderName should be at least 3 characters long")
        String cardHolderName
) {
}
