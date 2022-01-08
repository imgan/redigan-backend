package com.ecommerceapi.ecomerceapi.services.impl;

import com.ecommerceapi.ecomerceapi.dto.params.FileNameDTO;
import com.ecommerceapi.ecomerceapi.dto.request.Admin.AdminConfirmPasswordDTO;
import com.ecommerceapi.ecomerceapi.dto.request.Admin.AdminForgotPasswordDTO;
import com.ecommerceapi.ecomerceapi.dto.request.Admin.AdminFormRequestDTO;
import com.ecommerceapi.ecomerceapi.dto.request.Admin.AdminKeyRequestDTO;
import com.ecommerceapi.ecomerceapi.dto.request.FilterListRequestDTO;
import com.ecommerceapi.ecomerceapi.dto.request.Merchant.*;
import com.ecommerceapi.ecomerceapi.dto.response.ResponseDTO;
import com.ecommerceapi.ecomerceapi.dto.response.ResponseListDTO;
import com.ecommerceapi.ecomerceapi.exception.ResultNotFoundException;
import com.ecommerceapi.ecomerceapi.exception.ResultServiceException;
import com.ecommerceapi.ecomerceapi.filters.AuthFilter;
import com.ecommerceapi.ecomerceapi.repositories.AdminRepository;
import com.ecommerceapi.ecomerceapi.services.AmazonS3Service;
import com.ecommerceapi.ecomerceapi.services.EmailService;
import com.ecommerceapi.ecomerceapi.services.AdminServices;
import com.ecommerceapi.ecomerceapi.model.Admin;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.ecommerceapi.ecomerceapi.util.ConstantUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ValidationException;
import java.util.*;

@Component
public class AdminServiceImplements extends BaseServices implements AdminServices {
    @Autowired
    AdminRepository adminRepository;

    @Autowired
    EmailService emailService;

    @Autowired
    AuthFilter authFilter;

    @Autowired
    private AmazonS3Service amazonS3Service;

    @Value("${amazonProperties.endpointUrl}")
    private String endpointUrl;

    @Value("${amazonProperties.bucketName}")
    private String bucketName;

    @Value("${app.max.sizeImage}")
    private Integer maximumSize;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    /** LOGIN BY USERNAME */
    @Override
    public ResponseDTO loadUserByUsername(LoginRequestDTO loginReqDTO) {
        Map data = new HashMap();
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        ResponseDTO responseDTO = new ResponseDTO();
        Admin admin = adminRepository.findOneByUsername(loginReqDTO.getUsername());
        if (admin == null) throw new ResultNotFoundException("Username of Admin is not valid");
        if(admin.getStatus().equals(false)) throw new ResultNotFoundException("admin not active");
        if(!(passwordEncoder.matches(loginReqDTO.getPassword(), admin.getPassword())))
            throw new ResultNotFoundException("Password of Admin is not valid");
        logger.info("Password matches");
        try {
            String token = generateJwtToken(admin);
            data.put("token", token);
            data.put("userName", admin.getUsername());
            data.put("adminName", admin.getOfficerName());
            responseDTO.setCode(ConstantUtil.STATUS_SUCCESS);
            responseDTO.setInfo(ConstantUtil.MESSAGE_SUCCESS);
            responseDTO.setData(data);
            logger.info("result : " + responseDTO);
            return responseDTO;
        } catch (Exception e){
            throw new ResultServiceException(e.getMessage());
        }
    }

    /** GENERATE TOKEN */
    @Override
    public String generateJwtToken(Admin admin) {
        long timestamp = System.currentTimeMillis();
        String token = Jwts.builder().signWith(SignatureAlgorithm.HS256, ConstantUtil.API_SECRET_KEY)
                .setIssuedAt(new Date(timestamp))
                .setExpiration(new Date(timestamp + ConstantUtil.TOKEN_VALIDTY))
                .claim("officerId", admin.getId())
                .claim("email", admin.getEmail())
                .claim("username", admin.getUsername())
                .claim("phone", admin.getPhone())
                .claim("type", "officer").compact();
        return token;
    }

    /** ADMIN CREATE */
    @Override
    @Transactional
    public ResponseDTO officerRegister(AdminFormRequestDTO adminFormRequestDTO, String token) {

        /** Initialize */
        ResponseDTO responseDTO = new ResponseDTO();
        Map data = new HashMap();

        /** Check Token Admin Exist */
        Admin adminToken = authFilter.getAdminFromToken(token);
        if (adminToken == null) throw new ResultNotFoundException("Token admin has expired");

          /** Check Pin Admin Exist */
//        Boolean isPin = checkPin(adminFormRequestDTO.getPin() , adminToken.getPin());
//        if(isPin.equals(false)) throw new ResultNotFoundException("Wrong pin number");

        /** Check Password Confirmation */
        if (adminFormRequestDTO.getPassword() != null && adminFormRequestDTO.getPasswordConfirm() != null) {
            if (adminFormRequestDTO.getPassword().compareTo(adminFormRequestDTO.getPasswordConfirm()) != 0) {
                throw new ValidationException("Password confirmation isn't same");
            }
        }

        try {
            Admin adminRepo = adminRepository.findAllByEmailOrUsernameOrPhone(adminFormRequestDTO.getEmail(),
                    adminFormRequestDTO.getUsername(), adminFormRequestDTO.getPhone());
            if(adminRepo == null){
                logger.info("Admin not exist");

                /** Save Admin */
                Admin admin = new Admin();
                admin.setUsername(adminFormRequestDTO.getUsername());
                admin.setEmail(adminFormRequestDTO.getEmail().toLowerCase());
                admin.setOfficerName(adminFormRequestDTO.getOfficerName());
                admin.setPhone(adminFormRequestDTO.getPhone());
                admin.setPassword(adminFormRequestDTO.getPassword());
                admin.setRoleId(adminFormRequestDTO.getRoleId());
                admin.setStatus(1);
//                admin.setCreatedBy(adminToken.getUsername());
                admin.setCreatedDate(new Date());
                adminRepository.save(admin);
                emailService.sendRegisMessageAdmin(admin);

                /** Admin Object Result */
                data.put("username", admin.getUsername());
                data.put("email", admin.getEmail());
                data.put("officerName", admin.getOfficerName());
                data.put("phone", admin.getPhone());
                data.put("picture", "");
                data.put("roleId", admin.getRoleId());
                responseDTO.setCode(ConstantUtil.STATUS_SUCCESS);
                responseDTO.setInfo(ConstantUtil.MESSAGE_SUCCESS);
                responseDTO.setData(data);
                return responseDTO;
            } else {
                responseDTO.setCode(ConstantUtil.STATUS_EXISTING_DATA);
                responseDTO.setInfo(ConstantUtil.MESSAGE_EXISTING_DATA);
                responseDTO.setData(null);
                return responseDTO;
            }
        } catch (Exception e) {
            logger.error("[FATAL] " + e);
            throw new ResultServiceException(e.getMessage());
        }
    }

    /** ADMIN UPDATE */
    @Override
    @Transactional
    public ResponseDTO officerUpdate(AdminFormRequestDTO adminFormRequestDTO, String token) {

        /** Initialize */
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        ResponseDTO responseDTO = new ResponseDTO();
        Map data = new HashMap();

        /** Check Token Admin Exist */
        Admin adminToken = authFilter.getAdminFromToken(token);
        if (adminToken == null) throw new ResultNotFoundException("Token admin has expired");

        /** Check Pin Admin Exist */
//        Boolean isPin = checkPin(adminFormRequestDTO.getPin() , adminToken.getPin());
//        if(isPin.equals(false)) throw new ResultNotFoundException("Wrong pin number");

        /** Check Admin Exist */
        Admin admin = adminRepository.findOneByUsername(adminFormRequestDTO.getUsername());
        if (admin == null) throw new ResultNotFoundException("Username of Admin is not valid");

        /** Check Password Confirmation */
        if (adminFormRequestDTO.getPassword() != null && adminFormRequestDTO.getPasswordConfirm() != null && adminFormRequestDTO.getPasswordOld() != null) {
            if (adminFormRequestDTO.getPassword().compareTo(adminFormRequestDTO.getPasswordConfirm()) != 0) {
                throw new ValidationException("Password confirmation isn't same");
            }
            if(!(passwordEncoder.matches(adminFormRequestDTO.getPasswordOld(), admin.getPassword())))
                throw new ResultNotFoundException("Password of Admin is not valid");
            admin.setPassword(adminFormRequestDTO.getPassword());
        }

        try {
            /** Save Admin */
            admin.setUsername(adminFormRequestDTO.getUsername());
            admin.setEmail(adminFormRequestDTO.getEmail().toLowerCase());
            admin.setOfficerName(adminFormRequestDTO.getOfficerName());
            admin.setPhone(adminFormRequestDTO.getPhone());
            admin.setRoleId(adminFormRequestDTO.getRoleId());
            admin.setUpdatedBy(adminToken.getUsername());
            admin.setUpdatedDate(new Date());
            adminRepository.save(admin);

            /** Admin Object Result */
            String picture = admin.getPicture() != null ? endpointUrl + "/" + bucketName + "/" + admin.getPicture() : "";
            data.put("username", admin.getUsername());
            data.put("email", admin.getEmail());
            data.put("officerName", admin.getOfficerName());
            data.put("phone", admin.getPhone());
            data.put("picture", picture);
            data.put("roleId", admin.getRoleId());
            responseDTO.setCode(ConstantUtil.STATUS_SUCCESS);
            responseDTO.setInfo(ConstantUtil.MESSAGE_SUCCESS);
            responseDTO.setData(data);
            return responseDTO;
        } catch (Exception e){
            logger.error("[FATAL] " + e);
            throw new ResultServiceException(e.getMessage());
        }
    }

    /** ADMIN DETAIL */
    @Override
    public ResponseDTO officerView(AdminKeyRequestDTO adminKeyRequestDTO, String token) {

        /** Initialize */
        ResponseDTO responseDTO = new ResponseDTO();
        Map data = new HashMap();

        /** Check Token Admin Exist */
        Admin adminToken = authFilter.getAdminFromToken(token);
        if (adminToken == null) throw new ResultNotFoundException("Token admin has expired");

        /** Check Admin Exist */
        Admin admin = adminRepository.findOneByUsername(adminKeyRequestDTO.getUsername());
        if (admin == null) throw new ResultNotFoundException("Username of Admin is not valid");

        try {
            String picture = admin.getPicture() != null ? endpointUrl + "/" + bucketName + "/" + admin.getPicture() : "";
            /** Admin Object Result */
            data.put("username", admin.getUsername());
            data.put("email", admin.getEmail());
            data.put("officerName", admin.getOfficerName());
            data.put("phone", admin.getPhone());
            data.put("picture", picture);
            data.put("roleId", admin.getRoleId());
            data.put("status", admin.getStatus());
            responseDTO.setCode(ConstantUtil.STATUS_SUCCESS);
            responseDTO.setInfo(ConstantUtil.MESSAGE_SUCCESS);
            responseDTO.setData(data);
            return responseDTO;
        } catch (Exception e){
            logger.error("[FATAL] " + e);
            throw new ResultServiceException(e.getMessage());
        }
    }

    /** ADMIN LIST */
    @Override
    public ResponseListDTO officerList(FilterListRequestDTO filterListRequestDTO, String token) {

        /** Initialize */
        List<Map> officers = new ArrayList<>();
        ResponseListDTO responseListDTO = new ResponseListDTO();
        Map detail = new HashMap();

        /** Check Token Admin Exist */
        Admin adminToken = authFilter.getAdminFromToken(token);
        if (adminToken == null) throw new ResultNotFoundException("Token admin has expired");

        try {
            List<Admin> listData = adminRepository.findAllOffsetLimit(filterListRequestDTO.getSearch(),
                    filterListRequestDTO.getLimit(), filterListRequestDTO.getOffset());
            Integer countListData = adminRepository.findAllOffsetLimitCount(filterListRequestDTO.getSearch());
            for (Admin admin : listData) {
                Map officer = new HashMap<>();
                String picture = admin.getPicture() != null ? endpointUrl + "/" + bucketName + "/" + admin.getPicture() : "";
                officer.put("username", admin.getUsername());
                officer.put("email", admin.getEmail());
                officer.put("officerName", admin.getOfficerName());
                officer.put("phone", admin.getPhone());
                officer.put("picture", picture);
                officer.put("roleId", admin.getRoleId());
                officer.put("status", admin.getStatus());
                officers.add(officer);
            }
            detail.put("limit", filterListRequestDTO.getLimit());
            detail.put("total", countListData);
            detail.put("totalPage", (int) Math.ceil((double) countListData / filterListRequestDTO.getLimit()));

            responseListDTO.setCode(ConstantUtil.STATUS_SUCCESS);
            responseListDTO.setInfo(ConstantUtil.MESSAGE_SUCCESS);
            responseListDTO.setDetail(detail);
            responseListDTO.setData(officers);
            return responseListDTO;
        } catch (Exception e){
            logger.error("[FATAL] " + e);
            throw new ResultServiceException(e.getMessage());
        }
    }

    /** ADMIN DELETE */
    @Override
    public ResponseDTO officerDelete(AdminKeyRequestDTO adminKeyRequestDTO, String token) {

        /** Initialize */
        ResponseDTO responseDTO = new ResponseDTO();
        Map data = new HashMap();

        /** Check Token Admin Exist */
        Admin adminToken = authFilter.getAdminFromToken(token);
        if (adminToken == null) throw new ResultNotFoundException("Token admin has expired");

        /** Check Pin Admin Exist */
//        Boolean isPin = checkPin(adminKeyRequestDTO.getPin() , adminToken.getPin());
//        if(isPin.equals(false)) throw new ResultNotFoundException("Wrong pin number");

        /** Check Admin Exist */
        Admin admin = adminRepository.findOneByUsername(adminKeyRequestDTO.getUsername());
        if (admin == null) throw new ResultNotFoundException("Username of Admin is not valid");

        try {
            /** Delete Admin with Change Status */
            admin.setStatus(0);
            adminRepository.save(admin);

            String picture = admin.getPicture() != null ? endpointUrl + "/" + bucketName + "/" + admin.getPicture() : "";
            /** Admin Object Result */
            data.put("username", admin.getUsername());
            data.put("email", admin.getEmail());
            data.put("officerName", admin.getOfficerName());
            data.put("phone", admin.getPhone());
            data.put("picture", picture);
            data.put("roleId", admin.getRoleId());
            data.put("status", admin.getStatus());
            responseDTO.setCode(ConstantUtil.STATUS_SUCCESS);
            responseDTO.setInfo(ConstantUtil.MESSAGE_SUCCESS);
            responseDTO.setData(data);
            return responseDTO;
        } catch (Exception e){
            logger.error("[FATAL] " + e);
            throw new ResultServiceException(e.getMessage());
        }
    }

    @Override
    public ResponseDTO photoUpdate(PhotoUpdateDTO photoUpdateDTO, String token) {
        ResponseDTO responseDTO = new ResponseDTO();
        List<String> typeList = new ArrayList<>( List.of("jpg", "jpeg", "png") );
        Admin admin = authFilter.getAdminFromToken(token);
        if(admin == null) throw new ResultNotFoundException("admin not found");
        try {
            MultipartFile image = photoUpdateDTO.getImage();
            if (image != null) {
                if (!image.isEmpty()) {
                    /** Check Image Size */
                    if (image.getSize() > maximumSize) {
                        throw new ValidationException("Image size is too big. Maximum size is 5 MB");
                    }

                    /** Check Image Extension */
                    String typeFile = image.getOriginalFilename().substring(image.getOriginalFilename().lastIndexOf(".") + 1);
                    if (!typeList.contains(typeFile.toLowerCase())) {
                        throw new ValidationException("Image extension must be jpg, jpeg, png");
                    }

                    /** Local Upload
                    Path rootPath = Paths.get(uploadPath + "images/admins");
                     # Set Name for Image
                    LocalDate localDate = LocalDate.now(ZoneId.of("GMT+07:00"));
                    String formatLocalDate = localDate.format(DateTimeFormatter.ofPattern("dd_MM_yyyy"));
                    Instant instant = Instant.now();
                    long timeStampMillis = instant.toEpochMilli();
                     # Delete Old Image
                    if (admin.getPicture() != null) Files.delete(rootPath.resolve(admin.getPicture()));
                     # Save Image
                    String filename = admin.getId() + "_" + formatLocalDate + "_" + timeStampMillis + "." + typeFile;
                    Files.copy(image.getInputStream(), rootPath.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
                    */

                    /** AWS S3 Upload */
                    if (admin.getPicture() != null) amazonS3Service.deleteFileFromS3Bucket(endpointUrl +
                            "/" + bucketName + "/" + admin.getPicture());
                    FileNameDTO fileNameDTO = new FileNameDTO();
                    fileNameDTO.setFileModel("admin");
                    fileNameDTO.setId(admin.getId());
                    fileNameDTO.setImage(image);
                    String fileUrl = amazonS3Service.uploadFile(fileNameDTO);
                    logger.info("url: " + fileUrl);
                    String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
                    admin.setPicture(fileName);
                    adminRepository.save(admin);
                }
            }

            responseDTO.setData(null);
            responseDTO.setCode(ConstantUtil.STATUS_SUCCESS);
            responseDTO.setInfo(ConstantUtil.MESSAGE_SUCCESS);
            return responseDTO;
        }catch (Exception e){
            logger.error("[FATAL] " + e);
            throw new ResultServiceException(e.getMessage());
        }
    }

    /** Forgot Password */
    @Override
    public ResponseDTO forgotPassword(AdminForgotPasswordDTO adminForgotPasswordDTO) {
        ResponseDTO responseDTO = new ResponseDTO();
        Admin admin = adminRepository.findOneByEmail(adminForgotPasswordDTO.getEmail());
        if(admin == null){
            logger.info("Email not registered");
            throw new ResultNotFoundException("Email not registered");
        }
        try {
            String token = generateJwtToken(admin);
            admin.setToken_update(token);
            Admin adminToken = adminRepository.saveAndFlush(admin);
            emailService.sendForgotMessageAdmin(adminToken);
            responseDTO.setData(null);
            responseDTO.setCode(ConstantUtil.STATUS_SUCCESS);
            responseDTO.setInfo(ConstantUtil.MESSAGE_SUCCESS);
            return responseDTO;
        } catch (Exception e){
            logger.error("[FATAL] " + e);
            throw new ResultServiceException(e.getMessage());
        }
    }

    /** Confirm Password */
    @Override
    public ResponseDTO confirmPassword(AdminConfirmPasswordDTO adminConfirmPasswordDTO) {
        ResponseDTO responseDTO = new ResponseDTO();
        Admin admin = authFilter.getAdminFromToken(adminConfirmPasswordDTO.getToken());
        if (admin == null) throw new ResultNotFoundException("Admin is not found");
        if (adminConfirmPasswordDTO.getPassword().compareTo(adminConfirmPasswordDTO.getPasswordConfirm()) != 0) {
            throw new ResultNotFoundException("Password confirmation isn't same");
        }
        if(admin.getToken_update().compareTo(adminConfirmPasswordDTO.getToken()) != 0) {
            throw new ResultNotFoundException("Token is expired");
        }
        try {
            admin.setPassword(adminConfirmPasswordDTO.getPassword());
            admin.setToken_update(null);
            adminRepository.save(admin);
            responseDTO.setData(null);
            responseDTO.setCode(ConstantUtil.STATUS_SUCCESS);
            responseDTO.setInfo(ConstantUtil.MESSAGE_SUCCESS);
            return responseDTO;
        } catch (Exception e){
            logger.error("[FATAL] " + e);
            throw new ResultServiceException(e.getMessage());
        }
    }

    @Override
    public ResponseDTO updatePin(UpdatePinDTO updatePinDTO, String token) {
        Admin admin = authFilter.getAdminFromToken(token);
        if (admin == null) throw new ResultNotFoundException("Admin is not found");
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        ResponseDTO responseDTO = new ResponseDTO();
        if(admin == null) throw new ResultNotFoundException("admin not found");
        if (updatePinDTO.getPin() != null && updatePinDTO.getPinConfirm() != null) {
            if (updatePinDTO.getPin().compareTo(updatePinDTO.getPinConfirm()) != 0) {
                throw new ResultNotFoundException("confirm pin not match");
            }
        }
        try {
            admin.setPin(updatePinDTO.getPin());
            adminRepository.save(admin);
            responseDTO.setCode(ConstantUtil.STATUS_SUCCESS);
            responseDTO.setInfo(ConstantUtil.MESSAGE_SUCCESS);
            responseDTO.setData(null);
            return responseDTO;
        } catch (Exception e){
            logger.error("[FATAL] " + e);
            throw new ResultServiceException(e.getMessage());
        }
    }

}
