package com.ecommerceapi.ecomerceapi.services;

import com.ecommerceapi.ecomerceapi.dto.request.AttributeItem.AttributeCreateDTO;
import com.ecommerceapi.ecomerceapi.dto.request.AttributeItem.AttributeUpdateDTO;
import com.ecommerceapi.ecomerceapi.dto.response.ResponseDTO;
import com.ecommerceapi.ecomerceapi.dto.response.ResponseListDTO;
import com.ecommerceapi.ecomerceapi.model.AttributeItem;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface AttributeItemService {

    ResponseDTO createAttributeItem(AttributeCreateDTO attributeCreateDTO);

    ResponseDTO updateAttributeItem(AttributeUpdateDTO attributeUpdateDTO);

    List<AttributeItem> viewByItemId(Long itemId);
}
