package com.ecommerceapi.ecomerceapi.controllers;

import com.ecommerceapi.ecomerceapi.dto.request.Item.ItemDetailDTO;
import com.ecommerceapi.ecomerceapi.dto.request.Item.ItemFormRequestDTO;
import com.ecommerceapi.ecomerceapi.dto.request.FilterListRequestDTO;
import com.ecommerceapi.ecomerceapi.dto.request.Item.ItemImportDTO;
import com.ecommerceapi.ecomerceapi.dto.response.Item.ItemImportResponseDTO;
import com.ecommerceapi.ecomerceapi.dto.response.Item.ItemResponseDTO;
import com.ecommerceapi.ecomerceapi.dto.response.ResponseAnyDTO;
import com.ecommerceapi.ecomerceapi.dto.response.ResponseDTO;
import com.ecommerceapi.ecomerceapi.dto.response.ResponseListAnyDTO;
import com.ecommerceapi.ecomerceapi.filters.AuthFilter;
import com.ecommerceapi.ecomerceapi.repositories.ItemRepository;
import com.ecommerceapi.ecomerceapi.services.ItemServices;
import com.ecommerceapi.ecomerceapi.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/item")
public class ItemController extends BaseController {

    @Autowired
    private ItemServices itemServices;

    @Autowired
    private AuthFilter authFilter;

    @Autowired
    private ItemRepository itemRepository;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    /** Create Item Post Method Multipart Form Data */
    @PostMapping(value = "", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity itemCreate(@Valid @ModelAttribute ItemFormRequestDTO itemFormRequestDTO,
                HttpServletRequest request) {

        /** Check Token */
        String token = authFilter.getToken(request);
        if (token == null) throw new ValidationException("Token is empty");

        /** Create Item */
        ItemResponseDTO itemResponseDTO = itemServices.itemCreate(itemFormRequestDTO, token);

        /** Result Response */
        ResponseAnyDTO<ItemResponseDTO> responseAnyDTO = new ResponseAnyDTO<>(ConstantUtil.STATUS_SUCCESS,
                ConstantUtil.MESSAGE_SUCCESS, itemResponseDTO);

        logger.info("Item has Created");
        return new ResponseEntity<>(responseAnyDTO, HttpStatus.CREATED);
    }

    /** Update Item Put Method Multipart Form Data */
    @PutMapping(value = "", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity itemUpdate(@Valid @ModelAttribute ItemFormRequestDTO itemFormRequestDTO,
                HttpServletRequest request) {

        /** Check Token */
        String token = authFilter.getToken(request);
        if (token == null) throw new ValidationException("Token is empty");

        /** Update Item */
        ItemResponseDTO itemResponseDTO = itemServices.itemUpdate(itemFormRequestDTO, token);

        /** Result Response */
        ResponseAnyDTO<ItemResponseDTO> responseAnyDTO = new ResponseAnyDTO<>(ConstantUtil.STATUS_SUCCESS,
                ConstantUtil.MESSAGE_SUCCESS, itemResponseDTO);

        logger.info("Item has Updated");
        return new ResponseEntity<>(responseAnyDTO, HttpStatus.CREATED);
    }

    /** Detail Item Get Method */
    @GetMapping("/{id}")
    public ResponseEntity itemView(HttpServletRequest request, @PathVariable("id") Long id) {

        /** Check Token */
        String token = authFilter.getToken(request);
        if (token == null) throw new ValidationException("Token is empty");

        /** Check Id */
        if (!isExistingDataAndLongValue(id)) throw new ValidationException("Id must be number");

        /** Detail Item */
        ItemDetailDTO itemDetailDTO = new ItemDetailDTO();
        itemDetailDTO.setId(id);
        ItemResponseDTO itemResponseDTO = itemServices.itemView(itemDetailDTO, token);

        /** Result Response */
        ResponseAnyDTO<ItemResponseDTO> responseAnyDTO = new ResponseAnyDTO<>(ConstantUtil.STATUS_SUCCESS,
                ConstantUtil.MESSAGE_SUCCESS, itemResponseDTO);

        logger.info("Item Detailed");
        return new ResponseEntity<>(responseAnyDTO, HttpStatus.OK);
    }

    /** List Item Get Method */
    @GetMapping()
    public ResponseEntity itemList(@Valid FilterListRequestDTO filterListRequestdto, HttpServletRequest request) {

        /** Check Token */
        String token = authFilter.getToken(request);
        if (token == null) throw new ValidationException("Token is empty");

        /** List Item */
        List<ItemResponseDTO> itemResponseDTOList = itemServices.itemList(filterListRequestdto, token);
        Map detailList = itemServices.totalPage(filterListRequestdto, token);

        /** Result Response */
        ResponseListAnyDTO<ItemResponseDTO> responseListAnyDTO = new ResponseListAnyDTO<>(ConstantUtil.STATUS_SUCCESS,
                ConstantUtil.MESSAGE_SUCCESS, detailList, itemResponseDTOList);

        logger.info("Item Listed");
        return new ResponseEntity<>(responseListAnyDTO, HttpStatus.OK);
    }

    /** Remove Item Delete Method */
    @DeleteMapping("/{id}")
    public ResponseEntity itemDelete(HttpServletRequest request, @PathVariable Long id) {

        /** Check Token */
        String token = authFilter.getToken(request);
        if (token == null) throw new ValidationException("Token is empty");

        /** Check Id */
        if (!isExistingDataAndLongValue(id)) throw new ValidationException("Id must be number");

        /** Delete Item */
        ItemDetailDTO itemDetailDTO = new ItemDetailDTO();
        itemDetailDTO.setId(id);
        ItemResponseDTO itemResponseDTO = itemServices.itemDelete(itemDetailDTO, token);

        /** Result Response */
        ResponseAnyDTO<ItemResponseDTO> responseAnyDTO = new ResponseAnyDTO<>(ConstantUtil.STATUS_SUCCESS,
                ConstantUtil.MESSAGE_SUCCESS, itemResponseDTO);

        logger.info("Item Removed");
        return new ResponseEntity<>(responseAnyDTO, HttpStatus.OK);
    }

}
