package com.project.razorpay.merchant.mapper;

import com.project.razorpay.merchant.dto.response.ApiKeyCreateResponse;
import com.project.razorpay.merchant.dto.response.ApiKeyRespose;
import com.project.razorpay.merchant.entity.ApiKey;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ApiKeyMapper {

    ApiKeyCreateResponse toCreateResponse(ApiKey apiKey);

    List<ApiKeyRespose> toApiKeyResponseList(List<ApiKey> apiKeyList);

}
