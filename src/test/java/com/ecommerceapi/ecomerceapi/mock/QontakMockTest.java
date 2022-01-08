package com.ecommerceapi.ecomerceapi.mock;
import com.ecommerceapi.ecomerceapi.dto.request.Qontak.GetQontakTokenDTO;
import com.ecommerceapi.ecomerceapi.dto.response.Qontak.LoginResponseDTO;
import com.ecommerceapi.ecomerceapi.repositories.OrderDetailRepository;
import com.ecommerceapi.ecomerceapi.services.QontakService;
import com.ecommerceapi.ecomerceapi.services.impl.QontakServiceImplements;
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
public class QontakMockTest {
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
    OrderDetailRepository orderDetailRepository;

    @InjectMocks
    QontakService qontakService = new QontakServiceImplements();

    @BeforeEach
    void setupTest() {
        ReflectionTestUtils.setField(qontakService, "password", password);
        ReflectionTestUtils.setField(qontakService, "username", username);
        ReflectionTestUtils.setField(qontakService, "clientId", clientId);
        ReflectionTestUtils.setField(qontakService, "clientSecret", clientSecret);
        ReflectionTestUtils.setField(qontakService, "grantType", grantType);
        ReflectionTestUtils.setField(qontakService, "qontakBaseUrl", qontakBaseUrl);
        ReflectionTestUtils.setField(qontakService, "webClientBuilder", WebClient.builder());
    }

//    @DisplayName("Test Mock Create Attribute Item")
//    @Test
//    void testCreateAttributeItem() {
//        GetQontakTokenDTO getQontakTokenDTO = new GetQontakTokenDTO();
//        getQontakTokenDTO.setPassword(password);
//        getQontakTokenDTO.setUsername(username);
//        getQontakTokenDTO.setClient_id(clientId);
//        getQontakTokenDTO.setClient_secret(clientSecret);
//        getQontakTokenDTO.setGrant_type(grantType);
//
//        LoginResponseDTO loginResponseDTO = new LoginResponseDTO();
//        loginResponseDTO.setAccess_token("token");
//
//        when(webClientBuilder.build()).thenReturn(webClient);
//        when(webClient.post()).thenReturn(requestBodyUriSpec);
//        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
//        when(requestBodySpec.body(any(), eq(GetQontakTokenDTO.class))).thenReturn(requestHeadersSpec);
//        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
//        when(responseSpec.bodyToMono(eq(LoginResponseDTO.class))).thenReturn(Mono.just(loginResponseDTO));
//
//        LoginResponseDTO loginResponseDTOService = qontakService.getTokenQontak();
//        assertNotNull(loginResponseDTOService);
//    }
}
