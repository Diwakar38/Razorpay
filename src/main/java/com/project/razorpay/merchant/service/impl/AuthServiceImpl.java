package com.project.razorpay.merchant.service.impl;

import com.project.razorpay.common.enums.MerchantStatus;
import com.project.razorpay.common.enums.UserRole;
import com.project.razorpay.common.exceptions.DuplicateResourceException;
import com.project.razorpay.common.exceptions.ResourceNotFoundException;
import com.project.razorpay.merchant.dto.request.LoginRequest;
import com.project.razorpay.merchant.dto.request.MerchantSignupRequest;
import com.project.razorpay.merchant.dto.response.LoginResponse;
import com.project.razorpay.merchant.dto.response.MerchantResponse;
import com.project.razorpay.merchant.entity.AppUser;
import com.project.razorpay.merchant.entity.Merchant;
import com.project.razorpay.merchant.mapper.MerchantMapper;
import com.project.razorpay.merchant.repository.AppUserRepository;
import com.project.razorpay.merchant.repository.MerchantRepository;
import com.project.razorpay.merchant.security.JwtUtil;
import com.project.razorpay.merchant.service.AuthService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final MerchantRepository merchantRepository;
    private final AppUserRepository appUserRepository;
    private final MerchantMapper merchantMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Override
    @Transactional
    public MerchantResponse signup(MerchantSignupRequest request) {
        if(merchantRepository.existsByEmail(request.email())){
            throw new DuplicateResourceException("DUPLICATE_MERCHANT_EMAIL","Merchant with email already exists: " + request.email());
        }

//        Merchant merchant = Merchant.builder()
//                .name(request.name())
//                .email(request.email())
//                .businessType(request.businessType())
//                .businessName(request.businessName())
//                .merchantStatus(MerchantStatus.PENDING_KYC)
//                .build();
        Merchant merchant = merchantMapper.toEntityFromSignUpRequest(request);
        merchant.setMerchantStatus(MerchantStatus.PENDING_KYC);

        merchant = merchantRepository.save(merchant);

        AppUser appUser = AppUser.builder()
                .email(request.email())
                .merchant(merchant)
                .passwordHash(passwordEncoder.encode(request.password()))
                .role(UserRole.OWNER)
                .build();

        appUser = appUserRepository.save(appUser);

//        return new MerchantResponse(merchant.getId(),
//                                    merchant.getName(),
//                                    merchant.getEmail(),
//                                    merchant.getBusinessName(),
//                                    merchant.getBusinessType(),
//                                    merchant.getMerchantStatus());
        return merchantMapper.toResponse(merchant);

    }

    @Override
    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        AppUser appUser = appUserRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResourceNotFoundException("User", request.email()));

        String token = jwtUtil.generateAccessToken(appUser.getEmail(), appUser.getMerchant().getId(), appUser.getRole().toString());

        return new LoginResponse(token);
    }
}
