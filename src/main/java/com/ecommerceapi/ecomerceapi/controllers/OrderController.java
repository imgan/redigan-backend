package com.ecommerceapi.ecomerceapi.controllers;

import com.ecommerceapi.ecomerceapi.dto.request.FilterListRequestDTO;
import com.ecommerceapi.ecomerceapi.dto.request.Order.FilterListAllRequestDTO;
import com.ecommerceapi.ecomerceapi.dto.request.Order.OrderCreateDTO;
import com.ecommerceapi.ecomerceapi.dto.request.Order.UpdateIncomingDTO;
import com.ecommerceapi.ecomerceapi.dto.request.Order.UpdateOngoingDTO;
import com.ecommerceapi.ecomerceapi.dto.response.Order.IncomingOrderDTO;
import com.ecommerceapi.ecomerceapi.dto.response.ResponseAnyDTO;
import com.ecommerceapi.ecomerceapi.dto.response.ResponseDTO;
import com.ecommerceapi.ecomerceapi.dto.response.ResponseListDTO;
import com.ecommerceapi.ecomerceapi.dto.response.ResponseListDataDTO;
import com.ecommerceapi.ecomerceapi.filters.AuthFilter;
import com.ecommerceapi.ecomerceapi.services.OrderService;
import com.ecommerceapi.ecomerceapi.util.ConstantUtil;
import com.ecommerceapi.ecomerceapi.util.ValidateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/order")
public class OrderController extends BaseController{

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    OrderService orderService;

    @Autowired
    private AuthFilter authFilter;

    /** RESET EXPIRED */
    @GetMapping("/expired")
    public ResponseEntity<ResponseDTO> orderExpired(){
        ResponseDTO responseDTO = orderService.resetOrder();
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    /** CREATE ORDER */
    @PostMapping("/")
    public ResponseEntity<ResponseDTO> orderCreate(@Valid @RequestBody OrderCreateDTO orderCreateDTO) {
        ResponseDTO responseDTO = orderService.create(orderCreateDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    /** GET ORDER INCOMING BY MERCHANT */
    @GetMapping("/incoming")
    public ResponseEntity<ResponseListDTO> orderIncoming(HttpServletRequest request,
            FilterListRequestDTO filterListRequestdto) {
        String token = authFilter.getToken(request);
        ResponseListDTO responseListDTO = orderService.getIncomingOrderByMerchant(filterListRequestdto, token);
        return new ResponseEntity<>(responseListDTO, HttpStatus.OK);
    }

    /** GET ORDER ONGOING BY MERCHANT */
    @GetMapping("/ongoing")
    public ResponseEntity<ResponseListDTO> orderOngoing(HttpServletRequest request, FilterListRequestDTO filterListRequestdto) {
        String token = authFilter.getToken(request);
         ResponseListDTO responseListDTO = orderService.getOngoingOrderByMerchant(filterListRequestdto, token);
        return new ResponseEntity<>(responseListDTO, HttpStatus.OK);
    }

    /** GET ORDER SETTLED BY MERCHANT */
    @GetMapping("/settled")
    public ResponseEntity<ResponseListDTO> orderSettled(HttpServletRequest request,FilterListRequestDTO filterListRequestdto ) {
        String token = authFilter.getToken(request);
        ResponseListDTO responseListDTO = orderService.getSettleOrderByMerchant(filterListRequestdto,token);
        return new ResponseEntity<>(responseListDTO, HttpStatus.OK);
    }

    /** GET ORDER ALL */
    @GetMapping("/all")
    public ResponseEntity<ResponseAnyDTO> allOrder(@Valid FilterListAllRequestDTO filterListAllRequestDTO,
                HttpServletRequest request) {
        String token = authFilter.getToken(request);
        ResponseListDataDTO<Map> responseListDataDTO = orderService.getAllOrder(filterListAllRequestDTO, token);

        /** Result Response */
        ResponseAnyDTO<ResponseListDataDTO> responseAnyDTO = new ResponseAnyDTO<>(ConstantUtil.STATUS_SUCCESS,
                ConstantUtil.MESSAGE_SUCCESS, responseListDataDTO);

        return new ResponseEntity<>(responseAnyDTO, HttpStatus.OK);
    }

    /** GET ORDER ALL LIST */
    @GetMapping("/all-list")
    public ResponseEntity<ResponseAnyDTO> allOrderList(@Valid FilterListAllRequestDTO filterListAllRequestDTO,
                HttpServletRequest request) {
        String token = authFilter.getToken(request);
        ResponseListDataDTO<Map> responseListDataDTO = orderService.getAllOrderList(filterListAllRequestDTO, token);

        /** Result Response */
        ResponseAnyDTO<ResponseListDataDTO> responseAnyDTO = new ResponseAnyDTO<>(ConstantUtil.STATUS_SUCCESS,
                ConstantUtil.MESSAGE_SUCCESS, responseListDataDTO);

        return new ResponseEntity<>(responseAnyDTO, HttpStatus.OK);
    }

    /** UPDATE ORDER INCOMING BY MERCHANT */
    @PutMapping("/incoming")
    public ResponseEntity<ResponseDTO> orderIncoming(HttpServletRequest request, @Valid @RequestBody
            UpdateIncomingDTO updateIncomingDTO) {
        String token = authFilter.getToken(request);
                ResponseDTO responseDTO = orderService.updateIncomingOrder(updateIncomingDTO, token);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    /** UPDATE REJECT ORDER INCOMING BY MERCHANT */
    @PutMapping("/incoming/reject")
    public ResponseEntity<ResponseDTO> orderIncomingReject(HttpServletRequest request, @Valid @RequestBody
            UpdateIncomingDTO updateIncomingDTO) {
        String token = authFilter.getToken(request);
        ResponseDTO responseDTO = orderService.updateIncomingRejectOrder(updateIncomingDTO, token);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    /** UPDATE REJECT ORDER INCOMING BY MERCHANT */
    @PutMapping("/ongoing/reject")
    public ResponseEntity<ResponseDTO> orderOngoingReject(HttpServletRequest request, @Valid @RequestBody
            UpdateOngoingDTO updateOngoingDTO) {
        String token = authFilter.getToken(request);
        ResponseDTO responseDTO = orderService.updateOngoingRejectOrder(updateOngoingDTO, token);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    /** UPDATE ORDER ONGOING BY MERCHANT */
    @PutMapping("/ongoing")
    public ResponseEntity<ResponseDTO> orderOngoing(HttpServletRequest request, @Valid @RequestBody UpdateOngoingDTO
                                                    updateOngoingDTO) {
        String token = authFilter.getToken(request);
        ResponseDTO responseDTO = orderService.updateOngoingOrder(updateOngoingDTO, token);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    /** UPDATE ORDER ONGOING BY MERCHANT */
    @PutMapping("/ongoing/arrived")
    public ResponseEntity<ResponseDTO> orderOngoingArrived(HttpServletRequest request, @Valid @RequestBody UpdateOngoingDTO
            updateOngoingDTO) {
        String token = authFilter.getToken(request);
        ResponseDTO responseDTO = orderService.updateOngoingArrived(updateOngoingDTO, token);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    /** TRACK ORDER */
    @GetMapping("/track/{orderNumber}")
    public ResponseEntity<ResponseDTO> trackOrderNumber(@PathVariable String orderNumber) {
            ResponseDTO responseDTO = orderService.trackOrder(orderNumber);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    /** THANK YOU PAGE */
    @GetMapping("/thankyou/{orderNumber}")
    public ResponseEntity<ResponseDTO> thankYou(@PathVariable String orderNumber) {
        ResponseDTO responseDTO = orderService.thankYou(orderNumber);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }
}
