package com.project.razorpay.payment.controller;

import com.project.razorpay.payment.dto.request.CreateOrderRequest;
import com.project.razorpay.payment.dto.response.OrderResponse;
import com.project.razorpay.payment.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    UUID merchantId = UUID.fromString("e23bac60-26dc-49b0-a7f1-a9fb4eea3ca8"); // TODO: Replace it with merchant context

    @PostMapping
    public ResponseEntity<OrderResponse> create(@RequestBody @Valid CreateOrderRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.create(request, merchantId));
    }

}
