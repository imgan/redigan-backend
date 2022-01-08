package com.ecommerceapi.ecomerceapi.services;

import com.ecommerceapi.ecomerceapi.dto.request.Customer.*;
import com.ecommerceapi.ecomerceapi.dto.response.ResponseDTO;
import com.ecommerceapi.ecomerceapi.dto.response.ResponseListDTO;
import com.ecommerceapi.ecomerceapi.dto.request.Customer.FilterItemListRequestdto;
import com.ecommerceapi.ecomerceapi.dto.response.Customer.MerchantDetailResponseDTO;
import com.ecommerceapi.ecomerceapi.dto.response.Customer.MerchantItemResponseDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CustomerService {
    ResponseDTO customerRegister(CustomerRegisterDTO customerRegisterDTO);

    ResponseListDTO cartCalendarCheck(CustomerCheckDTO customerCheckDTO);

    ResponseListDTO cartCalendarCheckV2(CustomerCheckDTO customerCheckDTO);

    ResponseDTO customerUpdate(CustomerUpdateDTO customerUpdateDTO);

    ResponseDTO customerUpdateV2(CustomerUpdateDTO customerUpdateDTO);

    ResponseDTO customerView(CustomerViewDTO customerViewDTO);

    ResponseListDTO customerList(CustomerListDTO customerListDTO);

    ResponseDTO customerDelete(CustomerDeleteDTO customerDeleteDTO);

    ResponseListDTO merchantItemView(FilterItemListRequestdto itemListReq);

    ResponseListDTO merchantItemViewMisc(FilterItemListRequestdto itemListReq);
    /** Detail Item Merchant for Customer */
    MerchantItemResponseDTO itemDetail(Long id);

    /** Detail Merchant for Customer */
    ResponseDTO merchantDetail(String username);

}