package com.project.razorpay.payment.service;

import com.project.razorpay.payment.dto.request.CreateOrderRequest;
import com.project.razorpay.payment.dto.response.OrderResponse;

import java.util.UUID;

public interface OrderService {
    OrderResponse create(CreateOrderRequest request, UUID merchantId);
}
