package com.ecommerceapi.ecomerceapi.controllers;

import com.ecommerceapi.ecomerceapi.dto.request.Order.OrderCreateDTO;
import com.ecommerceapi.ecomerceapi.dto.response.Item.ItemResponseDTO;
import com.ecommerceapi.ecomerceapi.dto.response.Qontak.LoginResponseDTO;
import com.ecommerceapi.ecomerceapi.dto.response.ResponseAnyDTO;
import com.ecommerceapi.ecomerceapi.dto.response.ResponseDTO;
import com.ecommerceapi.ecomerceapi.services.QontakService;
import com.ecommerceapi.ecomerceapi.util.ConstantUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/qontak")
public class QontakController extends BaseController {

    @Autowired
    QontakService qontakService;

    @GetMapping("/token")
    public ResponseEntity<ResponseAnyDTO> getToken() {
        LoginResponseDTO loginResponseDTO = qontakService.getTokenQontak();
        /** Result Response */
        ResponseAnyDTO<LoginResponseDTO> responseAnyDTO = new ResponseAnyDTO<>(ConstantUtil.STATUS_SUCCESS,
                ConstantUtil.MESSAGE_SUCCESS, loginResponseDTO);
        return new ResponseEntity<>(responseAnyDTO, HttpStatus.OK);
    }


}
