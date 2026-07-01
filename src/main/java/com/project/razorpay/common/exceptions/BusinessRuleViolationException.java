package com.project.razorpay.common.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BusinessRuleViolationException extends RuntimeException {
    private final String errorCode;

    public BusinessRuleViolationException(String errorCode, String message) {
        this.errorCode = errorCode;
        super(message);
    }
}
