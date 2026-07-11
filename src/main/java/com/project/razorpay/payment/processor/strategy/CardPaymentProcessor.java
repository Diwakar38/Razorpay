package com.project.razorpay.payment.processor.strategy;

import com.project.razorpay.common.util.RandomizerUtil;
import com.project.razorpay.payment.processor.PaymentProcessor;
import com.project.razorpay.payment.processor.dto.PaymentProcessRequest;
import com.project.razorpay.payment.processor.dto.PaymentProcessResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class CardPaymentProcessor implements PaymentProcessor {

    public static final String PAN_CARD_DECLINED = "4000000000000002";
    public static final String PAN_CARD_EXPIRED = "4000000000000069";

    @Override
    public PaymentProcessResponse charge(PaymentProcessRequest request) {
        String pan = request.pan();
        if(PAN_CARD_DECLINED.equals(pan))  {
            log.warn("Card Declined");
            return new PaymentProcessResponse.Failure("CARD_DECLINED", "Card declined by bank");
        }
        if(PAN_CARD_EXPIRED.equals(pan))  {
            log.warn("Card Expired");
            return new PaymentProcessResponse.Failure("CARD_EXPIRED", "Card is expired");
        }

        String processorRef = "CARD_PROCESSOR_" + RandomizerUtil.randomBase64(16);

        return new PaymentProcessResponse.Pending(processorRef);
    }
}
