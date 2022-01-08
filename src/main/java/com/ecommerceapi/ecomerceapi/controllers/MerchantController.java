package com.ecommerceapi.ecomerceapi.controllers;

import com.ecommerceapi.ecomerceapi.dto.request.Merchant.*;
import com.ecommerceapi.ecomerceapi.dto.response.ResponseDTO;
import com.ecommerceapi.ecomerceapi.dto.response.ResponseListDTO;
import com.ecommerceapi.ecomerceapi.services.MerchantService;
import com.ecommerceapi.ecomerceapi.filters.AuthFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/merchant")
public class MerchantController extends BaseController{

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private AuthFilter authFilter;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    /** LOGIN */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<ResponseDTO> createAuthenticationToken(@Valid @RequestBody LoginRequestDTO loginReqDTO,
                HttpServletRequest request) {
        logger.info("Validation Success");
        loginReqDTO.setIpAddress(getIpAddress(request));
        ResponseDTO responseDTO = merchantService.loadUserByUsername(loginReqDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    /** REGISTER */
    @PostMapping("/")
    public ResponseEntity<ResponseDTO> profileRegister(@Valid @RequestBody MerchantRegisterDTO merchantRegisterDTO,
            HttpServletRequest request) {
        merchantRegisterDTO.setIpAddress(getIpAddress(request));
        ResponseDTO responseDTO = merchantService.profileRegister(merchantRegisterDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    /** PHOTO UPDATE */
    @PutMapping(value = "/photo", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<ResponseDTO> photoUpdate(@Valid @ModelAttribute PhotoUpdateDTO photoUpdateDTO,
                                                   HttpServletRequest request) {
        String identity = authFilter.getToken(request);
        ResponseDTO responseDTO = merchantService.photoUpdate(photoUpdateDTO, identity);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    /** UPDATE */
    @PutMapping("/update")
    public ResponseEntity<ResponseDTO> profileUpdate(@Valid @RequestBody MerchantUpdateDTO merchantUpdateDTO) {
        merchantUpdateDTO.setUserType("merchant");
        merchantUpdateDTO.setIpAddress(merchantUpdateDTO.getIpAddress());
        ResponseDTO  responseDTO  = merchantService.profileUpdate(merchantUpdateDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @PostMapping("/profile_view")
    public ResponseEntity<ResponseDTO> profileView(@Valid @RequestBody MerchantViewDTO merchantViewDTO) {
        merchantViewDTO.setUserType("merchant");
        ResponseDTO responseDTO = merchantService.profileView(merchantViewDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    /** Forgot Password */
    @PostMapping("/forgotpassword")
    public ResponseEntity<ResponseDTO> forgotPassword(@Valid @RequestBody MerchantForgotPasswordDTO merchantForgotPasswordDTO, HttpServletRequest request) {
        ResponseDTO responseDTO = merchantService.forgotPassword(merchantForgotPasswordDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    /** Confirm Password */
    @PostMapping("/confirmpassword")
    public ResponseEntity<ResponseDTO> confirmPassword(@Valid @RequestBody MerchantConfirmPasswordDTO merchantConfirmPassword, HttpServletRequest request) {
        ResponseDTO responseDTO = merchantService.confirmPassword(merchantConfirmPassword);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @GetMapping("/presetmessage")
    public ResponseEntity<ResponseDTO> presset(HttpServletRequest request) {
        String identity = authFilter.getToken(request);
        ResponseDTO responseDTO = merchantService.getPresetMessage(identity);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @PutMapping("/presetmessage")
    public ResponseEntity<ResponseDTO> pressetUpdate(HttpServletRequest request, @Valid @RequestBody PresetUpdateDTO presetUpdateDTO) {
        String identity = authFilter.getToken(request);
        ResponseDTO responseDTO = merchantService.updatePresetMessage(presetUpdateDTO, identity);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @PutMapping("/pin")
    public ResponseEntity<ResponseDTO> pinUpdate(HttpServletRequest request,
            @Valid @RequestBody UpdatePinDTO updatePinDTO) {
        String identity = authFilter.getToken(request);
        ResponseDTO responseDTO = merchantService.updatePin(updatePinDTO, identity);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @PostMapping("/dashboard")
    public ResponseEntity<ResponseDTO> dashboardView(HttpServletRequest request) {
        String identity = authFilter.getToken(request);
        ResponseDTO responseDTO = merchantService.dashboardView(identity);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @GetMapping("/list")
    public ResponseEntity<ResponseListDTO> merchantList(@Valid MerchantListDTO merchantListDTO) {
        merchantListDTO.setUserType("merchant");
        ResponseListDTO responseListDTO = merchantService.profileList(merchantListDTO);
        return new ResponseEntity<>(responseListDTO, HttpStatus.OK);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ResponseDTO> profileDelete(@Valid @RequestBody MerchantDeleteDTO merchantDeleteDTO) {
        merchantDeleteDTO.setUserType("merchant");
        ResponseDTO responseDTO = merchantService.profileDelete(merchantDeleteDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

}
