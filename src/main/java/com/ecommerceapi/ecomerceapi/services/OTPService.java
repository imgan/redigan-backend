package com.ecommerceapi.ecomerceapi.services;

import com.ecommerceapi.ecomerceapi.dto.request.Customer.RedisKeyDTO;
import com.ecommerceapi.ecomerceapi.dto.request.Customer.ValidateOTPDTO;
import com.ecommerceapi.ecomerceapi.dto.response.ResponseDTO;
import com.ecommerceapi.ecomerceapi.model.Customer;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface OTPService {

    ResponseDTO sendOTP(RedisKeyDTO redisKeyDTO);

    ResponseDTO getOTP(RedisKeyDTO redisKeyDTO);

    Boolean setRedisOTP(String number, String code);

    ResponseDTO validateOTP(ValidateOTPDTO validateOTPDTO);

    Map getRedisOTP(String number);

    Customer checkCustomer(String number);
}
