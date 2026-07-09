package com.project.razorpay.payment.config;

import com.project.razorpay.common.enums.PaymentMethod;
import com.project.razorpay.payment.gateway.PaymentAdapter;
import com.project.razorpay.payment.gateway.adapter.CardPaymentAdapter;
import com.project.razorpay.payment.gateway.adapter.NetBankingAdapter;
import com.project.razorpay.payment.gateway.adapter.UPIAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class PaymentAdapterConfig {

    private final NetBankingAdapter netBankingAdapter;
    private final CardPaymentAdapter cardPaymentAdapter;
    private final UPIAdapter upiAdapter;

    @Bean
    public Map<PaymentMethod, PaymentAdapter> paymentAdapterMap() {
        return Map.of(
                PaymentMethod.CARD, cardPaymentAdapter,
                PaymentMethod.NET_BANKING, netBankingAdapter,
                PaymentMethod.UPI, upiAdapter
        );
    }
}
