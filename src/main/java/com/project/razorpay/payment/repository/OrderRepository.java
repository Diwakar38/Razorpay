package com.project.razorpay.payment.repository;

import com.project.razorpay.payment.dto.response.OrderResponse;
import com.project.razorpay.payment.entity.OrderRecord;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<OrderRecord, UUID> {
    boolean existsByMerchantIdAndReceipt(UUID merchantId, String receipt);

    Optional<OrderRecord> findByIdAndMerchantId(UUID orderId, UUID merchantId);
}
