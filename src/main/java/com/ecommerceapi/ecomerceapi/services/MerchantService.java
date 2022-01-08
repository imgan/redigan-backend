package com.ecommerceapi.ecomerceapi.services;

import com.ecommerceapi.ecomerceapi.dto.request.Admin.MerchantCreateDTO;
import com.ecommerceapi.ecomerceapi.dto.request.Merchant.*;
import com.ecommerceapi.ecomerceapi.dto.response.ResponseDTO;
import com.ecommerceapi.ecomerceapi.dto.response.ResponseListDTO;
import com.ecommerceapi.ecomerceapi.model.Merchant;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
public interface MerchantService {
    ResponseDTO loadUserByUsername(LoginRequestDTO loginReqDTO);

    ResponseDTO updatePresetMessage(PresetUpdateDTO presetUpdateDTO, String identity);

    ResponseDTO updatePin(UpdatePinDTO updatePinDTO, String identity);

    ResponseDTO getPresetMessage(String identity);

    String generateJwtToken(Merchant merchant);

    ResponseDTO profileRegister(MerchantRegisterDTO merchantRegisterDTO);

    ResponseDTO createMerchant(MerchantCreateDTO merchantCreateDTO, String token);

    ResponseDTO profileUpdate(MerchantUpdateDTO merchantUpdateDTO);

    ResponseDTO profileView(MerchantViewDTO merchantViewDTO);

    ResponseListDTO profileList(MerchantListDTO merchantListDTO);

    ResponseDTO profileDelete(MerchantDeleteDTO merchantDeleteDTO);

    ResponseDTO dashboardView(String token);

    ResponseDTO photoUpdate(PhotoUpdateDTO photoUpdateDTO, String token);

    ResponseDTO forgotPassword(MerchantForgotPasswordDTO merchantForgotPasswordDTO);

    ResponseDTO confirmPassword(MerchantConfirmPasswordDTO merchantConfirmPasswordDTO);

    Boolean merchantImport(MultipartFile file);
}
