package com.project.razorpay.payment.service;

import com.project.razorpay.payment.dto.request.CreateOrderRequest;
import com.project.razorpay.payment.dto.response.OrderResponse;
import com.project.razorpay.payment.dto.response.PaymentResponse;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    OrderResponse create(CreateOrderRequest request, UUID merchantId);

    OrderResponse getById(UUID merchantId, UUID orderId);

    OrderResponse cancel(UUID merchantId, UUID orderId);

    List<PaymentResponse> listPayments(UUID merchantId, UUID orderId);
}
