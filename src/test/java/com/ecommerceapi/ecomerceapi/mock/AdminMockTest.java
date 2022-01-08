package com.ecommerceapi.ecomerceapi.mock;

import com.ecommerceapi.ecomerceapi.dto.params.FileNameDTO;
import com.ecommerceapi.ecomerceapi.dto.request.Admin.AdminConfirmPasswordDTO;
import com.ecommerceapi.ecomerceapi.dto.request.Admin.AdminForgotPasswordDTO;
import com.ecommerceapi.ecomerceapi.dto.request.Admin.AdminFormRequestDTO;
import com.ecommerceapi.ecomerceapi.dto.request.Admin.AdminKeyRequestDTO;
import com.ecommerceapi.ecomerceapi.dto.request.FilterListRequestDTO;
import com.ecommerceapi.ecomerceapi.dto.request.Merchant.LoginRequestDTO;
import com.ecommerceapi.ecomerceapi.dto.request.Merchant.PhotoUpdateDTO;
import com.ecommerceapi.ecomerceapi.dto.request.Merchant.UpdatePinDTO;
import com.ecommerceapi.ecomerceapi.dto.response.ResponseDTO;
import com.ecommerceapi.ecomerceapi.dto.response.ResponseListDTO;
import com.ecommerceapi.ecomerceapi.filters.AuthFilter;
import com.ecommerceapi.ecomerceapi.model.Admin;
import com.ecommerceapi.ecomerceapi.model.Merchant;
import com.ecommerceapi.ecomerceapi.repositories.AdminRepository;
import com.ecommerceapi.ecomerceapi.services.AdminServices;
import com.ecommerceapi.ecomerceapi.services.AmazonS3Service;
import com.ecommerceapi.ecomerceapi.services.EmailService;
import com.ecommerceapi.ecomerceapi.services.impl.AdminServiceImplements;
import com.ecommerceapi.ecomerceapi.services.impl.EmailServiceImplements;
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

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class AdminMockTest {
    String token;
    String userName;
    Admin admin;

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
    AdminRepository adminRepository;

    @Mock
    Admin adminMock = new Admin();

    @Mock
    AuthFilter authFilter;

    @InjectMocks
    AdminServices adminServices = new AdminServiceImplements();

    @BeforeEach
    void setupTest() {
        token = "Test token";
        userName = "admintest";
        admin = new Admin();

        ReflectionTestUtils.setField(adminServices, "maximumSize", maximumSize);
        ReflectionTestUtils.setField(adminServices, "endpointUrl", endpointUrl);
        ReflectionTestUtils.setField(adminServices, "bucketName", bucketName);
    }

    @DisplayName("Test Mock Login Admin")
    @Test
    void testLoginAdmin() throws Exception {
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO();
        loginRequestDTO.setUsername(userName);
        loginRequestDTO.setPassword("test1234");

        admin.setId(14L);
        admin.setUsername(loginRequestDTO.getUsername());
        admin.setPassword(loginRequestDTO.getPassword());
        admin.setEmail("admintest@mail.com");
        admin.setOfficerName("Admin Test");
        admin.setPhone("62");

        when(adminRepository.findOneByUsername(loginRequestDTO.getUsername())).thenReturn(admin);
        when(passwordEncoder.matches(loginRequestDTO.getPassword(), admin.getPassword())).thenReturn(true);

        ResponseDTO responseDTO = adminServices.loadUserByUsername(loginRequestDTO);
        assertNotNull(responseDTO);
        assertNotNull(responseDTO.getData());
        assertEquals(loginRequestDTO.getUsername(), responseDTO.getData().get("userName"));
        assertEquals(admin.getOfficerName(), responseDTO.getData().get("adminName"));

        verify(adminRepository).findOneByUsername(loginRequestDTO.getUsername());
    }

    @DisplayName("Test Mock Token Admin")
    @Test
    void testTokenAdmin() throws Exception {
        admin.setId(14L);
        admin.setUsername(userName);
        admin.setEmail("admintest@mail.com");
        admin.setPhone("62");

        String tokenGenerate = adminServices.generateJwtToken(admin);
        assertNotNull(tokenGenerate);
    }

    @DisplayName("Test Mock Create a Admin")
    @Test
    void testCreateAdmin() throws Exception {
        AdminFormRequestDTO adminFormRequestDTO = new AdminFormRequestDTO();
        adminFormRequestDTO.setUsername(userName);
        adminFormRequestDTO.setPassword("test1234");
        adminFormRequestDTO.setPasswordConfirm("test1234");
        adminFormRequestDTO.setEmail("admintest@mail.com");
        adminFormRequestDTO.setOfficerName("Admin Test");
        adminFormRequestDTO.setPhone("62");
        adminFormRequestDTO.setRoleId(14);

        admin.setUsername(userName);

        when(authFilter.getAdminFromToken(token)).thenReturn(admin);
        when(adminRepository.findAllByEmailOrUsernameOrPhone(adminFormRequestDTO.getEmail(),
                adminFormRequestDTO.getUsername(), adminFormRequestDTO.getPhone())).thenReturn(null);
        when(adminRepository.save(any(Admin.class))).thenReturn(adminMock);
        doNothing().when(emailService).sendRegisMessageAdmin(any(Admin.class));

        ResponseDTO responseDTO = adminServices.officerRegister(adminFormRequestDTO, token);
        assertNotNull(responseDTO);
        assertNotNull(responseDTO.getData().get("username"));
        assertEquals(adminFormRequestDTO.getUsername(), responseDTO.getData().get("username"));

        verify(adminRepository).findAllByEmailOrUsernameOrPhone(adminFormRequestDTO.getEmail(),
                adminFormRequestDTO.getUsername(), adminFormRequestDTO.getPhone());
        verify(adminRepository).save(any(Admin.class));
    }

    @DisplayName("Test Mock Update a Admin")
    @Test
    void testUpdateAdmin() throws Exception {
        AdminFormRequestDTO adminFormRequestDTO = new AdminFormRequestDTO();
        adminFormRequestDTO.setUsername(userName);
        adminFormRequestDTO.setPassword("test1234");
        adminFormRequestDTO.setPasswordConfirm("test1234");
        adminFormRequestDTO.setEmail("admintest@mail.com");
        adminFormRequestDTO.setOfficerName("Admin Test");
        adminFormRequestDTO.setPhone("62");
        adminFormRequestDTO.setRoleId(14);

        admin.setUsername(userName);

        when(authFilter.getAdminFromToken(token)).thenReturn(admin);
        when(adminRepository.findOneByUsername(adminFormRequestDTO.getUsername())).thenReturn(admin);
        when(adminRepository.save(any(Admin.class))).thenReturn(adminMock);

        ResponseDTO responseDTO = adminServices.officerUpdate(adminFormRequestDTO, token);
        assertNotNull(responseDTO);
        assertNotNull(responseDTO.getData().get("username"));
        assertEquals(adminFormRequestDTO.getUsername(), responseDTO.getData().get("username"));

        verify(adminRepository).findOneByUsername(adminFormRequestDTO.getUsername());
        verify(adminRepository).save(any(Admin.class));
    }

    @DisplayName("Test Mock View a Admin")
    @Test
    void testViewAdmin() throws Exception {
        AdminKeyRequestDTO adminKeyRequestDTO = new AdminKeyRequestDTO();
        adminKeyRequestDTO.setUsername(userName);

        admin.setUsername(userName);

        when(authFilter.getAdminFromToken(token)).thenReturn(admin);
        when(adminRepository.findOneByUsername(adminKeyRequestDTO.getUsername())).thenReturn(admin);

        ResponseDTO responseDTO = adminServices.officerView(adminKeyRequestDTO, token);
        assertNotNull(responseDTO);
        assertNotNull(responseDTO.getData().get("username"));
        assertEquals(adminKeyRequestDTO.getUsername(), responseDTO.getData().get("username"));

        verify(adminRepository).findOneByUsername(adminKeyRequestDTO.getUsername());
    }

    @DisplayName("Test Mock List Admins")
    @Test
    void testListAdmin() throws Exception {
        FilterListRequestDTO filterListRequestDTO = new FilterListRequestDTO();
        filterListRequestDTO.setSearch("Test");
        filterListRequestDTO.setLimit(2);
        filterListRequestDTO.setOffset(0);

        ArrayList<Admin> admins = new ArrayList<>();
        for (int i = 1; i <= 2; i++) {
            admin.setId((long) i);
            admin.setUsername(userName+i);
            admins.add(admin);
        }

        when(authFilter.getAdminFromToken(token)).thenReturn(admin);
        when(adminRepository.findAllOffsetLimit(filterListRequestDTO.getSearch(),
                filterListRequestDTO.getLimit(), filterListRequestDTO.getOffset())).thenReturn(admins);
        when(adminRepository.findAllOffsetLimitCount(filterListRequestDTO.getSearch())).thenReturn(2);

        ResponseListDTO responseListDTO = adminServices.officerList(filterListRequestDTO, token);
        assertNotNull(responseListDTO);
        assertEquals(2, responseListDTO.getData().size());

        verify(adminRepository).findAllOffsetLimit(filterListRequestDTO.getSearch(), filterListRequestDTO.getLimit(),
                filterListRequestDTO.getOffset());
        verify(adminRepository).findAllOffsetLimitCount(filterListRequestDTO.getSearch());
    }

    @DisplayName("Test Mock Delete a Admin")
    @Test
    void testDeleteAdmin() throws Exception {
        AdminKeyRequestDTO adminKeyRequestDTO = new AdminKeyRequestDTO();
        adminKeyRequestDTO.setUsername(userName);

        admin.setUsername(userName);

        when(authFilter.getAdminFromToken(token)).thenReturn(admin);
        when(adminRepository.findOneByUsername(adminKeyRequestDTO.getUsername())).thenReturn(admin);
        when(adminRepository.save(any(Admin.class))).thenReturn(adminMock);

        ResponseDTO responseDTO = adminServices.officerDelete(adminKeyRequestDTO, token);
        assertNotNull(responseDTO);
        assertNotNull(responseDTO.getData().get("username"));
        assertEquals(adminKeyRequestDTO.getUsername(), responseDTO.getData().get("username"));
        assertEquals(0, responseDTO.getData().get("status"));

        verify(adminRepository).findOneByUsername(adminKeyRequestDTO.getUsername());
        verify(adminRepository).save(any(Admin.class));
    }

    @DisplayName("Test Mock Photo Admin")
    @Test
    void testPhotoAdmin() throws Exception {
        String filename = "test.jpg";
        PhotoUpdateDTO photoUpdateDTO = new PhotoUpdateDTO();
        MultipartFile image = new MockMultipartFile("image", filename, MediaType.IMAGE_JPEG_VALUE,
                new byte[128]);
        photoUpdateDTO.setImage(image);

        admin.setId(14L);
        admin.setUsername(userName);
        admin.setPicture(filename);

        when(authFilter.getAdminFromToken(token)).thenReturn(admin);
        when(amazonS3Service.deleteFileFromS3Bucket(endpointUrl + "/" + bucketName + "/" + filename)).thenReturn("");
        when(amazonS3Service.uploadFile(any(FileNameDTO.class))).thenReturn(endpointUrl + "/" + bucketName + "/" + filename);
        when(adminRepository.save(any(Admin.class))).thenReturn(adminMock);

        ResponseDTO responseDTO = adminServices.photoUpdate(photoUpdateDTO, token);
        assertNotNull(responseDTO);
        assertEquals(ConstantUtil.STATUS_SUCCESS, responseDTO.getCode());

        verify(amazonS3Service).deleteFileFromS3Bucket(endpointUrl + "/" + bucketName + "/" + filename);
        verify(amazonS3Service).uploadFile(any(FileNameDTO.class));
        verify(adminRepository).save(any(Admin.class));
    }

    @DisplayName("Test Mock Forgot Password Admin")
    @Test
    void testForgotPasswordAdmin() throws Exception {
        AdminForgotPasswordDTO adminForgotPasswordDTO = new AdminForgotPasswordDTO();
        adminForgotPasswordDTO.setEmail("admintest@mail.com");

        adminMock.setEmail(adminForgotPasswordDTO.getEmail());

        when(adminRepository.findOneByEmail(adminForgotPasswordDTO.getEmail())).thenReturn(admin);
        when(adminRepository.saveAndFlush(any(Admin.class))).thenReturn(adminMock);
        doNothing().when(emailService).sendForgotMessageAdmin(any(Admin.class));

        ResponseDTO responseDTO = adminServices.forgotPassword(adminForgotPasswordDTO);
        assertNotNull(responseDTO);
        assertEquals(ConstantUtil.STATUS_SUCCESS, responseDTO.getCode());

        verify(adminRepository).findOneByEmail(adminForgotPasswordDTO.getEmail());
        verify(adminRepository).saveAndFlush(any(Admin.class));
    }

    @DisplayName("Test Mock Confirm Password Admin")
    @Test
    void testConfirmPasswordAdmin() throws Exception {
        AdminConfirmPasswordDTO adminConfirmPasswordDTO = new AdminConfirmPasswordDTO();
        adminConfirmPasswordDTO.setPassword("test1234");
        adminConfirmPasswordDTO.setPasswordConfirm("test1234");
        adminConfirmPasswordDTO.setToken(token);

        admin.setToken_update(token);

        when(authFilter.getAdminFromToken(adminConfirmPasswordDTO.getToken())).thenReturn(admin);
        when(adminRepository.save(any(Admin.class))).thenReturn(adminMock);

        ResponseDTO responseDTO = adminServices.confirmPassword(adminConfirmPasswordDTO);
        assertNotNull(responseDTO);
        assertEquals(ConstantUtil.STATUS_SUCCESS, responseDTO.getCode());

        verify(adminRepository).save(any(Admin.class));
    }

    @DisplayName("Test Mock Pin Admin")
    @Test
    void testPinAdmin() throws Exception {
        UpdatePinDTO updatePinDTO = new UpdatePinDTO();
        updatePinDTO.setPin(1234);
        updatePinDTO.setPinConfirm(1234);

        when(authFilter.getAdminFromToken(token)).thenReturn(admin);
        when(adminRepository.save(any(Admin.class))).thenReturn(adminMock);

        ResponseDTO responseDTO = adminServices.updatePin(updatePinDTO, token);
        assertNotNull(responseDTO);
        assertEquals(ConstantUtil.STATUS_SUCCESS, responseDTO.getCode());

        verify(adminRepository).save(any(Admin.class));
    }
}
