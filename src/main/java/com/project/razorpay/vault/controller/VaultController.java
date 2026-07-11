package com.project.razorpay.vault.controller;

import com.project.razorpay.vault.dto.request.TokenizeRequest;
import com.project.razorpay.vault.dto.response.TokenizeResponse;
import com.project.razorpay.vault.service.VaultService;
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
@RequestMapping("/v1/vault")
@RequiredArgsConstructor
public class VaultController {
    private final VaultService vaultService;

    private final UUID merchantId = UUID.fromString("af68ac1b-d93c-4826-abac-eb59ad950612");

    @PostMapping("/tokenize")
    public ResponseEntity<TokenizeResponse> tokenize(@Valid @RequestBody TokenizeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(vaultService.tokenize(request,merchantId));
    }
}
