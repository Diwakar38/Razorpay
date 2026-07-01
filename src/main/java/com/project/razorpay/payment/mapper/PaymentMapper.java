package com.project.razorpay.payment.mapper;

import com.project.razorpay.payment.dto.response.PaymentResponse;
import com.project.razorpay.payment.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PaymentMapper {

    @Mapping(source = "order.id", target = "orderId")
    PaymentResponse toResponse(Payment payment);

    @Mapping(source = "order.id", target = "orderId")
    List<PaymentResponse> toResponseList(List<Payment> paymentList);
}
