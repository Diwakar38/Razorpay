package com.project.razorpay.vault.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.Year;

public class ExpiryYearValidator implements ConstraintValidator<ExpiryYear, Integer> {
    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if (value == null) return false;
        return Year.now().getValue() <= value;
    }
}
