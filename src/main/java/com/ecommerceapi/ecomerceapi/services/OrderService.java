package com.ecommerceapi.ecomerceapi.services;

import com.ecommerceapi.ecomerceapi.dto.request.FilterListRequestDTO;
import com.ecommerceapi.ecomerceapi.dto.request.Order.FilterListAllRequestDTO;
import com.ecommerceapi.ecomerceapi.dto.request.Order.OrderCreateDTO;
import com.ecommerceapi.ecomerceapi.dto.request.Order.UpdateIncomingDTO;
import com.ecommerceapi.ecomerceapi.dto.request.Order.UpdateOngoingDTO;
import com.ecommerceapi.ecomerceapi.dto.response.ResponseDTO;
import com.ecommerceapi.ecomerceapi.dto.response.ResponseListDTO;
import com.ecommerceapi.ecomerceapi.dto.response.ResponseListDataDTO;
import com.ecommerceapi.ecomerceapi.model.Merchant;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface OrderService {

    ResponseDTO resetOrder();

    ResponseDTO create(OrderCreateDTO orderCreateDTO);

    String generateOrderNumber(Merchant merchant);

    ResponseListDTO getIncomingOrderByMerchant(FilterListRequestDTO filterListRequestDTO,String token);

    ResponseListDTO getOngoingOrderByMerchant(FilterListRequestDTO filterListRequestDTO, String token);

    ResponseListDTO getSettleOrderByMerchant(FilterListRequestDTO filterListRequestDTO, String token);

    ResponseListDataDTO<Map> getAllOrder(FilterListAllRequestDTO filterListAllRequestDTO, String token);

    ResponseListDataDTO<Map> getAllOrderList(FilterListAllRequestDTO filterListAllRequestDTO, String token);

    ResponseDTO updateIncomingOrder(UpdateIncomingDTO updateIncomingDTO, String token);

    ResponseDTO updateIncomingRejectOrder(UpdateIncomingDTO updateIncomingDTO, String token);

    ResponseDTO updateOngoingOrder(UpdateOngoingDTO updateOngoingDTO, String token);

    ResponseDTO updateOngoingArrived(UpdateOngoingDTO updateOngoingDTO, String token);

    ResponseDTO updateOngoingRejectOrder(UpdateOngoingDTO updateOngoingDTO, String token);

    ResponseDTO trackOrder(String OrderNumber);

    ResponseDTO thankYou(String OrderNumber);
}
