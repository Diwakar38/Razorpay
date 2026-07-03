package com.project.razorpay.payment.config;

import com.project.razorpay.common.enums.PaymentMethod;
import com.project.razorpay.payment.gateway.PaymentAdapter;
import com.project.razorpay.payment.gateway.adapter.CardPaymentAdapter;
import com.project.razorpay.payment.gateway.adapter.NetBankingAdapter;
import com.project.razorpay.payment.gateway.adapter.UPIAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class PaymentAdapterConfig {

    @Bean
    public Map<PaymentMethod, PaymentAdapter> paymentAdapterMap() {
        return Map.of(
                PaymentMethod.CARD, new CardPaymentAdapter(),
                PaymentMethod.NET_BANKING, new NetBankingAdapter(),
                PaymentMethod.UPI, new UPIAdapter()
        );
    }
}
