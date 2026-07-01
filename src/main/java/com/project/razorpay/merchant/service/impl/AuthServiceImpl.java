package com.project.razorpay.merchant.service.impl;

import com.project.razorpay.common.enums.MerchantStatus;
import com.project.razorpay.common.enums.UserRole;
import com.project.razorpay.common.exceptions.DuplicateResourceException;
import com.project.razorpay.merchant.dto.request.MerchantSignupRequest;
import com.project.razorpay.merchant.dto.response.MerchantResponse;
import com.project.razorpay.merchant.entity.AppUser;
import com.project.razorpay.merchant.entity.Merchant;
import com.project.razorpay.merchant.mapper.MerchantMapper;
import com.project.razorpay.merchant.repository.AppUserRepository;
import com.project.razorpay.merchant.repository.MerchantRepository;
import com.project.razorpay.merchant.service.AuthService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final MerchantRepository merchantRepository;
    private final AppUserRepository appUserRepository;
    private final MerchantMapper merchantMapper;

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
                .passwordHash(request.password()) // TODO: Encrypt using bcrypt
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
}
