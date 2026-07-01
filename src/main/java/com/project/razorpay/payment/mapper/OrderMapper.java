package com.project.razorpay.payment.mapper;

import com.project.razorpay.payment.dto.response.OrderResponse;
import com.project.razorpay.payment.entity.OrderRecord;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderMapper {

    OrderResponse toResponse(OrderRecord orderRecord);
}
