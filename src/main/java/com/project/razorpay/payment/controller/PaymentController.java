package com.project.razorpay.payment.controller;

import com.project.razorpay.payment.dto.request.PaymentInitRequest;
import com.project.razorpay.payment.dto.response.PaymentResponse;
import com.project.razorpay.payment.service.PaymentService;
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
@RequestMapping("/v1/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    private final UUID merchantId = UUID.fromString("af68ac1b-d93c-4826-abac-eb59ad950612");

    @PostMapping
    public ResponseEntity<PaymentResponse> initiate(@RequestBody @Valid PaymentInitRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(paymentService.initiate(merchantId, request));
    }
}
