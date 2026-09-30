package com.project.razorpay.merchant.repository;

import com.project.razorpay.common.enums.OutboxStatus;
import com.project.razorpay.merchant.entity.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {

    List<OutboxEvent> findByStatusOrderByCreatedAtAsc(OutboxStatus status);
}
