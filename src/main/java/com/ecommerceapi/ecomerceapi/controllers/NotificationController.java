package com.ecommerceapi.ecomerceapi.controllers;

import com.ecommerceapi.ecomerceapi.dto.request.Merchant.LoginRequestDTO;
import com.ecommerceapi.ecomerceapi.dto.request.Merchant.MerchantUpdateDTO;
import com.ecommerceapi.ecomerceapi.dto.request.PaymentGateway.NotificationRequestDTO;
import com.ecommerceapi.ecomerceapi.dto.response.ResponseDTO;
import com.ecommerceapi.ecomerceapi.filters.AuthFilter;
import com.ecommerceapi.ecomerceapi.services.OrderService;
import com.ecommerceapi.ecomerceapi.services.PaymentGatewayService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/notification")
public class NotificationController {

    @Autowired
    private AuthFilter authFilter;

    @Autowired
    PaymentGatewayService paymentGatewayService;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    /** UPDATE */
    @PutMapping("/")
    public ResponseEntity<ResponseDTO> notification(@Valid @RequestBody NotificationRequestDTO notificationRequestDTO,
                                                    HttpServletRequest httpServletRequest) {
        String token = authFilter.getToken(httpServletRequest);
        ResponseDTO  responseDTO  = paymentGatewayService.notification(notificationRequestDTO, token);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }
}
