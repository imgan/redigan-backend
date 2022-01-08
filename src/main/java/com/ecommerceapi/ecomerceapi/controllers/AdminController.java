package com.ecommerceapi.ecomerceapi.controllers;

import com.ecommerceapi.ecomerceapi.dto.params.FileNameDTO;
import com.ecommerceapi.ecomerceapi.dto.request.Admin.*;
import com.ecommerceapi.ecomerceapi.dto.request.Customer.*;
import com.ecommerceapi.ecomerceapi.dto.request.FilterListRequestDTO;
import com.ecommerceapi.ecomerceapi.dto.request.Item.ItemDetailDTO;
import com.ecommerceapi.ecomerceapi.dto.request.Item.ItemFilterListDTO;
import com.ecommerceapi.ecomerceapi.dto.request.Item.ItemFormRequestDTO;
import com.ecommerceapi.ecomerceapi.dto.request.Merchant.*;
import com.ecommerceapi.ecomerceapi.dto.request.Order.FilterListAllRequestDTO;
import com.ecommerceapi.ecomerceapi.dto.request.Order.OrderCreateDTO;
import com.ecommerceapi.ecomerceapi.dto.request.Order.UpdateIncomingDTO;
import com.ecommerceapi.ecomerceapi.dto.request.Order.UpdateOngoingDTO;
import com.ecommerceapi.ecomerceapi.dto.response.*;
import com.ecommerceapi.ecomerceapi.dto.response.Item.ItemResponseDTO;
import com.ecommerceapi.ecomerceapi.dto.response.Role.RoleResponseDTO;
import com.ecommerceapi.ecomerceapi.exception.ResultNotFoundException;
import com.ecommerceapi.ecomerceapi.filters.AuthFilter;
import com.ecommerceapi.ecomerceapi.model.Admin;
import com.ecommerceapi.ecomerceapi.model.Merchant;
import com.ecommerceapi.ecomerceapi.repositories.MerchantRepository;
import com.ecommerceapi.ecomerceapi.services.*;
import com.ecommerceapi.ecomerceapi.util.ConstantUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/officer")
public class AdminController extends BaseController {

    @Autowired
    private ItemServices itemServices;

    @Autowired
    private AdminServices adminServices;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private MerchantRepository merchantRepository;

    @Autowired
    private AuthFilter authFilter;

    @Autowired
    private AmazonS3Service amazonS3Service;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    /** LOGIN */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<ResponseDTO> createAuthenticationToken(@Valid @RequestBody LoginRequestDTO loginReqDTO,
                HttpServletRequest request) {
        logger.info("Validation Success");
        loginReqDTO.setIpAddress(getIpAddress(request));
        ResponseDTO responseDTO = adminServices.loadUserByUsername(loginReqDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    /** CREATE */
    @PostMapping()
    public ResponseEntity<ResponseDTO> officerRegister(@Valid @RequestBody AdminFormRequestDTO adminFormRequestDTO,
                HttpServletRequest request) {
        String token = authFilter.getToken(request);
        adminFormRequestDTO.setIpAddress(getIpAddress(request));
        ResponseDTO responseDTO = adminServices.officerRegister(adminFormRequestDTO, token);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    /** UPDATE */
    @PutMapping()
    public ResponseEntity<ResponseDTO> officerUpdate(@Valid @RequestBody AdminFormRequestDTO adminFormRequestDTO,
                HttpServletRequest request) {
        String token = authFilter.getToken(request);
        adminFormRequestDTO.setIpAddress(getIpAddress(request));
        ResponseDTO responseDTO = adminServices.officerUpdate(adminFormRequestDTO, token);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    /** DETAIL */
    @PostMapping("/profile_view")
    public ResponseEntity<ResponseDTO> officerView(@Valid @RequestBody AdminKeyRequestDTO adminKeyRequestDTO,
                HttpServletRequest request) {
        String token = authFilter.getToken(request);
        ResponseDTO responseDTO = adminServices.officerView(adminKeyRequestDTO, token);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    /** LIST */
    @GetMapping()
    public ResponseEntity<ResponseListDTO> officerList(@Valid FilterListRequestDTO filterListRequestdto,
                HttpServletRequest request) {
        String token = authFilter.getToken(request);
        ResponseListDTO responseListDTO = adminServices.officerList(filterListRequestdto, token);
        return new ResponseEntity<>(responseListDTO, HttpStatus.OK);
    }

    /** DELETE */
    @DeleteMapping("/profile_delete")
    public ResponseEntity<ResponseDTO> officerDelete(@Valid @RequestBody AdminKeyRequestDTO adminKeyRequestDTO,
                HttpServletRequest request) {
        String token = authFilter.getToken(request);
        ResponseDTO responseDTO = adminServices.officerDelete(adminKeyRequestDTO, token);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    /** PHOTO UPDATE */
    @PutMapping(value = "/photo", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<ResponseDTO> photoUpdate(@Valid @ModelAttribute PhotoUpdateDTO photoUpdateDTO,
                                                   HttpServletRequest request) {
        String identity = authFilter.getToken(request);
        ResponseDTO responseDTO = adminServices.photoUpdate(photoUpdateDTO, identity);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    /** Forgot Password */
    @PostMapping("/forgotpassword")
    public ResponseEntity<ResponseDTO> forgotPassword(@Valid @RequestBody AdminForgotPasswordDTO adminForgotPasswordDTO, HttpServletRequest request) {
        ResponseDTO responseDTO = adminServices.forgotPassword(adminForgotPasswordDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    /** Confirm Password */
    @PostMapping("/confirmpassword")
    public ResponseEntity<ResponseDTO> confirmPassword(@Valid @RequestBody AdminConfirmPasswordDTO adminConfirmPasswordDTO, HttpServletRequest request) {
        ResponseDTO responseDTO = adminServices.confirmPassword(adminConfirmPasswordDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    /** Update Pin */
    @PutMapping("/pin")
    public ResponseEntity<ResponseDTO> pinUpdate(HttpServletRequest request,
                @Valid @RequestBody UpdatePinDTO updatePinDTO) {
        String identity = authFilter.getToken(request);
        ResponseDTO responseDTO = adminServices.updatePin(updatePinDTO, identity);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    /** Create Merchant */
    @PostMapping("/merchant")
    public ResponseEntity<ResponseDTO> createMerchant(@Valid @RequestBody MerchantCreateDTO merchantCreateDTO,
                HttpServletRequest request) {
        String token = authFilter.getToken(request);
        merchantCreateDTO.setIpAddress(getIpAddress(request));
        ResponseDTO responseDTO = merchantService.createMerchant(merchantCreateDTO, token);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    /** Update Merchant */
    @PutMapping("/merchant")
    public ResponseEntity<ResponseDTO> updateMerchant(@Valid @RequestBody MerchantUpdateDTO merchantUpdateDTO,
                HttpServletRequest request) {
        String token = authFilter.getToken(request);
        merchantUpdateDTO.setUserType("admin");
        merchantUpdateDTO.setToken(token);
        merchantUpdateDTO.setIpAddress(getIpAddress(request));
        ResponseDTO  responseDTO  = merchantService.profileUpdate(merchantUpdateDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    /** View Merchant */
    @PostMapping("/merchant_view")
    public ResponseEntity<ResponseDTO> viewMerchant(@Valid @RequestBody MerchantViewDTO merchantViewDTO,
                HttpServletRequest request) {
        String token = authFilter.getToken(request);
        merchantViewDTO.setUserType("admin");
        merchantViewDTO.setToken(token);
        ResponseDTO responseDTO = merchantService.profileView(merchantViewDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    /** LIST MERCHANT */
    @GetMapping("/merchant")
    public ResponseEntity<ResponseListDTO> listMerchant(@Valid MerchantListDTO merchantListDTO,
                HttpServletRequest request) {
        String token = authFilter.getToken(request);
        merchantListDTO.setUserType("admin");
        merchantListDTO.setToken(token);
        ResponseListDTO responseListDTO = merchantService.profileList(merchantListDTO);
        return new ResponseEntity<>(responseListDTO, HttpStatus.OK);
    }

    /** DELETE MERCHANT */
    @DeleteMapping("/merchant_delete")
    public ResponseEntity<ResponseDTO> deleteMerchant(@Valid @RequestBody MerchantDeleteDTO merchantDeleteDTO,
                HttpServletRequest request) {
        String token = authFilter.getToken(request);
        merchantDeleteDTO.setUserType("admin");
        merchantDeleteDTO.setToken(token);
        ResponseDTO responseDTO = merchantService.profileDelete(merchantDeleteDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    /** CUSTOMER CREATE */
    @PostMapping("/customer")
    public ResponseEntity<ResponseDTO> createCustomer(@Valid @RequestBody CustomerRegisterDTO customerRegisterDTO,
                HttpServletRequest request) {
        String token = authFilter.getToken(request);
        customerRegisterDTO.setUserType("admin");
        customerRegisterDTO.setToken(token);
        ResponseDTO responseDTO = customerService.customerRegister(customerRegisterDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    /** CUSTOMER UPDATE */
    @PutMapping("/customer")
    public ResponseEntity<ResponseDTO> updateCustomer(@Valid @RequestBody CustomerUpdateDTO customerUpdateDTO,
                HttpServletRequest request) {
        String token = authFilter.getToken(request);
        customerUpdateDTO.setUserType("admin");
        customerUpdateDTO.setToken(token);
        ResponseDTO responseDTO = customerService.customerUpdate(customerUpdateDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    /** CUSTOMER VIEW */
    @PostMapping("/customer_view")
    public ResponseEntity<ResponseDTO> viewCustomer(@Valid @RequestBody CustomerViewDTO customerViewDTO,
                HttpServletRequest request) {
        String token = authFilter.getToken(request);
        customerViewDTO.setUserType("admin");
        customerViewDTO.setToken(token);
        ResponseDTO responseDTO = customerService.customerView(customerViewDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    /** CUSTOMER LIST*/
    @GetMapping("/customer")
    public ResponseEntity<ResponseListDTO> listCustomer(@Valid CustomerListDTO customerListDTO,
                HttpServletRequest request) {
        String token = authFilter.getToken(request);
        customerListDTO.setUserType("admin");
        customerListDTO.setToken(token);
        ResponseListDTO responseListDTO = customerService.customerList(customerListDTO);
        return new ResponseEntity<>(responseListDTO, HttpStatus.OK);
    }

    /** DELETE CUSTOMER */
    @DeleteMapping("/customer_delete")
    public ResponseEntity<ResponseDTO> deleteCustomer(@Valid @RequestBody CustomerDeleteDTO customerDeleteDTO,
                HttpServletRequest request) {
        String token = authFilter.getToken(request);
        customerDeleteDTO.setUserType("admin");
        customerDeleteDTO.setToken(token);
        ResponseDTO responseDTO = customerService.customerDelete(customerDeleteDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    /** CREATE ORDER */
    @PostMapping("/order")
    public ResponseEntity<ResponseDTO> createOrder(@Valid @RequestBody OrderCreateDTO orderCreateDTO,
                HttpServletRequest request) {
        String token = authFilter.getToken(request);
        orderCreateDTO.setUserType("admin");
        orderCreateDTO.setToken(token);
        ResponseDTO responseDTO = orderService.create(orderCreateDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    /** GET ORDER INCOMING BY MERCHANT */
    @GetMapping("/order/incoming")
    public ResponseEntity<ResponseListDTO> incomingOrder(@Valid FilterListRequestDTO filterListRequestdto,
                HttpServletRequest request) {
        String token = authFilter.getToken(request);
        filterListRequestdto.setUserType("admin");
        ResponseListDTO responseListDTO = orderService.getIncomingOrderByMerchant(filterListRequestdto, token);
        return new ResponseEntity<>(responseListDTO, HttpStatus.OK);
    }

    /** UPDATE ORDER INCOMING BY MERCHANT */
    @PutMapping("/order/incoming")
    public ResponseEntity<ResponseDTO> orderIncoming(@Valid @RequestBody UpdateIncomingDTO updateIncomingDTO,
                HttpServletRequest request) {
        String token = authFilter.getToken(request);
        updateIncomingDTO.setUserType("admin");
        ResponseDTO responseDTO = orderService.updateIncomingOrder(updateIncomingDTO, token);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    /** UPDATE REJECT ORDER INCOMING BY MERCHANT */
    @PutMapping("/order/incoming-reject")
    public ResponseEntity<ResponseDTO> orderIncomingReject(HttpServletRequest request, @Valid @RequestBody
            UpdateIncomingDTO updateIncomingDTO) {
        String token = authFilter.getToken(request);
        updateIncomingDTO.setUserType("admin");
        ResponseDTO responseDTO = orderService.updateIncomingRejectOrder(updateIncomingDTO, token);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    /** GET ORDER ONGOING BY MERCHANT */
    @GetMapping("/order/ongoing")
    public ResponseEntity<ResponseListDTO> orderOngoing(@Valid FilterListRequestDTO filterListRequestdto,
                HttpServletRequest request) {
        String token = authFilter.getToken(request);
        filterListRequestdto.setUserType("admin");
        ResponseListDTO responseListDTO = orderService.getOngoingOrderByMerchant(filterListRequestdto, token);
        return new ResponseEntity<>(responseListDTO, HttpStatus.OK);
    }

    /** UPDATE ORDER ONGOING BY MERCHANT */
    @PutMapping("/order/ongoing")
    public ResponseEntity<ResponseDTO> orderOngoing(@Valid @RequestBody UpdateOngoingDTO updateOngoingDTO,
                HttpServletRequest request) {
        String token = authFilter.getToken(request);
        updateOngoingDTO.setUserType("admin");
        ResponseDTO responseDTO = orderService.updateOngoingOrder(updateOngoingDTO, token);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    /** UPDATE REJECT ORDER ONGOING BY MERCHANT */
    @PutMapping("/order/ongoing-reject")
    public ResponseEntity<ResponseDTO> orderOngoingReject(@Valid @RequestBody UpdateOngoingDTO updateOngoingDTO,
                HttpServletRequest request) {
        String token = authFilter.getToken(request);
        updateOngoingDTO.setUserType("admin");
        ResponseDTO responseDTO = orderService.updateOngoingRejectOrder(updateOngoingDTO, token);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    /** UPDATE ARRIVED ORDER ONGOING BY MERCHANT */
    @PutMapping("/order/ongoing-arrived")
    public ResponseEntity<ResponseDTO> orderOngoingArrived(@Valid @RequestBody UpdateOngoingDTO updateOngoingDTO,
                HttpServletRequest request) {
        String token = authFilter.getToken(request);
        updateOngoingDTO.setUserType("admin");
        ResponseDTO responseDTO = orderService.updateOngoingArrived(updateOngoingDTO, token);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    /** GET ORDER SETTLED BY MERCHANT */
    @GetMapping("/order/settled")
    public ResponseEntity<ResponseListDTO> orderSettled(@Valid FilterListRequestDTO filterListRequestdto,
                HttpServletRequest request) {
        String token = authFilter.getToken(request);
        filterListRequestdto.setUserType("admin");
        ResponseListDTO responseListDTO = orderService.getSettleOrderByMerchant(filterListRequestdto,token);
        return new ResponseEntity<>(responseListDTO, HttpStatus.OK);
    }

    /** GET ORDER ALL */
    @GetMapping("/order/all")
    public ResponseEntity<ResponseAnyDTO> allOrder(@Valid FilterListAllRequestDTO filterListAllRequestDTO,
                HttpServletRequest request) {
        String token = authFilter.getToken(request);
        filterListAllRequestDTO.setUserType("admin");
        ResponseListDataDTO<Map> responseListDataDTO = orderService.getAllOrder(filterListAllRequestDTO, token);

        /** Result Response */
        ResponseAnyDTO<ResponseListDataDTO> responseAnyDTO = new ResponseAnyDTO<>(ConstantUtil.STATUS_SUCCESS,
                ConstantUtil.MESSAGE_SUCCESS, responseListDataDTO);

        return new ResponseEntity<>(responseAnyDTO, HttpStatus.OK);
    }

    /** GET ORDER ALL LIST */
    @GetMapping("/order/all-list")
    public ResponseEntity<ResponseAnyDTO> allOrderList(@Valid FilterListAllRequestDTO filterListAllRequestDTO,
                HttpServletRequest request) {
        String token = authFilter.getToken(request);
        filterListAllRequestDTO.setUserType("admin");
        ResponseListDataDTO<Map> responseListDataDTO = orderService.getAllOrderList(filterListAllRequestDTO, token);

        /** Result Response */
        ResponseAnyDTO<ResponseListDataDTO> responseAnyDTO = new ResponseAnyDTO<>(ConstantUtil.STATUS_SUCCESS,
                ConstantUtil.MESSAGE_SUCCESS, responseListDataDTO);

        return new ResponseEntity<>(responseAnyDTO, HttpStatus.OK);
    }

    @PostMapping(value = "/upload/{id}", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity uploadItem (HttpServletRequest request, @RequestParam("file") MultipartFile file,
                                      @PathVariable String id) {
        String token = authFilter.getToken(request);
        Admin admin = authFilter.getAdminFromToken(token);
        if (admin == null) throw new ResultNotFoundException("Admin is not found");
        Merchant merchant = merchantRepository.findOneByUsername(id);
        if(merchant == null)
            throw new ResultNotFoundException("merchant not found");
        Boolean itemImportResponseDTO = itemServices.itemImport(file,merchant);
        Map upload = new HashMap<>();
        upload.put("status",itemImportResponseDTO);
        /** Result Response */
        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setInfo(ConstantUtil.MESSAGE_SUCCESS);
        responseDTO.setCode(ConstantUtil.STATUS_SUCCESS);
        responseDTO.setData(upload);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    /** Create Item Post Method Multipart Form Data */
    @PostMapping(value = "/item", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity itemAdminCreate(@Valid @ModelAttribute ItemFormRequestDTO itemFormRequestDTO,
                                     HttpServletRequest request) {
        String token = authFilter.getToken(request);
        itemFormRequestDTO.setUserType("admin");
        ItemResponseDTO itemResponseDTO = itemServices.itemCreate(itemFormRequestDTO, token);
        ResponseAnyDTO<ItemResponseDTO> responseAnyDTO = new ResponseAnyDTO<>(ConstantUtil.STATUS_SUCCESS,
                ConstantUtil.MESSAGE_SUCCESS, itemResponseDTO);
        return new ResponseEntity<>(responseAnyDTO, HttpStatus.CREATED);
    }

    /** Update Item Put Method Multipart Form Data */
    @PutMapping(value = "/item", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity itemAdminUpdate(@Valid @ModelAttribute ItemFormRequestDTO itemFormRequestDTO,
                                     HttpServletRequest request) {
        String token = authFilter.getToken(request);
        itemFormRequestDTO.setUserType("admin");
        ItemResponseDTO itemResponseDTO = itemServices.itemUpdate(itemFormRequestDTO, token);
        ResponseAnyDTO<ItemResponseDTO> responseAnyDTO = new ResponseAnyDTO<>(ConstantUtil.STATUS_SUCCESS,
                ConstantUtil.MESSAGE_SUCCESS, itemResponseDTO);
        return new ResponseEntity<>(responseAnyDTO, HttpStatus.CREATED);
    }

    /** Detail Item Get Method */
    @GetMapping("/item/{id}")
    public ResponseEntity itemAdminView(HttpServletRequest request, @PathVariable("id") Long id) {
        if (!isExistingDataAndLongValue(id)) throw new ValidationException("Id must be number");
        String token = authFilter.getToken(request);
        ItemDetailDTO itemDetailDTO = new ItemDetailDTO();
        itemDetailDTO.setId(id);
        itemDetailDTO.setUserType("admin");
        ItemResponseDTO itemResponseDTO = itemServices.itemView(itemDetailDTO, token);
        ResponseAnyDTO<ItemResponseDTO> responseAnyDTO = new ResponseAnyDTO<>(ConstantUtil.STATUS_SUCCESS,
                ConstantUtil.MESSAGE_SUCCESS, itemResponseDTO);
        return new ResponseEntity<>(responseAnyDTO, HttpStatus.OK);
    }

    /** List Item Get Method */
    @GetMapping("/item")
    public ResponseEntity itemAdminList(@Valid FilterListRequestDTO filterListRequestdto, HttpServletRequest request) {
        String token = authFilter.getToken(request);
        filterListRequestdto.setUserType("admin");
        List<ItemResponseDTO> itemResponseDTOList = itemServices.itemList(filterListRequestdto, token);
        Map detailList = itemServices.totalPage(filterListRequestdto, token);
        ResponseListAnyDTO<ItemResponseDTO> responseListAnyDTO = new ResponseListAnyDTO<>(ConstantUtil.STATUS_SUCCESS,
                ConstantUtil.MESSAGE_SUCCESS, detailList, itemResponseDTOList);
        return new ResponseEntity<>(responseListAnyDTO, HttpStatus.OK);
    }

    /** Remove Item Delete Method */
    @DeleteMapping("/item/{id}")
    public ResponseEntity itemAdminDelete(HttpServletRequest request, @PathVariable Long id) {
        if (!isExistingDataAndLongValue(id)) throw new ValidationException("Id must be number");
        String token = authFilter.getToken(request);
        ItemDetailDTO itemDetailDTO = new ItemDetailDTO();
        itemDetailDTO.setId(id);
        itemDetailDTO.setUserType("admin");
        ItemResponseDTO itemResponseDTO = itemServices.itemDelete(itemDetailDTO, token);
        ResponseAnyDTO<ItemResponseDTO> responseAnyDTO = new ResponseAnyDTO<>(ConstantUtil.STATUS_SUCCESS,
                ConstantUtil.MESSAGE_SUCCESS, itemResponseDTO);
        return new ResponseEntity<>(responseAnyDTO, HttpStatus.OK);
    }

    /** List Item Merchant for Admin */
    @GetMapping("/items")
    public ResponseEntity<ResponseListDTO> itemsAdminList(@Valid ItemFilterListDTO itemFilterListDTO,
            HttpServletRequest request) {
        String token = authFilter.getToken(request);
        ResponseListDTO responseListDTO = itemServices.itemsList(itemFilterListDTO, token);
        return new ResponseEntity<>(responseListDTO, HttpStatus.OK);
    }

    @PostMapping(value = "/upload_attribute", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity uploadItemAttribute (HttpServletRequest request, @RequestParam("file") MultipartFile file) {
        String token = authFilter.getToken(request);
        Admin admin = authFilter.getAdminFromToken(token);
        if (admin == null) throw new ResultNotFoundException("Admin is not found");
        Boolean itemAttrImportResponseDTO = itemServices.itemAttributeImport(file);
        Map upload = new HashMap<>();
        upload.put("status", itemAttrImportResponseDTO);
        /** Result Response */
        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setInfo(ConstantUtil.MESSAGE_SUCCESS);
        responseDTO.setCode(ConstantUtil.STATUS_SUCCESS);
        responseDTO.setData(upload);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @PostMapping(value = "/upload_merchant", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity uploadMerchant (HttpServletRequest request, @RequestParam("file") MultipartFile file) {
        String token = authFilter.getToken(request);
        Admin admin = authFilter.getAdminFromToken(token);
        if (admin == null) throw new ResultNotFoundException("Admin is not found");
        Boolean merchantImportResponseDTO = merchantService.merchantImport(file);
        Map upload = new HashMap<>();
        upload.put("status", merchantImportResponseDTO);
        /** Result Response */
        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setInfo(ConstantUtil.MESSAGE_SUCCESS);
        responseDTO.setCode(ConstantUtil.STATUS_SUCCESS);
        responseDTO.setData(upload);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

}
