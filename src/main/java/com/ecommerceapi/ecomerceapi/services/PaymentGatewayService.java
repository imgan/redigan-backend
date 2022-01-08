package com.ecommerceapi.ecomerceapi.services;

import com.ecommerceapi.ecomerceapi.dto.request.PaymentGateway.ChargeRequestDTO;
import com.ecommerceapi.ecomerceapi.dto.request.PaymentGateway.NotificationRequestDTO;
import com.ecommerceapi.ecomerceapi.dto.response.PaymentGateway.ChargeResponseDTO;
import com.ecommerceapi.ecomerceapi.dto.response.ResponseDTO;
import org.springframework.stereotype.Service;

@Service
public interface PaymentGatewayService {

    ChargeResponseDTO charge(ChargeRequestDTO chargeRequestDTO);

    ResponseDTO notification(NotificationRequestDTO notificationRequestDTO, String token);
}
