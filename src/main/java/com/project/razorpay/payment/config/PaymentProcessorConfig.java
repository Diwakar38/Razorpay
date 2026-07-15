package com.project.razorpay.payment.config;

import com.project.razorpay.common.enums.PaymentMethod;
import com.project.razorpay.payment.processor.PaymentProcessor;
import com.project.razorpay.payment.processor.strategy.CardPaymentProcessor;
import com.project.razorpay.payment.processor.strategy.NetBankingPaymentProcessor;
import com.project.razorpay.payment.processor.strategy.UpiPaymentProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class PaymentProcessorConfig {

    private final CardPaymentProcessor cardPaymentProcessor;
    private final NetBankingPaymentProcessor netBankingPaymentProcessor;
    private final UpiPaymentProcessor upiPaymentProcessor;

    @Bean
    public Map<PaymentMethod, PaymentProcessor> paymentProcessorMap() {
        return Map.of(
                PaymentMethod.CARD, cardPaymentProcessor,
                PaymentMethod.UPI, upiPaymentProcessor,
                PaymentMethod.NET_BANKING, netBankingPaymentProcessor
        );
    }
}
