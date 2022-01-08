package com.ecommerceapi.ecomerceapi.mock;

import com.ecommerceapi.ecomerceapi.dto.request.Customer.RedisKeyDTO;
import com.ecommerceapi.ecomerceapi.dto.request.Customer.ValidateOTPDTO;
import com.ecommerceapi.ecomerceapi.dto.response.ResponseDTO;
import com.ecommerceapi.ecomerceapi.model.Customer;
import com.ecommerceapi.ecomerceapi.model.QontakConfig;
import com.ecommerceapi.ecomerceapi.repositories.CustomerRepository;
import com.ecommerceapi.ecomerceapi.repositories.QontakRepository;
import com.ecommerceapi.ecomerceapi.services.OTPService;
import com.ecommerceapi.ecomerceapi.services.QontakService;
import com.ecommerceapi.ecomerceapi.services.impl.OTPServiceImplements;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class OTPMockTest {
    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Mock
    private ValueOperations valueOperations;

    @Mock
    CustomerRepository customerRepository;

    @Mock
    QontakService qontakService;

    @Mock
    QontakRepository qontakRepository;

    @InjectMocks
    OTPService otpService = new OTPServiceImplements();

    @BeforeEach
    void setupTest() {
        ReflectionTestUtils.setField(otpService, "redisTemplate", redisTemplate);
    }

    @DisplayName("Test Mock Send OTP")
    @Test
    void testSendOTP() {
        RedisKeyDTO redisKeyDTO = new RedisKeyDTO();
        redisKeyDTO.setPhoneNumber("62");

        Customer customer = new Customer();
        customer.setPhone(redisKeyDTO.getPhoneNumber());

        when(customerRepository.findOneByPhone(redisKeyDTO.getPhoneNumber())).thenReturn(customer);
        when(redisTemplate.getExpire(redisKeyDTO.getPhoneNumber(), TimeUnit.SECONDS)).thenReturn(4L);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(anyString())).thenReturn(null);
        when(redisTemplate.delete(anyString())).thenReturn(true);
        doNothing().when(valueOperations).set(anyString(), anyString());
        when(redisTemplate.expire(redisKeyDTO.getPhoneNumber(), 32, TimeUnit.SECONDS)).thenReturn(true);
        when(qontakRepository.findOneByName("SEND_OTP")).thenReturn(new QontakConfig());
        when(qontakService.sendMessageOTP(anyString(), any(QontakConfig.class), anyString())).thenReturn(true);

        ResponseDTO responseDTO = otpService.sendOTP(redisKeyDTO);
        assertNotNull(responseDTO);

        verify(customerRepository).findOneByPhone(redisKeyDTO.getPhoneNumber());
        verify(redisTemplate).getExpire(redisKeyDTO.getPhoneNumber(), TimeUnit.SECONDS);
        verify(valueOperations).get(anyString());
        verify(redisTemplate).delete(anyString());
        verify(redisTemplate).expire(redisKeyDTO.getPhoneNumber(), 32, TimeUnit.SECONDS);
        verify(qontakRepository).findOneByName("SEND_OTP");
        verify(qontakService).sendMessageOTP(anyString(), any(QontakConfig.class), anyString());

        redisKeyDTO.setPassword("passtest");
        customer.setPassword(redisKeyDTO.getPassword());

        when(passwordEncoder.matches(redisKeyDTO.getPassword(), customer.getPassword())).thenReturn(true);

        ResponseDTO responseDTOPass = otpService.sendOTP(redisKeyDTO);
        assertNotNull(responseDTOPass);

        when(customerRepository.findOneByPhone(redisKeyDTO.getPhoneNumber())).thenReturn(null);

        ResponseDTO responseDTONewCust = otpService.sendOTP(redisKeyDTO);
        assertNotNull(responseDTONewCust);
    }

    @DisplayName("Test Mock Validate OTP")
    @Test
    void testValidateOTP() {
        ValidateOTPDTO validateOTPDTO = new ValidateOTPDTO();
        validateOTPDTO.setPhoneNumber("62");
        validateOTPDTO.setOtp("1234");

        when(redisTemplate.getExpire(validateOTPDTO.getPhoneNumber(), TimeUnit.SECONDS)).thenReturn(4L);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(anyString())).thenReturn("1234");

        ResponseDTO responseDTO = otpService.validateOTP(validateOTPDTO);
        assertNotNull(responseDTO);
        assertNotNull(responseDTO.getData());
        assertEquals("1234", responseDTO.getData().get("code"));

        verify(redisTemplate).getExpire(validateOTPDTO.getPhoneNumber(), TimeUnit.SECONDS);
        verify(valueOperations).get(anyString());
    }
}
