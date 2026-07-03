package com.project.razorpay.payment.processor.dto;

public sealed interface PaymentProcessResponse permits
        PaymentProcessResponse.Pending,
        PaymentProcessResponse.Success,
        PaymentProcessResponse.Failure {

    record Pending(String processorReference) implements PaymentProcessResponse{}

    record Success(String processorReference, String bankReference) implements PaymentProcessResponse{}

    record Failure(String errorCode, String errorDescription) implements PaymentProcessResponse{}
}
