package com.project.razorpay.merchant.entity;

import com.project.razorpay.common.entity.BaseEntity;
import com.project.razorpay.common.enums.BusinessType;
import com.project.razorpay.common.enums.MerchantStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "merchant", indexes = {
        @Index(name = "idx_merchant_status", columnList = "merchant_status")
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Merchant extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(length = 20)
    private String contactNumber;

    @Column(length = 50)
    @Enumerated(EnumType.STRING)
    private BusinessType businessType;

    @Column(length = 100)
    private String businessName;

    @Column(length = 200)
    private String websiteUrl;

    @Column(length = 200, nullable = false)
    @Enumerated(EnumType.STRING)
    private MerchantStatus merchantStatus =  MerchantStatus.PENDING_KYC;

    @Column(length = 200)
    private String address;

    @Column(length = 20)
    private String gstId;

    @Column(length = 20)
    private String panId;

    @Column(length = 200)
    private String settlementBankAccount;

    @Column(length = 20)
    private String settlementBankIfsc;

    @Column(length = 200)
    private String settlementBankAccountHolderName;
}
