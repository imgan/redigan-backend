package com.ecommerceapi.ecomerceapi.services.impl;

import com.ecommerceapi.ecomerceapi.dto.request.FilterListRequestDTO;
import com.ecommerceapi.ecomerceapi.dto.request.Order.FilterListAllRequestDTO;
import com.ecommerceapi.ecomerceapi.dto.request.Order.OrderCreateDTO;
import com.ecommerceapi.ecomerceapi.dto.request.Order.UpdateIncomingDTO;
import com.ecommerceapi.ecomerceapi.dto.request.Order.UpdateOngoingDTO;
import com.ecommerceapi.ecomerceapi.dto.request.PaymentGateway.ChargeRequestDTO;
import com.ecommerceapi.ecomerceapi.dto.response.PaymentGateway.ChargeResponseDTO;
import com.ecommerceapi.ecomerceapi.dto.response.ResponseDTO;
import com.ecommerceapi.ecomerceapi.dto.response.ResponseListDTO;
import com.ecommerceapi.ecomerceapi.dto.response.ResponseListDataDTO;
import com.ecommerceapi.ecomerceapi.exception.ResultNotFoundException;
import com.ecommerceapi.ecomerceapi.exception.ResultServiceException;
import com.ecommerceapi.ecomerceapi.filters.AuthFilter;
import com.ecommerceapi.ecomerceapi.model.*;
import com.ecommerceapi.ecomerceapi.repositories.*;
import com.ecommerceapi.ecomerceapi.services.OrderService;
import com.ecommerceapi.ecomerceapi.services.PaymentGatewayService;
import com.ecommerceapi.ecomerceapi.services.QontakService;
import com.ecommerceapi.ecomerceapi.util.ConstantUtil;
import com.ecommerceapi.ecomerceapi.util.DateTimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class OrderServicesImplements extends BaseServices implements OrderService {

    @Autowired
    PaymentGatewayService paymentGatewayService;

    @Autowired
    AttributeItemRepository attributeItemRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    OrderDetailAttributeRepository orderDetailAttributeRepository;

    @Autowired
    MerchantRepository merchantRepository;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    OrderDetailRepository orderDetailRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    LogOrderRepository logOrderRepository;

    @Autowired
    AuthFilter authFilter;

    @Autowired
    QontakRepository qontakRepository;

    @Autowired
    QontakService qontakService;

    @Autowired
    ItemAvailableDayRepository itemAvailableDayRepository;

    @Autowired
    AvailableStockRepository availableStockRepository;


    Logger logger = LoggerFactory.getLogger(this.getClass());

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public ResponseDTO resetOrder() {
        ResponseDTO responseDTO = new ResponseDTO();
        List<Order> listOrder = orderRepository.findOrderByStatusPending();
        for (Order order : listOrder){
            order.setOrderStatus((long) 4);
            orderRepository.save(order);
        }
        responseDTO.setData(null);
        responseDTO.setCode(ConstantUtil.STATUS_SUCCESS);
        responseDTO.setInfo(ConstantUtil.MESSAGE_SUCCESS);
        return responseDTO;
    }

    /** CREATE NEW ORDER*/
    @Transactional
    @Override
    public ResponseDTO create(OrderCreateDTO orderCreateDTO) {
        ZoneId defaultZoneId = ZoneId.systemDefault();
        Calendar c = Calendar.getInstance();
        Calendar mydate = new GregorianCalendar();
        mydate.setTime(orderCreateDTO.getDeliveryDate());
        logger.info("calendar" +c.toString() + c.getTime());
        Integer totalCanBuy = null;
        Integer assemblyTime;
        List<Integer> listOfAssemblyTime = new ArrayList<>();
        Map data = new HashMap();
        ResponseDTO responseDTO = new ResponseDTO();
        Customer customer = customerRepository.findOneByPhone(orderCreateDTO.getPhoneNumber());
        if(customer == null)
            throw new ResultNotFoundException("Customer Not found");
        Merchant merchant = merchantRepository.findOneByUsername(orderCreateDTO.getMerchantUserName());
        if(merchant == null)
            throw new ResultNotFoundException("merchant Not found");
        Order order = new Order();
        /** Check Admin Exist */
        if(orderCreateDTO.getUserType().compareTo("admin") == 0) {
            Admin admin = authFilter.getAdminFromToken(orderCreateDTO.getToken());
            if (admin == null) throw new ResultNotFoundException("Admin is not found");
            Boolean isPin = checkPin(orderCreateDTO.getPin() , admin.getPin());
            if(isPin.equals(false)) throw new ResultNotFoundException("wrong pin number");
            order.setCreatedBy(admin.getUsername());
        } else {
            order.setCreatedBy(customer.getId().toString());
        }

        String orderNumber = generateOrderNumber(merchant);

        try {
            logger.info("Insert Order transaction");
            Integer totalPrice = 0;
            Integer sequence = 1;
            for (Map items : orderCreateDTO.getItemDetail()) {
                Item item = itemRepository.findOneById(Long.valueOf(items.get("itemId").toString()));

                String dt = sdf.format(orderCreateDTO.getDeliveryDate());
                logger.info("type " + item.getType().toString());
                /** TYPE ITEM 1 */
                if(item.getType() == 1){
                    c.setTime(sdf.parse(dt));
                    totalCanBuy = calculatedStock(Long.valueOf(items.get("itemId").toString()),
                            sdf.format(orderCreateDTO.getDeliveryDate()));
                    Integer stockNow = totalCanBuy;
                    logger.info("Total Can Buy " + totalCanBuy);
                    if(totalCanBuy < Integer.valueOf(items.get("qty").toString())){
                        for(Integer i = 0; i<= 365; i++){
                            dt = sdf.format(c.getTime());  // dt is now the new date
                            totalCanBuy = calculatedStock(Long.valueOf(items.get("itemId").toString()),
                                    dt);
                            if(totalCanBuy >=  Integer.valueOf(items.get("qty").toString())){
                                break;
                            }
                            c.add(Calendar.DATE, 1);  // number of days to add
                        }
                        data.put("itemId", (String) items.get("itemId"));
                        data.put("availableStockNow",stockNow);
                        data.put("availableAt",dt);
                        data.put("availableStock", totalCanBuy);
                        responseDTO.setCode(ConstantUtil.STATUS_DATA_NOT_FOUND);
                        responseDTO.setInfo(ConstantUtil.OUT_OF_STOCK);
                        responseDTO.setData(data);
                        return responseDTO;
                    }
                } else {

                    /** TYPE ITEM 2 */
                    List<Integer> itemAvailableDay = itemAvailableDayRepository.getAvailableDayByItem(
                            Long.valueOf(items.get("itemId").toString()));
                    dt = sdf.format(c.getTime());
                    Integer dayOfWeek = mydate.get(Calendar.DAY_OF_WEEK);
                    Boolean contains = itemAvailableDay.contains(dayOfWeek);
                    if(!contains){
                        for(Integer i = 0; i<= 365; i++){
                            dt = sdf.format(c.getTime());  // dt is now the new date
                            dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
                            totalCanBuy = calculatedStock(Long.valueOf(items.get("itemId").toString()),
                                    dt);
                            contains = itemAvailableDay.contains(dayOfWeek);
                            if(totalCanBuy >=  Integer.valueOf(items.get("qty").toString())){
                                if(contains){
                                    break;
                                }
                            }

                            c.add(Calendar.DATE, 1);  // number of days to add
                        }
                        data.put("availableAt",sdf.format(c.getTime()));
                        data.put("availableStock", totalCanBuy);
                        responseDTO.setCode(ConstantUtil.STATUS_DATA_NOT_FOUND);
                        responseDTO.setInfo(ConstantUtil.OUT_OF_STOCK);
                        responseDTO.setData(data);
                        return responseDTO;
                    }
                    totalCanBuy = calculatedStock(Long.valueOf(items.get("itemId").toString()),
                            sdf.format(orderCreateDTO.getDeliveryDate()));
                    logger.info("Total Can Buy " + totalCanBuy);
                    if(totalCanBuy <  Integer.valueOf(items.get("qty").toString())){
                        Integer stockNow = totalCanBuy;
                        for(Integer i = 0; i<= 365; i++){
                            dt = sdf.format(c.getTime());  // dt is now the new date
                            dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
                            totalCanBuy = calculatedStock(Long.valueOf(items.get("itemId").toString()),
                                    dt);
                            contains = itemAvailableDay.contains(dayOfWeek);
                            if(totalCanBuy >=  Integer.valueOf(items.get("qty").toString())){
                                if(contains){
                                    break;
                                }
                            }

                            c.add(Calendar.DATE, 1);  // number of days to add
                        }
                        data.put("itemId", (String) items.get("itemId"));
                        data.put("availableStockNow",stockNow);
                        data.put("availableAt",sdf.format(c.getTime()));
                        data.put("availableStock", totalCanBuy);
                        responseDTO.setCode(ConstantUtil.STATUS_DATA_NOT_FOUND);
                        responseDTO.setInfo(ConstantUtil.OUT_OF_STOCK);
                        responseDTO.setData(data);
                        return responseDTO;
                    }
                }

                /** INSERT Order detail */
                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setItemId(Long.valueOf(items.get("itemId").toString()));
                orderDetail.setAdditionalInfo(items.get("info").toString());
                orderDetail.setCreatedDate(new Date());
                orderDetail.setOrderNumber(orderNumber);
                orderDetail.setQty(Integer.valueOf(items.get("qty").toString()));
                Integer price = itemRepository.findPriceItemById(Long.valueOf(items.get("itemId").toString()));
                totalPrice += price * Integer.valueOf(items.get("qty").toString());
                orderDetailRepository.save(orderDetail);
                /** INSERT ATTRIBUTE ITEM */
                List<Map> attribute = (List<Map>) items.get("attribute");
                for(Integer i = 1; i<= Integer.valueOf(items.get("qty").toString()); i++) {
                    for (Map map : attribute) {
                        OrderAttributeDetail orderAttributeDetail = new OrderAttributeDetail();
                        AttributeItem attributeItem = attributeItemRepository.findOneByIdAndItemId(
                                Long.valueOf(map.get("attributeItemId").toString()),
                                Long.valueOf(items.get("itemId").toString()));
                        orderAttributeDetail.setOrderNumber(orderNumber);
                        orderAttributeDetail.setAttributeItem(attributeItem);
                        orderAttributeDetail.setSequence(sequence);
                        orderAttributeDetail.setCreatedDate(new Date());
                        orderAttributeDetail.setItem(item);
                        orderAttributeDetail.setAdditionalInfo(String.valueOf(map.get("additionalInfo")));
                        if (attributeItem == null)
                            throw new ResultNotFoundException("Attribute item not found on this item ");

                        totalPrice += Integer.valueOf(attributeItem.getPrice().toString());
                        orderDetailAttributeRepository.save(orderAttributeDetail);

                        if (attributeItem.getStock() < 1)
                            throw new ResultNotFoundException("stock not found " + attributeItem.getName());

                        attributeItem.setStock(attributeItem.getStock() - 1);
                        attributeItemRepository.save(attributeItem);
                    }
                }
                /** Insert Available stock */
                AvailableStock availableStock = new AvailableStock();
                availableStock.setStockQty(Integer.valueOf(items.get("qty").toString()));
                availableStock.setDate(orderCreateDTO.getDeliveryDate());
                availableStock.setOrderNumber(orderNumber);
                availableStock.setItem(item);
                availableStockRepository.save(availableStock);
                assemblyTime = getAssemblyTime(Long.valueOf(items.get("itemId").toString()));
                listOfAssemblyTime.add(assemblyTime);
                sequence++;
            }

            Integer maxAssemblyTime = getMaxValue(listOfAssemblyTime);
            LocalDate today = new Date().toInstant()
                    .atZone(defaultZoneId)
                    .toLocalDate();

            LocalDate deliveryDate = today.plusDays(maxAssemblyTime);
            Date dateDeliveryFinal = Date.from(deliveryDate.atStartOfDay(defaultZoneId).toInstant());

            /**COMPARE DELIVERY DATE */
            if(!dateDeliveryFinal.before(orderCreateDTO.getDeliveryDate())){
                /** Delivery Date < Assembly Date */
                data.put("availableDeliveryDateAt",sdf.format(dateDeliveryFinal));
                responseDTO.setCode(ConstantUtil.STATUS_DATA_NOT_FOUND);
                responseDTO.setInfo(ConstantUtil.MESSAGE_DATA_NOT_FOUND);
                responseDTO.setData(data);
                return responseDTO;
            }

            order.setDeliveryTime(orderCreateDTO.getDeliveryTime());
            order.setAmount(totalPrice);
            order.setDeliveryFee(0);
            order.setAddress(customer.getAddress());
            order.setCreatedDate(new Date());
            order.setOrderNumber(orderNumber);
            order.setPaid(false);
            order.setOrderStatus(Long.valueOf(1));
            String myDate = getDateFormat(dateDeliveryFinal);
            order.setDeliveryDate(orderCreateDTO.getDeliveryDate());
            order.setMerchant(merchant);
            order.setCustomerId(customer.getId());
            order.setCustomerName(customer.getCustomerName());
            orderRepository.save(order);

            /** SEND WHATSAPP QONTAK */
            QontakConfig qontakConfig = qontakRepository.findOneByName("ORDER_CREATE");
            Boolean isSend = qontakService.sendOrderMessageIncoming(qontakConfig,merchant,order);


            data.put("order_number", order.getOrderNumber());

            LogOrder logOrder = new LogOrder();
            logOrder.setData(data.toString());
            logOrder.setCreatedDate(new Date());
            logOrder.setStatus(true);
            logOrder.setOrderNumber(order.getOrderNumber());
            logOrderRepository.save(logOrder);

            responseDTO.setCode(ConstantUtil.STATUS_SUCCESS);
            responseDTO.setInfo(ConstantUtil.MESSAGE_SUCCESS);
            responseDTO.setData(data);
            return responseDTO;
        } catch (Exception e) {
            logger.error("[FATAL] " + e);
            throw new ResultServiceException(e.getMessage());
        }
    }

    public static List<?> convertObjectToList(Object obj) {
        List<?> list = new ArrayList<>();
        if (obj.getClass().isArray()) {
            list = Arrays.asList((Object[])obj);
        } else if (obj instanceof Collection) {
            list = new ArrayList<>((Collection<?>)obj);
        }
        return list;
    }

    private String getDateFormat(Date dateCurrent) throws ParseException {
        DateFormat formatter = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH);
        formatter.setTimeZone(TimeZone.getTimeZone("Asia/Jakarta"));
        Date date = (Date)formatter.parse(dateCurrent.toString());
        System.out.println(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        String formatedDate = cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH) + 1) + "-" + cal.get(Calendar.DATE);
        return formatedDate;
    }

    /** Calculate STOCK */
    private Integer calculatedStock(Long itemId,
                                    String deliveryDate) throws ParseException {
        Item item = itemRepository.findOneById(itemId);
        Integer stockBought = availableStockRepository.getQtyCanBuy(itemId, deliveryDate);
        Integer totalCanBuy = item.getMaxItem() - stockBought;
        return totalCanBuy;
    }

    public Integer getWorkingDaysBetweenTwoDates(Date startDate, Date endDate, List<ItemAvailableDay> itemAvailableDay) {
        Calendar startCal = Calendar.getInstance();
        startCal.setTime(startDate);

        Calendar endCal = Calendar.getInstance();
        endCal.setTime(endDate);

        int workDays = 0;

        //Return 0 if start and end are the same
        if (startCal.getTimeInMillis() == endCal.getTimeInMillis()) {
            return 0;
        }

        if (startCal.getTimeInMillis() > endCal.getTimeInMillis()) {
            startCal.setTime(endDate);
            endCal.setTime(startDate);
        }


        for (ItemAvailableDay x : itemAvailableDay ){
            workDays++;
        }
        return workDays;
    }

    /** DELIVERY DATE
     * @return*/
    private Integer getAssemblyTime(Long itemId) {
        Item item = itemRepository.findOneById(itemId);
        return item.getAssemblyTime();
    }

    //Find maximum (largest) value in array using loop
    private static int getMaxValue(List<Integer> listOfIntegers){
        Integer max = listOfIntegers
                .stream()
                .mapToInt(v -> v)
                .max().orElseThrow(NoSuchElementException::new);
        return max;
    }

    /** AUTO GENERATE ORDER NUMBER*/
    @Override
    public String generateOrderNumber(Merchant merchant) {
        String orderNumber = DateTimeUtil.convertDateToStringCustomized(new Date(), "yyyyMMddHHmmss");
        orderNumber = "O"+orderNumber+merchant.getId();
        return orderNumber;
    }

    /** GET INCOMING ORDER BY MERCHANT*/
    @Override
    public ResponseListDTO getIncomingOrderByMerchant(FilterListRequestDTO filterListRequestDTO, String token) {
        ResponseListDTO responseListDTO = new ResponseListDTO();
        List<Map> orderList = new ArrayList<>();
        Map detail = new HashMap();
        Merchant merchant;

        /** Check Admin Exist */
        if(filterListRequestDTO.getUserType().compareTo("admin") == 0) {
            Admin admin = authFilter.getAdminFromToken(token);
            if (admin == null) throw new ResultNotFoundException("Admin is not found");
            merchant = merchantRepository.findOneByUsername(filterListRequestDTO.getMerchant());
        } else {
            merchant = authFilter.getMerchantFromToken(token);
        }

        if(merchant == null)
            throw new ResultNotFoundException("merchant not found");
        try {
            List<Order> listOrder = orderRepository.getIncomingOrderByMerchant(merchant.getId(),
                    filterListRequestDTO.getOffset(), filterListRequestDTO.getLimit(), filterListRequestDTO.getSearch(),
                    filterListRequestDTO.getStartDate(), filterListRequestDTO.getEndDate());
            Integer countListOrder = orderRepository.getIncomingOrderByMerchantCount(merchant.getId(),
                    filterListRequestDTO.getSearch(), filterListRequestDTO.getStartDate(), filterListRequestDTO.getEndDate());
            for (Order order : listOrder) {
                List<Map> ListitemDetailMap = new ArrayList<>();
                Map orderMap = new HashMap<>();
                Map customerMap = new HashMap();
                List<Map> orderDetails = orderRepository.findTrackOneByOrderNumber(order.getOrderNumber());
                Integer sequence = 1;
                for (Map orderDetail : orderDetails){
                    Map orderMapItem = new HashMap<>();
                    List<Map> OrderDetailAMap = new ArrayList<>();
                    orderMapItem.put("itemName",orderDetail.get("itemName"));
                    orderMapItem.put("additionalInfo", orderDetail.get("info"));
                    orderMapItem.put("isMiscellaneous", orderDetail.get("is_miscellaneous"));
                    orderMapItem.put("qty",orderDetail.get("qty"));
                    List<OrderAttributeDetail> orderAttributeDetails = orderDetailAttributeRepository
                            .findByOrderNumberAndItemIdAndSequence(order.getOrderNumber(),
                                    Long.valueOf(orderDetail.get("itemId").toString()),
                            sequence);
                    logger.info("sequence " + sequence);
                    for (OrderAttributeDetail orderAttributeDetail : orderAttributeDetails){
                        Map orderMapAItem = new HashMap<>();
                        orderMapAItem.put("attributeId",orderAttributeDetail.getAttributeItem().getId());
                        orderMapAItem.put("name", orderAttributeDetail.getAttributeItem().getName());
                        orderMapAItem.put("price", orderAttributeDetail.getAttributeItem().getPrice());
                        OrderDetailAMap.add(orderMapAItem);
                        orderMapItem.put("attributeItem", OrderDetailAMap);
                    }
                    ListitemDetailMap.add(orderMapItem);
                    sequence++;
                }
                Customer customer = customerRepository.findOneById(order.getCustomerId());
                if(customer == null) {
                    customer = new Customer();
                    customer.setId(0L);
                }
                customerMap.put("customerName", customer.getCustomerName());
                customerMap.put("address",customer.getAddress());
                customerMap.put("phoneNumber", customer.getPhone());
                customerMap.put("city",customer.getCity());
                customerMap.put("postalCode",customer.getPostalCode());

                orderMap.put("isPaid",order.getPaid());
                orderMap.put("deliveryDate", sdf.format(order.getDeliveryDate()));
                orderMap.put("itemDetail",ListitemDetailMap);
                orderMap.put("orderNumber",order.getOrderNumber());
                orderMap.put("amount",order.getAmount());
                orderMap.put("createdDate",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(order.getCreatedDate()));
                orderMap.put("orderStatus",order.getOrderStatus());
                orderMap.put("deliveryFee",order.getDeliveryFee());
                orderMap.put("deliveryDate",sdf.format(order.getDeliveryDate()));
                orderMap.put("additionalInfo",order.getAdditionalInfo());
                orderMap.put("additionalFee",order.getAdditionalFee());
                orderMap.put("additionalFee",order.getAdditionalFee());
                orderMap.put("availableDelivery",order.getDeliveryTime());
                orderMap.put("customer",customerMap);
                orderMap.put("createdAt", DateTimeUtil.convertDateToStringCustomized(merchant.getCreatedDate(),"dd-MM-yyyy"));
                orderList.add(orderMap);
            }
            detail.put("limit", filterListRequestDTO.getLimit());
            detail.put("total", countListOrder);
            detail.put("totalPage", (int) Math.ceil((double) countListOrder / filterListRequestDTO.getLimit()));

            responseListDTO.setCode(ConstantUtil.STATUS_SUCCESS);
            responseListDTO.setInfo(ConstantUtil.MESSAGE_SUCCESS);
            responseListDTO.setDetail(detail);
            responseListDTO.setData(orderList);
            return responseListDTO;
        } catch (Exception e){
            logger.error("[FATAL] " + e);
            throw new ResultServiceException(e.getMessage());
        }
    }



    /** GET ONGOING ORDER BY MERCHANT*/
    @Override
    public ResponseListDTO getOngoingOrderByMerchant(FilterListRequestDTO filterListRequestDTO, String token) {
        ResponseListDTO responseListDTO = new ResponseListDTO();
        List<Map> orderList = new ArrayList<>();
        Map detail = new HashMap();
        Merchant merchant;

        /** Check Admin Exist */
        if(filterListRequestDTO.getUserType().compareTo("admin") == 0) {
            Admin admin = authFilter.getAdminFromToken(token);
            if (admin == null) throw new ResultNotFoundException("Admin is not found");
            merchant = merchantRepository.findOneByUsername(filterListRequestDTO.getMerchant());
        } else {
            merchant = authFilter.getMerchantFromToken(token);
        }

        if(merchant == null)
            throw new ResultNotFoundException("merchant not found");
        try {
            List<Order> listOrder = orderRepository.getOngoingOrderByMerchant(merchant.getId(),
                    filterListRequestDTO.getOffset(), filterListRequestDTO.getLimit(),filterListRequestDTO.getSearch(),
                    filterListRequestDTO.getPaid(), filterListRequestDTO.getStartDate(), filterListRequestDTO.getEndDate());
            Integer countListOrder = orderRepository.getOngoingOrderByMerchantCount(merchant.getId(),
                    filterListRequestDTO.getSearch(), filterListRequestDTO.getPaid(), filterListRequestDTO.getStartDate(),
                    filterListRequestDTO.getEndDate());
            for (Order order : listOrder) {
                Map orderMap = new HashMap<>();
                Map customerMap = new HashMap();
                List<Map> ListItemDetailMap = new ArrayList<>();
                List<Map> orderDetails = orderRepository.findTrackOneByOrderNumber(order.getOrderNumber());
                Integer sequence = 1;
                for (Map orderDetail : orderDetails){
                    List<Map> OrderDetailAMap = new ArrayList<>();
                    Map orderMapItem = new HashMap<>();
                    orderMapItem.put("itemName",orderDetail.get("itemName"));
                    orderMapItem.put("additionalInfo", orderDetail.get("info"));
                    orderMapItem.put("qty",orderDetail.get("qty"));
                    orderMapItem.put("isMiscellaneous", orderDetail.get("is_miscellaneous"));
                    List<OrderAttributeDetail> orderAttributeDetails = orderDetailAttributeRepository
                            .findByOrderNumberAndItemIdAndSequence(order.getOrderNumber(),
                                    Long.valueOf(orderDetail.get("itemId").toString().toString()),
                                    sequence);
                    for (OrderAttributeDetail orderAttributeDetail : orderAttributeDetails){
                        Map orderMapAItem = new HashMap<>();
                        orderMapAItem.put("attributeId",orderAttributeDetail.getAttributeItem().getId());
                        orderMapAItem.put("name", orderAttributeDetail.getAttributeItem().getName());
                        orderMapAItem.put("price", orderAttributeDetail.getAttributeItem().getPrice());
                        OrderDetailAMap.add(orderMapAItem);
                        orderMapItem.put("attributeItem", OrderDetailAMap);
                    }
                    ListItemDetailMap.add(orderMapItem);
                    sequence++;
                }
                Customer customer = customerRepository.findOneById(order.getCustomerId());
                if(customer == null) {
                    customer = new Customer();
                    customer.setId(0L);
                }
                customerMap.put("customerName", customer.getCustomerName());
                customerMap.put("address",customer.getAddress());
                customerMap.put("phoneNumber", customer.getPhone());
                customerMap.put("city",customer.getCity());
                customerMap.put("postalCode",customer.getPostalCode());

                orderMap.put("trackingLink",order.getTrackingLink());
                orderMap.put("isPaid",order.getPaid());
                orderMap.put("deliveryDate", sdf.format(order.getDeliveryDate()));
                orderMap.put("itemDetail",ListItemDetailMap);
                orderMap.put("orderNumber",order.getOrderNumber());
                orderMap.put("amount",order.getAmount());
                orderMap.put("createdDate",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(order.getCreatedDate()));
                orderMap.put("orderStatus",order.getOrderStatus());
                orderMap.put("deliveryFee",order.getDeliveryFee());
                orderMap.put("deliveryDate",sdf.format(order.getDeliveryDate()));
                orderMap.put("additionalInfo",order.getAdditionalInfo());
                orderMap.put("additionalFee",order.getAdditionalFee());
                orderMap.put("availableDelivery",order.getDeliveryTime());
                orderMap.put("customer",customerMap);
                orderMap.put("createdAt", DateTimeUtil.convertDateToStringCustomized(merchant.getCreatedDate(),"dd-MM-yyyy"));
                orderList.add(orderMap);
            }
            detail.put("limit", filterListRequestDTO.getLimit());
            detail.put("total", countListOrder);
            detail.put("totalPage", (int) Math.ceil((double) countListOrder / filterListRequestDTO.getLimit()));

            responseListDTO.setCode(ConstantUtil.STATUS_SUCCESS);
            responseListDTO.setInfo(ConstantUtil.MESSAGE_SUCCESS);
            responseListDTO.setDetail(detail);
            responseListDTO.setData(orderList);
            return responseListDTO;
        } catch (Exception e){
            logger.error("[FATAL] " + e);
            throw new ResultServiceException(e.getMessage());
        }
    }

    /** GET SETTLE ORDER BY MERCHANT*/
    @Override
    public ResponseListDTO getSettleOrderByMerchant(FilterListRequestDTO filterListRequestDTO, String token) {
        ResponseListDTO responseListDTO = new ResponseListDTO();
        List<Map> orderList = new ArrayList<>();
        Map detail = new HashMap();
        Merchant merchant;

        /** Check Admin Exist */
        if(filterListRequestDTO.getUserType().compareTo("admin") == 0) {
            Admin admin = authFilter.getAdminFromToken(token);
            if (admin == null) throw new ResultNotFoundException("Admin is not found");
            merchant = merchantRepository.findOneByUsername(filterListRequestDTO.getMerchant());
        } else {
            merchant = authFilter.getMerchantFromToken(token);
        }
        if(merchant == null)
            throw new ResultNotFoundException("merchant not found");
        try {
            List<Order> listOrder = orderRepository.getSettleOrderByMerchant(merchant.getId(), filterListRequestDTO.getOffset(),
                    filterListRequestDTO.getLimit(), filterListRequestDTO.getSearch(), filterListRequestDTO.getStartDate(),
                    filterListRequestDTO.getEndDate());
            Integer countListOrder = orderRepository.getSettleOrderByMerchantCount(merchant.getId(), filterListRequestDTO.getSearch(),
                    filterListRequestDTO.getStartDate(), filterListRequestDTO.getEndDate());
            for (Order order : listOrder) {
                Map orderMap = new HashMap<>();
                Map customerMap = new HashMap();
                List<Map> ListItemDetailMap = new ArrayList<>();
                List<Map> orderDetails = orderRepository.findTrackOneByOrderNumber(order.getOrderNumber());
                Integer sequence = 1;
                for (Map orderDetail : orderDetails){
                    Map orderMapItem = new HashMap<>();
                    List<Map> OrderDetailAMap = new ArrayList<>();
                    orderMapItem.put("itemName",orderDetail.get("itemName"));
                    orderMapItem.put("additionalInfo", orderDetail.get("info"));
                    orderMapItem.put("qty",orderDetail.get("qty"));
                    orderMapItem.put("isMiscellaneous", orderDetail.get("is_miscellaneous"));
                    List<OrderAttributeDetail> orderAttributeDetails = orderDetailAttributeRepository
                            .findByOrderNumberAndItemIdAndSequence(order.getOrderNumber(),
                                    Long.valueOf(orderDetail.get("itemId").toString()),
                                    sequence);
                    for (OrderAttributeDetail orderAttributeDetail : orderAttributeDetails){
                        Map orderMapAItem = new HashMap<>();
                        orderMapAItem.put("attributeId",orderAttributeDetail.getAttributeItem().getId());
                        orderMapAItem.put("name", orderAttributeDetail.getAttributeItem().getName());
                        orderMapAItem.put("price", orderAttributeDetail.getAttributeItem().getPrice());
                        OrderDetailAMap.add(orderMapAItem);
                        orderMapItem.put("attributeItem", OrderDetailAMap);
                    }
                    ListItemDetailMap.add(orderMapItem);
                    sequence++;
                }
                Customer customer = customerRepository.findOneById(order.getCustomerId());
                if(customer == null) {
                    customer = new Customer();
                    customer.setId(0L);
                }
                customerMap.put("customerName", customer.getCustomerName());
                customerMap.put("address",customer.getAddress());
                customerMap.put("phoneNumber", customer.getPhone());
                customerMap.put("city",customer.getCity());
                customerMap.put("postalCode",customer.getPostalCode());

                orderMap.put("itemDetail",ListItemDetailMap);
                orderMap.put("orderNumber",order.getOrderNumber());
                orderMap.put("amount",order.getAmount());
                orderMap.put("reason",order.getReason());
                orderMap.put("isPaid",order.getPaid());
                orderMap.put("createdDate",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(order.getCreatedDate()));
                orderMap.put("orderStatus",order.getOrderStatus());
                orderMap.put("deliveryFee",order.getDeliveryFee());
                orderMap.put("additionalInfo",order.getAdditionalInfo());
                orderMap.put("additionalFee",order.getAdditionalFee());
                orderMap.put("availableDelivery",order.getDeliveryTime());
                orderMap.put("deliveryDate",sdf.format(order.getDeliveryDate()));


                orderMap.put("customer",customerMap);
                orderMap.put("createdAt", DateTimeUtil.convertDateToStringCustomized(merchant.getCreatedDate(),"dd-MM-yyyy"));
                orderList.add(orderMap);
            }
            detail.put("limit", filterListRequestDTO.getLimit());
            detail.put("total", countListOrder);
            detail.put("totalPage", (int) Math.ceil((double) countListOrder / filterListRequestDTO.getLimit()));

            responseListDTO.setCode(ConstantUtil.STATUS_SUCCESS);
            responseListDTO.setInfo(ConstantUtil.MESSAGE_SUCCESS);
            responseListDTO.setDetail(detail);
            responseListDTO.setData(orderList);
            return responseListDTO;
        } catch (Exception e){
            logger.error("[FATAL] " + e);
            throw new ResultServiceException(e.getMessage());
        }
    }

    /** GET ALL ORDER */
    @Override
    public ResponseListDataDTO<Map> getAllOrder(FilterListAllRequestDTO filterListAllRequestDTO, String token) {
        ArrayList<Map> orderList = new ArrayList<>();
        Merchant merchant;
        List<Integer> status = new ArrayList<>();

        /** Check Admin Exist */
        if(filterListAllRequestDTO.getUserType().compareTo("admin") == 0) {
            Admin admin = authFilter.getAdminFromToken(token);
            if (admin == null) throw new ResultNotFoundException("Admin is not found");
            if(isExistingDataAndStringValue(filterListAllRequestDTO.getMerchant())) {
                merchant = merchantRepository.findOneByUsername(filterListAllRequestDTO.getMerchant());
            } else {
                merchant = new Merchant();
                merchant.setId(0L);
            }
        } else {
            merchant = authFilter.getMerchantFromToken(token);
        }

        if (merchant == null)
            throw new ResultNotFoundException("merchant not found");
        try {
            if(filterListAllRequestDTO.getStatus().compareTo("incoming") == 0) {
                status = new ArrayList<>( List.of(1) );
            } else if(filterListAllRequestDTO.getStatus().compareTo("ongoing") == 0) {
                status = new ArrayList<>( List.of(2) );
            } else if(filterListAllRequestDTO.getStatus().compareTo("settled") == 0) {
                status = new ArrayList<>( List.of(3, 4) );
            } else {
                status = new ArrayList<>( List.of(1, 2, 3, 4) );
            }

            List<Order> listOrder = orderRepository.getAllOrder(merchant.getId(),
                    filterListAllRequestDTO.getOffset(), filterListAllRequestDTO.getLimit(), filterListAllRequestDTO.getSearch(),
                    filterListAllRequestDTO.getStartDate(), filterListAllRequestDTO.getEndDate(), status);
            Integer countListOrder = orderRepository.getAllOrderCount(merchant.getId(), filterListAllRequestDTO.getSearch(),
                    filterListAllRequestDTO.getStartDate(), filterListAllRequestDTO.getEndDate(), status);
            for (Order order : listOrder) {
                List<Map> ListitemDetailMap = new ArrayList<>();
                Map orderMap = new HashMap<>();
                Map customerMap = new HashMap();
                List<Map> orderDetails = orderRepository.findTrackOneByOrderNumber(order.getOrderNumber());
                Integer sequence = 1;
                for (Map orderDetail : orderDetails) {
                    List<Map> OrderDetailAMap = new ArrayList<>();
                    Map orderMapItem = new HashMap<>();
                    orderMapItem.put("itemName", orderDetail.get("itemName"));
                    orderMapItem.put("additionalInfo", orderDetail.get("info"));
                    orderMapItem.put("isMiscellaneous", orderDetail.get("is_miscellaneous"));
                    orderMapItem.put("qty", orderDetail.get("qty"));
                    orderMapItem.put("price", orderDetail.get("price"));
                    List<OrderAttributeDetail> orderAttributeDetails = orderDetailAttributeRepository
                            .findByOrderNumberAndItemIdAndSequence(order.getOrderNumber(),
                                    Long.valueOf(orderDetail.get("itemId").toString()),
                                    sequence);
                    for (OrderAttributeDetail orderAttributeDetail : orderAttributeDetails){
                        Map orderMapAItem = new HashMap<>();
                        orderMapAItem.put("attributeId",orderAttributeDetail.getAttributeItem().getId());
                        orderMapAItem.put("name", orderAttributeDetail.getAttributeItem().getName());
                        orderMapAItem.put("price", orderAttributeDetail.getAttributeItem().getPrice());
                        OrderDetailAMap.add(orderMapAItem);
                        orderMapItem.put("attributeItem", OrderDetailAMap);
                    }
                    ListitemDetailMap.add(orderMapItem);
                    sequence++;
                }

                Customer customer = customerRepository.findOneById(order.getCustomerId());
                if(customer == null) {
                    customer = new Customer();
                    customer.setId(0L);
                }
                customerMap.put("customerName", customer.getCustomerName());
                customerMap.put("address", customer.getAddress());
                customerMap.put("phoneNumber", customer.getPhone());
                customerMap.put("city", customer.getCity());
                customerMap.put("postalCode", customer.getPostalCode());

                orderMap.put("merchantName", order.getMerchant().getUsername());
                orderMap.put("storeName",order.getMerchant().getStoreName());
                orderMap.put("isPaid", order.getPaid());
                orderMap.put("reason", order.getReason());
                orderMap.put("deliveryDate", sdf.format(order.getDeliveryDate()));
                orderMap.put("itemDetail", ListitemDetailMap);
                orderMap.put("orderNumber", order.getOrderNumber());
                orderMap.put("amount", order.getAmount());
                orderMap.put("createdDate", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(order.getCreatedDate()));
                orderMap.put("orderStatus", order.getOrderStatus());
                orderMap.put("deliveryFee", order.getDeliveryFee());
                orderMap.put("additionalInfo", order.getAdditionalInfo());
                orderMap.put("additionalFee", order.getAdditionalFee());
                orderMap.put("availableDelivery", order.getDeliveryTime());
                orderMap.put("trackingLink", order.getTrackingLink());
                orderMap.put("customer", customerMap);
                orderMap.put("createdAt", sdf.format(order.getCreatedDate()));
                if(order.getUpdatedDate() != null) {
                    orderMap.put("updatedAt", sdf.format(order.getUpdatedDate()));
                } else {
                    orderMap.put("updatedAt", sdf.format(order.getCreatedDate()));
                }
                orderList.add(orderMap);
            }
            Integer page = (int) Math.ceil((double) countListOrder / filterListAllRequestDTO.getLimit());

            ResponseListDataDTO<Map> responseListDataDTO = new ResponseListDataDTO<>(filterListAllRequestDTO.getLimit(),
                    page, countListOrder, filterListAllRequestDTO.getSearch(), orderList);

            return responseListDataDTO;
        } catch (Exception e) {
            logger.error("[FATAL] " + e);
            throw new ResultServiceException(e.getMessage());
        }
    }


    /** GET ALL ORDER LIST */
    @Override
    public ResponseListDataDTO<Map> getAllOrderList(FilterListAllRequestDTO filterListAllRequestDTO, String token) {
        ArrayList<Map> orderList = new ArrayList<>();
        Merchant merchant;
        List<Integer> status = new ArrayList<>();
        Integer isOngoing = 0;

        /** Check Admin Exist */
        if(filterListAllRequestDTO.getUserType().compareTo("admin") == 0) {
            Admin admin = authFilter.getAdminFromToken(token);
            if (admin == null) throw new ResultNotFoundException("Admin is not found");
            if(isExistingDataAndStringValue(filterListAllRequestDTO.getMerchant())) {
                merchant = merchantRepository.findOneByUsername(filterListAllRequestDTO.getMerchant());
            } else {
                merchant = new Merchant();
                merchant.setId(0L);
            }
        } else {
            merchant = authFilter.getMerchantFromToken(token);
        }

        if (merchant == null)
            throw new ResultNotFoundException("merchant not found");
        try {
            if(filterListAllRequestDTO.getStatus().compareTo("incoming") == 0) {
                status = new ArrayList<>( List.of(1) );
            } else if(filterListAllRequestDTO.getStatus().compareTo("ongoing") == 0) {
                status = new ArrayList<>( List.of(2) );
                isOngoing = 1;
            } else if(filterListAllRequestDTO.getStatus().compareTo("settled") == 0) {
                status = new ArrayList<>( List.of(3, 4) );
            } else {
                status = new ArrayList<>( List.of(1, 2, 3, 4) );
            }

            List<Order> listOrder = orderRepository.getAllOrderList(merchant.getId(), filterListAllRequestDTO.getSearch(),
                    filterListAllRequestDTO.getStartDate(), filterListAllRequestDTO.getEndDate(), status, isOngoing);
            for (Order order : listOrder) {
                List<Map> ListitemDetailMap = new ArrayList<>();
                Map orderMap = new HashMap<>();
                Map customerMap = new HashMap();
                List<Map> orderDetails = orderRepository.findTrackOneByOrderNumber(order.getOrderNumber());
                Integer sequence = 1;
                for (Map orderDetail : orderDetails) {
                    List<Map> OrderDetailAMap = new ArrayList<>();
                    Map orderMapItem = new HashMap<>();
                    orderMapItem.put("itemName", orderDetail.get("itemName"));
                    orderMapItem.put("additionalInfo", orderDetail.get("info"));
                    orderMapItem.put("isMiscellaneous", orderDetail.get("is_miscellaneous"));
                    orderMapItem.put("qty", orderDetail.get("qty"));
                    orderMapItem.put("price", orderDetail.get("price"));
                    List<OrderAttributeDetail> orderAttributeDetails = orderDetailAttributeRepository
                            .findByOrderNumberAndItemIdAndSequence(order.getOrderNumber(),
                                    Long.valueOf(orderDetail.get("itemId").toString()),
                                    sequence);
                    for (OrderAttributeDetail orderAttributeDetail : orderAttributeDetails){
                        Map orderMapAItem = new HashMap<>();
                        orderMapAItem.put("attributeId",orderAttributeDetail.getAttributeItem().getId());
                        orderMapAItem.put("name", orderAttributeDetail.getAttributeItem().getName());
                        orderMapAItem.put("price", orderAttributeDetail.getAttributeItem().getPrice());
                        OrderDetailAMap.add(orderMapAItem);
                        orderMapItem.put("attributeItem", OrderDetailAMap);
                    }
                    ListitemDetailMap.add(orderMapItem);
                }

                Customer customer = customerRepository.findOneById(order.getCustomerId());
                if(customer == null) {
                    customer = new Customer();
                    customer.setId(0L);
                }
                customerMap.put("customerName", customer.getCustomerName());
                customerMap.put("address", customer.getAddress());
                customerMap.put("phoneNumber", customer.getPhone());
                customerMap.put("city", customer.getCity());
                customerMap.put("postalCode", customer.getPostalCode());

                orderMap.put("merchantName", order.getMerchant().getUsername());
                orderMap.put("storeName", order.getMerchant().getStoreName());
                orderMap.put("isPaid", order.getPaid());
                orderMap.put("deliveryDate", sdf.format(order.getDeliveryDate()));
                orderMap.put("itemDetail", ListitemDetailMap);
                orderMap.put("orderNumber", order.getOrderNumber());
                orderMap.put("amount", order.getAmount());
                orderMap.put("createdDate", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(order.getCreatedDate()));
                orderMap.put("orderStatus", order.getOrderStatus());
                orderMap.put("deliveryFee", order.getDeliveryFee());
                orderMap.put("additionalInfo", order.getAdditionalInfo());
                orderMap.put("additionalFee", order.getAdditionalFee());
                orderMap.put("availableDelivery", order.getDeliveryTime());
                orderMap.put("trackingLink", order.getTrackingLink());
                orderMap.put("customer", customerMap);
                orderMap.put("createdAt", DateTimeUtil.convertDateToStringCustomized(order.getMerchant().getCreatedDate(), "dd-MM-yyyy"));
                orderMap.put("updatedAt", DateTimeUtil.convertDateToStringCustomized(order.getMerchant().getUpdatedDate(), "dd-MM-yyyy"));
                orderList.add(orderMap);
            }
            Integer page = (int) Math.ceil((double) orderList.size() / filterListAllRequestDTO.getLimit());

            ResponseListDataDTO<Map> responseListDataDTO = new ResponseListDataDTO<>(filterListAllRequestDTO.getLimit(),
                    page, orderList.size(), filterListAllRequestDTO.getSearch(), orderList);

            return responseListDataDTO;
        } catch (Exception e) {
            logger.error("[FATAL] " + e);
            throw new ResultServiceException(e.getMessage());
        }
    }

    /** UPDATE INCOMING ORDER BY MERCHANT*/
    @Override
    @Transactional
    public ResponseDTO updateIncomingOrder(UpdateIncomingDTO updateIncomingDTO, String token) {
        ResponseDTO responseDTO = new ResponseDTO();
        Map data = new HashMap();
        Merchant merchant;
        Boolean isPin;
        String _username;
        /** Check Admin Exist */
        if(updateIncomingDTO.getUserType().compareTo("admin") == 0) {
            Admin admin = authFilter.getAdminFromToken(token);
            if (admin == null) throw new ResultNotFoundException("Admin is not found");
            merchant = merchantRepository.findOneByUsername(updateIncomingDTO.getMerchant());
            _username = admin.getUsername();
        } else {
            merchant = authFilter.getMerchantFromToken(token);
            _username = merchant.getUsername();
        }
        Order order = orderRepository.findOneByOrderNumber(updateIncomingDTO.getOrderNumber());
        if(order == null)
            throw new ResultNotFoundException("order not found");
        if(updateIncomingDTO.getUserType().compareTo("admin") == 0 && merchant == null) {
            merchant = order.getMerchant();
        }
        if(merchant == null)
            throw new ResultNotFoundException("merchant not found");

        if(!isExistingDataAndIntegerValue(updateIncomingDTO.getDeliveryFee())){
            updateIncomingDTO.setDeliveryFee(0);
        }

        if(!isExistingDataAndIntegerValue(updateIncomingDTO.getAdditionalFee())){
            updateIncomingDTO.setAdditionalFee(0);
        }

        if(!isExistingDataAndStringValue(updateIncomingDTO.getAdditionalInfo())){
            updateIncomingDTO.setAdditionalInfo("none");
        }
        try {
            order.setDeliveryFee(updateIncomingDTO.getDeliveryFee());
            order.setAdditionalFee(updateIncomingDTO.getAdditionalFee());
            order.setAdditionalInfo(updateIncomingDTO.getAdditionalInfo());
            order.setAdditionalInfo(updateIncomingDTO.getAdditionalInfo());
            order.setOrderStatus(Long.valueOf(2));
            order.setUpdatedBy(_username);
            order.setUpdatedDate(new Date());
            orderRepository.save(order);
            Customer customer = customerRepository.findOneById(order.getCustomerId());
            if (customer == null)
                throw new ResultNotFoundException("customer not found");

            /** SEND PAYMENT GATEWAY */
//            ChargeRequestDTO cr = new ChargeRequestDTO();
//            cr.setTransaction_id(updateIncomingDTO.getOrderNumber());
//            cr.setAmount(updateIncomingDTO.getAdditionalFee()+updateIncomingDTO.getDeliveryFee()+order.getAmount());
//            cr.setMerchant_id(merchant.getId());
//
//            ChargeResponseDTO chargeResponseDTO = paymentGatewayService.charge(cr);

            LogOrder logOrder = new LogOrder();
            logOrder.setCreatedDate(new Date());
            logOrder.setData(updateIncomingDTO.toString());
            logOrder.setStatus(false);
            logOrder.setOrderNumber(order.getOrderNumber());
            logOrderRepository.save(logOrder);

//            /** SEND WHATSAPP QONTAK */
//            QontakConfig qontakConfig = qontakRepository.findOneByName("ORDER_ACCEPT");
//            Boolean isSend = qontakService.sendOrderMessageAccept(updateIncomingDTO, chargeResponseDTO, merchant,qontakConfig,customer,order);

            data.put("orderNumber", order.getOrderNumber());
            data.put("additionalInfo", order.getAdditionalInfo());
            data.put("additionalFee", order.getAdditionalFee());
            data.put("deliveryFee", order.getDeliveryFee());
            responseDTO.setCode(ConstantUtil.STATUS_SUCCESS);
            responseDTO.setInfo(ConstantUtil.MESSAGE_SUCCESS);
            responseDTO.setData(data);
            return responseDTO;
        } catch (Exception e){
            logger.error("[FATAL] " + e);
            throw new ResultServiceException(e.getMessage());
        }
    }

    /** REJECT INCOMING ORDER BY MERCHANT */
    @Transactional
    @Override
    public ResponseDTO updateIncomingRejectOrder(UpdateIncomingDTO updateIncomingDTO, String token) {
        ResponseDTO responseDTO = new ResponseDTO();
        Map data = new HashMap();
        Merchant merchant = new Merchant();
        Boolean isPin;
        String _username;
        /** Check Admin Exist */
        if(updateIncomingDTO.getUserType().compareTo("admin") == 0) {
            Admin admin = authFilter.getAdminFromToken(token);
            if (admin == null) throw new ResultNotFoundException("Admin is not found");
            isPin = true;
            _username = admin.getUsername();
        } else {
            merchant = authFilter.getMerchantFromToken(token);
            isPin = checkPin(updateIncomingDTO.getPin() , merchant.getPin());
            _username = merchant.getUsername();
        }
        if(isPin.equals(false))
            throw new ResultNotFoundException("wrong pin number");
        Order order = orderRepository.findOneByOrderNumber(updateIncomingDTO.getOrderNumber());
        if(order == null)
            throw new ResultNotFoundException("order not found");
        if(updateIncomingDTO.getUserType().compareTo("admin") == 0 && merchant == null) {
            merchant = order.getMerchant();
        }
        if(merchant == null)
            throw new ResultNotFoundException("merchant not found");
        Customer customer = customerRepository.findOneById(order.getCustomerId());
        try {
            order.setOrderStatus(Long.valueOf(4));
            order.setReason(updateIncomingDTO.getReason());
            order.setUpdatedBy(_username);
            order.setUpdatedDate(new Date());
            orderRepository.save(order);

            /** RETURN STOCK */
            availableStockRepository.deleteByOrderNumberStock(order.getOrderNumber());

            /** SEND WHATSAPP QONTAK */
            if (customer != null) {
                QontakConfig qontakConfig = qontakRepository.findOneByName("ORDER_REJECT");
                Boolean isSend = qontakService.sendOrderMessageReject(qontakConfig, customer, order);
            }

            LogOrder logOrder = new LogOrder();
            logOrder.setData(updateIncomingDTO.toString());
            logOrder.setCreatedDate(new Date());
            logOrder.setStatus(false);
            logOrder.setOrderNumber(order.getOrderNumber());
            logOrderRepository.save(logOrder);

            data.put("orderNumber", order.getOrderNumber());
            responseDTO.setCode(ConstantUtil.STATUS_SUCCESS);
            responseDTO.setInfo(ConstantUtil.MESSAGE_SUCCESS);
            responseDTO.setData(data);
            return responseDTO;
        } catch (Exception e){
            logger.error("[FATAL] " + e);
            throw new ResultServiceException(e.getMessage());
        }
    }

    /** UPDATE ONGOING ORDER BY MERCHANT*/
    @Override
    @Transactional
    public ResponseDTO updateOngoingOrder(UpdateOngoingDTO updateOngoingDTO, String token) {
        Map data = new HashMap();
        ResponseDTO responseDTO = new ResponseDTO();
        Merchant merchant;
        Boolean isPin;
        String _username;
        /** Check Admin Exist */
        if(updateOngoingDTO.getUserType().compareTo("admin") == 0) {
            Admin admin = authFilter.getAdminFromToken(token);
            if (admin == null) throw new ResultNotFoundException("Admin is not found");
            merchant = merchantRepository.findOneByUsername(updateOngoingDTO.getMerchant());

            if((updateOngoingDTO.getTrackingLink() == null || updateOngoingDTO.getTrackingLink().isEmpty()) &&
                    updateOngoingDTO.getIsPaid()) {
                isPin = checkPin(updateOngoingDTO.getPin() , admin.getPin());
                if(isPin.equals(false)) throw new ResultNotFoundException("wrong pin number");
            }
            _username = admin.getUsername();
        } else {
            merchant = authFilter.getMerchantFromToken(token);
            _username = merchant.getUsername();
        }

        Order order = orderRepository.findOneByOrderNumber(updateOngoingDTO.getOrderNumber());
        if(order == null)
            throw new ResultNotFoundException("order not found");
        if(updateOngoingDTO.getUserType().compareTo("admin") == 0 && merchant == null) {
            merchant = order.getMerchant();
        }
        if(merchant == null)
            throw new ResultNotFoundException("merchant not found");
        Customer customer = customerRepository.findOneById(order.getCustomerId());
        if (customer == null)
            throw new ResultNotFoundException("customer not found");
        try {
            order.setPaid(updateOngoingDTO.getIsPaid());
            order.setTrackingLink(updateOngoingDTO.getTrackingLink());
            order.setUpdatedBy(_username);
            order.setUpdatedDate(new Date());
            orderRepository.save(order);

            LogOrder logOrder = new LogOrder();
            logOrder.setData(updateOngoingDTO.toString());
            logOrder.setCreatedDate(new Date());
            logOrder.setStatus(true);
            logOrder.setOrderNumber(order.getOrderNumber());
            logOrderRepository.save(logOrder);

            if(updateOngoingDTO.getTrackingLink() != null){
                /** SEND WHATSAPP QONTAK */
                QontakConfig qontakConfig = qontakRepository.findOneByName("ORDER_DELIVERY");
                Boolean isSend = qontakService.sendOrderMessageDelivery(merchant,qontakConfig,customer,order);
            }


            data.put("order_number", order.getOrderNumber());
            responseDTO.setCode(ConstantUtil.STATUS_SUCCESS);
            responseDTO.setInfo(ConstantUtil.MESSAGE_SUCCESS);
            responseDTO.setData(data);
            return responseDTO;
        } catch (Exception e){
            logger.error("[FATAL] " + e);
            throw new ResultServiceException(e.getMessage());
        }
    }

    /** UPDATE ARRIVED ORDER BY MERCHANT */
    @Transactional
    @Override
    public ResponseDTO updateOngoingArrived(UpdateOngoingDTO updateOngoingDTO, String token) {
        Map data = new HashMap();
        ResponseDTO responseDTO = new ResponseDTO();
        Merchant merchant;
        Boolean isPin;
        String _username;
        /** Check Admin Exist */
        if(updateOngoingDTO.getUserType().compareTo("admin") == 0) {
            Admin admin = authFilter.getAdminFromToken(token);
            if (admin == null) throw new ResultNotFoundException("Admin is not found");
            merchant = merchantRepository.findOneByUsername(updateOngoingDTO.getMerchant());
            _username = admin.getUsername();
        } else {
            merchant = authFilter.getMerchantFromToken(token);
            _username = merchant.getUsername();
        }

        Order order = orderRepository.findOneByOrderNumber(updateOngoingDTO.getOrderNumber());
        if(order == null)
            throw new ResultNotFoundException("order not found");
        if(updateOngoingDTO.getUserType().compareTo("admin") == 0 && merchant == null) {
            merchant = order.getMerchant();
        }
        if(merchant == null)
            throw new ResultNotFoundException("merchant not found");
        Customer customer = customerRepository.findOneById(order.getCustomerId());
        if (customer == null)
            throw new ResultNotFoundException("customer not found");

        try {
            order.setOrderStatus(Long.valueOf(3));
            order.setUpdatedBy(_username);
            order.setUpdatedDate(new Date());
            orderRepository.save(order);

            /** SEND WHATSAPP QONTAK */

            QontakConfig qontakConfig = qontakRepository.findOneByName("ORDER_SETTLE");
            Boolean isSend = qontakService.sendOrderMessageSettle(merchant,qontakConfig,customer,order);

            LogOrder logOrder = new LogOrder();
            logOrder.setData(updateOngoingDTO.toString());
            logOrder.setCreatedDate(new Date());
            logOrder.setStatus(true);
            logOrder.setOrderNumber(order.getOrderNumber());
            logOrderRepository.save(logOrder);
            data.put("order_number", order.getOrderNumber());
            responseDTO.setCode(ConstantUtil.STATUS_SUCCESS);
            responseDTO.setInfo(ConstantUtil.MESSAGE_SUCCESS);
            responseDTO.setData(data);
            return responseDTO;
        } catch (Exception e){
            logger.error("[FATAL] " + e);
            throw new ResultServiceException(e.getMessage());
        }
    }

    /** REJECT ONGOING ORDER BY MERCHANT*/
    @Transactional
    @Override
    public ResponseDTO updateOngoingRejectOrder(UpdateOngoingDTO updateOngoingDTO, String token) {
        Map data = new HashMap();
        ResponseDTO responseDTO = new ResponseDTO();
        Merchant merchant;
        Boolean isPin;
        String _username;
        /** Check Admin Exist */
        if(updateOngoingDTO.getUserType().compareTo("admin") == 0) {
            Admin admin = authFilter.getAdminFromToken(token);
            if (admin == null) throw new ResultNotFoundException("Admin is not found");
            isPin = checkPin(updateOngoingDTO.getPin() , admin.getPin());
            merchant = merchantRepository.findOneByUsername(updateOngoingDTO.getMerchant());
            _username = admin.getUsername();
        } else {
            merchant = authFilter.getMerchantFromToken(token);
            isPin = checkPin(updateOngoingDTO.getPin() , merchant.getPin());
            _username = merchant.getUsername();
        }
        if(isPin.equals(false))
            throw new ResultNotFoundException("wrong pin number");
        Order order = orderRepository.findOneByOrderNumber(updateOngoingDTO.getOrderNumber());
        if(order == null)
            throw new ResultNotFoundException("order not found");
        if(updateOngoingDTO.getUserType().compareTo("admin") == 0 && merchant == null) {
            merchant = order.getMerchant();
        }
        if(merchant == null)
            throw new ResultNotFoundException("merchant not found");
        Customer customer = customerRepository.findOneById(order.getCustomerId());
        try {
            order.setOrderStatus(Long.valueOf(4));
            order.setUpdatedBy(_username);
            order.setUpdatedDate(new Date());
            orderRepository.save(order);
            LogOrder logOrder = new LogOrder();
            logOrder.setData(updateOngoingDTO.toString());
            logOrder.setCreatedDate(new Date());
            logOrder.setStatus(true);
            logOrder.setOrderNumber(order.getOrderNumber());
            logOrderRepository.save(logOrder);
            data.put("order_number", order.getOrderNumber());

            /** RETURN STOCK */
            availableStockRepository.deleteByOrderNumberStock(order.getOrderNumber());

            /** SEND WHATSAPP QONTAK */
            if (customer != null) {
                QontakConfig qontakConfig = qontakRepository.findOneByName("ORDER_REJECT");
                Boolean isSend = qontakService.sendOrderMessageReject(qontakConfig, customer, order);
            }

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
    public ResponseDTO trackOrder(String OrderNumber) {
        ResponseDTO responseDTO = new ResponseDTO();
        Map orderMap = new HashMap();
        Map customerMap = new HashMap();
        List<Map> ListitemDetailMap = new ArrayList<>();
        Order order = orderRepository.findOneByOrderNumber(OrderNumber);
        if(order == null )
            throw new ResultNotFoundException("Order not found");
        Customer customer = customerRepository.findOneById(order.getCustomerId());
        if (customer == null)
            throw new ResultNotFoundException("customer not found");
        Integer totalPrice = 0;
        List<Map> ListOrderDetail = orderRepository.findTrackOneByOrderNumber(OrderNumber);
        Integer sequence = 1;
        for (Map orderDetail : ListOrderDetail) {
            List<Map> OrderDetailAMap = new ArrayList<>();
            Map orderMapItem = new HashMap<>();
            orderMapItem.put("itemName",orderDetail.get("itemName"));
            orderMapItem.put("additionalInfo", orderDetail.get("info"));
            orderMapItem.put("qty",orderDetail.get("qty"));
            orderMapItem.put("isMiscellaneous", orderDetail.get("is_miscellaneous"));
            orderMapItem.put("price",orderDetail.get("price"));
            totalPrice += Integer.valueOf(orderDetail.get("price").toString());
            List<OrderAttributeDetail> orderAttributeDetails = orderDetailAttributeRepository
                    .findByOrderNumberAndItemIdAndSequence(order.getOrderNumber(),
                            Long.valueOf(orderDetail.get("itemId").toString()),
                            sequence);
            for (OrderAttributeDetail orderAttributeDetail : orderAttributeDetails){
                Map orderMapAItem = new HashMap<>();
                orderMapAItem.put("attributeId",orderAttributeDetail.getAttributeItem().getId());
                orderMapAItem.put("name", orderAttributeDetail.getAttributeItem().getName());
                orderMapAItem.put("price", orderAttributeDetail.getAttributeItem().getPrice());
                OrderDetailAMap.add(orderMapAItem);
                orderMapItem.put("attributeItem", OrderDetailAMap);
            }
            ListitemDetailMap.add(orderMapItem);
            sequence++;
        }

        try {
            customerMap.put("customerName",order.getCustomerName());
            customerMap.put("customerPhone",customer.getPhone());
            customerMap.put("customerAddress", order.getAddress());

            orderMap.put("isPaid",order.getPaid());
            orderMap.put("orderNumber", order.getOrderNumber());
            orderMap.put("orderDate", order.getCreatedDate());
            orderMap.put("orderStatus", order.getOrderStatus());
            orderMap.put("customerDetail",customerMap);
            orderMap.put("deliveryDate",sdf.format(order.getDeliveryDate()));
            orderMap.put("itemDetail",ListitemDetailMap);
            orderMap.put("availableDelivery", order.getDeliveryTime());
            orderMap.put("totalPrice",totalPrice);
            responseDTO.setCode(ConstantUtil.STATUS_SUCCESS);
            responseDTO.setInfo(ConstantUtil.MESSAGE_SUCCESS);
            responseDTO.setData(orderMap);
            return responseDTO;
        } catch (Exception e){
            logger.error("[FATAL] " + e);
            throw new ResultServiceException(e.getMessage());
        }

    }

    @Override
    public ResponseDTO thankYou(String OrderNumber) {
        ResponseDTO responseDTO = new ResponseDTO();
        Map orderMap = new HashMap();
        Map customerMap = new HashMap();
        List<Map> ListItemDetailMap = new ArrayList<>();
        Order order = orderRepository.findOneByOrderNumber(OrderNumber);
        if(order == null )
            throw new ResultNotFoundException("Order not found");
        Customer customer = customerRepository.findOneById(order.getCustomerId());
        if (customer == null)
            throw new ResultNotFoundException("customer not found");

        Integer totalPrice = 0;
        List<Map> ListorderDetail = orderRepository.findTrackOneByOrderNumber(OrderNumber);
        Integer sequence = 1;
        for (Map orderDetail : ListorderDetail) {
            List<Map> OrderDetailAMap = new ArrayList<>();
            Map orderMapItem = new HashMap<>();
            orderMapItem.put("itemName",orderDetail.get("itemName"));
            orderMapItem.put("additionalInfo", orderDetail.get("info"));
            orderMapItem.put("isMiscellaneous", orderDetail.get("is_miscellaneous"));
            orderMapItem.put("qty",orderDetail.get("qty"));
            orderMapItem.put("price",orderDetail.get("price"));
            totalPrice += Integer.valueOf(orderDetail.get("price").toString());
            List<OrderAttributeDetail> orderAttributeDetails = orderDetailAttributeRepository
                    .findByOrderNumberAndItemIdAndSequence(order.getOrderNumber(),
                            Long.valueOf(orderDetail.get("itemId").toString()),
                            sequence);
            for (OrderAttributeDetail orderAttributeDetail : orderAttributeDetails){
                Map orderMapAItem = new HashMap<>();
                orderMapAItem.put("attributeId",orderAttributeDetail.getAttributeItem().getId());
                orderMapAItem.put("name", orderAttributeDetail.getAttributeItem().getName());
                orderMapAItem.put("price", orderAttributeDetail.getAttributeItem().getPrice());
                OrderDetailAMap.add(orderMapAItem);
                orderMapItem.put("attributeItem", OrderDetailAMap);
            }
            ListItemDetailMap.add(orderMapItem);
            sequence++;
        }
        try {
            customerMap.put("customerName",order.getCustomerName());
            customerMap.put("customerPhone",customer.getPhone());
            customerMap.put("customerAddress", order.getAddress());
            customerMap.put("city",customer.getCity());
            customerMap.put("postalCode", customer.getPostalCode());
            orderMap.put("orderNumber", order.getOrderNumber());
            orderMap.put("orderDate", order.getCreatedDate());
            orderMap.put("availableDelivery", order.getDeliveryTime());
            orderMap.put("deliveryDate",sdf.format(order.getDeliveryDate()));
            orderMap.put("orderStatus", order.getOrderStatus());
            orderMap.put("customerDetail",customerMap);
            orderMap.put("itemDetail",ListItemDetailMap);
            orderMap.put("totalPrice",totalPrice);
            orderMap.put("storeName",order.getMerchant().getStoreName());
            orderMap.put("operationPhone",order.getMerchant().getOperationNumber());
            responseDTO.setCode(ConstantUtil.STATUS_SUCCESS);
            responseDTO.setInfo(ConstantUtil.MESSAGE_SUCCESS);
            responseDTO.setData(orderMap);
            return responseDTO;
        } catch (Exception e){
            logger.error("[FATAL] " + e);
            throw new ResultServiceException(e.getMessage());
        }

    }

}
