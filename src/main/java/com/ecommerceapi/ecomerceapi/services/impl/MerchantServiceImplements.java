package com.ecommerceapi.ecomerceapi.services.impl;

import com.ecommerceapi.ecomerceapi.dto.params.FileNameDTO;
import com.ecommerceapi.ecomerceapi.dto.request.Admin.MerchantCreateDTO;
import com.ecommerceapi.ecomerceapi.dto.request.Merchant.*;
import com.ecommerceapi.ecomerceapi.dto.response.ResponseDTO;
import com.ecommerceapi.ecomerceapi.dto.response.ResponseListDTO;
import com.ecommerceapi.ecomerceapi.exception.ResultNotFoundException;
import com.ecommerceapi.ecomerceapi.exception.ResultServiceException;
import com.ecommerceapi.ecomerceapi.filters.AuthFilter;
import com.ecommerceapi.ecomerceapi.helpers.ExcelHelper;
import com.ecommerceapi.ecomerceapi.model.Admin;
import com.ecommerceapi.ecomerceapi.repositories.ItemRepository;
import com.ecommerceapi.ecomerceapi.repositories.MerchantRepository;
import com.ecommerceapi.ecomerceapi.repositories.OrderRepository;
import com.ecommerceapi.ecomerceapi.services.AmazonS3Service;
import com.ecommerceapi.ecomerceapi.services.EmailService;
import com.ecommerceapi.ecomerceapi.services.MerchantService;
import com.ecommerceapi.ecomerceapi.model.Merchant;
import com.ecommerceapi.ecomerceapi.util.DateTimeUtil;
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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class MerchantServiceImplements extends BaseServices implements MerchantService {
    @Autowired
    MerchantRepository merchantRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    EmailService emailService;

    @Autowired
    OrderRepository orderRepository;

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
        Merchant merchant = merchantRepository.findOneByUsername(loginReqDTO.getUsername());
        if(merchant == null){
             merchant = merchantRepository.findOneByEmail(loginReqDTO.getUsername());
        }

        if (merchant == null) throw new ResultNotFoundException("Username of Merchant is not valid");
        if(merchant.getDeleted().equals(true)) throw new ResultNotFoundException("merchant deleted");
        if(merchant.getStatus().equals(0)) throw new ResultNotFoundException("merchant not active");

        if(!(passwordEncoder.matches(loginReqDTO.getPassword(), merchant.getPassword())))
            throw new ResultNotFoundException("Password of Merchant is not valid");
        logger.info("Password matches");
        try {
            String token = generateJwtToken(merchant);
            data.put("token", token);
            data.put("userName", merchant.getUsername());
            data.put("storeName",merchant.getStoreName());
            responseDTO.setCode(ConstantUtil.STATUS_SUCCESS);
            responseDTO.setInfo(ConstantUtil.MESSAGE_SUCCESS);
            responseDTO.setData(data);
            logger.info("result : " + responseDTO);
            return responseDTO;
        } catch (Exception e){
            throw new ResultServiceException(e.getMessage());
        }
    }

    @Override
    public ResponseDTO updatePresetMessage(PresetUpdateDTO presetUpdateDTO, String identity) {
        Merchant merchant = authFilter.getMerchantFromToken(identity);
        ResponseDTO responseDTO = new ResponseDTO();
        Map data = new HashMap();
        if(merchant == null )
            throw new ResultNotFoundException("merchant not found");
        try {
            merchant.setPresetMessage(presetUpdateDTO.getPresetMessage());
            merchantRepository.save(merchant);
            data.put("presetMessage",merchant.getPresetMessage());
            responseDTO.setCode(ConstantUtil.STATUS_SUCCESS);
            responseDTO.setInfo(ConstantUtil.MESSAGE_SUCCESS);
            responseDTO.setData(data);
            return responseDTO;
        } catch (Exception e){
            throw new ResultServiceException(e.getMessage());
        }
    }

    @Override
    public ResponseDTO updatePin(UpdatePinDTO updatePinDTO, String token) {
        Merchant merchant = authFilter.getMerchantFromToken(token);
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        ResponseDTO responseDTO = new ResponseDTO();
        if(merchant == null)
            throw new ResultNotFoundException("merchant not found");

        if(!updatePinDTO.getOldPin().equals(merchant.getPin()))
            throw new ResultNotFoundException("old pin not match");
        if (updatePinDTO.getPin() != null && updatePinDTO.getPinConfirm() != null) {
            if (updatePinDTO.getPin().compareTo(updatePinDTO.getPinConfirm()) != 0) {
                throw new ResultNotFoundException("confirm pin not match");
            }
        }
        try {
            merchant.setPin(updatePinDTO.getPin());
            merchantRepository.save(merchant);
            responseDTO.setCode(ConstantUtil.STATUS_SUCCESS);
            responseDTO.setInfo(ConstantUtil.MESSAGE_SUCCESS);
            responseDTO.setData(null);
            return responseDTO;
        } catch (Exception e){
            logger.error("[FATAL] " + e);
            throw new ResultServiceException(e.getMessage());
        }
    }

    /** GET PRESET MESSAGE */
    @Override
    public ResponseDTO getPresetMessage(String identity) {
        Merchant merchant = authFilter.getMerchantFromToken(identity);
        ResponseDTO responseDTO = new ResponseDTO();
        Map data = new HashMap();
        if(merchant == null )
            throw new ResultNotFoundException("merchant not found");
        try {
            data.put("presetMessage",merchant.getPresetMessage());
            responseDTO.setCode(ConstantUtil.STATUS_SUCCESS);
            responseDTO.setInfo(ConstantUtil.MESSAGE_SUCCESS);
            responseDTO.setData(data);
            return responseDTO;
        } catch (Exception e){
            logger.error("[FATAL] " + e);
            throw new ResultServiceException(e.getMessage());
        }
    }

    /** GENERATE TOKEN */
    @Override
    public String generateJwtToken(Merchant merchant) {
        long timestamp = System.currentTimeMillis();
        String token = Jwts.builder().signWith(SignatureAlgorithm.HS256, ConstantUtil.API_SECRET_KEY)
                .setIssuedAt(new Date(timestamp))
                .setExpiration(new Date(timestamp + ConstantUtil.TOKEN_VALIDTY))
                .claim("merchantId", merchant.getId())
                .claim("email", merchant.getEmail())
                .claim("username", merchant.getUsername())
                .claim("phone", merchant.getPhone())
                .claim("type", "merchant").compact();
        return token;
    }

    /** PROFILE REGISTER */
    @Override
    @Transactional
    public ResponseDTO profileRegister(MerchantRegisterDTO merchantRegisterDTO) {
        ResponseDTO responseDTO = new ResponseDTO();
        Map data = new HashMap();
        if (merchantRegisterDTO.getPassword() != null && merchantRegisterDTO.getPasswordConfirm() != null) {
            if (merchantRegisterDTO.getPassword().compareTo(merchantRegisterDTO.getPasswordConfirm()) != 0) {
                throw new ResultNotFoundException("Konfirmasi password tidak sama");
            }
        }
        try {
            List<Merchant> merchantExist = merchantRepository.findAllByEmailOrUsernameOrPhone(merchantRegisterDTO.getEmail(),
                    merchantRegisterDTO.getUsername(), merchantRegisterDTO.getPhoneNumber());

            if (merchantExist.size() < 1){
                logger.info("Mechant not exist");
                Merchant merchant = new Merchant();
                if(merchantRegisterDTO.getAvailableDelivery() == null){
                    merchant.setAvailableDelivery("1,2,3");
                } else {
                    merchant.setAvailableDelivery(merchantRegisterDTO.getAvailableDelivery());
                }
                merchant.setUsername(merchantRegisterDTO.getUsername());
                merchant.setCreatedBy("System");
                merchant.setEmail(merchantRegisterDTO.getEmail().toLowerCase());
                merchant.setPhone(merchantRegisterDTO.getPhoneNumber());
                merchant.setStoreName(merchantRegisterDTO.getStorename());
                merchant.setAddress(merchantRegisterDTO.getAddress());
                merchant.setBankAccount(merchantRegisterDTO.getBankaccount());
                merchant.setBankName(merchantRegisterDTO.getBankname());
                merchant.setBankAccountName(merchantRegisterDTO.getBankaccountname());
                merchant.setOperationNumber(merchantRegisterDTO.getOperationNumber());
                merchant.setPassword(merchantRegisterDTO.getPassword());
                merchant.setCity(merchantRegisterDTO.getCity());
                merchant.setPostalCode(merchantRegisterDTO.getPostalCode());
                merchant.setStatus(0);
                merchant.setDeleted(false);
                merchant.setCreatedDate(new Date());
                merchantRepository.save(merchant);
                emailService.sendRegisMessage(merchant);
                data.put("username", merchant.getUsername());
                data.put("storeName", merchant.getStoreName());
                data.put("email", merchant.getEmail());
                data.put("phone", merchant.getPhone());
                data.put("address", merchant.getAddress());
                data.put("bankAccount", merchant.getBankAccount());
                data.put("bankName", merchant.getBankName());
                data.put("availableDelivery", merchant.getAvailableDelivery());
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

    /** CREATE MERCHANT */
    @Override
    @Transactional
    public ResponseDTO createMerchant(MerchantCreateDTO merchantCreateDTO, String token) {
        ResponseDTO responseDTO = new ResponseDTO();
        Map data = new HashMap();
        /** Check Admin Exist */
        Admin admin = authFilter.getAdminFromToken(token);
        /** Check Confirm Password */
        if (merchantCreateDTO.getPassword() != null && merchantCreateDTO.getPasswordConfirm() != null) {
            if (merchantCreateDTO.getPassword().compareTo(merchantCreateDTO.getPasswordConfirm()) != 0) {
                throw new ResultNotFoundException("Password confirmation isn't same");
            }
        }
        try {
            List<Merchant> merchantExist = merchantRepository.findAllByEmailOrUsernameOrPhone(merchantCreateDTO.getEmail(),
                    merchantCreateDTO.getUsername(), merchantCreateDTO.getPhoneNumber());
            if(merchantExist.size() < 1){
                logger.info("Merchant not exist");
                Merchant merchant = new Merchant();
                if(merchantCreateDTO.getAvailableDelivery() == null){
                    merchant.setAvailableDelivery("1,2,3");
                } else {
                    merchant.setAvailableDelivery(merchantCreateDTO.getAvailableDelivery());
                }
                merchant.setUsername(merchantCreateDTO.getUsername());
                merchant.setEmail(merchantCreateDTO.getEmail().toLowerCase());
                merchant.setPhone(merchantCreateDTO.getPhoneNumber());
                merchant.setStoreName(merchantCreateDTO.getStoreName());
                merchant.setAddress(merchantCreateDTO.getAddress());
                merchant.setBankAccount(merchantCreateDTO.getBankAccount());
                merchant.setBankAccountName(merchantCreateDTO.getBankAccountName());
                merchant.setBankName(merchantCreateDTO.getBankName());
                merchant.setOperationNumber(merchantCreateDTO.getOperationNumber());
                merchant.setPassword(merchantCreateDTO.getPassword());
                merchant.setCity(merchantCreateDTO.getCity());
                merchant.setPostalCode(merchantCreateDTO.getPostalCode());
                merchant.setPin(merchantCreateDTO.getPin());
                merchant.setStatus(1);
                merchant.setDeleted(false);
                merchant.setCreatedDate(new Date());
                merchant.setCreatedBy(admin.getUsername());
                merchantRepository.save(merchant);
                emailService.sendRegisMessage(merchant);
                data.put("username", merchant.getUsername());
                data.put("storeName", merchant.getStoreName());
                data.put("email", merchant.getEmail());
                data.put("phone", merchant.getPhone());
                data.put("address", merchant.getAddress());
                data.put("bankAccount", merchant.getBankAccount());
                data.put("bankName", merchant.getBankName());
                data.put("availableDelivery", merchant.getAvailableDelivery());
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

    /** PROFILE UPDATE */
    @Override
    @Transactional
    public ResponseDTO profileUpdate(MerchantUpdateDTO merchantUpdateDTO) {
        Map data = new HashMap();
        Map detail = new HashMap();
        Boolean isPin = false;
        ResponseDTO responseDTO = new ResponseDTO();
        Merchant merchant = merchantRepository.findAllByUsername(merchantUpdateDTO.getUsername());
        if (merchant == null ){
            logger.info("Merchant not exist");
            throw new ResultNotFoundException("Merchant not exist");
        }
        /** Check Admin Exist */
        if(merchantUpdateDTO.getUserType().compareTo("merchant") != 0) {
            Admin admin = authFilter.getAdminFromToken(merchantUpdateDTO.getToken());
            if (admin == null) throw new ResultNotFoundException("Admin is not found");
            isPin = checkPin(merchantUpdateDTO.getPin() , admin.getPin());
            merchant.setUpdatedBy(admin.getUsername());
        } else {
            isPin = checkPin(merchantUpdateDTO.getPin() , merchant.getPin());
            merchant.setUpdatedBy(merchantUpdateDTO.getUsername());
        }
//        if(isPin.equals(false)) throw new ResultNotFoundException("wrong pin number");
        /** UPDATE PASSWORD IF EXIST */
        if (merchantUpdateDTO.getPassword() != null && merchantUpdateDTO.getPasswordConfirm() != null) {
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            if(merchantUpdateDTO.getOldPassword() == null)
                throw new ResultNotFoundException("oldPassword cannot be null or empty");
            if(!(passwordEncoder.matches(merchantUpdateDTO.getOldPassword(), merchant.getPassword())))
                throw new ResultNotFoundException("Current Password of Merchant is not valid");

            if (merchantUpdateDTO.getPassword().compareTo(merchantUpdateDTO.getPasswordConfirm()) != 0) {
                throw new ResultNotFoundException("Konfirmasi password tidak sama");
            }
            merchant.setPassword(merchantUpdateDTO.getPassword());
        }

        try {
            merchant.setAvailableDelivery(merchantUpdateDTO.getAvailableDelivery());
            merchant.setEmail(merchantUpdateDTO.getEmail());
            merchant.setPhone(merchantUpdateDTO.getPhoneNumber());
            merchant.setAddress(merchantUpdateDTO.getAddress());
            merchant.setBankName(merchantUpdateDTO.getBankName());
            merchant.setBankAccountName(merchantUpdateDTO.getBankAccountName());
            merchant.setBankAccount(merchantUpdateDTO.getBankAccount());
            merchant.setWorkingday(merchantUpdateDTO.getWorkingDay());
            merchant.setOpenhour(merchantUpdateDTO.getOpenHour());
            merchant.setCloseHour(merchantUpdateDTO.getCloseHour());
            merchant.setStoreName(merchantUpdateDTO.getStoreName());
            merchant.setCity(merchantUpdateDTO.getCity());
            merchant.setPostalCode(merchantUpdateDTO.getPostalCode());
            merchant.setOperationNumber(merchantUpdateDTO.getOperationNumber());
            merchant.setAbout(merchantUpdateDTO.getAbout());
            if (merchantUpdateDTO.getStatus() != null) merchant.setStatus(merchantUpdateDTO.getStatus());
            merchant.setUpdatedDate(new Date());
            merchantRepository.save(merchant);

            detail.put("openHour", merchant.getOpenhour());
            detail.put("workingDays", merchant.getWorkingday());
            data.put("username", merchant.getUsername());
            data.put("email", merchant.getEmail());
            data.put("phone", merchant.getPhone());
            data.put("address", merchant.getAddress());
            data.put("bankAccount", merchant.getBankAccount());
            data.put("bankName", merchant.getBankName());
            data.put("availableDelivery", merchant.getAvailableDelivery());
            data.put("detail",detail);
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
    public ResponseDTO profileView(MerchantViewDTO merchantViewDTO) {
        ResponseDTO responseDTO = new ResponseDTO();
        Map data = new HashMap();
        Map detail = new HashMap();
        /** Check Admin Exist */
        if(merchantViewDTO.getUserType().compareTo("merchant") != 0) {
            Admin admin = authFilter.getAdminFromToken(merchantViewDTO.getToken());
            if (admin == null) throw new ResultNotFoundException("Admin is not found");
        }
        Merchant merchant = merchantRepository.findOneByUsername(merchantViewDTO.getUsername());
        if(merchant == null){
            logger.info("Merchant not exist");
            throw new ResultNotFoundException("Merchant not exist");
        }
        try {
            String picture = merchant.getPicture() != null ? endpointUrl + "/" + bucketName + "/" + merchant.getPicture() : "";
            detail.put("workingDays",merchant.getWorkingday());
            detail.put("openHour",merchant.getOpenhour());
            data.put("operationNumber", merchant.getOperationNumber());
            data.put("username", merchant.getUsername());
            data.put("address", merchant.getAddress());
            data.put("email", merchant.getEmail());
            data.put("phoneNumber",merchant.getPhone());
            data.put("detail",detail);
            data.put("bankName",merchant.getBankName());
            data.put("bankAccountName",merchant.getBankAccountName());
            data.put("status", merchant.getStatus());
            data.put("bankAccount", merchant.getBankAccount());
            data.put("picture", picture);
            data.put("storeName", merchant.getStoreName());
            data.put("about", merchant.getAbout());
            data.put("city", merchant.getCity());
            data.put("availableDelivery",merchant.getAvailableDelivery());
            data.put("postalCode", merchant.getPostalCode());
            data.put("createdAt", merchant.getCreatedDate());
            data.put("isDeleted", merchant.getDeleted());
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
    public ResponseListDTO profileList(MerchantListDTO merchantListDTO) {
        List<Map> merchants = new ArrayList<>();
        ResponseListDTO responseListDTO = new ResponseListDTO();
        Map detail = new HashMap<>();
        Map details = new HashMap<>();

        if(merchantListDTO.getUserType().compareTo("merchant") != 0) {
            Admin admin = authFilter.getAdminFromToken(merchantListDTO.getToken());
            if (admin == null) throw new ResultNotFoundException("Admin is not found");
        }
        try{
            List<Merchant> listData = merchantRepository.findAllOffsetLimit(merchantListDTO.getOffset(), merchantListDTO.getLimit(), merchantListDTO.getSearch());
            Integer countListData = merchantRepository.findAllCount(merchantListDTO.getSearch());

            for (Merchant merchant : listData) {
                String picture = merchant.getPicture() != null ? endpointUrl + "/" + bucketName + "/" + merchant.getPicture() : "";
                Map merchantMap = new HashMap<>();
                detail.put("workingDays",merchant.getWorkingday());
                detail.put("openHour",merchant.getOpenhour());
                merchantMap.put("phoneNumber",merchant.getPhone());
                merchantMap.put("username",merchant.getUsername());
                merchantMap.put("picture",picture);
                merchantMap.put("email",merchant.getEmail());
                merchantMap.put("storeName",merchant.getStoreName());
                merchantMap.put("address",merchant.getAddress());
                merchantMap.put("bankName",merchant.getBankName());
                merchantMap.put("bankAccount",merchant.getBankAccount());
                merchantMap.put("bankAccountName",merchant.getBankAccountName());
                merchantMap.put("operationNumber",merchant.getOperationNumber());
                merchantMap.put("about",merchant.getAbout());
                merchantMap.put("status",merchant.getStatus());
                merchantMap.put("city",merchant.getCity());
                merchantMap.put("availableDelivery",merchant.getAvailableDelivery());
                merchantMap.put("postalCode",merchant.getPostalCode());
                merchantMap.put("createdAt", DateTimeUtil.convertDateToStringCustomized(merchant.getCreatedDate(),"dd-MM-yyyy"));
                merchantMap.put("detail",detail);
                merchants.add(merchantMap);
            }
            details.put("limit", merchantListDTO.getLimit());
            details.put("total", countListData);
            details.put("totalPage", (int) Math.ceil((double) countListData / merchantListDTO.getLimit()));

            responseListDTO.setCode(ConstantUtil.STATUS_SUCCESS);
            responseListDTO.setInfo(ConstantUtil.MESSAGE_SUCCESS);
            responseListDTO.setDetail(details);
            responseListDTO.setData(merchants);
            return responseListDTO;
        } catch (Exception e){
            logger.error("[FATAL] " + e);
            throw new ResultServiceException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public ResponseDTO profileDelete(MerchantDeleteDTO merchantDeleteDTO) {
        ResponseDTO responseDTO = new ResponseDTO();
        /** Check Admin Exist */
        if(merchantDeleteDTO.getUserType().compareTo("merchant") != 0) {
            Admin admin = authFilter.getAdminFromToken(merchantDeleteDTO.getToken());
            if (admin == null) throw new ResultNotFoundException("Admin is not found");
            Boolean isPin = checkPin(merchantDeleteDTO.getPin() , admin.getPin());
//            if(isPin.equals(false)) throw new ResultNotFoundException("wrong pin number");
        }
        Merchant merchant = merchantRepository.findOneByUsername(merchantDeleteDTO.getUsername());
        if(merchant == null){
            logger.info("Merchant not exist");
            throw new ResultNotFoundException("Merchant not exist");
        }
        try{
            merchant.setDeleted(true);
            merchant.setStatus(0);
            merchantRepository.save(merchant);
            responseDTO.setCode(ConstantUtil.STATUS_SUCCESS);
            responseDTO.setInfo(ConstantUtil.MESSAGE_SUCCESS);
            responseDTO.setData(null);
            return responseDTO;
        } catch (Exception e){
            logger.error("[FATAL] " + e);
            throw new ResultServiceException(e.getMessage());
        }
    }

    @Override
    public ResponseDTO dashboardView(String token) {
        Map data = new HashMap<>();
        ResponseDTO responseDTO = new ResponseDTO();
        Merchant merchant = authFilter.getMerchantFromToken(token);
        if(merchant == null ){
            throw new ResultNotFoundException("Merchant not found");
        }

        Integer totalItem = itemRepository.countAllItemByMerchant(merchant.getId());
        if (totalItem == null)
            throw new ResultNotFoundException("total item not found");
        Integer incomingOrder = orderRepository.countAllOrderIncomingByMerchant(merchant.getId());
        if (incomingOrder == null)
            throw new ResultNotFoundException("incomingOrder not found");
        Integer ongoingOrder = orderRepository.countAllOrderOngoingByMerchant(merchant.getId());
        if (ongoingOrder == null)
            throw new ResultNotFoundException("ongoingOrder not found");
        Integer settledOrder = orderRepository.countAllOrderSettledByMerchant(merchant.getId());
        if (settledOrder == null)
            throw new ResultNotFoundException("settledOrder not found");
        Integer totalRevenue = orderRepository.findPrevRevenueByMerchant(merchant.getId());
        if (totalRevenue == null)
            throw new ResultNotFoundException("totalRevenue not found");
        Integer revenueThisMonth = orderRepository.findThisRevenueByMerchant(merchant.getId());
        if (revenueThisMonth == null)
            throw new ResultNotFoundException("revenueThisMonth not found");
        try {
            Map order = new HashMap<>();
            order.put("incommingOrder",incomingOrder);
            order.put("ongoing",ongoingOrder);
            order.put("settled",settledOrder);

            Map revenue = new HashMap<>();
            revenue.put("totalRevenue",totalRevenue);
            revenue.put("thisMonth", revenueThisMonth);

            data.put("revenue",revenue);
            data.put("totalItem",totalItem);
            data.put("order", order);
            data.put("name",merchant.getUsername());
            data.put("storeName",merchant.getStoreName());

            responseDTO.setData(data);
            responseDTO.setCode(ConstantUtil.STATUS_SUCCESS);
            responseDTO.setInfo(ConstantUtil.MESSAGE_SUCCESS);
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
        Merchant merchant = authFilter.getMerchantFromToken(token);
       if(merchant == null)
           throw new ResultNotFoundException("merchant not found");
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

                    /** AWS S3 Upload */
                    if (merchant.getPicture() != null) amazonS3Service.deleteFileFromS3Bucket(endpointUrl +
                            "/" + bucketName + "/" + merchant.getPicture());
                    FileNameDTO fileNameDTO = new FileNameDTO();
                    fileNameDTO.setFileModel("merchant");
                    fileNameDTO.setId(merchant.getId());
                    fileNameDTO.setImage(image);
                    String fileUrl = amazonS3Service.uploadFile(fileNameDTO);
                    logger.info("url: " + fileUrl);
                    String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
                    merchant.setPicture(fileName);
                    merchantRepository.save(merchant);
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
    public ResponseDTO forgotPassword(MerchantForgotPasswordDTO merchantForgotPasswordDTO) {
        ResponseDTO responseDTO = new ResponseDTO();
        Merchant merchant = merchantRepository.findOneByEmail(merchantForgotPasswordDTO.getEmail());
        if(merchant == null){
            logger.info("Email not registered");
            throw new ResultNotFoundException("Email not registered");
        }
        try {
            String token = generateJwtToken(merchant);
            merchant.setToken_update(token);
            Merchant merchantToken = merchantRepository.saveAndFlush(merchant);
            emailService.sendForgotMessage(merchantToken);
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
    public ResponseDTO confirmPassword(MerchantConfirmPasswordDTO merchantConfirmPasswordDTO) {
        ResponseDTO responseDTO = new ResponseDTO();
        Merchant merchant = authFilter.getMerchantFromToken(merchantConfirmPasswordDTO.getToken());
        if (merchant == null) throw new ResultNotFoundException("Merchant is not found");
        if (merchantConfirmPasswordDTO.getPassword().compareTo(merchantConfirmPasswordDTO.getPasswordConfirm()) != 0) {
            throw new ResultNotFoundException("Password confirmation isn't same");
        }
        if(merchant.getToken_update().compareTo(merchantConfirmPasswordDTO.getToken()) != 0) {
            throw new ResultNotFoundException("Token is expired");
        }
        try {
            merchant.setPassword(merchantConfirmPasswordDTO.getPassword());
            merchant.setToken_update(null);
            merchantRepository.save(merchant);
            responseDTO.setData(null);
            responseDTO.setCode(ConstantUtil.STATUS_SUCCESS);
            responseDTO.setInfo(ConstantUtil.MESSAGE_SUCCESS);
            return responseDTO;
        } catch (Exception e){
            logger.error("[FATAL] " + e);
            throw new ResultServiceException(e.getMessage());
        }
    }

    @Override @Transactional
    public Boolean merchantImport(MultipartFile file) {
        try {
            List<Merchant> merchants = ExcelHelper.excelMerchant(file.getInputStream());

            for (Merchant merchant : merchants) {
                List<Merchant> merchantExist = merchantRepository.findAllByEmailOrUsernameOrPhone(merchant.getEmail(),
                        merchant.getUsername(), merchant.getOperationNumber());
                if(merchantExist.size() < 1){
                    merchantRepository.save(merchant);
                }
            }
            return true;
        } catch (IOException e) {
            throw new ResultServiceException("failed upload excel");
        }
    }
}
