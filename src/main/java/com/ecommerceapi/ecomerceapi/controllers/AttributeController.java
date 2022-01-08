package com.ecommerceapi.ecomerceapi.controllers;

import com.ecommerceapi.ecomerceapi.dto.request.AttributeItem.AttributeCreateDTO;
import com.ecommerceapi.ecomerceapi.dto.request.AttributeItem.AttributeUpdateDTO;
import com.ecommerceapi.ecomerceapi.dto.request.Merchant.LoginRequestDTO;
import com.ecommerceapi.ecomerceapi.dto.response.ResponseDTO;
import com.ecommerceapi.ecomerceapi.dto.response.ResponseListAnyDTO;
import com.ecommerceapi.ecomerceapi.dto.response.ResponseListDTO;
import com.ecommerceapi.ecomerceapi.model.AttributeItem;
import com.ecommerceapi.ecomerceapi.services.AttributeItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/attribute")
public class AttributeController extends BaseController {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    AttributeItemService attributeItemService;

    /** CREATE */
    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<ResponseDTO> createAttributeItem(@Valid @RequestBody AttributeCreateDTO attributeCreateDTO,
        HttpServletRequest request) {
        ResponseDTO responseDTO = attributeItemService.createAttributeItem(attributeCreateDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    /** UPDATE */
    @RequestMapping(value = "", method = RequestMethod.PUT)
    public ResponseEntity<ResponseDTO> updateAttributeItem(@Valid @RequestBody AttributeUpdateDTO attributeUpdateDTO,
                                                           HttpServletRequest request) {
        ResponseDTO responseDTO = attributeItemService.updateAttributeItem(attributeUpdateDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

}
