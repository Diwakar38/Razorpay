package com.project.razorpay.payment.processor.strategy;

import com.project.razorpay.common.util.RandomizerUtil;
import com.project.razorpay.payment.processor.PaymentProcessor;
import com.project.razorpay.payment.processor.dto.PaymentProcessRequest;
import com.project.razorpay.payment.processor.dto.PaymentProcessResponse;

public class UpiPaymentProcessor implements PaymentProcessor {
    @Override
    public PaymentProcessResponse charge(PaymentProcessRequest request) {
        final String VPA_CODE_FAIL = "fail@oksbi";

        String bankCode = request.methodDetails() != null ?
                request.methodDetails().get("BANK").toString() : null;

        // Simulation
        if(VPA_CODE_FAIL.equals(bankCode)) {
            return new PaymentProcessResponse.Failure("UPI_REJECTED",
                                                      "Bank rejected the transaction registration");
        }

        String processorRef = "NBK_PROCESSOR_" + RandomizerUtil.randomBase64(16);

        String bankRef = "BANK_REF/" + RandomizerUtil.randomBase64(16);

        return new PaymentProcessResponse.Pending(processorRef);
    }
}
