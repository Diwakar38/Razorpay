package com.project.razorpay.payment.repository;

import com.project.razorpay.payment.entity.PaymentTransactionLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PaymentTransitionLogRepository extends JpaRepository<PaymentTransactionLog, UUID> {
}
