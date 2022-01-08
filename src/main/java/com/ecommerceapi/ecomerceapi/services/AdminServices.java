package com.ecommerceapi.ecomerceapi.services;

import com.ecommerceapi.ecomerceapi.dto.request.Admin.AdminConfirmPasswordDTO;
import com.ecommerceapi.ecomerceapi.dto.request.Admin.AdminForgotPasswordDTO;
import com.ecommerceapi.ecomerceapi.dto.request.Admin.AdminFormRequestDTO;
import com.ecommerceapi.ecomerceapi.dto.request.Admin.AdminKeyRequestDTO;
import com.ecommerceapi.ecomerceapi.dto.request.Customer.RedisKeyDTO;
import com.ecommerceapi.ecomerceapi.dto.request.FilterListRequestDTO;
import com.ecommerceapi.ecomerceapi.dto.request.Merchant.LoginRequestDTO;
import com.ecommerceapi.ecomerceapi.dto.request.Merchant.PhotoUpdateDTO;
import com.ecommerceapi.ecomerceapi.dto.request.Merchant.UpdatePinDTO;
import com.ecommerceapi.ecomerceapi.dto.response.ResponseDTO;
import com.ecommerceapi.ecomerceapi.dto.response.ResponseListDTO;
import com.ecommerceapi.ecomerceapi.model.Admin;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface AdminServices {
    ResponseDTO loadUserByUsername(LoginRequestDTO loginReqDTO);

    String generateJwtToken(Admin admin);

    ResponseDTO officerRegister(AdminFormRequestDTO adminFormRequestDTO, String token);

    ResponseDTO officerUpdate(AdminFormRequestDTO adminFormRequestDTO, String token);

    ResponseDTO officerView(AdminKeyRequestDTO adminKeyRequestDTO, String token);

    ResponseListDTO officerList(FilterListRequestDTO filterListRequestDTO, String token);

    ResponseDTO officerDelete(AdminKeyRequestDTO adminKeyRequestDTO, String token);

    ResponseDTO photoUpdate(PhotoUpdateDTO photoUpdateDTO, String token);

    ResponseDTO forgotPassword(AdminForgotPasswordDTO adminForgotPasswordDTO);

    ResponseDTO confirmPassword(AdminConfirmPasswordDTO adminConfirmPasswordDTO);

    ResponseDTO updatePin(UpdatePinDTO updatePinDTO, String identity);

}
