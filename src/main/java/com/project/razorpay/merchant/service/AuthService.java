package com.project.razorpay.merchant.service;

import com.project.razorpay.merchant.dto.request.LoginRequest;
import com.project.razorpay.merchant.dto.request.MerchantSignupRequest;
import com.project.razorpay.merchant.dto.response.LoginResponse;
import com.project.razorpay.merchant.dto.response.MerchantResponse;
import jakarta.validation.Valid;

public interface AuthService {
    MerchantResponse signup(MerchantSignupRequest request);

    LoginResponse login(LoginRequest request);
}
