package com.ecommerceapi.ecomerceapi.mock;

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
import com.ecommerceapi.ecomerceapi.filters.AuthFilter;
import com.ecommerceapi.ecomerceapi.model.*;
import com.ecommerceapi.ecomerceapi.repositories.*;
import com.ecommerceapi.ecomerceapi.services.OrderService;
import com.ecommerceapi.ecomerceapi.services.PaymentGatewayService;
import com.ecommerceapi.ecomerceapi.services.QontakService;
import com.ecommerceapi.ecomerceapi.services.impl.OrderServicesImplements;
import com.ecommerceapi.ecomerceapi.util.ConstantUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class OrderMockTest {
    String token;
    String userName;
    String orderNumber;
    Order order;

    @Mock
    private PaymentGatewayService paymentGatewayService;

    @Mock
    private QontakService qontakService;

    @Mock
    MerchantRepository merchantRepository;

    @Mock
    CustomerRepository customerRepository;

    @Mock
    OrderDetailRepository orderDetailRepository;

    @Mock
    ItemRepository itemRepository;

    @Mock
    LogOrderRepository logOrderRepository;

    @Mock
    QontakRepository qontakRepository;

    @Mock
    ItemAvailableDayRepository itemAvailableDayRepository;

    @Mock
    AvailableStockRepository availableStockRepository;

    @Mock
    AttributeItemRepository attributeItemRepository;

    @Mock
    OrderDetailAttributeRepository orderDetailAttributeRepository;

    @Mock
    OrderRepository orderRepository;

    @Mock
    Order orderMock = new Order();

    @Mock
    AuthFilter authFilter;

    @InjectMocks
    OrderService orderService = new OrderServicesImplements();

    @BeforeEach
    void setupTest() {
        token = "Test Token";
        userName = "usertest";
        orderNumber = "on_test4";
        order = new Order();

        ReflectionTestUtils.setField(orderService, "sdf", new SimpleDateFormat("yyyy-MM-dd"));
    }

    @DisplayName("Test Mock Reset Order")
    @Test
    void testResetOrder() {
        ArrayList<Order> orders = new ArrayList<>();
        for (int i = 1; i <= 2; i++) {
            order.setId((long) i);
            order.setCreatedBy("Test");
            orders.add(order);
        }

        when(orderRepository.findOrderByStatusPending()).thenReturn(orders);
        when(orderRepository.save(any(Order.class))).thenReturn(orderMock);

        ResponseDTO responseDTO = orderService.resetOrder();
        assertNotNull(responseDTO);
        assertEquals(ConstantUtil.STATUS_SUCCESS, responseDTO.getCode());

        verify(orderRepository).findOrderByStatusPending();
        verify(orderRepository, times(2)).save(any(Order.class));
    }

    @DisplayName("Test Mock Create Order")
    @Test
    void testCreateOrder() throws Exception {
        ArrayList<Map> attributesDto = new ArrayList<>();
        Map attributeDto = new HashMap<>();
        attributeDto.put("attributeItemId", 14);
        attributeDto.put("additionalInfo", "testing...");
        attributesDto.add(attributeDto);

        ArrayList<Map> itemsDto = new ArrayList<>();
        Map itemDto = new HashMap();
        itemDto.put("itemId", 14);
        itemDto.put("qty", 1);
        itemDto.put("info", "testing...");
        itemDto.put("attribute", attributesDto);
        itemsDto.add(itemDto);

        Date dt = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(dt);
        c.add(Calendar.DATE, 4);
        dt = c.getTime();

        OrderCreateDTO orderCreateDTO = new OrderCreateDTO();
        orderCreateDTO.setPhoneNumber("62");
        orderCreateDTO.setMerchantUserName("merchantTest");
        orderCreateDTO.setDeliveryDate(dt);
        orderCreateDTO.setDeliveryTime("07:00");
        orderCreateDTO.setItemDetail(itemsDto);

        Customer customer = new Customer();
        customer.setId(14L);
        Merchant merchant = new Merchant();
        merchant.setId(14L);
        Item item = new Item();
        item.setType((short) 1);
        item.setMaxItem(4);
        item.setAssemblyTime(1);

        AttributeItem attributeItem = new AttributeItem();
        attributeItem.setName("TestAttribute");
        attributeItem.setStock(2L);
        attributeItem.setPrice(100L);

        when(customerRepository.findOneByPhone(orderCreateDTO.getPhoneNumber())).thenReturn(customer);
        when(merchantRepository.findOneByUsername(orderCreateDTO.getMerchantUserName())).thenReturn(merchant);
        when(itemRepository.findOneById(anyLong())).thenReturn(item);
        when(availableStockRepository.getQtyCanBuy(anyLong(), anyString())).thenReturn(2); // calculatedStock
        when(itemRepository.findPriceItemById(anyLong())).thenReturn(1000);
        when(orderDetailRepository.save(any(OrderDetail.class))).thenReturn(new OrderDetail());
        when(attributeItemRepository.findOneByIdAndItemId(anyLong(), anyLong())).thenReturn(attributeItem);
        when(orderDetailAttributeRepository.save(any(OrderAttributeDetail.class))).thenReturn(new OrderAttributeDetail());
        when(attributeItemRepository.save(any(AttributeItem.class))).thenReturn(new AttributeItem());
        when(availableStockRepository.save(any(AvailableStock.class))).thenReturn(new AvailableStock());
        when(orderRepository.save(any(Order.class))).thenReturn(orderMock);
        when(qontakRepository.findOneByName("ORDER_CREATE")).thenReturn(new QontakConfig());
        when(qontakService.sendOrderMessageIncoming(any(QontakConfig.class), any(Merchant.class), any(Order.class))).thenReturn(true);
        when(logOrderRepository.save(any(LogOrder.class))).thenReturn(new LogOrder());

        ResponseDTO responseDTO = orderService.create(orderCreateDTO);
        assertNotNull(responseDTO);
        assertNotNull(responseDTO.getData());

        verify(customerRepository).findOneByPhone(orderCreateDTO.getPhoneNumber());
        verify(merchantRepository).findOneByUsername(orderCreateDTO.getMerchantUserName());
        verify(itemRepository, times(3)).findOneById(anyLong());
        verify(availableStockRepository).getQtyCanBuy(anyLong(), anyString());
        verify(itemRepository).findPriceItemById(anyLong());
        verify(orderDetailRepository).save(any(OrderDetail.class));
        verify(attributeItemRepository).findOneByIdAndItemId(anyLong(), anyLong());
        verify(orderDetailAttributeRepository).save(any(OrderAttributeDetail.class));
        verify(attributeItemRepository).save(any(AttributeItem.class));
        verify(availableStockRepository).save(any(AvailableStock.class));
        verify(orderRepository).save(any(Order.class));
        verify(qontakRepository).findOneByName("ORDER_CREATE");
        verify(qontakService).sendOrderMessageIncoming(any(QontakConfig.class), any(Merchant.class), any(Order.class));
        verify(logOrderRepository).save(any(LogOrder.class));

        item.setType((short) 2);

        when(itemAvailableDayRepository.getAvailableDayByItem(anyLong())).thenReturn(new ArrayList<>(List.of(1, 2)));

        ResponseDTO responseDTO2 = orderService.create(orderCreateDTO);
        assertNotNull(responseDTO2);
        assertNotNull(responseDTO2.getData());

        verify(itemAvailableDayRepository).getAvailableDayByItem(anyLong());
    }

    @DisplayName("Test Mock Generate Order Number")
    @Test
    void testGenerateOrderNumber() throws Exception {
        Merchant merchant = new Merchant();
        merchant.setId(14L);

        String orderNumber = orderService.generateOrderNumber(merchant);
        assertNotNull(orderNumber);
    }

    @DisplayName("Test Mock List Incoming Order")
    @Test
    void testListIncomingOrder() throws Exception {
        FilterListRequestDTO filterListRequestdto = new FilterListRequestDTO();
        filterListRequestdto.setLimit(2);
        filterListRequestdto.setOffset(0);
        filterListRequestdto.setSearch("");
        filterListRequestdto.setStartDate(new SimpleDateFormat("yyyy-mm-dd").format(new Date()));
        filterListRequestdto.setEndDate(new SimpleDateFormat("yyyy-mm-dd").format(new Date()));

        ArrayList<Order> orders = new ArrayList<>();
        for (int i = 1; i <= 2; i++) {
            order.setId((long) i);
            order.setOrderNumber("on_test"+i);
            order.setCustomerId(14L);
            order.setDeliveryDate(new Date());
            order.setCreatedDate(new Date());
            orders.add(order);
        }

        ArrayList<Map> orderDetails = new ArrayList<>();
        Map orderDetail = new HashMap();
        orderDetail.put("itemId", 14);
        orderDetail.put("itemName", "testItem");
        orderDetail.put("info", "Test Item");
        orderDetail.put("is_miscellaneous", false);
        orderDetail.put("qty", 1);
        orderDetails.add(orderDetail);

        Customer customer = new Customer();
        customer.setId(14L);
        Merchant merchant = new Merchant();
        merchant.setId(14L);
        merchant.setCreatedDate(new Date());

        List<OrderAttributeDetail> orderAttributeDetails = new ArrayList<>();
        OrderAttributeDetail orderAttributeDetail = new OrderAttributeDetail();
        AttributeItem attributeItem = new AttributeItem();
        attributeItem.setId(14L);
        attributeItem.setName("TestAttribute");
        attributeItem.setPrice(100L);
        orderAttributeDetail.setAttributeItem(attributeItem);
        orderAttributeDetails.add(orderAttributeDetail);

        when(authFilter.getMerchantFromToken(token)).thenReturn(merchant);
        when(orderRepository.getIncomingOrderByMerchant(merchant.getId(), filterListRequestdto.getOffset(),
                filterListRequestdto.getLimit(), filterListRequestdto.getSearch(), filterListRequestdto.getStartDate(),
                filterListRequestdto.getEndDate())).thenReturn(orders);
        when(orderRepository.getIncomingOrderByMerchantCount(merchant.getId(), filterListRequestdto.getSearch(),
                filterListRequestdto.getStartDate(), filterListRequestdto.getEndDate())).thenReturn(2);
        when(orderRepository.findTrackOneByOrderNumber(anyString())).thenReturn(orderDetails);
        when(orderDetailAttributeRepository.findByOrderNumberAndItemIdAndSequence(anyString(), anyLong(), anyInt())).
                thenReturn(orderAttributeDetails);
        when(customerRepository.findOneById(anyLong())).thenReturn(customer);

        ResponseListDTO responseListDTO = orderService.getIncomingOrderByMerchant(filterListRequestdto, token);
        assertNotNull(responseListDTO);
        assertEquals(2, responseListDTO.getData().size());

        verify(orderRepository).getIncomingOrderByMerchant(merchant.getId(), filterListRequestdto.getOffset(),
                filterListRequestdto.getLimit(), filterListRequestdto.getSearch(), filterListRequestdto.getStartDate(),
                filterListRequestdto.getEndDate());
        verify(orderRepository).getIncomingOrderByMerchantCount(merchant.getId(), filterListRequestdto.getSearch(),
                filterListRequestdto.getStartDate(), filterListRequestdto.getEndDate());
        verify(orderRepository, times(2)).findTrackOneByOrderNumber(anyString());
        verify(orderDetailAttributeRepository, times(2)).findByOrderNumberAndItemIdAndSequence(anyString(), anyLong(), anyInt());
        verify(customerRepository, times(2)).findOneById(anyLong());
    }

    @DisplayName("Test Mock List Ongoing Order")
    @Test
    void testListOngoingOrder() throws Exception {
        FilterListRequestDTO filterListRequestdto = new FilterListRequestDTO();
        filterListRequestdto.setLimit(2);
        filterListRequestdto.setOffset(0);
        filterListRequestdto.setSearch("");
        filterListRequestdto.setPaid(1);
        filterListRequestdto.setStartDate(new SimpleDateFormat("yyyy-mm-dd").format(new Date()));
        filterListRequestdto.setEndDate(new SimpleDateFormat("yyyy-mm-dd").format(new Date()));

        ArrayList<Order> orders = new ArrayList<>();
        for (int i = 1; i <= 2; i++) {
            order.setId((long) i);
            order.setOrderNumber("on_test"+i);
            order.setCustomerId(14L);
            order.setDeliveryDate(new Date());
            order.setCreatedDate(new Date());
            orders.add(order);
        }

        ArrayList<Map> orderDetails = new ArrayList<>();
        Map orderDetail = new HashMap();
        orderDetail.put("itemId", 14);
        orderDetail.put("itemName", "testItem");
        orderDetail.put("info", "Test Item");
        orderDetail.put("is_miscellaneous", false);
        orderDetail.put("qty", 1);
        orderDetails.add(orderDetail);

        Customer customer = new Customer();
        customer.setId(14L);
        Merchant merchant = new Merchant();
        merchant.setId(14L);
        merchant.setCreatedDate(new Date());

        List<OrderAttributeDetail> orderAttributeDetails = new ArrayList<>();
        OrderAttributeDetail orderAttributeDetail = new OrderAttributeDetail();
        AttributeItem attributeItem = new AttributeItem();
        attributeItem.setId(14L);
        attributeItem.setName("TestAttribute");
        attributeItem.setPrice(100L);
        orderAttributeDetail.setAttributeItem(attributeItem);
        orderAttributeDetails.add(orderAttributeDetail);

        when(authFilter.getMerchantFromToken(token)).thenReturn(merchant);
        when(orderRepository.getOngoingOrderByMerchant(merchant.getId(), filterListRequestdto.getOffset(),
                filterListRequestdto.getLimit(), filterListRequestdto.getSearch(), filterListRequestdto.getPaid(),
                filterListRequestdto.getStartDate(), filterListRequestdto.getEndDate())).thenReturn(orders);
        when(orderRepository.getOngoingOrderByMerchantCount(merchant.getId(), filterListRequestdto.getSearch(),
                filterListRequestdto.getPaid(), filterListRequestdto.getStartDate(), filterListRequestdto.getEndDate())).
                thenReturn(2);
        when(orderRepository.findTrackOneByOrderNumber(anyString())).thenReturn(orderDetails);
        when(orderDetailAttributeRepository.findByOrderNumberAndItemIdAndSequence(anyString(), anyLong(), anyInt())).
                thenReturn(orderAttributeDetails);
        when(customerRepository.findOneById(anyLong())).thenReturn(customer);

        ResponseListDTO responseListDTO = orderService.getOngoingOrderByMerchant(filterListRequestdto, token);
        assertNotNull(responseListDTO);
        assertEquals(2, responseListDTO.getData().size());

        verify(orderRepository).getOngoingOrderByMerchant(merchant.getId(), filterListRequestdto.getOffset(),
                filterListRequestdto.getLimit(), filterListRequestdto.getSearch(), filterListRequestdto.getPaid(),
                filterListRequestdto.getStartDate(), filterListRequestdto.getEndDate());
        verify(orderRepository).getOngoingOrderByMerchantCount(merchant.getId(), filterListRequestdto.getSearch(),
                filterListRequestdto.getPaid(), filterListRequestdto.getStartDate(), filterListRequestdto.getEndDate());
        verify(orderRepository, times(2)).findTrackOneByOrderNumber(anyString());
        verify(orderDetailAttributeRepository, times(2)).findByOrderNumberAndItemIdAndSequence(anyString(), anyLong(), anyInt());
        verify(customerRepository, times(2)).findOneById(anyLong());
    }

    @DisplayName("Test Mock List Settled Order")
    @Test
    void testListSettledOrder() throws Exception {
        FilterListRequestDTO filterListRequestdto = new FilterListRequestDTO();
        filterListRequestdto.setLimit(2);
        filterListRequestdto.setOffset(0);
        filterListRequestdto.setSearch("");
        filterListRequestdto.setStartDate(new SimpleDateFormat("yyyy-mm-dd").format(new Date()));
        filterListRequestdto.setEndDate(new SimpleDateFormat("yyyy-mm-dd").format(new Date()));

        ArrayList<Order> orders = new ArrayList<>();
        for (int i = 1; i <= 2; i++) {
            order.setId((long) i);
            order.setOrderNumber("on_test"+i);
            order.setCustomerId(14L);
            order.setDeliveryDate(new Date());
            order.setCreatedDate(new Date());
            orders.add(order);
        }

        ArrayList<Map> orderDetails = new ArrayList<>();
        Map orderDetail = new HashMap();
        orderDetail.put("itemId", 14);
        orderDetail.put("itemName", "testItem");
        orderDetail.put("info", "Test Item");
        orderDetail.put("is_miscellaneous", false);
        orderDetail.put("qty", 1);
        orderDetails.add(orderDetail);

        Customer customer = new Customer();
        customer.setId(14L);
        Merchant merchant = new Merchant();
        merchant.setId(14L);
        merchant.setCreatedDate(new Date());

        List<OrderAttributeDetail> orderAttributeDetails = new ArrayList<>();
        OrderAttributeDetail orderAttributeDetail = new OrderAttributeDetail();
        AttributeItem attributeItem = new AttributeItem();
        attributeItem.setId(14L);
        attributeItem.setName("TestAttribute");
        attributeItem.setPrice(100L);
        orderAttributeDetail.setAttributeItem(attributeItem);
        orderAttributeDetails.add(orderAttributeDetail);

        when(authFilter.getMerchantFromToken(token)).thenReturn(merchant);
        when(orderRepository.getSettleOrderByMerchant(merchant.getId(), filterListRequestdto.getOffset(),
                filterListRequestdto.getLimit(), filterListRequestdto.getSearch(), filterListRequestdto.getStartDate(),
                filterListRequestdto.getEndDate())).thenReturn(orders);
        when(orderRepository.getSettleOrderByMerchantCount(merchant.getId(), filterListRequestdto.getSearch(),
                filterListRequestdto.getStartDate(), filterListRequestdto.getEndDate())).
                thenReturn(2);
        when(orderRepository.findTrackOneByOrderNumber(anyString())).thenReturn(orderDetails);
        when(orderDetailAttributeRepository.findByOrderNumberAndItemIdAndSequence(anyString(), anyLong(), anyInt())).
                thenReturn(orderAttributeDetails);
        when(customerRepository.findOneById(anyLong())).thenReturn(customer);

        ResponseListDTO responseListDTO = orderService.getSettleOrderByMerchant(filterListRequestdto, token);
        assertNotNull(responseListDTO);
        assertEquals(2, responseListDTO.getData().size());

        verify(orderRepository).getSettleOrderByMerchant(merchant.getId(), filterListRequestdto.getOffset(),
                filterListRequestdto.getLimit(), filterListRequestdto.getSearch(), filterListRequestdto.getStartDate(),
                filterListRequestdto.getEndDate());
        verify(orderRepository).getSettleOrderByMerchantCount(merchant.getId(), filterListRequestdto.getSearch(),
                filterListRequestdto.getStartDate(), filterListRequestdto.getEndDate());
        verify(orderRepository, times(2)).findTrackOneByOrderNumber(anyString());
        verify(orderDetailAttributeRepository, times(2)).findByOrderNumberAndItemIdAndSequence(anyString(), anyLong(), anyInt());
        verify(customerRepository, times(2)).findOneById(anyLong());
    }

    @DisplayName("Test Mock List All Order")
    @Test
    void testListAllOrder() throws Exception {
        FilterListAllRequestDTO filterListAllRequestDTO = new FilterListAllRequestDTO();
        filterListAllRequestDTO.setLimit(2);
        filterListAllRequestDTO.setOffset(0);
        filterListAllRequestDTO.setSearch("");
        filterListAllRequestDTO.setStartDate(new SimpleDateFormat("yyyy-mm-dd").format(new Date()));
        filterListAllRequestDTO.setEndDate(new SimpleDateFormat("yyyy-mm-dd").format(new Date()));
        filterListAllRequestDTO.setUserType("admin");

        Merchant merchant = new Merchant();
        merchant.setId(14L);
        merchant.setUsername("merchanttest");
        merchant.setStoreName("Merchant Test");
        merchant.setCreatedDate(new Date());

        ArrayList<Order> orders = new ArrayList<>();
        for (int i = 1; i <= 2; i++) {
            order.setId((long) i);
            order.setOrderNumber("on_test"+i);
            order.setCustomerId(14L);
            order.setDeliveryDate(new Date());
            order.setCreatedDate(new Date());
            order.setMerchant(merchant);
            orders.add(order);
        }

        ArrayList<Map> orderDetails = new ArrayList<>();
        Map orderDetail = new HashMap();
        orderDetail.put("itemId", 14);
        orderDetail.put("itemName", "testItem");
        orderDetail.put("info", "Test Item");
        orderDetail.put("is_miscellaneous", false);
        orderDetail.put("qty", 1);
        orderDetails.add(orderDetail);

        Admin admin = new Admin();
        Customer customer = new Customer();
        customer.setId(14L);

        List<OrderAttributeDetail> orderAttributeDetails = new ArrayList<>();
        OrderAttributeDetail orderAttributeDetail = new OrderAttributeDetail();
        AttributeItem attributeItem = new AttributeItem();
        attributeItem.setId(14L);
        attributeItem.setName("TestAttribute");
        attributeItem.setPrice(100L);
        orderAttributeDetail.setAttributeItem(attributeItem);
        orderAttributeDetails.add(orderAttributeDetail);

        when(authFilter.getAdminFromToken(token)).thenReturn(admin);
        when(orderRepository.getAllOrder(anyLong(), anyInt(), anyInt(), anyString(), anyString(), anyString(),
                anyList())).thenReturn(orders);
        when(orderRepository.getAllOrderCount(anyLong(), anyString(), anyString(), anyString(), anyList())).
                thenReturn(2);
        when(orderRepository.findTrackOneByOrderNumber(anyString())).thenReturn(orderDetails);
        when(orderDetailAttributeRepository.findByOrderNumberAndItemIdAndSequence(anyString(), anyLong(), anyInt())).
                thenReturn(orderAttributeDetails);
        when(customerRepository.findOneById(anyLong())).thenReturn(customer);

        ResponseListDataDTO<Map> responseListDataDTO = orderService.getAllOrder(filterListAllRequestDTO, token);
        assertNotNull(responseListDataDTO);
        assertEquals(2, responseListDataDTO.getData().size());

        verify(orderRepository).getAllOrder(anyLong(), anyInt(), anyInt(), anyString(), anyString(), anyString(),
                anyList());
        verify(orderRepository).getAllOrderCount(anyLong(), anyString(), anyString(), anyString(), anyList());
        verify(orderRepository, times(2)).findTrackOneByOrderNumber(anyString());
        verify(orderDetailAttributeRepository, times(2)).findByOrderNumberAndItemIdAndSequence(anyString(), anyLong(), anyInt());
        verify(customerRepository, times(2)).findOneById(anyLong());
    }

    @DisplayName("Test Mock List All Order Data")
    @Test
    void testListAllOrderData() throws Exception {
        FilterListAllRequestDTO filterListAllRequestDTO = new FilterListAllRequestDTO();
        filterListAllRequestDTO.setLimit(2);
        filterListAllRequestDTO.setOffset(0);
        filterListAllRequestDTO.setSearch("");
        filterListAllRequestDTO.setStartDate(new SimpleDateFormat("yyyy-mm-dd").format(new Date()));
        filterListAllRequestDTO.setEndDate(new SimpleDateFormat("yyyy-mm-dd").format(new Date()));
        filterListAllRequestDTO.setUserType("admin");

        Merchant merchant = new Merchant();
        merchant.setId(14L);
        merchant.setUsername("merchanttest");
        merchant.setStoreName("Merchant Test");
        merchant.setCreatedDate(new Date());

        ArrayList<Order> orders = new ArrayList<>();
        for (int i = 1; i <= 2; i++) {
            order.setId((long) i);
            order.setOrderNumber("on_test"+i);
            order.setCustomerId(14L);
            order.setDeliveryDate(new Date());
            order.setCreatedDate(new Date());
            order.setMerchant(merchant);
            orders.add(order);
        }

        ArrayList<Map> orderDetails = new ArrayList<>();
        Map orderDetail = new HashMap();
        orderDetail.put("itemId", 14);
        orderDetail.put("itemName", "testItem");
        orderDetail.put("info", "Test Item");
        orderDetail.put("is_miscellaneous", false);
        orderDetail.put("qty", 1);
        orderDetails.add(orderDetail);

        Admin admin = new Admin();
        Customer customer = new Customer();
        customer.setId(14L);

        List<OrderAttributeDetail> orderAttributeDetails = new ArrayList<>();
        OrderAttributeDetail orderAttributeDetail = new OrderAttributeDetail();
        AttributeItem attributeItem = new AttributeItem();
        attributeItem.setId(14L);
        attributeItem.setName("TestAttribute");
        attributeItem.setPrice(100L);
        orderAttributeDetail.setAttributeItem(attributeItem);
        orderAttributeDetails.add(orderAttributeDetail);

        when(authFilter.getAdminFromToken(token)).thenReturn(admin);
        when(orderRepository.getAllOrderList(anyLong(), anyString(), anyString(), anyString(), anyList(), anyInt())).
                thenReturn(orders);
        when(orderRepository.findTrackOneByOrderNumber(anyString())).thenReturn(orderDetails);
        when(orderDetailAttributeRepository.findByOrderNumberAndItemIdAndSequence(anyString(), anyLong(), anyInt())).
                thenReturn(orderAttributeDetails);
        when(customerRepository.findOneById(anyLong())).thenReturn(customer);

        ResponseListDataDTO<Map> responseListDataDTO = orderService.getAllOrderList(filterListAllRequestDTO, token);
        assertNotNull(responseListDataDTO);
        assertEquals(2, responseListDataDTO.getTotal());

        verify(orderRepository).getAllOrderList(anyLong(), anyString(), anyString(), anyString(), anyList(), anyInt());
        verify(orderRepository, times(2)).findTrackOneByOrderNumber(anyString());
        verify(orderDetailAttributeRepository, times(2)).findByOrderNumberAndItemIdAndSequence(anyString(), anyLong(), anyInt());
        verify(customerRepository, times(2)).findOneById(anyLong());
    }

    @DisplayName("Test Mock Accept Incoming Order")
    @Test
    void testAcceptIncoming() throws Exception {
        UpdateIncomingDTO updateIncomingDTO = new UpdateIncomingDTO();
        updateIncomingDTO.setDeliveryFee(1000);
        updateIncomingDTO.setAdditionalInfo("Info...");
        updateIncomingDTO.setAdditionalFee(1000);
        updateIncomingDTO.setOrderNumber(orderNumber);

        Customer customer = new Customer();
        Merchant merchant = new Merchant();
        merchant.setId(14L);
        order.setOrderNumber(updateIncomingDTO.getOrderNumber());
        order.setCustomerId(14L);
        order.setAmount(2000);

        when(authFilter.getMerchantFromToken(token)).thenReturn(merchant);
        when(orderRepository.findOneByOrderNumber(updateIncomingDTO.getOrderNumber())).thenReturn(order);
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(customerRepository.findOneById(anyLong())).thenReturn(customer);
//        when(paymentGatewayService.charge(any(ChargeRequestDTO.class))).thenReturn(new ChargeResponseDTO());
        when(logOrderRepository.save(any(LogOrder.class))).thenReturn(new LogOrder());
//        when(qontakRepository.findOneByName("ORDER_ACCEPT")).thenReturn(new QontakConfig());
//        when(qontakService.sendOrderMessageAccept(any(UpdateIncomingDTO.class), any(ChargeResponseDTO.class),
//                any(Merchant.class), any(QontakConfig.class), any(Customer.class), any(Order.class))).thenReturn(true);

        ResponseDTO responseDTO = orderService.updateIncomingOrder(updateIncomingDTO, token);
        assertNotNull(responseDTO);
        assertNotNull(responseDTO.getData());
        assertEquals(updateIncomingDTO.getOrderNumber(), responseDTO.getData().get("orderNumber"));

        verify(orderRepository).findOneByOrderNumber(updateIncomingDTO.getOrderNumber());
        verify(orderRepository).save(any(Order.class));
        verify(customerRepository).findOneById(anyLong());
//        verify(paymentGatewayService).charge(any(ChargeRequestDTO.class));
        verify(logOrderRepository).save(any(LogOrder.class));
//        verify(qontakRepository).findOneByName("ORDER_ACCEPT");
//        verify(qontakService).sendOrderMessageAccept(any(UpdateIncomingDTO.class), any(ChargeResponseDTO.class),
//                any(Merchant.class), any(QontakConfig.class), any(Customer.class), any(Order.class));
    }

    @DisplayName("Test Mock Reject Incoming Order")
    @Test
    void testRejectIncoming() throws Exception {
        UpdateIncomingDTO updateIncomingDTO = new UpdateIncomingDTO();
        updateIncomingDTO.setOrderNumber(orderNumber);
        updateIncomingDTO.setReason("Test");

        Customer customer = new Customer();
        Merchant merchant = new Merchant();
        order.setOrderNumber(updateIncomingDTO.getOrderNumber());
        order.setCustomerId(14L);

        when(authFilter.getMerchantFromToken(token)).thenReturn(merchant);
        when(orderRepository.findOneByOrderNumber(updateIncomingDTO.getOrderNumber())).thenReturn(order);
        when(customerRepository.findOneById(anyLong())).thenReturn(customer);
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        doNothing().when(availableStockRepository).deleteByOrderNumberStock(anyString());
        when(qontakRepository.findOneByName("ORDER_REJECT")).thenReturn(new QontakConfig());
        when(qontakService.sendOrderMessageReject(any(QontakConfig.class), any(Customer.class), any(Order.class))).
                thenReturn(true);
        when(logOrderRepository.save(any(LogOrder.class))).thenReturn(new LogOrder());

        ResponseDTO responseDTO = orderService.updateIncomingRejectOrder(updateIncomingDTO, token);
        assertNotNull(responseDTO);
        assertNotNull(responseDTO.getData());
        assertEquals(updateIncomingDTO.getOrderNumber(), responseDTO.getData().get("orderNumber"));

        verify(orderRepository).findOneByOrderNumber(updateIncomingDTO.getOrderNumber());
        verify(customerRepository).findOneById(anyLong());
        verify(orderRepository).save(any(Order.class));
        verify(qontakRepository).findOneByName("ORDER_REJECT");
        verify(qontakService).sendOrderMessageReject(any(QontakConfig.class), any(Customer.class), any(Order.class));
        verify(logOrderRepository).save(any(LogOrder.class));
    }

    @DisplayName("Test Mock Accept Ongoing Order")
    @Test
    void testAcceptOngoing() throws Exception {
        UpdateOngoingDTO updateOngoingDTO = new UpdateOngoingDTO();
        updateOngoingDTO.setOrderNumber(orderNumber);
        updateOngoingDTO.setIsPaid(true);
        updateOngoingDTO.setTrackingLink("tracktest");

        Customer customer = new Customer();
        Merchant merchant = new Merchant();
        order.setOrderNumber(updateOngoingDTO.getOrderNumber());
        order.setCustomerId(14L);

        when(authFilter.getMerchantFromToken(token)).thenReturn(merchant);
        when(orderRepository.findOneByOrderNumber(updateOngoingDTO.getOrderNumber())).thenReturn(order);
        when(customerRepository.findOneById(anyLong())).thenReturn(customer);
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(logOrderRepository.save(any(LogOrder.class))).thenReturn(new LogOrder());
        when(qontakRepository.findOneByName("ORDER_DELIVERY")).thenReturn(new QontakConfig());
        when(qontakService.sendOrderMessageDelivery(any(Merchant.class), any(QontakConfig.class), any(Customer.class),
                any(Order.class))).thenReturn(true);

        ResponseDTO responseDTO = orderService.updateOngoingOrder(updateOngoingDTO, token);
        assertNotNull(responseDTO);
        assertNotNull(responseDTO.getData());
        assertEquals(updateOngoingDTO.getOrderNumber(), responseDTO.getData().get("order_number"));

        verify(orderRepository).findOneByOrderNumber(updateOngoingDTO.getOrderNumber());
        verify(customerRepository).findOneById(anyLong());
        verify(orderRepository).save(any(Order.class));
        verify(logOrderRepository).save(any(LogOrder.class));
        verify(qontakRepository).findOneByName("ORDER_DELIVERY");
        verify(qontakService).sendOrderMessageDelivery(any(Merchant.class), any(QontakConfig.class), any(Customer.class),
                any(Order.class));
    }

    @DisplayName("Test Mock Reject Ongoing Order")
    @Test
    void testRejectOngoing() throws Exception {
        UpdateOngoingDTO updateOngoingDTO = new UpdateOngoingDTO();
        updateOngoingDTO.setOrderNumber(orderNumber);
        updateOngoingDTO.setIsPaid(true);
        updateOngoingDTO.setTrackingLink("tracktest");

        Customer customer = new Customer();
        Merchant merchant = new Merchant();
        order.setOrderNumber(updateOngoingDTO.getOrderNumber());
        order.setCustomerId(14L);

        when(authFilter.getMerchantFromToken(token)).thenReturn(merchant);
        when(orderRepository.findOneByOrderNumber(updateOngoingDTO.getOrderNumber())).thenReturn(order);
        when(customerRepository.findOneById(anyLong())).thenReturn(customer);
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(logOrderRepository.save(any(LogOrder.class))).thenReturn(new LogOrder());
        doNothing().when(availableStockRepository).deleteByOrderNumberStock(anyString());
        when(qontakRepository.findOneByName("ORDER_REJECT")).thenReturn(new QontakConfig());
        when(qontakService.sendOrderMessageReject(any(QontakConfig.class), any(Customer.class), any(Order.class))).
                thenReturn(true);

        ResponseDTO responseDTO = orderService.updateOngoingRejectOrder(updateOngoingDTO, token);
        assertNotNull(responseDTO);
        assertNotNull(responseDTO.getData());
        assertEquals(updateOngoingDTO.getOrderNumber(), responseDTO.getData().get("order_number"));

        verify(orderRepository).findOneByOrderNumber(updateOngoingDTO.getOrderNumber());
        verify(customerRepository).findOneById(anyLong());
        verify(orderRepository).save(any(Order.class));
        verify(logOrderRepository).save(any(LogOrder.class));
        verify(qontakRepository).findOneByName("ORDER_REJECT");
        verify(qontakService).sendOrderMessageReject(any(QontakConfig.class), any(Customer.class), any(Order.class));
    }

    @DisplayName("Test Mock Arrived Ongoing Order")
    @Test
    void testArrivedOngoing() throws Exception {
        UpdateOngoingDTO updateOngoingDTO = new UpdateOngoingDTO();
        updateOngoingDTO.setOrderNumber(orderNumber);
        updateOngoingDTO.setIsPaid(true);
        updateOngoingDTO.setTrackingLink("tracktest");

        Customer customer = new Customer();
        Merchant merchant = new Merchant();
        order.setOrderNumber(updateOngoingDTO.getOrderNumber());
        order.setCustomerId(14L);

        when(authFilter.getMerchantFromToken(token)).thenReturn(merchant);
        when(orderRepository.findOneByOrderNumber(updateOngoingDTO.getOrderNumber())).thenReturn(order);
        when(customerRepository.findOneById(anyLong())).thenReturn(customer);
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(qontakRepository.findOneByName("ORDER_SETTLE")).thenReturn(new QontakConfig());
        when(qontakService.sendOrderMessageSettle(any(Merchant.class), any(QontakConfig.class), any(Customer.class),
                any(Order.class))).thenReturn(true);
        when(logOrderRepository.save(any(LogOrder.class))).thenReturn(new LogOrder());

        ResponseDTO responseDTO = orderService.updateOngoingArrived(updateOngoingDTO, token);
        assertNotNull(responseDTO);
        assertNotNull(responseDTO.getData());
        assertEquals(updateOngoingDTO.getOrderNumber(), responseDTO.getData().get("order_number"));

        verify(orderRepository).findOneByOrderNumber(updateOngoingDTO.getOrderNumber());
        verify(customerRepository).findOneById(anyLong());
        verify(orderRepository).save(any(Order.class));
        verify(qontakRepository).findOneByName("ORDER_SETTLE");
        verify(qontakService).sendOrderMessageSettle(any(Merchant.class), any(QontakConfig.class), any(Customer.class),
                any(Order.class));
        verify(logOrderRepository).save(any(LogOrder.class));
    }

    @DisplayName("Test Mock Tracking Order")
    @Test
    void testTrackingOrder() throws Exception {
        ArrayList<Map> orderDetails = new ArrayList<>();
        for (int i = 1; i <= 2; i++) {
            Map orderDetail = new HashMap<>();
            orderDetail.put("itemId", 14);
            orderDetail.put("itemName", "testname");
            orderDetail.put("info", "Test");
            orderDetail.put("qty", 1);
            orderDetail.put("is_miscellaneous", false);
            orderDetail.put("price", 1000);
            orderDetails.add(orderDetail);
        }

        Customer customer = new Customer();
        order.setOrderNumber(orderNumber);
        order.setCustomerId(14L);
        order.setDeliveryDate(new Date());

        List<OrderAttributeDetail> orderAttributeDetails = new ArrayList<>();
        OrderAttributeDetail orderAttributeDetail = new OrderAttributeDetail();
        AttributeItem attributeItem = new AttributeItem();
        attributeItem.setId(14L);
        attributeItem.setName("TestAttribute");
        attributeItem.setPrice(100L);
        orderAttributeDetail.setAttributeItem(attributeItem);
        orderAttributeDetails.add(orderAttributeDetail);

        when(orderRepository.findOneByOrderNumber(orderNumber)).thenReturn(order);
        when(customerRepository.findOneById(anyLong())).thenReturn(customer);
        when(orderRepository.findTrackOneByOrderNumber(orderNumber)).thenReturn(orderDetails);
        when(orderDetailAttributeRepository.findByOrderNumberAndItemIdAndSequence(anyString(), anyLong(), anyInt())).
                thenReturn(orderAttributeDetails);

        ResponseDTO responseDTO = orderService.trackOrder(orderNumber);
        assertNotNull(responseDTO);
        assertNotNull(responseDTO.getData());
        assertEquals(orderNumber, responseDTO.getData().get("orderNumber"));

        verify(orderRepository).findOneByOrderNumber(orderNumber);
        verify(customerRepository).findOneById(anyLong());
        verify(orderRepository).findTrackOneByOrderNumber(orderNumber);
        verify(orderDetailAttributeRepository, times(2)).findByOrderNumberAndItemIdAndSequence(anyString(), anyLong(), anyInt());
    }

    @DisplayName("Test Mock Thanks Order")
    @Test
    void testThanksOrder() throws Exception {
        ArrayList<Map> orderDetails = new ArrayList<>();
        Map orderDetail = new HashMap<>();
        orderDetail.put("itemId", 14);
        orderDetail.put("itemName", "testname");
        orderDetail.put("info", "Test");
        orderDetail.put("qty", 1);
        orderDetail.put("is_miscellaneous", false);
        orderDetail.put("price", 1000);
        orderDetails.add(orderDetail);

        Customer customer = new Customer();
        Merchant merchant = new Merchant();
        merchant.setStoreName("Merchant Test");
        merchant.setOperationNumber("62");
        order.setOrderNumber(orderNumber);
        order.setCustomerId(14L);
        order.setDeliveryDate(new Date());
        order.setMerchant(merchant);

        List<OrderAttributeDetail> orderAttributeDetails = new ArrayList<>();
        OrderAttributeDetail orderAttributeDetail = new OrderAttributeDetail();
        AttributeItem attributeItem = new AttributeItem();
        attributeItem.setId(14L);
        attributeItem.setName("TestAttribute");
        attributeItem.setPrice(100L);
        orderAttributeDetail.setAttributeItem(attributeItem);
        orderAttributeDetails.add(orderAttributeDetail);

        when(orderRepository.findOneByOrderNumber(orderNumber)).thenReturn(order);
        when(customerRepository.findOneById(anyLong())).thenReturn(customer);
        when(orderRepository.findTrackOneByOrderNumber(orderNumber)).thenReturn(orderDetails);
        when(orderDetailAttributeRepository.findByOrderNumberAndItemIdAndSequence(anyString(), anyLong(), anyInt())).
                thenReturn(orderAttributeDetails);

        ResponseDTO responseDTO = orderService.thankYou(orderNumber);
        assertNotNull(responseDTO);
        assertNotNull(responseDTO.getData());
        assertEquals(orderNumber, responseDTO.getData().get("orderNumber"));

        verify(orderRepository).findOneByOrderNumber(orderNumber);
        verify(customerRepository).findOneById(anyLong());
        verify(orderRepository).findTrackOneByOrderNumber(orderNumber);
        verify(orderDetailAttributeRepository).findByOrderNumberAndItemIdAndSequence(anyString(), anyLong(), anyInt());
    }
}
