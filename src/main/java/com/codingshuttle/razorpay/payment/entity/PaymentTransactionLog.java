package com.codingshuttle.razorpay.payment.entity;

import com.codingshuttle.razorpay.common.enums.PaymentActor;
import com.codingshuttle.razorpay.common.enums.PaymentEvent;
import com.codingshuttle.razorpay.common.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "payment_transaction_log")
public class PaymentTransactionLog {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private PaymentStatus from_status;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private PaymentStatus to_status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PaymentEvent event_type;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private PaymentActor actor;

    @Column(length = 200)
    private String reason;

    @Column(nullable = false)
    private LocalDateTime occuredAt;
}
