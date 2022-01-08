package com.ecommerceapi.ecomerceapi.mock;

import com.ecommerceapi.ecomerceapi.dto.request.PaymentGateway.ChargeRequestDTO;
import com.ecommerceapi.ecomerceapi.dto.request.PaymentGateway.NotificationRequestDTO;
import com.ecommerceapi.ecomerceapi.dto.response.PaymentGateway.ChargeResponseDTO;
import com.ecommerceapi.ecomerceapi.dto.response.ResponseDTO;
import com.ecommerceapi.ecomerceapi.filters.AuthFilter;
import com.ecommerceapi.ecomerceapi.model.*;
import com.ecommerceapi.ecomerceapi.repositories.*;
import com.ecommerceapi.ecomerceapi.services.PaymentGatewayService;
import com.ecommerceapi.ecomerceapi.services.QontakService;
import com.ecommerceapi.ecomerceapi.services.impl.PaymentGatewayImplements;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class PaymentGatewayMockTest {
    String token;

    @Value("${paymentGateway.baseUrl}")
    private String BaseUrl;

    @Mock
    AuthFilter authFilter;

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    @Mock
    WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    WebClient.RequestBodySpec requestBodySpec;

    @Mock
    WebClient.ResponseSpec responseSpec;

    @Mock
    WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    Mono mono;

    @Mock
    OrderRepository orderRepository;

    @Mock
    QontakRepository qontakRepository;

    @Mock
    LogOrderRepository logOrderRepository;

    @Mock
    MerchantRepository merchantRepository;

    @Mock
    CustomerRepository customerRepository;

    @Mock
    QontakService qontakService;

    @InjectMocks
    PaymentGatewayService paymentGatewayService = new PaymentGatewayImplements();

    @BeforeEach
    void setupTest() {
        token = "Test Token";
    }

//    @DisplayName("Test Mock Charge")
//    @Test
//    void testCharge() {
//        ReflectionTestUtils.setField(paymentGatewayService, "BaseUrl", BaseUrl);
//        ReflectionTestUtils.setField(paymentGatewayService, "webClientBuilder", WebClient.builder());
//
//        ChargeRequestDTO chargeRequestDTO = new ChargeRequestDTO();
//        chargeRequestDTO.setTransaction_id("14");
//        chargeRequestDTO.setAmount(100);
//        chargeRequestDTO.setMerchant_id(14L);
//
//        ChargeResponseDTO chargeResponseDTO = new ChargeResponseDTO();
//        chargeResponseDTO.setTransaction_id("14");
//        chargeResponseDTO.setAmount("100");
//        chargeResponseDTO.setUnique_amount("100");
//        chargeResponseDTO.setMerchant_id("14");
//        chargeResponseDTO.setExpired_at("2000-01-01");
//
//        when(webClientBuilder.build()).thenReturn(webClient);
//        when(webClient.post()).thenReturn(requestBodyUriSpec);
//        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
//        when(requestBodySpec.header(anyString(), anyString())).thenReturn(requestBodySpec);
//        when(requestHeadersSpec.header(anyString(), anyString())).thenReturn(requestHeadersSpec);
//        when(requestBodySpec.body(any(), eq(ChargeRequestDTO.class))).thenReturn(requestHeadersSpec);
//        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
//        when(responseSpec.bodyToMono(eq(ChargeResponseDTO.class))).thenReturn(Mono.just(chargeResponseDTO));
//
//        ChargeResponseDTO employeeMono = paymentGatewayService.charge(chargeRequestDTO);
//        assertNotNull(employeeMono);
//    }

    @DisplayName("Test Mock Notification")
    @Test
    void testNotification() {
        NotificationRequestDTO notificationRequestDTO = new NotificationRequestDTO();
        notificationRequestDTO.setOrderNumber("on_test4");

        Merchant merchant = new Merchant();
        merchant.setId(14L);
        Order order = new Order();
        order.setOrderNumber(notificationRequestDTO.getOrderNumber());
        order.setCustomerId(14L);
        order.setMerchant(merchant);
        Customer customer = new Customer();

        when(orderRepository.findOneByOrderNumber(anyString())).thenReturn(order);
        when(customerRepository.findOneById(anyLong())).thenReturn(customer);
        when(authFilter.getAdminFromToken(token)).thenReturn(new Admin());
        when(merchantRepository.findOneById(anyLong())).thenReturn(merchant);
        when(orderRepository.save(order)).thenReturn(order);
        when(qontakRepository.findOneByName("ORDER_NOTIFICATION")).thenReturn(new QontakConfig());
        when(qontakService.sendOrderMessageNotification(any(QontakConfig.class), any(Merchant.class), any(Order.class),
                any(Customer.class))).thenReturn(true);
        when(logOrderRepository.save(any(LogOrder.class))).thenReturn(new LogOrder());

        ResponseDTO responseDTO = paymentGatewayService.notification(notificationRequestDTO, token);
        assertNotNull(responseDTO);
        assertNotNull(responseDTO.getData());
        assertEquals(notificationRequestDTO.getOrderNumber(), responseDTO.getData().get("order_number"));

        verify(orderRepository).findOneByOrderNumber(anyString());
        verify(customerRepository).findOneById(anyLong());
        verify(merchantRepository).findOneById(anyLong());
        verify(orderRepository).save(order);
        verify(qontakRepository).findOneByName("ORDER_NOTIFICATION");
        verify(qontakService).sendOrderMessageNotification(any(QontakConfig.class), any(Merchant.class), any(Order.class),
                any(Customer.class));
        verify(logOrderRepository).save(any(LogOrder.class));
    }
}
