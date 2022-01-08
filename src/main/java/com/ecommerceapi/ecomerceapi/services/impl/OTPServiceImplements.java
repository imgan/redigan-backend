package com.ecommerceapi.ecomerceapi.services.impl;

import com.ecommerceapi.ecomerceapi.dto.request.Customer.RedisKeyDTO;
import com.ecommerceapi.ecomerceapi.dto.request.Customer.ValidateOTPDTO;
import com.ecommerceapi.ecomerceapi.dto.response.ResponseDTO;
import com.ecommerceapi.ecomerceapi.exception.ResultNotFoundException;
import com.ecommerceapi.ecomerceapi.exception.ResultServiceException;
import com.ecommerceapi.ecomerceapi.model.Customer;
import com.ecommerceapi.ecomerceapi.model.QontakConfig;
import com.ecommerceapi.ecomerceapi.repositories.CustomerRepository;
import com.ecommerceapi.ecomerceapi.repositories.QontakRepository;
import com.ecommerceapi.ecomerceapi.services.OTPService;
import com.ecommerceapi.ecomerceapi.services.QontakService;
import com.ecommerceapi.ecomerceapi.util.ConstantUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Component
public class OTPServiceImplements extends BaseServices implements OTPService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    QontakService qontakService;

    @Autowired
    QontakRepository qontakRepository;

    @Override
    public ResponseDTO sendOTP(RedisKeyDTO redisKeyDTO) {
        ResponseDTO responseDTO = new ResponseDTO();
        Map data = new HashMap();
        Integer number = new Random().nextInt(999999);
        String keyNumber = String.format("%06d", number);
        /** CHECK CUSTOMER */
        Customer customer = customerRepository.findOneByPhone(redisKeyDTO.getPhoneNumber());
        try {
            if(customer != null) {
                /** CUSTOMER EXIST */
                if(redisKeyDTO.getPassword() == null){
                    /**CUSTOMER EXIST WITH NO PASSWORD */

                    /**CHECK TO REDIS */
                    Map redisData = getRedisOTP(redisKeyDTO.getPhoneNumber());
                    if(redisData.get("value") != null){
                        data.put("otp", redisData.get("value"));
                        data.put("isPassword", false);
                        data.put("phoneNumber", redisKeyDTO.getPhoneNumber());
                        responseDTO.setCode(ConstantUtil.STATUS_SUCCESS);
                        responseDTO.setInfo(ConstantUtil.MESSAGE_SUCCESS);
                        responseDTO.setData(data);
                        return responseDTO;
                    } else {
                        /** SET TO REDIS */
                        setRedisOTP(redisKeyDTO.getPhoneNumber(), keyNumber);
                        /** SEND WHATSAPP QONTAK */
                        QontakConfig qontakConfig = qontakRepository.findOneByName("SEND_OTP");
                        qontakService.sendMessageOTP(customer.getPhone(),qontakConfig, keyNumber);
                        data.put("otp", keyNumber);
                        data.put("isPassword", false);
                        data.put("phoneNumber", redisKeyDTO.getPhoneNumber());
                        responseDTO.setCode(ConstantUtil.STATUS_SUCCESS);
                        responseDTO.setInfo(ConstantUtil.MESSAGE_SUCCESS);
                        responseDTO.setData(data);
                        return responseDTO;
                    }

                } else {
                    /** CHECK WITH PASSWORD */
                    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                    if(!(passwordEncoder.matches(redisKeyDTO.getPassword(), customer.getPassword())))
                        throw new ResultNotFoundException("Password of customer is not valid");

                    data.put("id",customer.getId());
                    data.put("customerName", customer.getCustomerName());
                    data.put("phoneNumber", customer.getPhone());
                    data.put("email", customer.getEmail());
                    data.put("status", customer.getStatus());
                    data.put("address", customer.getAddress());
                    data.put("city", customer.getCity());
                    data.put("postalCode", customer.getPostalCode());

                    responseDTO.setCode(ConstantUtil.STATUS_SUCCESS);
                    responseDTO.setInfo(ConstantUtil.MESSAGE_SUCCESS);
                    responseDTO.setData(data);
                    return responseDTO;
                }
            } else {
                logger.info("Customer not yet register");
                /**CHECK TO REDIS */
                Map redisData = getRedisOTP(redisKeyDTO.getPhoneNumber());
                if(redisData.get("value") != null){
                    logger.info("found on redis");
                    data.put("otp", redisData.get("value"));
                    data.put("isPassword", false);
                    data.put("phoneNumber", redisKeyDTO.getPhoneNumber());
                    responseDTO.setCode(ConstantUtil.STATUS_SUCCESS);
                    responseDTO.setInfo(ConstantUtil.MESSAGE_SUCCESS);
                    responseDTO.setData(data);
                    return responseDTO;
                } else {
                    /** SET TO REDIS */
                    setRedisOTP(redisKeyDTO.getPhoneNumber(), keyNumber);
                    /** SEND WHATSAPP QONTAK */
                    QontakConfig qontakConfig = qontakRepository.findOneByName("SEND_OTP");
                    qontakService.sendMessageOTP(redisKeyDTO.getPhoneNumber(),qontakConfig, keyNumber);
                    data.put("otp", keyNumber);
                    data.put("isPassword", null);
                    data.put("phoneNumber", redisKeyDTO.getPhoneNumber());
                    responseDTO.setCode(ConstantUtil.STATUS_SUCCESS);
                    responseDTO.setInfo(ConstantUtil.MESSAGE_SUCCESS);
                    responseDTO.setData(data);
                    return responseDTO;
                }
            }
        } catch (Exception e){
            logger.error("[FATAL] " + e);
            throw new ResultServiceException(e.getMessage());
        }
    }

    @Override
    public ResponseDTO getOTP(RedisKeyDTO redisKeyDTO) {
        return null;
    }

    @Override
    public Boolean setRedisOTP(String number, String code) {
        try {
            redisTemplate.delete(number);
            redisTemplate.opsForValue().set(number, code);
            redisTemplate.expire(number, 32, TimeUnit.SECONDS);
            return true;
        } catch (Exception e){
            logger.error("[FATAL] " + e);
            throw new ResultServiceException(e.getMessage());
        }
    }

    @Override
    public Map getRedisOTP(String number) {
        Long time = redisTemplate.getExpire(number, TimeUnit.SECONDS).longValue();
        String value = redisTemplate.opsForValue().get(number);

        Map keyMap = new HashMap<>();
        keyMap.put("time", time);
        keyMap.put("value", value);
        return keyMap;
    }

    @Override
    public ResponseDTO validateOTP(ValidateOTPDTO validateOTPDTO) {
        try {
            ResponseDTO responseDTO = new ResponseDTO();
            Map data = new HashMap();
            Map redisData = getRedisOTP(validateOTPDTO.getPhoneNumber());
            if(redisData.get("value") != null ){
                if(validateOTPDTO.getOtp().equals(redisData.get("value"))){
                    data.put("code", redisData.get("value"));
                    data.put("time", redisData.get("time"));
                    data.put("phoneNumber", validateOTPDTO.getPhoneNumber());
                    responseDTO.setCode(ConstantUtil.STATUS_SUCCESS);
                    responseDTO.setInfo(ConstantUtil.MESSAGE_SUCCESS);
                    responseDTO.setData(data);
                    return responseDTO;
                }
            }
            data.put("code", null);
            data.put("time", null);
            data.put("phoneNumber", null);
            responseDTO.setCode(ConstantUtil.STATUS_ACCESS_DENIED);
            responseDTO.setInfo(ConstantUtil.MESSAGE_OTP_EXPIRED);
            responseDTO.setData(data);
            return responseDTO;
        } catch (Exception e){
            logger.error("[FATAL] " + e);
            throw new ResultServiceException(e.getMessage());
        }
    }

    @Override
    public Customer checkCustomer(String number) {
        return null;
    }

}
