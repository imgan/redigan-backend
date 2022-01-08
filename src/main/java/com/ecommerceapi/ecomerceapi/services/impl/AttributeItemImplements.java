package com.ecommerceapi.ecomerceapi.services.impl;

import com.ecommerceapi.ecomerceapi.dto.request.AttributeItem.AttributeCreateDTO;
import com.ecommerceapi.ecomerceapi.dto.request.AttributeItem.AttributeUpdateDTO;
import com.ecommerceapi.ecomerceapi.dto.response.ResponseDTO;
import com.ecommerceapi.ecomerceapi.dto.response.ResponseListDTO;
import com.ecommerceapi.ecomerceapi.exception.ResultNotFoundException;
import com.ecommerceapi.ecomerceapi.exception.ResultServiceException;
import com.ecommerceapi.ecomerceapi.model.AttributeItem;
import com.ecommerceapi.ecomerceapi.model.Customer;
import com.ecommerceapi.ecomerceapi.model.Item;
import com.ecommerceapi.ecomerceapi.repositories.AttributeItemRepository;
import com.ecommerceapi.ecomerceapi.repositories.ItemRepository;
import com.ecommerceapi.ecomerceapi.services.AttributeItemService;
import com.ecommerceapi.ecomerceapi.util.ConstantUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class AttributeItemImplements extends BaseServices implements AttributeItemService {

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    AttributeItemRepository attributeItemRepository;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public ResponseDTO createAttributeItem(AttributeCreateDTO attributeCreateDTO) {
        ResponseDTO responseDTO = new ResponseDTO();
        Map data = new HashMap();
        AttributeItem attributeItem = new AttributeItem();
        Item item = itemRepository.findOneById(attributeCreateDTO.getItemId());
        if(item == null)
            throw new ResultNotFoundException("item not found");
        try {
            attributeItem.setItem(item);
            attributeItem.setName(attributeCreateDTO.getName());
            attributeItem.setPrice(attributeCreateDTO.getPrice());
            attributeItem.setStock(attributeCreateDTO.getQty());
            attributeItem.setCreatedAt(new Date());
            attributeItem.setEnabled(true);
            attributeItemRepository.saveAndFlush(attributeItem);
            data.put("itemId" , item.getId());
            data.put("name", attributeCreateDTO.getName());
            data.put("qty", attributeCreateDTO.getQty());
            data.put("price", attributeCreateDTO.getPrice());

            responseDTO.setCode(ConstantUtil.STATUS_SUCCESS);
            responseDTO.setInfo(ConstantUtil.MESSAGE_SUCCESS);
            responseDTO.setData(data);
            return responseDTO;
        } catch (Exception e){
            logger.error("[FATAL] " + e);
            throw new ResultServiceException(e.getMessage());
        }
    }

    @Override
    public ResponseDTO updateAttributeItem(AttributeUpdateDTO attributeUpdateTO) {
        ResponseDTO responseDTO = new ResponseDTO();
        Map data = new HashMap();
        AttributeItem attributeItem = attributeItemRepository.findOneById(attributeUpdateTO.getId());
        if(attributeItem == null )
            throw new ResultNotFoundException("attribute item not found");
        Item item = itemRepository.findOneById(attributeItem.getItem().getId());
        if(item == null)
            throw new ResultNotFoundException("Item not found");
        try {
            attributeItem.setStock(attributeUpdateTO.getQty());
            attributeItem.setItem(item);
            attributeItem.setName(attributeUpdateTO.getName());
            attributeItem.setPrice(attributeUpdateTO.getPrice());
            attributeItem.setEnabled(attributeUpdateTO.getEnabled());
            attributeItem.setUpdateAt(new Date());
            attributeItemRepository.saveAndFlush(attributeItem);
            data.put("id", attributeUpdateTO.getId());
            data.put("name", attributeUpdateTO.getName());
            data.put("price", attributeUpdateTO.getPrice());
            data.put("qty", attributeUpdateTO.getQty());

            responseDTO.setCode(ConstantUtil.STATUS_SUCCESS);
            responseDTO.setInfo(ConstantUtil.MESSAGE_SUCCESS);
            responseDTO.setData(data);
            return responseDTO;
        } catch (Exception e){
            logger.error("[FATAL] " + e);
            throw new ResultServiceException(e.getMessage());
        }
    }

    @Override
    public List<AttributeItem> viewByItemId(Long itemId) {
        List<AttributeItem> listAttributeItem = attributeItemRepository.findAllByItemIdAndEnabledAtt(itemId, true);
        return listAttributeItem;
    }
}
