package com.ecommerceapi.ecomerceapi.services;

import com.ecommerceapi.ecomerceapi.dto.request.Order.UpdateIncomingDTO;
import com.ecommerceapi.ecomerceapi.dto.request.Qontak.SendBroadCastMessageDTO;
import com.ecommerceapi.ecomerceapi.dto.response.PaymentGateway.ChargeResponseDTO;
import com.ecommerceapi.ecomerceapi.dto.response.Qontak.LoginResponseDTO;
import com.ecommerceapi.ecomerceapi.dto.response.ResponseDTO;
import com.ecommerceapi.ecomerceapi.model.Customer;
import com.ecommerceapi.ecomerceapi.model.Merchant;
import com.ecommerceapi.ecomerceapi.model.Order;
import com.ecommerceapi.ecomerceapi.model.QontakConfig;
import org.apache.commons.collections.map.MultiValueMap;
import org.springframework.stereotype.Service;

@Service
public interface QontakService {

    LoginResponseDTO getTokenQontak();

    Boolean sendMessageOTP(String phone, QontakConfig qontakConfig, String otp);

    Boolean sendOrderMessageIncoming(QontakConfig qontakConfig, Merchant merchant, Order order);

    Boolean sendOrderMessageAccept(UpdateIncomingDTO updateIncomingDTO,ChargeResponseDTO chargeResponseDTO, Merchant merchant, QontakConfig qontakConfig, Customer customer, Order order);

    Boolean sendOrderMessageSettle(Merchant merchant, QontakConfig qontakConfig, Customer customer, Order order);

    Boolean sendOrderMessageDelivery(Merchant merchant,QontakConfig qontakConfig, Customer customer, Order order);

    Boolean sendOrderMessageReject(QontakConfig qontakConfig, Customer customer, Order order);

    Boolean sendOrderMessageNotification(QontakConfig qontakConfig, Merchant merchant , Order order , Customer customer);
}
