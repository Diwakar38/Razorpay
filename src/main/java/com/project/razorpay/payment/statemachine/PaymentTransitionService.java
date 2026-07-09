package com.project.razorpay.payment.statemachine;

import com.project.razorpay.common.enums.PaymentActor;
import com.project.razorpay.common.enums.PaymentEvent;
import com.project.razorpay.common.enums.PaymentStatus;
import com.project.razorpay.payment.entity.Payment;
import com.project.razorpay.payment.entity.PaymentTransactionLog;
import com.project.razorpay.payment.repository.PaymentTransitionLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentTransitionService {

    private final PaymentTransitionLogRepository paymentTransitionLogRepository;
    private final PaymentStatemachine paymentStatemachine;

    public PaymentStatus apply(Payment payment, PaymentEvent event) {
        PaymentStatus next = paymentStatemachine.transition(payment.getStatus(), event);
        payment.setStatus(next);

        PaymentTransactionLog log = PaymentTransactionLog.builder()
                .payment(payment)
                .from_status(payment.getStatus())
                .event_type(event)
                .to_status(next)
                .actor(PaymentActor.SYSTEM) // TODO: Fetch merchant context to identify actor
                .occuredAt(LocalDateTime.now())
                .build();

        paymentTransitionLogRepository.save(log);
        return next;
    }
}
