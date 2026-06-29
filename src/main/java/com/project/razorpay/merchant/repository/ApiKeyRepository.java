package com.project.razorpay.merchant.repository;

import com.project.razorpay.merchant.dto.response.ApiKeyRespose;
import com.project.razorpay.merchant.entity.ApiKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ApiKeyRepository extends JpaRepository<ApiKey, UUID> {
    List<ApiKey> findByMerchant_Id(UUID merchantId); // _ means we're going one level deep in merchant
}
