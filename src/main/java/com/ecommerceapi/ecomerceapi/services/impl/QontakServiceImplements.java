package com.ecommerceapi.ecomerceapi.services.impl;

import com.ecommerceapi.ecomerceapi.dto.request.Order.UpdateIncomingDTO;
import com.ecommerceapi.ecomerceapi.dto.request.Qontak.GetQontakTokenDTO;
import com.ecommerceapi.ecomerceapi.dto.request.Qontak.SendBroadCastMessageDTO;
import com.ecommerceapi.ecomerceapi.dto.response.PaymentGateway.ChargeResponseDTO;
import com.ecommerceapi.ecomerceapi.dto.response.Qontak.BroadCastNewOrderDTO;
import com.ecommerceapi.ecomerceapi.dto.response.Qontak.LoginResponseDTO;
import com.ecommerceapi.ecomerceapi.dto.response.ResponseDTO;
import com.ecommerceapi.ecomerceapi.exception.ResultServiceException;
import com.ecommerceapi.ecomerceapi.model.*;
import com.ecommerceapi.ecomerceapi.repositories.OrderDetailRepository;
import com.ecommerceapi.ecomerceapi.services.QontakService;
import com.ecommerceapi.ecomerceapi.util.ConstantUtil;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.servlet.function.ServerResponse;
import reactor.core.publisher.Mono;

import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class QontakServiceImplements extends BaseServices implements QontakService {

    @Value("${qontakProperties.username}")
    private String username;

    @Value("${qontakProperties.password}")
    private String password;

    @Value("${qontakProperties.grant_type}")
    private String grantType;

    @Value("${qontakProperties.client_id}")
    private String clientId;

    @Value("${qontakProperties.client_secret}")
    private String clientSecret;

    @Value("${qontakProperties.baseUrlQontak}")
    private String qontakBaseUrl;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Autowired
    OrderDetailRepository orderDetailRepository;

    @Override
    public LoginResponseDTO getTokenQontak() {
        GetQontakTokenDTO getQontakTokenDTO = new GetQontakTokenDTO();
        getQontakTokenDTO.setPassword(password);
        getQontakTokenDTO.setUsername(username);
        getQontakTokenDTO.setClient_id(clientId);
        getQontakTokenDTO.setClient_secret(clientSecret);
        getQontakTokenDTO.setGrant_type(grantType);

        logger.info("body" + getQontakTokenDTO);

       LoginResponseDTO loginResponseDTO = webClientBuilder.build()
                .post()
                .uri(qontakBaseUrl+"/oauth/token")
                .body(Mono.just(getQontakTokenDTO), GetQontakTokenDTO.class)
                .retrieve()
                .bodyToMono(LoginResponseDTO.class)
                .block();
       logger.info("Access token "+loginResponseDTO.getAccess_token().toString());
       return loginResponseDTO;
    }

    @Override
    public Boolean sendMessageOTP(String phone, QontakConfig qontakConfig, String otp) {
        MultiValueMap bodyValues = new LinkedMultiValueMap<>();
        List<Map> bodyList = new ArrayList<>();
        Map body = new HashMap();

        HashMap codes = new HashMap();
        Map parameters = new HashMap();
        body.put("key","1");
        body.put("value","otp");
        body.put("value_text",otp);
        bodyList.add(body);

        parameters.put("body",bodyList);
        codes.put("code","id");

        SendBroadCastMessageDTO sb = new SendBroadCastMessageDTO();
        sb.setTo_name("New Customer");
        sb.setTo_number(phone);
        sb.setMessage_template_id(qontakConfig.getMessageTemplateId());
        sb.setChannel_integration_id(qontakConfig.getChannelIntegrationId());
        sb.setLanguage(codes);
        sb.setParameters("body",parameters);
        BroadCastNewOrderDTO broadCastNewOrderDTO = webClientBuilder.build()
                .post()
                .uri(qontakBaseUrl+"/api/open/v1/broadcasts/whatsapp/direct")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + qontakConfig.getToken())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(Mono.just(sb),SendBroadCastMessageDTO.class)
                .retrieve().bodyToMono(BroadCastNewOrderDTO.class)
                .block();
        return true;
    }

    @Override
    public Boolean sendOrderMessageIncoming(QontakConfig qontakConfig, Merchant merchant, Order order) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        logger.info("send wa to " + merchant.getOperationNumber());
        MultiValueMap bodyValues = new LinkedMultiValueMap<>();
        List<Map> bodyList = new ArrayList<>();
        Map body = new HashMap();
        Map body1 = new HashMap();
        Map body2 = new HashMap();
        Map body3 = new HashMap();
        Map body4 = new HashMap();
        Map body5 = new HashMap();
        Map body6 = new HashMap();

        List<Map> listOrder = orderDetailRepository.findAllByOrderNumberItemName(order.getOrderNumber());

        String itemList ="";
        Integer i = 1;
        for (Map orderDetail : listOrder){
            itemList += "- "+orderDetail.get("qty") + " "+  orderDetail.get("name")+" ";
            i++;
        }
        HashMap codes = new HashMap();
        Map parameters = new HashMap();
        body.put("key","1");
        body.put("value","storename");
        body.put("value_text",merchant.getStoreName());
        bodyList.add(body);

        body1.put("key","2");
        body1.put("value","ordernumber");
        body1.put("value_text",order.getCustomerName());
        bodyList.add(body1);

        body2.put("key","3");
        body2.put("value","name");
        body2.put("value_text",order.getAddress());
        bodyList.add(body2);

        body3.put("key","4");
        body3.put("value","amount");
        body3.put("value_text","Rp."+String.format("%,.2f", Double.valueOf(order.getAmount())));
        bodyList.add(body3);

        body4.put("key","5");
        body4.put("value","date");
        body4.put("value_text",sdf.format(order.getDeliveryDate()));
        bodyList.add(body4);

        body5.put("key","6");
        body5.put("value","itemlist");
        body5.put("value_text", itemList);
        bodyList.add(body5);

        body6.put("key","7");
        body6.put("value","link");
        body6.put("value_text",ConstantUtil.FRONTENDLINK);
        bodyList.add(body6);

        parameters.put("body",bodyList);
        codes.put("code","id");

        SendBroadCastMessageDTO sb = new SendBroadCastMessageDTO();
        sb.setTo_name(merchant.getStoreName());
        sb.setTo_number(merchant.getOperationNumber());
        sb.setMessage_template_id(qontakConfig.getMessageTemplateId());
        sb.setChannel_integration_id(qontakConfig.getChannelIntegrationId());
        sb.setLanguage(codes);
        sb.setParameters("body",parameters);
            BroadCastNewOrderDTO broadCastNewOrderDTO = webClientBuilder.build()
                    .post()
                    .uri(qontakBaseUrl+"/api/open/v1/broadcasts/whatsapp/direct")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + qontakConfig.getToken())
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body(Mono.just(sb),SendBroadCastMessageDTO.class)
                    .retrieve().bodyToMono(BroadCastNewOrderDTO.class)
                    .block();
        return true;
    }

    @Override
    public Boolean sendOrderMessageAccept(UpdateIncomingDTO incomingDTO,ChargeResponseDTO chargeResponseDTO, Merchant merchant, QontakConfig qontakConfig, Customer customer, Order order) {
        MultiValueMap bodyValues = new LinkedMultiValueMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        List<Map> bodyList = new ArrayList<>();
        Map body = new HashMap();
        Map body1 = new HashMap();
        Map body2 = new HashMap();
        Map body3 = new HashMap();
        Map body4 = new HashMap();
        Map body5 = new HashMap();
        Map body6 = new HashMap();
        Map body7 = new HashMap();
        Map body8 = new HashMap();
        Map body9 = new HashMap();
        HashMap codes = new HashMap();
        Map parameters = new HashMap();
        body.put("key","1");
        body.put("value","customername");
        body.put("value_text",customer.getCustomerName());
        bodyList.add(body);

        String bankName = ConstantUtil.BANK+" "+ConstantUtil.NOREK+" An "+ConstantUtil.PT_TRANSFER;
        body1.put("key","2");
        body1.put("value","bankname");
        body1.put("value_text",bankName);
        bodyList.add(body1);
        Integer addFee = 0;
        Integer deliveryFee = 0;

        if(isExistingDataAndIntegerValue(incomingDTO.getAdditionalFee())){
            addFee = incomingDTO.getAdditionalFee();
        } else {
            addFee = 0;
        }

        if(isExistingDataAndIntegerValue(incomingDTO.getDeliveryFee())){
            deliveryFee = incomingDTO.getDeliveryFee();
        } else {
            deliveryFee = 0;
        }

        body2.put("key","3");
        body2.put("value","amount");
        body2.put("value_text","Rp."+String.format("%,.2f",Double.valueOf(Double.valueOf(chargeResponseDTO.getUnique_amount()))));
        bodyList.add(body2);

        body3.put("key","4");
        body3.put("value","expired");
        body3.put("value_text",chargeResponseDTO.getExpired_at());
        bodyList.add(body3);

        Double pesanan = Double.valueOf(chargeResponseDTO.getUnique_amount()) - order.getAdditionalFee() - order.getDeliveryFee();
        body4.put("key","5");
        body4.put("value","pesanan");
        body4.put("value_text","Rp."+String.format("%,.2f",pesanan));
        bodyList.add(body4);

        String ongkir;
        if(deliveryFee>0){
            ongkir = "Rp."+String.format("%,.2f", Double.valueOf(deliveryFee));
        } else {
            ongkir = "-";
        }

        body5.put("key","6");
        body5.put("value","ongkir");
        body5.put("value_text",ongkir);
        bodyList.add(body5);

        String deliveryFinal;
        if(order.getAdditionalFee()>0){
             deliveryFinal = "Rp."+String.format("%,.2f", Double.valueOf(order.getAdditionalFee()));
        } else {
             deliveryFinal = "-";
        }
        body6.put("key","7");
        body6.put("value","deliveryfee");
        body6.put("value_text",deliveryFinal);
        bodyList.add(body6);
        String addInfo;
        if(isExistingDataAndStringValue(incomingDTO.getAdditionalInfo())){
            addInfo = incomingDTO.getAdditionalInfo();
        } else {
            addInfo = "none";
        }
        body7.put("key","8");
        body7.put("value","totalbiaya");
        body7.put("value_text","Rp."+String.format("%,.2f", Double.valueOf(Double.valueOf(chargeResponseDTO.getUnique_amount()))));
        bodyList.add(body7);

        body8.put("key","9");
        body8.put("value","penjelasanbiaya");
        body8.put("value_text",addInfo);
        bodyList.add(body8);

        body9.put("key","10");
        body9.put("value","link");
        body9.put("value_text",ConstantUtil.TRACKINGLINK+order.getOrderNumber());
        bodyList.add(body9);

        parameters.put("body",bodyList);
        codes.put("code","id");

        SendBroadCastMessageDTO sb = new SendBroadCastMessageDTO();
        sb.setTo_name(customer.getCustomerName());
        sb.setTo_number(customer.getPhone());
        sb.setMessage_template_id(qontakConfig.getMessageTemplateId());
        sb.setChannel_integration_id(qontakConfig.getChannelIntegrationId());
        sb.setLanguage(codes);
        sb.setParameters("body",parameters);
        BroadCastNewOrderDTO broadCastNewOrderDTO = webClientBuilder.build()
                .post()
                .uri(qontakBaseUrl+"/api/open/v1/broadcasts/whatsapp/direct")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + qontakConfig.getToken())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(Mono.just(sb),SendBroadCastMessageDTO.class)
                .retrieve().bodyToMono(BroadCastNewOrderDTO.class)
                .block();
        return true;
    }

    @Override
    public Boolean sendOrderMessageSettle(Merchant merchant,QontakConfig qontakConfig, Customer customer, Order order) {
        MultiValueMap bodyValues = new LinkedMultiValueMap<>();
        List<Map> bodyList = new ArrayList<>();
        Map body = new HashMap();
        Map body1 = new HashMap();

        HashMap codes = new HashMap();
        Map parameters = new HashMap();
        body.put("key","1");
        body.put("value","ordernumber");
        body.put("value_text",order.getOrderNumber());
        bodyList.add(body);

        body1.put("key","2");
        body1.put("value","storename");
        body1.put("value_text",merchant.getStoreName());
        bodyList.add(body1);

        parameters.put("body",bodyList);
        codes.put("code","id");

        SendBroadCastMessageDTO sb = new SendBroadCastMessageDTO();
        sb.setTo_name(customer.getCustomerName());
        sb.setTo_number(customer.getPhone());
        sb.setMessage_template_id(qontakConfig.getMessageTemplateId());
        sb.setChannel_integration_id(qontakConfig.getChannelIntegrationId());
        sb.setLanguage(codes);
        sb.setParameters("body",parameters);
        BroadCastNewOrderDTO broadCastNewOrderDTO = webClientBuilder.build()
                .post()
                .uri(qontakBaseUrl+"/api/open/v1/broadcasts/whatsapp/direct")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + qontakConfig.getToken())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(Mono.just(sb),SendBroadCastMessageDTO.class)
                .retrieve().bodyToMono(BroadCastNewOrderDTO.class)
                .block();
        return true;
    }

    @Override
    public Boolean sendOrderMessageDelivery(Merchant merchant,QontakConfig qontakConfig, Customer customer, Order order) {
        MultiValueMap bodyValues = new LinkedMultiValueMap<>();
        List<Map> bodyList = new ArrayList<>();
        Map body = new HashMap();
        Map body1 = new HashMap();
        Map body2 = new HashMap();
        Map body3 = new HashMap();

        HashMap codes = new HashMap();
        Map parameters = new HashMap();
        body.put("key","1");
        body.put("value","ordernumber");
        body.put("value_text",order.getOrderNumber());
        bodyList.add(body);

        body1.put("key","2");
        body1.put("value","storename");
        body1.put("value_text",merchant.getStoreName());
        bodyList.add(body1);
        String Link;
        if(order.getTrackingLink() == null){
             Link = "Link tidak di cantumkan";
        } else {
            Link = order.getTrackingLink();
        }
        body2.put("key","4");
        body2.put("value","customername");
        body2.put("value_text",Link);
        bodyList.add(body2);

        body3.put("key","3");
        body3.put("value","alamat");
        body3.put("value_text",customer.getAddress()+" "+customer.getCity()+" "+customer.getPostalCode());
        bodyList.add(body3);

        parameters.put("body",bodyList);
        codes.put("code","id");

        SendBroadCastMessageDTO sb = new SendBroadCastMessageDTO();
        sb.setTo_name(customer.getCustomerName());
        sb.setTo_number(customer.getPhone());
        sb.setMessage_template_id(qontakConfig.getMessageTemplateId());
        sb.setChannel_integration_id(qontakConfig.getChannelIntegrationId());
        sb.setLanguage(codes);
        sb.setParameters("body",parameters);
        BroadCastNewOrderDTO broadCastNewOrderDTO = webClientBuilder.build()
                .post()
                .uri(qontakBaseUrl+"/api/open/v1/broadcasts/whatsapp/direct")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + qontakConfig.getToken())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(Mono.just(sb),SendBroadCastMessageDTO.class)
                .retrieve().bodyToMono(BroadCastNewOrderDTO.class)
                .block();
        return true;
    }

    @Override
    public Boolean sendOrderMessageReject(QontakConfig qontakConfig, Customer customer, Order order) {
        MultiValueMap bodyValues = new LinkedMultiValueMap<>();
        List<Map> bodyList = new ArrayList<>();
        Map body = new HashMap();
        Map body1 = new HashMap();

        HashMap codes = new HashMap();
        Map parameters = new HashMap();
        body.put("key","1");
        body.put("value","customername");
        body.put("value_text",customer.getCustomerName());
        bodyList.add(body);

        body1.put("key","2");
        body1.put("value","reason");
        body1.put("value_text",order.getReason());
        bodyList.add(body1);

        parameters.put("body",bodyList);
        codes.put("code","id");

        SendBroadCastMessageDTO sb = new SendBroadCastMessageDTO();
        sb.setTo_name(customer.getCustomerName());
        sb.setTo_number(customer.getPhone());
        sb.setMessage_template_id(qontakConfig.getMessageTemplateId());
        sb.setChannel_integration_id(qontakConfig.getChannelIntegrationId());
        sb.setLanguage(codes);
        sb.setParameters("body",parameters);
        BroadCastNewOrderDTO broadCastNewOrderDTO = webClientBuilder.build()
                .post()
                .uri(qontakBaseUrl+"/api/open/v1/broadcasts/whatsapp/direct")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + qontakConfig.getToken())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(Mono.just(sb),SendBroadCastMessageDTO.class)
                .retrieve().bodyToMono(BroadCastNewOrderDTO.class)
                .block();
        return true;
    }

    @Override
    public Boolean sendOrderMessageNotification(QontakConfig qontakConfig, Merchant merchant, Order order, Customer customer) {
        MultiValueMap bodyValues = new LinkedMultiValueMap<>();
        List<Map> bodyList = new ArrayList<>();
        Map body = new HashMap();
        Map body1 = new HashMap();
        Map body2 = new HashMap();
        Map body3 = new HashMap();

        HashMap codes = new HashMap();
        Map parameters = new HashMap();
        body.put("key","1");
        body.put("value","amount");
        body.put("value_text","Rp."+String.format("%,.2f", Double.valueOf(order.getAmount()+order.getAdditionalFee()+order.getDeliveryFee())));
        bodyList.add(body);

        body1.put("key","2");
        body1.put("value","merchantname");
        body1.put("value_text",merchant.getStoreName());
        bodyList.add(body1);


        body2.put("key","3");
        body2.put("value","orderid");
        body2.put("value_text",order.getOrderNumber());
        bodyList.add(body2);

        body3.put("key","4");
        body3.put("value","link");
        body3.put("value_text",ConstantUtil.TRACKINGLINK+order.getOrderNumber());
        bodyList.add(body3);

        parameters.put("body",bodyList);
        codes.put("code","id");

        SendBroadCastMessageDTO sb = new SendBroadCastMessageDTO();
        sb.setTo_name(customer.getCustomerName());
        sb.setTo_number(customer.getPhone());
        sb.setMessage_template_id(qontakConfig.getMessageTemplateId());
        sb.setChannel_integration_id(qontakConfig.getChannelIntegrationId());
        sb.setLanguage(codes);
        sb.setParameters("body",parameters);
        BroadCastNewOrderDTO broadCastNewOrderDTO = webClientBuilder.build()
                .post()
                .uri(qontakBaseUrl+"/api/open/v1/broadcasts/whatsapp/direct")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + qontakConfig.getToken())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(Mono.just(sb),SendBroadCastMessageDTO.class)
                .retrieve().bodyToMono(BroadCastNewOrderDTO.class)
                .block();
        return true;
    }

}
