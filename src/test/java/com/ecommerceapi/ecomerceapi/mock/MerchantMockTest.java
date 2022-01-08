package com.ecommerceapi.ecomerceapi.mock;

import com.ecommerceapi.ecomerceapi.dto.params.FileNameDTO;
import com.ecommerceapi.ecomerceapi.dto.request.Admin.MerchantCreateDTO;
import com.ecommerceapi.ecomerceapi.dto.request.Merchant.*;
import com.ecommerceapi.ecomerceapi.dto.response.ResponseDTO;
import com.ecommerceapi.ecomerceapi.dto.response.ResponseListDTO;
import com.ecommerceapi.ecomerceapi.filters.AuthFilter;
import com.ecommerceapi.ecomerceapi.model.Admin;
import com.ecommerceapi.ecomerceapi.model.Item;
import com.ecommerceapi.ecomerceapi.model.ItemAvailableDay;
import com.ecommerceapi.ecomerceapi.model.Merchant;
import com.ecommerceapi.ecomerceapi.repositories.ItemRepository;
import com.ecommerceapi.ecomerceapi.repositories.MerchantRepository;
import com.ecommerceapi.ecomerceapi.repositories.OrderRepository;
import com.ecommerceapi.ecomerceapi.services.AmazonS3Service;
import com.ecommerceapi.ecomerceapi.services.EmailService;
import com.ecommerceapi.ecomerceapi.services.MerchantService;
import com.ecommerceapi.ecomerceapi.services.impl.EmailServiceImplements;
import com.ecommerceapi.ecomerceapi.services.impl.MerchantServiceImplements;
import com.ecommerceapi.ecomerceapi.util.ConstantUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class MerchantMockTest {
    String token;
    String userName;
    Merchant merchant;

    @Value("${amazonProperties.endpointUrl}")
    private String endpointUrl;

    @Value("${amazonProperties.bucketName}")
    private String bucketName;

    @Value("${app.max.sizeImage}")
    private Integer maximumSize;

    @Mock
    private AmazonS3Service amazonS3Service;

    @Mock
    private EmailService emailService = new EmailServiceImplements();

    @Mock
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Mock
    MerchantRepository merchantRepository;

    @Mock
    ItemRepository itemRepository;

    @Mock
    OrderRepository orderRepository;

    @Mock
    Merchant merchantMock = new Merchant();

    @Mock
    AuthFilter authFilter;

    @InjectMocks
    MerchantService merchantService = new MerchantServiceImplements();

    @BeforeEach
    void setupTest() {
        token = "Test token";
        userName = "merchanttest";
        merchant = new Merchant();

        ReflectionTestUtils.setField(merchantService, "maximumSize", maximumSize);
        ReflectionTestUtils.setField(merchantService, "endpointUrl", endpointUrl);
        ReflectionTestUtils.setField(merchantService, "bucketName", bucketName);
    }

    @DisplayName("Test Mock Login Merchant")
    @Test
    void testLoginMerchant() throws Exception {
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO();
        loginRequestDTO.setUsername(userName);
        loginRequestDTO.setPassword("test1234");

        merchant.setId(14L);
        merchant.setUsername(loginRequestDTO.getUsername());
        merchant.setPassword(loginRequestDTO.getPassword());
        merchant.setEmail("merchanttest@mail.com");
        merchant.setStoreName("Merchant Test");
        merchant.setPhone("62");
        merchant.setDeleted(false);

        when(merchantRepository.findOneByUsername(loginRequestDTO.getUsername())).thenReturn(merchant);
        when(merchantRepository.findOneByEmail(loginRequestDTO.getUsername())).thenReturn(merchant);
        when(passwordEncoder.matches(loginRequestDTO.getPassword(), merchant.getPassword())).thenReturn(true);

        ResponseDTO responseDTO = merchantService.loadUserByUsername(loginRequestDTO);
        assertNotNull(responseDTO);
        assertNotNull(responseDTO.getData());
        assertEquals(loginRequestDTO.getUsername(), responseDTO.getData().get("userName"));
        assertEquals(merchant.getStoreName(), responseDTO.getData().get("storeName"));

        verify(merchantRepository).findOneByUsername(loginRequestDTO.getUsername());
    }

    @DisplayName("Test Mock Update Preset Message")
    @Test
    void testUpdatePresetMessage() throws Exception {
        PresetUpdateDTO presetUpdateDTO = new PresetUpdateDTO();
        presetUpdateDTO.setPresetMessage("Test Preset");

        when(authFilter.getMerchantFromToken(token)).thenReturn(merchant);
        when(merchantRepository.save(any(Merchant.class))).thenReturn(merchantMock);

        ResponseDTO responseDTO = merchantService.updatePresetMessage(presetUpdateDTO, token);
        assertNotNull(responseDTO);
        assertEquals(presetUpdateDTO.getPresetMessage(), responseDTO.getData().get("presetMessage"));

        verify(merchantRepository).save(any(Merchant.class));
    }

    @DisplayName("Test Mock Pin Merchant")
    @Test
    void testPinMerchant() throws Exception {
        UpdatePinDTO updatePinDTO = new UpdatePinDTO();
        updatePinDTO.setPin(1234);
        updatePinDTO.setPinConfirm(1234);
        updatePinDTO.setOldPin(123);

        merchant.setPin(123);

        when(authFilter.getMerchantFromToken(token)).thenReturn(merchant);
        when(merchantRepository.save(any(Merchant.class))).thenReturn(merchantMock);

        ResponseDTO responseDTO = merchantService.updatePin(updatePinDTO, token);
        assertNotNull(responseDTO);
        assertEquals(ConstantUtil.STATUS_SUCCESS, responseDTO.getCode());

        verify(merchantRepository).save(any(Merchant.class));
    }

    @DisplayName("Test Mock View Preset Message")
    @Test
    void testViewPresetMessage() throws Exception {
        merchant.setPresetMessage("Test Preset");

        when(authFilter.getMerchantFromToken(token)).thenReturn(merchant);

        ResponseDTO responseDTO = merchantService.getPresetMessage(token);
        assertNotNull(responseDTO);
        assertEquals(merchant.getPresetMessage(), responseDTO.getData().get("presetMessage"));

        verify(authFilter).getMerchantFromToken(token);
    }

    @DisplayName("Test Mock Token Merchant")
    @Test
    void testTokenMerchant() throws Exception {
        merchant.setId(14L);
        merchant.setUsername(userName);
        merchant.setEmail("merchanttest@mail.com");
        merchant.setPhone("62");

        String tokenGenerate = merchantService.generateJwtToken(merchant);
        assertNotNull(tokenGenerate);
    }

    @DisplayName("Test Mock Register a Merchant")
    @Test
    void testRegisterMerchant() throws Exception {
        MerchantRegisterDTO merchantRegisterDTO = new MerchantRegisterDTO();
        merchantRegisterDTO.setUsername(userName);
        merchantRegisterDTO.setPassword("test1234");
        merchantRegisterDTO.setPasswordConfirm("test1234");
        merchantRegisterDTO.setStoreName("Merchant Test");
        merchantRegisterDTO.setEmail("merchanttest@mail.com");
        merchantRegisterDTO.setPhoneNumber("62");
        merchantRegisterDTO.setAddress("Indonesia");
        merchantRegisterDTO.setBankAccount("AccountTest");
        merchantRegisterDTO.setBankAccountName("AccountTestName");
        merchantRegisterDTO.setBankName("TestName");
        merchantRegisterDTO.setOperationNumber("021");

        when(merchantRepository.findAllByEmailOrUsernameOrPhone(merchantRegisterDTO.getEmail(),
                merchantRegisterDTO.getUsername(), merchantRegisterDTO.getPhoneNumber())).thenReturn(new ArrayList<>());
        when(merchantRepository.save(any(Merchant.class))).thenReturn(merchantMock);
        doNothing().when(emailService).sendRegisMessage(any(Merchant.class));

        ResponseDTO responseDTO = merchantService.profileRegister(merchantRegisterDTO);
        assertNotNull(responseDTO);
        assertEquals(merchantRegisterDTO.getUsername(), responseDTO.getData().get("username"));

        verify(merchantRepository).findAllByEmailOrUsernameOrPhone(merchantRegisterDTO.getEmail(),
                merchantRegisterDTO.getUsername(), merchantRegisterDTO.getPhoneNumber());
        verify(merchantRepository).save(any(Merchant.class));
    }

    @DisplayName("Test Mock Create a Merchant")
    @Test
    void testCreateMerchant() throws Exception {
        MerchantCreateDTO merchantCreateDTO = new MerchantCreateDTO();
        merchantCreateDTO.setUsername(userName);
        merchantCreateDTO.setPassword("test1234");
        merchantCreateDTO.setPasswordConfirm("test1234");
        merchantCreateDTO.setStoreName("Merchant Test");
        merchantCreateDTO.setEmail("merchanttest@mail.com");
        merchantCreateDTO.setPhoneNumber("62");
        merchantCreateDTO.setAddress("Indonesia");
        merchantCreateDTO.setBankAccount("AccountTest");
        merchantCreateDTO.setBankAccountName("AccountTestName");
        merchantCreateDTO.setBankName("TestName");
        merchantCreateDTO.setOperationNumber("021");

        when(authFilter.getAdminFromToken(token)).thenReturn(new Admin());
        when(merchantRepository.findAllByEmailOrUsernameOrPhone(merchantCreateDTO.getEmail(),
                merchantCreateDTO.getUsername(), merchantCreateDTO.getPhoneNumber())).thenReturn(new ArrayList<>());
        when(merchantRepository.save(any(Merchant.class))).thenReturn(merchantMock);
        doNothing().when(emailService).sendRegisMessage(any(Merchant.class));

        ResponseDTO responseDTO = merchantService.createMerchant(merchantCreateDTO, token);
        assertNotNull(responseDTO);
        assertEquals(merchantCreateDTO.getUsername(), responseDTO.getData().get("username"));

        verify(merchantRepository).findAllByEmailOrUsernameOrPhone(merchantCreateDTO.getEmail(),
                merchantCreateDTO.getUsername(), merchantCreateDTO.getPhoneNumber());
        verify(merchantRepository).save(any(Merchant.class));
    }

    @DisplayName("Test Mock Update a Merchant")
    @Test
    void testUpdateMerchant() throws Exception {
        MerchantUpdateDTO merchantUpdateDTO = new MerchantUpdateDTO();
        merchantUpdateDTO.setUsername(userName);
        merchantUpdateDTO.setPassword("test1234");
        merchantUpdateDTO.setPasswordConfirm("test1234");
        merchantUpdateDTO.setOldPassword("test1234");
        merchantUpdateDTO.setStoreName("Merchant Test");
        merchantUpdateDTO.setEmail("merchanttest@mail.com");
        merchantUpdateDTO.setPhoneNumber("62");
        merchantUpdateDTO.setAddress("Indonesia");
        merchantUpdateDTO.setBankAccount("AccountTest");
        merchantUpdateDTO.setBankAccountName("AccountTestName");
        merchantUpdateDTO.setBankName("TestName");
        merchantUpdateDTO.setOperationNumber("021");
        merchantUpdateDTO.setUserType("merchant");

        merchant.setUsername(merchantUpdateDTO.getUsername());
        merchant.setPassword(merchantUpdateDTO.getOldPassword());

        when(merchantRepository.findAllByUsername(merchantUpdateDTO.getUsername())).thenReturn(merchant);
        when(passwordEncoder.matches(merchantUpdateDTO.getPassword(), merchant.getPassword())).thenReturn(true);
        when(merchantRepository.save(any(Merchant.class))).thenReturn(merchantMock);

        ResponseDTO responseDTO = merchantService.profileUpdate(merchantUpdateDTO);
        assertNotNull(responseDTO);
        assertEquals(merchantUpdateDTO.getUsername(), responseDTO.getData().get("username"));

        verify(merchantRepository).findAllByUsername(merchantUpdateDTO.getUsername());
        verify(merchantRepository).save(any(Merchant.class));
    }

    @DisplayName("Test Mock View a Merchant")
    @Test
    void testViewMerchant() throws Exception {
        MerchantViewDTO merchantViewDTO = new MerchantViewDTO();
        merchantViewDTO.setUsername(userName);
        merchantViewDTO.setUserType("merchant");

        merchant.setUsername(merchantViewDTO.getUsername());

        when(merchantRepository.findOneByUsername(merchantViewDTO.getUsername())).thenReturn(merchant);

        ResponseDTO responseDTO = merchantService.profileView(merchantViewDTO);
        assertNotNull(responseDTO);
        assertNotNull(responseDTO.getData().get("username"));
        assertEquals(merchantViewDTO.getUsername(), responseDTO.getData().get("username"));

        verify(merchantRepository).findOneByUsername(merchantViewDTO.getUsername());
    }

    @DisplayName("Test Mock List Merchants")
    @Test
    void testListMerchant() throws Exception {
        MerchantListDTO merchantListDTO = new MerchantListDTO();
        merchantListDTO.setOffset(0);
        merchantListDTO.setLimit(2);
        merchantListDTO.setUserType("admin");
        merchantListDTO.setToken(token);

        ArrayList<Merchant> merchants = new ArrayList<>();
        for (int i = 1; i <= 2; i++) {
            merchant.setId((long) i);
            merchant.setUsername(userName+i);
            merchants.add(merchant);
        }

        when(authFilter.getAdminFromToken(merchantListDTO.getToken())).thenReturn(new Admin());
        when(merchantRepository.findAllOffsetLimit(merchantListDTO.getOffset(), merchantListDTO.getLimit(),
                merchantListDTO.getSearch())).thenReturn(merchants);
        when(merchantRepository.findAllCount(merchantListDTO.getSearch())).thenReturn(2);

        ResponseListDTO responseListDTO = merchantService.profileList(merchantListDTO);
        assertNotNull(responseListDTO);
        assertEquals(2, responseListDTO.getData().size());

        verify(merchantRepository).findAllOffsetLimit(merchantListDTO.getOffset(), merchantListDTO.getLimit(),
                merchantListDTO.getSearch());
        verify(merchantRepository).findAllCount(merchantListDTO.getSearch());
    }

    @DisplayName("Test Mock Delete a Merchant")
    @Test
    void testDeleteMerchant() throws Exception {
        MerchantDeleteDTO merchantDeleteDTO = new MerchantDeleteDTO();
        merchantDeleteDTO.setUsername(userName);
        merchantDeleteDTO.setUserType("admin");
        merchantDeleteDTO.setToken(token);

        when(authFilter.getAdminFromToken(merchantDeleteDTO.getToken())).thenReturn(new Admin());
        when(merchantRepository.findOneByUsername(merchantDeleteDTO.getUsername())).thenReturn(merchant);
        when(merchantRepository.save(any(Merchant.class))).thenReturn(merchantMock);

        ResponseDTO responseDTO = merchantService.profileDelete(merchantDeleteDTO);
        assertNotNull(responseDTO);
        assertEquals(ConstantUtil.STATUS_SUCCESS, responseDTO.getCode());

        verify(merchantRepository).findOneByUsername(merchantDeleteDTO.getUsername());
        verify(merchantRepository).save(any(Merchant.class));
    }

    @DisplayName("Test Mock Dashboard Merchant")
    @Test
    void testDashboardMerchant() throws Exception {
        merchant.setId(14L);

        when(authFilter.getMerchantFromToken(token)).thenReturn(merchant);
        when(itemRepository.countAllItemByMerchant(merchant.getId())).thenReturn(2);
        when(orderRepository.countAllOrderIncomingByMerchant(merchant.getId())).thenReturn(2);
        when(orderRepository.countAllOrderOngoingByMerchant(merchant.getId())).thenReturn(2);
        when(orderRepository.countAllOrderSettledByMerchant(merchant.getId())).thenReturn(2);
        when(orderRepository.findPrevRevenueByMerchant(merchant.getId())).thenReturn(2);
        when(orderRepository.findThisRevenueByMerchant(merchant.getId())).thenReturn(2);

        ResponseDTO responseDTO = merchantService.dashboardView(token);
        assertNotNull(responseDTO);
        Map<String, String> order = (Map<String, String>) responseDTO.getData().get("order");
        assertEquals(2, order.get("incommingOrder"));
        assertEquals(2, order.get("ongoing"));
        assertEquals(2, order.get("settled"));
        Map<String, String> revenue = (Map<String, String>) responseDTO.getData().get("revenue");
        assertEquals(2, revenue.get("totalRevenue"));
        assertEquals(2, revenue.get("thisMonth"));
        assertEquals(2, responseDTO.getData().get("totalItem"));

        verify(itemRepository).countAllItemByMerchant(merchant.getId());
        verify(orderRepository).countAllOrderIncomingByMerchant(merchant.getId());
        verify(orderRepository).countAllOrderOngoingByMerchant(merchant.getId());
        verify(orderRepository).countAllOrderSettledByMerchant(merchant.getId());
        verify(orderRepository).findPrevRevenueByMerchant(merchant.getId());
        verify(orderRepository).findThisRevenueByMerchant(merchant.getId());
    }

    @DisplayName("Test Mock Photo Merchant")
    @Test
    void testPhotoMerchant() throws Exception {
        String filename = "test.jpg";
        PhotoUpdateDTO photoUpdateDTO = new PhotoUpdateDTO();
        MultipartFile image = new MockMultipartFile("image", filename, MediaType.IMAGE_JPEG_VALUE,
                new byte[128]);
        photoUpdateDTO.setImage(image);

        merchant.setId(14L);
        merchant.setUsername(userName);
        merchant.setPicture(filename);

        when(authFilter.getMerchantFromToken(token)).thenReturn(merchant);
        when(amazonS3Service.deleteFileFromS3Bucket(endpointUrl + "/" + bucketName + "/" + filename)).thenReturn("");
        when(amazonS3Service.uploadFile(any(FileNameDTO.class))).thenReturn(endpointUrl + "/" + bucketName + "/" + filename);
        when(merchantRepository.save(any(Merchant.class))).thenReturn(merchantMock);

        ResponseDTO responseDTO = merchantService.photoUpdate(photoUpdateDTO, token);
        assertNotNull(responseDTO);
        assertEquals(ConstantUtil.STATUS_SUCCESS, responseDTO.getCode());

        verify(amazonS3Service).deleteFileFromS3Bucket(endpointUrl + "/" + bucketName + "/" + filename);
        verify(amazonS3Service).uploadFile(any(FileNameDTO.class));
        verify(merchantRepository).save(any(Merchant.class));
    }

    @DisplayName("Test Mock Forgot Password Merchant")
    @Test
    void testForgotPasswordMerchant() throws Exception {
        MerchantForgotPasswordDTO merchantForgotPasswordDTO = new MerchantForgotPasswordDTO();
        merchantForgotPasswordDTO.setEmail("admintest@mail.com");

        merchantMock.setEmail(merchantForgotPasswordDTO.getEmail());

        when(merchantRepository.findOneByEmail(merchantForgotPasswordDTO.getEmail())).thenReturn(merchant);
        when(merchantRepository.saveAndFlush(any(Merchant.class))).thenReturn(merchantMock);
        doNothing().when(emailService).sendForgotMessage(any(Merchant.class));

        ResponseDTO responseDTO = merchantService.forgotPassword(merchantForgotPasswordDTO);
        assertNotNull(responseDTO);
        assertEquals(ConstantUtil.STATUS_SUCCESS, responseDTO.getCode());

        verify(merchantRepository).findOneByEmail(merchantForgotPasswordDTO.getEmail());
        verify(merchantRepository).saveAndFlush(any(Merchant.class));
    }

    @DisplayName("Test Mock Confirm Password Merchant")
    @Test
    void testConfirmPasswordMerchant() throws Exception {
        MerchantConfirmPasswordDTO merchantConfirmPasswordDTO = new MerchantConfirmPasswordDTO();
        merchantConfirmPasswordDTO.setPassword("test1234");
        merchantConfirmPasswordDTO.setPasswordConfirm("test1234");
        merchantConfirmPasswordDTO.setToken(token);

        merchant.setToken_update(token);

        when(authFilter.getMerchantFromToken(merchantConfirmPasswordDTO.getToken())).thenReturn(merchant);
        when(merchantRepository.save(any(Merchant.class))).thenReturn(merchantMock);

        ResponseDTO responseDTO = merchantService.confirmPassword(merchantConfirmPasswordDTO);
        assertNotNull(responseDTO);
        assertEquals(ConstantUtil.STATUS_SUCCESS, responseDTO.getCode());

        verify(merchantRepository).save(any(Merchant.class));
    }

    @DisplayName("Test Mock Import Merchant")
    @Test
    void testImportItem() throws Exception {
        List<Merchant> merchants = new ArrayList<>();
        String excelfile = "MerchantTestImport.xlsx";

        for (int i = 1; i <= 2; i++) {
            Merchant merchantList = new Merchant();
            merchantList.setId((long) i);
            merchantList.setStoreName("TestMerchant"+i);
            merchantList.setAddress("Address"+i);
            merchants.add(merchantList);
        }

        Path path = Paths.get("src/test/resources/files", excelfile);
        InputStream is = Files.newInputStream(path);
        MultipartFile excel = new MockMultipartFile("file", excelfile, MediaType.MULTIPART_FORM_DATA_VALUE, is);

        when(merchantRepository.findAllByEmailOrUsernameOrPhone(anyString(), anyString(), anyString())).thenReturn(merchants);
        when(merchantRepository.save(any(Merchant.class))).thenReturn(merchant);

        Boolean merchantImportResponseDTO = merchantService.merchantImport(excel);
        assertNotNull(merchantImportResponseDTO);
        assertEquals(true, merchantImportResponseDTO);

        verify(merchantRepository).findAllByEmailOrUsernameOrPhone(anyString(), anyString(), anyString());
    }
}
