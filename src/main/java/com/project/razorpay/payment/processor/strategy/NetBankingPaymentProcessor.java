package com.project.razorpay.payment.processor.strategy;

import com.project.razorpay.common.util.RandomizerUtil;
import com.project.razorpay.payment.processor.PaymentProcessor;
import com.project.razorpay.payment.processor.dto.PaymentProcessRequest;
import com.project.razorpay.payment.processor.dto.PaymentProcessResponse;

public class NetBankingPaymentProcessor implements PaymentProcessor {
    @Override
    public PaymentProcessResponse charge(PaymentProcessRequest request) {

        final String BANK_CODE_FAIL = "BANK_CODE_FAIL";

        String bankCode = request.methodDetails() != null ?
                request.methodDetails().get("BANK").toString() : null;

        // Simulation
        if(BANK_CODE_FAIL.equals(bankCode)) {
            return new PaymentProcessResponse.Failure("BANK_REJECTED",
                                                      "Bank rejected the transaction registration");
        }

        String processorRef = "NBK_PROCESSOR_" + RandomizerUtil.randomBase64(16);

        String redirectRef = "http://REDIRECT_BANK.com/" + processorRef;

        return new PaymentProcessResponse.Pending(processorRef);
    }
}
