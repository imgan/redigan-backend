package com.ecommerceapi.ecomerceapi.controllers;

import com.ecommerceapi.ecomerceapi.dto.request.Customer.*;
import com.ecommerceapi.ecomerceapi.dto.response.ResponseDTO;
import com.ecommerceapi.ecomerceapi.dto.response.ResponseListDTO;
import com.ecommerceapi.ecomerceapi.dto.request.Customer.FilterItemListRequestdto;
import com.ecommerceapi.ecomerceapi.dto.response.Customer.MerchantItemResponseDTO;
import com.ecommerceapi.ecomerceapi.dto.response.ResponseAnyDTO;
import com.ecommerceapi.ecomerceapi.services.CustomerService;
import com.ecommerceapi.ecomerceapi.services.OTPService;
import com.ecommerceapi.ecomerceapi.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.Map;

@RestController
@RequestMapping("/customer")
public class CustomerController extends BaseController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private OTPService otpService;
    Logger logger = LoggerFactory.getLogger(this.getClass());

    /** CUSTOMER REGISTER */
    @PostMapping("/")
    public ResponseEntity<ResponseDTO> customerRegister(@Valid @RequestBody CustomerRegisterDTO customerRegisterDTO) {
        ResponseDTO responseDTO = customerService.customerRegister(customerRegisterDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    /** CUSTOMER UPDATE */
    @PutMapping("/")
    public ResponseEntity<ResponseDTO> customerUpdate(@Valid @RequestBody CustomerUpdateDTO customerUpdateDTO,
        HttpServletRequest request) {
        ResponseDTO responseDTO = customerService.customerUpdateV2(customerUpdateDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    /** CUSTOMER VIEW */
    @PostMapping("/view")
    public ResponseEntity<ResponseDTO> profileView(@Valid @RequestBody CustomerViewDTO customerViewDTO) {
        ResponseDTO responseDTO = customerService.customerView(customerViewDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    /** List Item Merchant for Customer */
    @GetMapping("/merchant_items")
    public ResponseEntity<ResponseListDTO> merchantItemView(@Valid FilterItemListRequestdto itemListReq) {
        ResponseListDTO responseListDTO = customerService.merchantItemView(itemListReq);
        return new ResponseEntity<>(responseListDTO, HttpStatus.OK);
    }

    /** List Item Merchant for Customer Cart */
    @PostMapping("/check_cart")
    public ResponseEntity<ResponseListDTO> checkCart(@Valid @RequestBody CustomerCheckDTO customerCheckDTO) {
        ResponseListDTO responseListDTO = customerService.cartCalendarCheck(customerCheckDTO);
        return new ResponseEntity<>(responseListDTO, HttpStatus.OK);
    }

    /** List Item Merchant for Customer Cart */
    @PostMapping("/check_cartv2")
    public ResponseEntity<ResponseListDTO> checkCartv2(@Valid @RequestBody CustomerCheckDTO customerCheckDTO) {
        ResponseListDTO responseListDTO = customerService.cartCalendarCheckV2(customerCheckDTO);
        return new ResponseEntity<>(responseListDTO, HttpStatus.OK);
    }

    /** List Item Misc Merchant for Customer */
    @GetMapping("/merchant_items_misc")
    public ResponseEntity<ResponseListDTO> merchantItemViewMisc(@Valid FilterItemListRequestdto itemListReq) {
        ResponseListDTO responseListDTO = customerService.merchantItemViewMisc(itemListReq);
        return new ResponseEntity<>(responseListDTO, HttpStatus.OK);
    }

    /** Detail Item Merchant for Customer */
    @GetMapping("/item_detail/{id}")
    public ResponseEntity itemDetail(@PathVariable Long id) {
        if (!isExistingDataAndLongValue(id)) throw new ValidationException("Id must be number");
        MerchantItemResponseDTO itemData = customerService.itemDetail(id);
        ResponseAnyDTO<MerchantItemResponseDTO> responseAnyDTO = new ResponseAnyDTO<>(ConstantUtil.STATUS_SUCCESS,
                ConstantUtil.MESSAGE_SUCCESS, itemData);
        return new ResponseEntity<>(responseAnyDTO, HttpStatus.OK);
    }

    /** Detail Merchant for Customer */
    @GetMapping("/merchant_detail/{username}")
    public ResponseEntity merchantDetail(@PathVariable String username) {
        if (!isExistingDataAndStringValue(username)) throw new ValidationException("Username must be string");
        ResponseDTO responseDTO = customerService.merchantDetail(username);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    /** send OTP for Customer */
    @PostMapping("/sendOTP")
    public ResponseEntity sendOTP(@Valid @RequestBody RedisKeyDTO redisKeyDTO) {
        ResponseDTO responseDTO = otpService.sendOTP(redisKeyDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    /** Validate OTP */
    @PostMapping("/validateOTP")
    public ResponseEntity<ResponseDTO> GetOTP(@Valid @RequestBody ValidateOTPDTO validateOTPDTO,
                                                    HttpServletRequest request) {
        ResponseDTO responseDTO = otpService.validateOTP(validateOTPDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }
}
