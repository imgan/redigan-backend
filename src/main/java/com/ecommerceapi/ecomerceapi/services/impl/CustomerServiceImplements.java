package com.ecommerceapi.ecomerceapi.services.impl;

import com.ecommerceapi.ecomerceapi.dto.request.Customer.*;
import com.ecommerceapi.ecomerceapi.dto.response.ResponseDTO;
import com.ecommerceapi.ecomerceapi.dto.response.ResponseListDTO;
import com.ecommerceapi.ecomerceapi.dto.request.Customer.FilterItemListRequestdto;
import com.ecommerceapi.ecomerceapi.dto.response.Customer.MerchantItemResponseDTO;
import com.ecommerceapi.ecomerceapi.exception.ResultExistException;
import com.ecommerceapi.ecomerceapi.exception.ResultNotFoundException;
import com.ecommerceapi.ecomerceapi.exception.ResultServiceException;
import com.ecommerceapi.ecomerceapi.filters.AuthFilter;
import com.ecommerceapi.ecomerceapi.model.*;
import com.ecommerceapi.ecomerceapi.repositories.*;
import com.ecommerceapi.ecomerceapi.services.AttributeItemService;
import com.ecommerceapi.ecomerceapi.services.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.ecommerceapi.ecomerceapi.util.ConstantUtil;
import com.ecommerceapi.ecomerceapi.util.DateTimeUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class CustomerServiceImplements extends BaseServices implements CustomerService {
    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    MerchantRepository merchantRepository;

    @Autowired
    AvailableStockRepository availableStockRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    ItemAvailableDayRepository itemAvailableDayRepository;

    @Autowired
    AuthFilter authFilter;

    @Autowired
    AttributeItemService attributeItemService;

    @Value("${amazonProperties.endpointUrl}")
    private String endpointUrl;

    @Value("${amazonProperties.bucketName}")
    private String bucketName;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    /** CUSTOMER REGISTER*/
    @Override
    @Transactional
    public ResponseDTO customerRegister(CustomerRegisterDTO customerRegisterDTO) {
        ResponseDTO responseDTO = new ResponseDTO();
        Map data = new HashMap();
        Customer newCustomer = new Customer();
        if(customerRegisterDTO.getUserType().compareTo("admin") == 0) {
            Admin admin = authFilter.getAdminFromToken(customerRegisterDTO.getToken());
            if (admin == null) throw new ResultNotFoundException("Admin is not found");
            Boolean isPin = checkPin(customerRegisterDTO.getPin() , admin.getPin());
            if(isPin.equals(false)) throw new ResultNotFoundException("wrong pin number");
            newCustomer.setCreatedBy(admin.getUsername());
        } else {
            newCustomer.setCreatedBy(customerRegisterDTO.getCustomerName());
        }
        Customer customer = customerRepository.findOneByPhone(customerRegisterDTO.getPhoneNumber());
        if(customer != null){
            throw new ResultNotFoundException("Customer already exist");
        }
        try {
            if(customerRegisterDTO.getPassword() != null)
                newCustomer.setPassword(customerRegisterDTO.getPassword());
            newCustomer.setAddress(customerRegisterDTO.getAddress());
            newCustomer.setPhone(customerRegisterDTO.getPhoneNumber());
            newCustomer.setCustomerName(customerRegisterDTO.getCustomerName());
            newCustomer.setEmail(customerRegisterDTO.getEmail());
            newCustomer.setCity(customerRegisterDTO.getCity());
            newCustomer.setPostalCode(customerRegisterDTO.getPostalCode());
            newCustomer.setCreatedDate(new Date());
            newCustomer.setStatus(true);
            newCustomer.setDeleted(false);
            customerRepository.save(newCustomer);
            data.put("customerName", newCustomer.getCustomerName());
            data.put("phoneNumber", newCustomer.getPhone());
            data.put("email", newCustomer.getEmail());
            data.put("address", newCustomer.getAddress());
            data.put("city", newCustomer.getCity());
            data.put("postalCode", newCustomer.getPostalCode());
            responseDTO.setCode(ConstantUtil.STATUS_SUCCESS);
            responseDTO.setInfo(ConstantUtil.MESSAGE_SUCCESS);
            responseDTO.setData(data);
            return responseDTO;
        } catch (Exception e) {
            logger.error("[FATAL] " + e);
            throw new ResultServiceException(e.getMessage());
        }
    }

    @Override
    public ResponseListDTO cartCalendarCheck(CustomerCheckDTO customerCheckDTO) {

        ResponseListDTO responseListDTO = new ResponseListDTO();
        Integer totalCanBuy = null;
        Integer assemblyTime;
        List<Map> items = new ArrayList<>();
        Map detail = new HashMap();
        String dt = sdf.format(new Date());
        List<Item> listData = itemRepository.findAllItemIn(customerCheckDTO.getItemId());
        try {
            /** Item List Result */
            for (Item item : listData) {
                List<String> outStockDate = new ArrayList<>();
                Map itemMap = new HashMap<>();
                Calendar c = new GregorianCalendar();
                c.setTime(new Date());
                /** TYPE ITEM 1 */
                if(item.getType() == 1){
                    logger.info("ITEM TYPE 1");
                    /** CALCULATED DATE */
                    c = Calendar.getInstance();
                    for(Integer i = 0; i<= 31; i++){
                        dt = sdf.format(c.getTime());  // dt is now the new date
                        logger.info("item id " +item.getId());
                        totalCanBuy = calculatedStock(item.getId(),
                                dt);
                        if(totalCanBuy < 1 ){
                            outStockDate.add(dt);
                        }
                        c.add(Calendar.DATE, 1);  // number of days to add
                    }
                    itemMap.put("disableCalendar", outStockDate);
                } else {
                    /** TYPE ITEM 2 */
                    List<Integer> itemAvailableDay = itemAvailableDayRepository.getAvailableDayByItem(
                            item.getId());
                    logger.info(itemAvailableDay.toString());
                    c = Calendar.getInstance();

                    for(Integer i = 0; i<= 31; i++){
                        Integer dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
                        Boolean contains = itemAvailableDay.contains(dayOfWeek);
                        logger.info("is contain " +contains);
                        if(!contains){
                            dt = sdf.format(c.getTime());  // dt is now the new date
                            c.add(Calendar.DATE, 1);  // number of days to add
                            outStockDate.add(dt);
                            logger.info("not contain"+ dt +" day of week " + dayOfWeek);
                        } else {
                            dt = sdf.format(c.getTime());  // dt is now the new date
                            logger.info("contain"+ dt +" day of week" + dayOfWeek);
                            totalCanBuy = calculatedStock(item.getId(),
                                    dt);
                            if(totalCanBuy < 1){
                                outStockDate.add(dt);
                            }
                            c.add(Calendar.DATE, 1);  // number of days to add
                        }
                    }
                    itemMap.put("disableCalendar", outStockDate);
                }


                List<Map> attributeItemMap = new ArrayList<>();
                List<AttributeItem> attributeItems = attributeItemService.viewByItemId(item.getId());
                for(AttributeItem attributeItem : attributeItems){
                    Map attribute = new HashMap();
                    /** Maping */
                    attribute.put("attributeId" , attributeItem.getId());
                    attribute.put("price", attributeItem.getPrice());
                    attribute.put("name", attributeItem.getName());
                    attribute.put("stock", attributeItem.getStock());
                    attribute.put("enabled", attributeItem.getEnabled());
                    attributeItemMap.add(attribute);
                }
                String picture = item.getPicture() != null ? endpointUrl + "/" + bucketName + "/" + item.getPicture() : "";
                List<Integer> availableDay = item.getType() > 1 ? item.getDayIndexList() : new ArrayList<Integer>();
                itemMap.put("id",item.getId());
                itemMap.put("itemName",item.getName());
                itemMap.put("price",item.getPrice());
                itemMap.put("attributeItems", attributeItemMap);
                itemMap.put("maxItemPerDay",item.getMaxItem());
                itemMap.put("description",item.getDescription());
                itemMap.put("assemblyTime",item.getAssemblyTime());
                itemMap.put("image",picture);
                itemMap.put("outStock",item.getOutStock());
                itemMap.put("type",item.getType());

                itemMap.put("availableDay",availableDay);
                items.add(itemMap);
            }

            responseListDTO.setCode(ConstantUtil.STATUS_SUCCESS);
            responseListDTO.setInfo(ConstantUtil.MESSAGE_SUCCESS);
            responseListDTO.setDetail(detail);
            responseListDTO.setData(items);
            return responseListDTO;
        } catch (Exception e) {
            logger.error("[FATAL]" + e);
            throw new ResultServiceException(e.getMessage());
        }
    }

    @Override
    public ResponseListDTO cartCalendarCheckV2(CustomerCheckDTO customerCheckDTO) {
        ResponseListDTO responseListDTO = new ResponseListDTO();
        Integer totalCanBuy = null;
        List<Map> dataReq = customerCheckDTO.getItemsId();
        List<Item> listData = new ArrayList<>();
        List<Map> items = new ArrayList<>();
        Map detail = new HashMap();
        String dt = sdf.format(new Date());

        try {
            /** Item List Result */
            for (Map itemList : customerCheckDTO.getItemsId()){
                Item item = itemRepository.findOneById(Long.valueOf(itemList.get("itemId").toString()));
                List<String> outStockDate = new ArrayList<>();
                Map itemMap = new HashMap<>();
                Calendar c = new GregorianCalendar();
                c.setTime(new Date());
                /** TYPE ITEM 1 */
                if(item.getType() == 1){
                    logger.info("ITEM TYPE 1");
                    /** CALCULATED DATE */
                    c = Calendar.getInstance();
                    for(Integer i = 0; i<= 31; i++){
                        dt = sdf.format(c.getTime());  // dt is now the new date
                        logger.info("item id " +item.getId());
                        totalCanBuy = calculatedStock(item.getId(),
                                dt);
                        if(totalCanBuy < Long.valueOf(itemList.get("qty").toString())){
                            outStockDate.add(dt);
                        }
                        c.add(Calendar.DATE, 1);  // number of days to add
                    }
                    itemMap.put("disableCalendar", outStockDate);
                } else {
                    /** TYPE ITEM 2 */
                    List<Integer> itemAvailableDay = itemAvailableDayRepository.getAvailableDayByItem(
                            item.getId());
                    logger.info(itemAvailableDay.toString());
                    c = Calendar.getInstance();

                    for(Integer i = 0; i<= 31; i++){
                        Integer dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
                        Boolean contains = itemAvailableDay.contains(dayOfWeek);
                        logger.info("is contain " +contains);
                        if(!contains){
                            dt = sdf.format(c.getTime());  // dt is now the new date
                            c.add(Calendar.DATE, 1);  // number of days to add
                            outStockDate.add(dt);
                            logger.info("not contain"+ dt +" day of week " + dayOfWeek);
                        } else {
                            dt = sdf.format(c.getTime());  // dt is now the new date
                            logger.info("contain"+ dt +" day of week" + dayOfWeek);
                            totalCanBuy = calculatedStock(item.getId(),
                                    dt);
                            if(totalCanBuy < Long.valueOf(itemList.get("qty").toString())){
                                outStockDate.add(dt);
                            }
                            c.add(Calendar.DATE, 1);  // number of days to add
                        }
                    }
                    itemMap.put("disableCalendar", outStockDate);
                }


                List<Map> attributeItemMap = new ArrayList<>();
                List<AttributeItem> attributeItems = attributeItemService.viewByItemId(item.getId());
                for(AttributeItem attributeItem : attributeItems){
                    Map attribute = new HashMap();
                    /** Maping */
                    attribute.put("attributeId" , attributeItem.getId());
                    attribute.put("price", attributeItem.getPrice());
                    attribute.put("name", attributeItem.getName());
                    attribute.put("stock", attributeItem.getStock());
                    attribute.put("enabled", attributeItem.getEnabled());
                    attributeItemMap.add(attribute);
                }
                String picture = item.getPicture() != null ? endpointUrl + "/" + bucketName + "/" + item.getPicture() : "";
                List<Integer> availableDay = item.getType() > 1 ? item.getDayIndexList() : new ArrayList<Integer>();
                itemMap.put("id",item.getId());
                itemMap.put("itemName",item.getName());
                itemMap.put("price",item.getPrice());
                itemMap.put("attributeItems", attributeItemMap);
                itemMap.put("maxItemPerDay",item.getMaxItem());
                itemMap.put("description",item.getDescription());
                itemMap.put("assemblyTime",item.getAssemblyTime());
                itemMap.put("image",picture);
                itemMap.put("outStock",item.getOutStock());
                itemMap.put("type",item.getType());

                itemMap.put("availableDay",availableDay);
                items.add(itemMap);
            }

            responseListDTO.setCode(ConstantUtil.STATUS_SUCCESS);
            responseListDTO.setInfo(ConstantUtil.MESSAGE_SUCCESS);
            responseListDTO.setDetail(detail);
            responseListDTO.setData(items);
            return responseListDTO;
        } catch (Exception e) {
            logger.error("[FATAL]" + e);
            throw new ResultServiceException(e.getMessage());
        }
    }

    /** CUSTOMER UPDATE BY ADMIN */
    @Override
    @Transactional
    public ResponseDTO customerUpdate(CustomerUpdateDTO customerUpdateDTO) {
        ResponseDTO responseDTO = new ResponseDTO();
        Map data = new HashMap();
        Customer customerExist = customerRepository.findOneById(customerUpdateDTO.getId());
        Customer customerByPhone = customerRepository.findOneByPhone(customerUpdateDTO.getPhoneNumber());
        if(customerByPhone != null){
            if(!customerExist.getId().equals(customerByPhone.getId()))
                throw new ResultExistException("Phone Number already registered");
        }
        if(customerExist == null)
            throw new ResultNotFoundException("Customer Not found");
        Admin admin = authFilter.getAdminFromToken(customerUpdateDTO.getToken());
        customerExist.setUpdatedBy(admin.getUsername());
        try {
            if(customerUpdateDTO.getPassword() != null)
                customerExist.setPassword(customerUpdateDTO.getPassword());
            customerExist.setPhone(customerUpdateDTO.getPhoneNumber());
            customerExist.setCustomerName(customerUpdateDTO.getCustomerName());
            if (customerUpdateDTO.getStatus() != null) customerExist.setStatus(customerUpdateDTO.getStatus());
            customerExist.setEmail(customerUpdateDTO.getEmail());
            customerExist.setPhone(customerUpdateDTO.getPhoneNumber());
            customerExist.setAddress(customerUpdateDTO.getAddress());
            customerExist.setPostalCode(customerUpdateDTO.getPostalCode());
            customerExist.setCity(customerUpdateDTO.getCity());
            customerExist.setUpdatedDate(new Date());
            customerRepository.save(customerExist);
            data.put("id",customerExist.getId());
            data.put("customerName", customerExist.getCustomerName());
            data.put("phoneNumber", customerExist.getPhone());
            data.put("email", customerExist.getEmail());
            data.put("address", customerExist.getAddress());
            data.put("city", customerExist.getCity());
            data.put("postalCode", customerExist.getPostalCode());
            responseDTO.setCode(ConstantUtil.STATUS_SUCCESS);
            responseDTO.setInfo(ConstantUtil.MESSAGE_SUCCESS);
            responseDTO.setData(data);
            return responseDTO;
        } catch (Exception e) {
            logger.error("[FATAL] " + e);
            throw new ResultServiceException(e.getMessage());
        }
    }

    /** CUSTOMER UPDATE BY CUSTOMER */
    @Override
    @Transactional
    public ResponseDTO customerUpdateV2(CustomerUpdateDTO customerUpdateDTO) {
        ResponseDTO responseDTO = new ResponseDTO();
        Map data = new HashMap();
        Customer customerByPhone = customerRepository.findOneByPhone(customerUpdateDTO.getPhoneNumber());
        if(customerByPhone == null)
            throw new ResultNotFoundException("Customer Not found");
        try {
            if(customerUpdateDTO.getPassword() != null)
                customerByPhone.setPassword(customerUpdateDTO.getPassword());
            customerByPhone.setPhone(customerUpdateDTO.getPhoneNumber());
            customerByPhone.setCustomerName(customerUpdateDTO.getCustomerName());
            if (customerUpdateDTO.getStatus() != null) customerByPhone.setStatus(customerUpdateDTO.getStatus());
            customerByPhone.setEmail(customerUpdateDTO.getEmail());
            customerByPhone.setPhone(customerUpdateDTO.getPhoneNumber());
            customerByPhone.setAddress(customerUpdateDTO.getAddress());
            customerByPhone.setPostalCode(customerUpdateDTO.getPostalCode());
            customerByPhone.setCity(customerUpdateDTO.getCity());
            customerByPhone.setUpdatedDate(new Date());
            customerRepository.save(customerByPhone);
            data.put("id",customerByPhone.getId());
            data.put("customerName", customerByPhone.getCustomerName());
            data.put("phoneNumber", customerByPhone.getPhone());
            data.put("email", customerByPhone.getEmail());
            data.put("address", customerByPhone.getAddress());
            data.put("city", customerByPhone.getCity());
            data.put("postalCode", customerByPhone.getPostalCode());
            responseDTO.setCode(ConstantUtil.STATUS_SUCCESS);
            responseDTO.setInfo(ConstantUtil.MESSAGE_SUCCESS);
            responseDTO.setData(data);
            return responseDTO;
        } catch (Exception e) {
            logger.error("[FATAL] " + e);
            throw new ResultServiceException(e.getMessage());
        }
    }

    /** CUSTOMER VIEW */
    @Override
    public ResponseDTO customerView(CustomerViewDTO customerViewDTO) {
        ResponseDTO responseDTO = new ResponseDTO();
        Boolean isPassword = false;
        Map data = new HashMap();
        if(customerViewDTO.getUserType().compareTo("admin") == 0) {
            Admin admin = authFilter.getAdminFromToken(customerViewDTO.getToken());
            if (admin == null) throw new ResultNotFoundException("Admin is not found");
        }
        Customer customer = customerRepository.findOneByPhone(customerViewDTO.getPhoneNumber());
        if (customer == null) {
            throw new ResultNotFoundException("Customer is not found");
        }
        try {
            if(customer.getPassword() != null )
                isPassword = true;
            data.put("id",customer.getId());
            data.put("customerName", customer.getCustomerName());
            data.put("phoneNumber", customer.getPhone());
            data.put("email", customer.getEmail());
            data.put("isPassword", isPassword);
            data.put("status", customer.getStatus());
            data.put("address", customer.getAddress());
            data.put("city", customer.getCity());
            data.put("postalCode", customer.getPostalCode());
            responseDTO.setCode(ConstantUtil.STATUS_SUCCESS);
            responseDTO.setInfo(ConstantUtil.MESSAGE_SUCCESS);
            responseDTO.setData(data);
            return responseDTO;
        } catch (Exception e) {
            logger.error("[FATAL] " + e);
            throw new ResultServiceException(e.getMessage());
        }
    }

    /** CUSTOMER LIST */
    @Override
    public ResponseListDTO customerList(CustomerListDTO customerListDTO) {
        ResponseListDTO responseListDTO = new ResponseListDTO();
        List<Map> customers = new ArrayList<>();
        Map detail = new HashMap();
        if(customerListDTO.getUserType().compareTo("admin") == 0) {
            Admin admin = authFilter.getAdminFromToken(customerListDTO.getToken());
            if (admin == null) throw new ResultNotFoundException("Admin is not found");
        }
        try {
            List<Customer> listData = customerRepository.getAllOffsetLimit(customerListDTO.getOffset(),
                    customerListDTO.getLimit(), customerListDTO.getSearch());
            Integer countListData = customerRepository.getAllOffsetLimitCount(customerListDTO.getSearch());
            for (Customer customer : listData) {
                Map customerMap = new HashMap<>();
                customerMap.put("id",customer.getId());
                customerMap.put("customerName", customer.getCustomerName());
                customerMap.put("phoneNumber", customer.getPhone());
                customerMap.put("email", customer.getEmail());
                customerMap.put("status", customer.getStatus());
                customerMap.put("city", customer.getCity());
                customerMap.put("postalCode", customer.getPostalCode());
                customerMap.put("address", customer.getAddress());
                customers.add(customerMap);
            }
            detail.put("limit", customerListDTO.getLimit());
            detail.put("total", countListData);
            detail.put("totalPage", (int) Math.ceil((double) countListData / customerListDTO.getLimit()));

            responseListDTO.setCode(ConstantUtil.STATUS_SUCCESS);
            responseListDTO.setInfo(ConstantUtil.MESSAGE_SUCCESS);
            responseListDTO.setDetail(detail);
            responseListDTO.setData(customers);
            return responseListDTO;
        } catch (Exception e) {
            logger.error("[FATAL] " + e);
            throw new ResultServiceException(e.getMessage());
        }
    }

    /** CUSTOMER DELETE */
    @Override
    public ResponseDTO customerDelete(CustomerDeleteDTO customerDeleteDTO) {
        ResponseDTO responseDTO = new ResponseDTO();
        if(customerDeleteDTO.getUserType().compareTo("admin") == 0) {
            Admin admin = authFilter.getAdminFromToken(customerDeleteDTO.getToken());
            if (admin == null) throw new ResultNotFoundException("Admin is not found");
            Boolean isPin = checkPin(customerDeleteDTO.getPin() , admin.getPin());
            if(isPin.equals(false)) throw new ResultNotFoundException("wrong pin number");
        }
        Customer customer = customerRepository.findOneByPhone(customerDeleteDTO.getPhone());
        if (customer == null) {
            throw new ResultNotFoundException("Customer is not found");
        }
        try {
            customer.setStatus(false);
            customer.setDeleted(true);
            customerRepository.save(customer);
            responseDTO.setCode(ConstantUtil.STATUS_SUCCESS);
            responseDTO.setInfo(ConstantUtil.MESSAGE_SUCCESS);
            responseDTO.setData(null);
            return responseDTO;
        } catch (Exception e) {
            logger.error("[FATAL] " + e);
            throw new ResultServiceException(e.getMessage());
        }
    }


    /** List Item Merchant for Customer */
    @Override
    public ResponseListDTO merchantItemView(FilterItemListRequestdto itemListReq) {
        ResponseListDTO responseListDTO = new ResponseListDTO();
        List<Map> items = new ArrayList<>();
        List<String> outStockDate = new ArrayList<>();
        Map detail = new HashMap();

        Merchant merchant = merchantRepository.findOneByUsername(itemListReq.getUsername());
        if (merchant == null) throw new ResultNotFoundException("Merchant is not found");
        if (merchant.getDeleted().equals(true) || merchant.getStatus().equals(0))
            throw new ResultNotFoundException("Merchant is not active");
        try {
            List<Item> listData = itemRepository.findAllOffsetLimitByMerchantIdNon(merchant.getId(), itemListReq.getSearch(),
                    itemListReq.getLimit(), itemListReq.getOffset());
            Integer countListData = itemRepository.findAllOffsetLimitByMerchantIdNonCount(merchant.getId(), itemListReq.getSearch());

            /** Item List Result */
            for (Item item : listData) {
                Map itemMap = new HashMap<>();

                List<Map> attributeItemMap = new ArrayList<>();
                List<AttributeItem> attributeItems = attributeItemService.viewByItemId(item.getId());
                for(AttributeItem attributeItem : attributeItems){
                    Map attribute = new HashMap();
                    /** Maping */
                    attribute.put("attributeId" , attributeItem.getId());
                    attribute.put("price", attributeItem.getPrice());
                    attribute.put("name", attributeItem.getName());
                    attribute.put("stock", attributeItem.getStock());
                    attribute.put("enabled", attributeItem.getEnabled());
                    attributeItemMap.add(attribute);
                }
                String picture = item.getPicture() != null ? endpointUrl + "/" + bucketName + "/" + item.getPicture() : "";
                List<Integer> availableDay = item.getType() > 1 ? item.getDayIndexList() : new ArrayList<Integer>();
                itemMap.put("id",item.getId());
                itemMap.put("itemName",item.getName());
                itemMap.put("price",item.getPrice());
                itemMap.put("attributeItems", attributeItemMap);
                itemMap.put("maxItemPerDay",item.getMaxItem());
                itemMap.put("description",item.getDescription());
                itemMap.put("assemblyTime",item.getAssemblyTime());
                itemMap.put("image",picture);
                itemMap.put("outStock",item.getOutStock());
                itemMap.put("type",item.getType());
                itemMap.put("availableCalendar", outStockDate);
                itemMap.put("availableDay",availableDay);
                items.add(itemMap);
            }
            detail.put("limit", itemListReq.getLimit());
            detail.put("total", countListData);
            detail.put("totalPage", (int) Math.ceil((double) countListData / itemListReq.getLimit()));

            responseListDTO.setCode(ConstantUtil.STATUS_SUCCESS);
            responseListDTO.setInfo(ConstantUtil.MESSAGE_SUCCESS);
            responseListDTO.setDetail(detail);
            responseListDTO.setData(items);
            return responseListDTO;
        } catch (Exception e) {
            logger.error("[FATAL]" + e);
            throw new ResultServiceException(e.getMessage());
        }
    }

    /** Calculate STOCK */
    private Integer calculatedStock(Long itemId,
                                    String deliveryDate) throws ParseException {
        Item item = itemRepository.findOneById(itemId);
        Integer stockBought = availableStockRepository.getQtyCanBuy(itemId, deliveryDate);
        Integer totalCanBuy = item.getMaxItem() - stockBought;
        return totalCanBuy;
    }

    @Override
    public ResponseListDTO merchantItemViewMisc(FilterItemListRequestdto itemListReq) {
        ResponseListDTO responseListDTO = new ResponseListDTO();
        List<Map> items = new ArrayList<>();
        Map detail = new HashMap();

        Merchant merchant = merchantRepository.findOneByUsername(itemListReq.getUsername());
        if (merchant == null) throw new ResultNotFoundException("Merchant is not found");
        if (merchant.getDeleted().equals(true) || merchant.getStatus().equals(false))
            throw new ResultNotFoundException("Merchant is not active");
        try {
            List<Item> listData = itemRepository.findAllOffsetLimitByMerchantIdMisc(merchant.getId(), itemListReq.getSearch(),
                    itemListReq.getLimit(), itemListReq.getOffset());
            Integer countListData = itemRepository.findAllOffsetLimitByMerchantIdMiscCount(merchant.getId(), itemListReq.getSearch());

            /** Item List Result */
            for (Item item : listData) {
                Map itemMap = new HashMap<>();
                String picture = item.getPicture() != null ? endpointUrl + "/" + bucketName + "/" + item.getPicture() : "";
                List<Integer> availableDay = item.getType() > 1 ? item.getDayIndexList() : new ArrayList<Integer>();
                itemMap.put("id",item.getId());
                itemMap.put("itemName",item.getName());
                itemMap.put("price",item.getPrice());
                itemMap.put("maxItemPerDay",item.getMaxItem());
                itemMap.put("description",item.getDescription());
                itemMap.put("assemblyTime",item.getAssemblyTime());
                itemMap.put("image",picture);
                itemMap.put("outStock",item.getOutStock());
                itemMap.put("type",item.getType());
                itemMap.put("availableDay",availableDay);
                items.add(itemMap);
            }
            detail.put("limit", itemListReq.getLimit());
            detail.put("total", countListData);
            detail.put("totalPage", (int) Math.ceil((double) countListData / itemListReq.getLimit()));

            responseListDTO.setCode(ConstantUtil.STATUS_SUCCESS);
            responseListDTO.setInfo(ConstantUtil.MESSAGE_SUCCESS);
            responseListDTO.setDetail(detail);
            responseListDTO.setData(items);
            return responseListDTO;
        } catch (Exception e) {
            logger.error("[FATAL]" + e);
            throw new ResultServiceException(e.getMessage());
        }
    }

    /** DETAIL ITEM MERCHANT */
    @Override
    public MerchantItemResponseDTO itemDetail(Long id) {
        MerchantItemResponseDTO merchantItemResponseDTO = new MerchantItemResponseDTO();
        List<Map> attributeItemMap = new ArrayList<>();
        Item item = itemRepository.findOneById(id);
        if (item == null) throw new ResultNotFoundException("Item is not found");
        List<AttributeItem> attributeItems = attributeItemService.viewByItemId(item.getId());
        String picture = item.getPicture() != null ? endpointUrl + "/" + bucketName + "/" + item.getPicture() : "";
        List<Integer> availableDay = item.getType() > 1 ? item.getDayIndexList() : new ArrayList<Integer>();
        try {

            for(AttributeItem attributeItem : attributeItems){
                Map attribute = new HashMap();
                /** Maping */
                attribute.put("attributeId" , attributeItem.getId());
                attribute.put("price", attributeItem.getPrice());
                attribute.put("name", attributeItem.getName());
                attribute.put("stock", attributeItem.getStock());
                attribute.put("enabled", attributeItem.getEnabled());
                attributeItemMap.add(attribute);
            }
            merchantItemResponseDTO.setId(item.getId());
            merchantItemResponseDTO.setName(item.getName());
            merchantItemResponseDTO.setPrice(item.getPrice());
            merchantItemResponseDTO.setMaxItem(item.getMaxItem());
            merchantItemResponseDTO.setDescription(item.getDescription());
            merchantItemResponseDTO.setAssemblyTime(item.getAssemblyTime());
            merchantItemResponseDTO.setImage(picture);
            merchantItemResponseDTO.setOutStock(item.getOutStock());
            merchantItemResponseDTO.setType(item.getType());
            merchantItemResponseDTO.setAvailableDay(availableDay);
            merchantItemResponseDTO.setAttributeItems(attributeItemMap);
            return merchantItemResponseDTO;
        } catch (Exception e) {
            logger.error("[FATAL] " + e);
            throw new ResultServiceException(e.getMessage());
        }
    }

    @Override
    public ResponseDTO merchantDetail(String username) {
        ResponseDTO responseDTO = new ResponseDTO();
        Map data = new HashMap();
        /** Find Merchant by Username */
        Merchant merchant = merchantRepository.findOneByUsername(username);
        if (merchant == null) throw new ResultNotFoundException("Merchant is not found");
        if (merchant.getDeleted().equals(true) || merchant.getStatus().equals(false))
            throw new ResultNotFoundException("Merchant is not active");
        try {
            /** Item Object Result */
            String picture = merchant.getPicture() != null ? endpointUrl + "/" + bucketName + "/" + merchant.getPicture() : "";
            data.put("username",merchant.getUsername());
            data.put("phoneNumber",merchant.getPhone());
            data.put("address",merchant.getAddress());
            data.put("about",merchant.getAbout());
            data.put("city",merchant.getCity());
            data.put("postalCode",merchant.getPostalCode());
            data.put("storeName",merchant.getStoreName());
            data.put("picture",picture);
            data.put("availableDelivery",merchant.getAvailableDelivery());
            responseDTO.setCode(ConstantUtil.STATUS_SUCCESS);
            responseDTO.setInfo(ConstantUtil.MESSAGE_SUCCESS);
            responseDTO.setData(data);
            return responseDTO;
        } catch (Exception e) {
            logger.error("[FATAL]" + e);
            throw new ResultServiceException(e.getMessage());
        }
    }
}
