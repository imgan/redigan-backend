package com.ecommerceapi.ecomerceapi.services.impl;

import com.ecommerceapi.ecomerceapi.dto.request.PaymentGateway.ChargeRequestDTO;
import com.ecommerceapi.ecomerceapi.dto.request.PaymentGateway.NotificationRequestDTO;
import com.ecommerceapi.ecomerceapi.dto.request.Qontak.SendBroadCastMessageDTO;
import com.ecommerceapi.ecomerceapi.dto.response.PaymentGateway.ChargeResponseDTO;
import com.ecommerceapi.ecomerceapi.dto.response.Qontak.BroadCastNewOrderDTO;
import com.ecommerceapi.ecomerceapi.dto.response.ResponseDTO;
import com.ecommerceapi.ecomerceapi.exception.ResultNotFoundException;
import com.ecommerceapi.ecomerceapi.filters.AuthFilter;
import com.ecommerceapi.ecomerceapi.model.*;
import com.ecommerceapi.ecomerceapi.repositories.*;
import com.ecommerceapi.ecomerceapi.services.PaymentGatewayService;
import com.ecommerceapi.ecomerceapi.services.QontakService;
import com.ecommerceapi.ecomerceapi.util.ConstantUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class PaymentGatewayImplements implements PaymentGatewayService {

    @Autowired
    AuthFilter authFilter;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Autowired
    QontakRepository qontakRepository;

    @Autowired
    QontakService qontakService;

    @Autowired
    LogOrderRepository logOrderRepository;

    @Autowired
    MerchantRepository merchantRepository;

    @Autowired
    CustomerRepository customerRepository;

    @Value("${paymentGateway.baseUrl}")
    private String BaseUrl;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public ChargeResponseDTO charge(ChargeRequestDTO chargeRequestDTO) {

        ChargeResponseDTO cr = webClientBuilder.build()
                .post()
                .uri(BaseUrl+"/charge")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(Mono.just(chargeRequestDTO),ChargeRequestDTO.class)
                .retrieve().bodyToMono(ChargeResponseDTO.class)
                .block();

        return cr;
    }

    @Override
    public ResponseDTO notification(NotificationRequestDTO notificationRequestDTO, String token) {
        ResponseDTO responseDTO = new ResponseDTO();
        Map data = new HashMap();

        Order order = orderRepository.findOneByOrderNumber(notificationRequestDTO.getOrderNumber());
        Customer customer = customerRepository.findOneById(order.getCustomerId());
        if (customer == null) throw new ResultNotFoundException("Customer not found");

        /** Check Token Admin Exist */
        Admin adminToken = authFilter.getAdminFromToken(token);
        if (adminToken == null) throw new ResultNotFoundException("Token admin has expired");

        if(order == null)
            throw new ResultNotFoundException("order number not found");

        Merchant merchant = merchantRepository.findOneById(order.getMerchant().getId());

        if(merchant == null)
            throw new ResultNotFoundException("merchant not found");


        order.setPaid(true);
        orderRepository.save(order);

        /** SEND WHATSAPP QONTAK */
        QontakConfig qontakConfig = qontakRepository.findOneByName("ORDER_NOTIFICATION");
        Boolean isSend = qontakService.sendOrderMessageNotification(qontakConfig,merchant,order,customer);

        LogOrder logOrder = new LogOrder();
        logOrder.setData(notificationRequestDTO.toString());
        logOrder.setCreatedDate(new Date());
        logOrder.setStatus(true);
        logOrder.setOrderNumber(order.getOrderNumber());
        logOrderRepository.save(logOrder);
        data.put("order_number", order.getOrderNumber());
        responseDTO.setCode(ConstantUtil.STATUS_SUCCESS);
        responseDTO.setInfo(ConstantUtil.MESSAGE_SUCCESS);
        responseDTO.setData(data);
        return responseDTO;
    }

}
