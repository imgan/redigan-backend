package com.ecommerceapi.ecomerceapi.services;

import com.ecommerceapi.ecomerceapi.dto.request.Item.ItemDetailDTO;
import com.ecommerceapi.ecomerceapi.dto.request.Item.ItemFilterListDTO;
import com.ecommerceapi.ecomerceapi.dto.request.Item.ItemFormRequestDTO;
import com.ecommerceapi.ecomerceapi.dto.request.FilterListRequestDTO;
import com.ecommerceapi.ecomerceapi.dto.response.Item.ItemResponseDTO;
import com.ecommerceapi.ecomerceapi.dto.response.ResponseListDTO;
import com.ecommerceapi.ecomerceapi.model.Merchant;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Service
public interface ItemServices {

    ItemResponseDTO itemCreate(ItemFormRequestDTO itemFormRequestDTO, String token);

    ItemResponseDTO itemUpdate(ItemFormRequestDTO itemFormRequestDTO, String token);

    ItemResponseDTO itemView(ItemDetailDTO itemDetailDTO, String token);

    List<ItemResponseDTO> itemList(FilterListRequestDTO filterListRequestdto, String token);

    ItemResponseDTO itemDelete(ItemDetailDTO itemDetailDTO, String token);

    Map totalPage(FilterListRequestDTO filterListRequestdto, String token);

    Boolean itemImport(MultipartFile file, Merchant merchant);

    ResponseListDTO itemsList(ItemFilterListDTO itemFilterListDTO, String token);

    Boolean itemAttributeImport(MultipartFile file);
}
