package com.ecommerceapi.ecomerceapi.mock;

import com.ecommerceapi.ecomerceapi.dto.request.Customer.*;
import com.ecommerceapi.ecomerceapi.dto.response.Customer.MerchantItemResponseDTO;
import com.ecommerceapi.ecomerceapi.dto.response.ResponseDTO;
import com.ecommerceapi.ecomerceapi.dto.response.ResponseListDTO;
import com.ecommerceapi.ecomerceapi.filters.AuthFilter;
import com.ecommerceapi.ecomerceapi.model.*;
import com.ecommerceapi.ecomerceapi.repositories.*;
import com.ecommerceapi.ecomerceapi.services.AttributeItemService;
import com.ecommerceapi.ecomerceapi.services.CustomerService;
import com.ecommerceapi.ecomerceapi.services.impl.AttributeItemImplements;
import com.ecommerceapi.ecomerceapi.services.impl.CustomerServiceImplements;
import com.ecommerceapi.ecomerceapi.util.ConstantUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class CustomerMockTest {
    String token;
    String phoneNumber;
    Customer customer;

    @Value("${amazonProperties.endpointUrl}")
    private String endpointUrl;

    @Value("${amazonProperties.bucketName}")
    private String bucketName;

    @Mock
    CustomerRepository customerRepository;

    @Mock
    MerchantRepository merchantRepository;

    @Mock
    ItemRepository itemRepository;

    @Mock
    ItemAvailableDayRepository itemAvailableDayRepository;

    @Mock
    AvailableStockRepository availableStockRepository;

    @Mock
    AttributeItemService attributeItemService = new AttributeItemImplements();

    @Mock
    Customer customerMock = new Customer();

    @Mock
    AuthFilter authFilter;

    @InjectMocks
    CustomerService customerService = new CustomerServiceImplements();

    @BeforeEach
    void setupTest() {
        token = "Test token";
        phoneNumber = "62";
        customer = new Customer();

        ReflectionTestUtils.setField(customerService, "endpointUrl", endpointUrl);
        ReflectionTestUtils.setField(customerService, "bucketName", bucketName);
    }

    @DisplayName("Test Mock Create a Customer")
    @Test
    void testCreateCustomer() {
        CustomerRegisterDTO customerRegisterDTO = new CustomerRegisterDTO();
        customerRegisterDTO.setPhoneNumber(phoneNumber);
        customerRegisterDTO.setEmail("customertest@mail.com");
        customerRegisterDTO.setCustomerName("Customer Test");
        customerRegisterDTO.setAddress("Indonesia");

        when(customerRepository.findOneByPhone(customerRegisterDTO.getPhoneNumber())).thenReturn(null);
        when(customerRepository.save(any(Customer.class))).thenReturn(customerMock);

        ResponseDTO responseDTO = customerService.customerRegister(customerRegisterDTO);
        assertNotNull(responseDTO);
        assertNotNull(responseDTO.getData().get("phoneNumber"));
        assertEquals(customerRegisterDTO.getPhoneNumber(), responseDTO.getData().get("phoneNumber"));

        verify(customerRepository).findOneByPhone(customerRegisterDTO.getPhoneNumber());
        verify(customerRepository).save(any(Customer.class));
    }

    @DisplayName("Test Mock Calendar Cart")
    @Test
    void testCalendarCart() {
        ReflectionTestUtils.setField(customerService, "sdf", new SimpleDateFormat("yyyy-MM-dd"));
        CustomerCheckDTO customerCheckDTO = new CustomerCheckDTO();
        customerCheckDTO.setItemId(new ArrayList<>( List.of(14) ));

        Item item = new Item();
        ArrayList<Item> items = new ArrayList<>();
        item.setId(14L);
        item.setName("TestItem");
        item.setType((short) 2);
        item.setPicture("test.jpg");
        item.setMaxItem(4);
        items.add(item);

        AttributeItem attributeItem = new AttributeItem();
        ArrayList<AttributeItem> attributeItems = new ArrayList<>();
        attributeItem.setId(14L);
        attributeItem.setName("TestAttribute");
        attributeItem.setStock(0L);
        attributeItem.setPrice(100L);
        attributeItems.add(attributeItem);

        when(itemRepository.findAllItemIn(anyList())).thenReturn(items);
        when(itemAvailableDayRepository.getAvailableDayByItem(anyLong())).
                thenReturn(new ArrayList<>( List.of(1) ));
        when(itemRepository.findOneById(anyLong())).thenReturn(item);
        when(availableStockRepository.getQtyCanBuy(anyLong(), anyString())).thenReturn(2);
        when(attributeItemService.viewByItemId(anyLong())).thenReturn(attributeItems);

        ResponseListDTO responseListDTO = customerService.cartCalendarCheck(customerCheckDTO);
        assertNotNull(responseListDTO);
        assertEquals(1, responseListDTO.getData().size());

        verify(itemRepository).findAllItemIn(anyList());
        verify(itemAvailableDayRepository).getAvailableDayByItem(anyLong());
//        verify(itemRepository, times(4)).findOneById(anyLong());
//        verify(availableStockRepository, times(4)).getQtyCanBuy(anyLong(), anyString());
        verify(attributeItemService).viewByItemId(anyLong());
    }

    @DisplayName("Test Mock Update a Customer")
    @Test
    void testUpdateCustomer() {
        CustomerUpdateDTO customerUpdateDTO = new CustomerUpdateDTO();
        customerUpdateDTO.setId(14L);
        customerUpdateDTO.setPhoneNumber(phoneNumber);
        customerUpdateDTO.setEmail("customertest@mail.com");
        customerUpdateDTO.setCustomerName("Customer Test");
        customerUpdateDTO.setAddress("Indonesia");
        customerUpdateDTO.setToken(token);

        customer.setId(customerUpdateDTO.getId());
        Admin admin = new Admin();
        admin.setUsername("admintest");

        when(customerRepository.findOneById(customerUpdateDTO.getId())).thenReturn(customer);
        when(customerRepository.findOneByPhone(customerUpdateDTO.getPhoneNumber())).thenReturn(customer);
        when(authFilter.getAdminFromToken(customerUpdateDTO.getToken())).thenReturn(admin);
        when(customerRepository.save(any(Customer.class))).thenReturn(customerMock);

        ResponseDTO responseDTO = customerService.customerUpdate(customerUpdateDTO);
        assertNotNull(responseDTO);
        assertNotNull(responseDTO.getData().get("phoneNumber"));
        assertEquals(customerUpdateDTO.getPhoneNumber(), responseDTO.getData().get("phoneNumber"));

        verify(customerRepository).findOneById(customerUpdateDTO.getId());
        verify(customerRepository).findOneByPhone(customerUpdateDTO.getPhoneNumber());
        verify(customerRepository).save(any(Customer.class));
    }

    @DisplayName("Test Mock Update V2 a Customer")
    @Test
    void testUpdateV2Customer() {
        CustomerUpdateDTO customerUpdateDTO = new CustomerUpdateDTO();
        customerUpdateDTO.setId(14L);
        customerUpdateDTO.setPhoneNumber(phoneNumber);
        customerUpdateDTO.setEmail("customertest@mail.com");
        customerUpdateDTO.setCustomerName("Customer Test");
        customerUpdateDTO.setAddress("Indonesia");
        customerUpdateDTO.setToken(token);

        customer.setId(customerUpdateDTO.getId());

        when(customerRepository.findOneByPhone(customerUpdateDTO.getPhoneNumber())).thenReturn(customer);
        when(customerRepository.save(any(Customer.class))).thenReturn(customerMock);

        ResponseDTO responseDTO = customerService.customerUpdateV2(customerUpdateDTO);
        assertNotNull(responseDTO);
        assertNotNull(responseDTO.getData().get("phoneNumber"));
        assertEquals(customerUpdateDTO.getPhoneNumber(), responseDTO.getData().get("phoneNumber"));

        verify(customerRepository).findOneByPhone(customerUpdateDTO.getPhoneNumber());
        verify(customerRepository).save(any(Customer.class));
    }

    @DisplayName("Test Mock View a Customer")
    @Test
    void testViewCustomer() {
        CustomerViewDTO customerViewDTO = new CustomerViewDTO();
        customerViewDTO.setPhoneNumber(phoneNumber);

        customer.setPhone(phoneNumber);

        when(customerRepository.findOneByPhone(customerViewDTO.getPhoneNumber())).thenReturn(customer);

        ResponseDTO responseDTO = customerService.customerView(customerViewDTO);
        assertNotNull(responseDTO);
        assertNotNull(responseDTO.getData().get("phoneNumber"));
        assertEquals(customerViewDTO.getPhoneNumber(), responseDTO.getData().get("phoneNumber"));

        verify(customerRepository).findOneByPhone(customerViewDTO.getPhoneNumber());
    }

    @DisplayName("Test Mock List Customers")
    @Test
    void testListCustomer() {
        CustomerListDTO customerListDTO = new CustomerListDTO();
        customerListDTO.setOffset(0);
        customerListDTO.setLimit(2);
        customerListDTO.setUserType("admin");
        customerListDTO.setToken(token);

        ArrayList<Customer> customers = new ArrayList<>();
        for (int i = 1; i <= 2; i++) {
            customer.setId((long) i);
            customer.setPhone(phoneNumber+i);
            customers.add(customer);
        }

        when(authFilter.getAdminFromToken(customerListDTO.getToken())).thenReturn(new Admin());
        when(customerRepository.getAllOffsetLimit(customerListDTO.getOffset(), customerListDTO.getLimit(),
                customerListDTO.getSearch())).thenReturn(customers);
        when(customerRepository.getAllOffsetLimitCount(customerListDTO.getSearch())).thenReturn(2);

        ResponseListDTO responseListDTO = customerService.customerList(customerListDTO);
        assertNotNull(responseListDTO);
        assertEquals(2, responseListDTO.getData().size());

        verify(customerRepository).getAllOffsetLimit(customerListDTO.getOffset(), customerListDTO.getLimit(),
                customerListDTO.getSearch());
        verify(customerRepository).getAllOffsetLimitCount(customerListDTO.getSearch());
    }

    @DisplayName("Test Mock Delete a Customer")
    @Test
    void testDeleteCustomer() {
        CustomerDeleteDTO customerDeleteDTO = new CustomerDeleteDTO();
        customerDeleteDTO.setPhone(phoneNumber);
        customerDeleteDTO.setUserType("admin");
        customerDeleteDTO.setToken(token);

        customer.setPhone(phoneNumber);

        when(authFilter.getAdminFromToken(customerDeleteDTO.getToken())).thenReturn(new Admin());
        when(customerRepository.findOneByPhone(customerDeleteDTO.getPhone())).thenReturn(customer);
        when(customerRepository.save(any(Customer.class))).thenReturn(customerMock);

        ResponseDTO responseDTO = customerService.customerDelete(customerDeleteDTO);
        assertNotNull(responseDTO);
        assertEquals(ConstantUtil.STATUS_SUCCESS, responseDTO.getCode());

        verify(customerRepository).findOneByPhone(customerDeleteDTO.getPhone());
        verify(customerRepository).save(any(Customer.class));
    }

    @DisplayName("Test Mock List Item Merchant")
    @Test
    void testItemMerchantList() {
        FilterItemListRequestdto filterItemListRequestdto = new FilterItemListRequestdto();
        filterItemListRequestdto.setUsername("merchanttest");
        filterItemListRequestdto.setLimit(2);
        filterItemListRequestdto.setOffset(0);

        Merchant merchant = new Merchant();
        merchant.setId(14L);

        List<Item> items = new ArrayList<>();
        for (int i = 1; i <= 2; i++) {
            Item item = new Item();
            item.setId((long) i);
            item.setName("TestItem"+i);
            item.setPrice(1000L);
            item.setMaxItem(5);
            item.setAssemblyTime(1);
            item.setMerchant(merchant);
            item.setEnabled(true);
            item.setOutStock(false);
            item.setType((short) 1);
            items.add(item);
        }

        when(merchantRepository.findOneByUsername(filterItemListRequestdto.getUsername())).thenReturn(merchant);
        when(itemRepository.findAllOffsetLimitByMerchantIdNon(merchant.getId(), filterItemListRequestdto.getSearch(),
                filterItemListRequestdto.getLimit(), filterItemListRequestdto.getOffset())).thenReturn(items);
        when(itemRepository.findAllOffsetLimitByMerchantIdNonCount(merchant.getId(), filterItemListRequestdto.getSearch()))
                .thenReturn(2);

        ResponseListDTO responseListDTO = customerService.merchantItemView(filterItemListRequestdto);
        assertNotNull(responseListDTO);
        assertEquals(2, responseListDTO.getData().size());

        verify(merchantRepository).findOneByUsername(filterItemListRequestdto.getUsername());
        verify(itemRepository).findAllOffsetLimitByMerchantIdNon(merchant.getId(), filterItemListRequestdto.getSearch(),
                filterItemListRequestdto.getLimit(), filterItemListRequestdto.getOffset());
        verify(itemRepository).findAllOffsetLimitByMerchantIdNonCount(merchant.getId(), filterItemListRequestdto.getSearch());
    }

    @DisplayName("Test Mock List Item Merchant Miscellaneous")
    @Test
    void testItemMerchantListMisc() {
        FilterItemListRequestdto filterItemListRequestdto = new FilterItemListRequestdto();
        filterItemListRequestdto.setUsername("merchanttest");
        filterItemListRequestdto.setLimit(2);
        filterItemListRequestdto.setOffset(0);

        Merchant merchant = new Merchant();
        merchant.setId(14L);

        List<Item> items = new ArrayList<>();
        for (int i = 1; i <= 2; i++) {
            Item item = new Item();
            item.setId((long) i);
            item.setName("TestItem"+i);
            item.setPrice(1000L);
            item.setMaxItem(5);
            item.setAssemblyTime(1);
            item.setMerchant(merchant);
            item.setEnabled(true);
            item.setOutStock(false);
            item.setType((short) 1);
            item.setMiscellaneous(true);
            items.add(item);
        }

        when(merchantRepository.findOneByUsername(filterItemListRequestdto.getUsername())).thenReturn(merchant);
        when(itemRepository.findAllOffsetLimitByMerchantIdMisc(merchant.getId(), filterItemListRequestdto.getSearch(),
                filterItemListRequestdto.getLimit(), filterItemListRequestdto.getOffset())).thenReturn(items);
        when(itemRepository.findAllOffsetLimitByMerchantIdMiscCount(merchant.getId(), filterItemListRequestdto.getSearch()))
                .thenReturn(2);

        ResponseListDTO responseListDTO = customerService.merchantItemViewMisc(filterItemListRequestdto);
        assertNotNull(responseListDTO);
        assertEquals(2, responseListDTO.getData().size());

        verify(merchantRepository).findOneByUsername(filterItemListRequestdto.getUsername());
        verify(itemRepository).findAllOffsetLimitByMerchantIdMisc(merchant.getId(), filterItemListRequestdto.getSearch(),
                filterItemListRequestdto.getLimit(), filterItemListRequestdto.getOffset());
        verify(itemRepository).findAllOffsetLimitByMerchantIdMiscCount(merchant.getId(), filterItemListRequestdto.getSearch());
    }

    @DisplayName("Test Mock Detail Item Merchant")
    @Test
    void testDetailItemMerchant() {
        Long id = 14L;
        String filename = "test.jpg";

        Item item = new Item();
        item.setId(id);
        item.setName("TestItem");
        item.setPicture(filename);
        item.setEnabled(true);
        item.setType((short) 1);

        AttributeItem attributeItem = new AttributeItem();
        ArrayList<AttributeItem> attributeItems = new ArrayList<>();
        attributeItem.setId(14L);
        attributeItem.setName("TestAttribute");
        attributeItem.setStock(0L);
        attributeItem.setPrice(100L);
        attributeItems.add(attributeItem);

        when(itemRepository.findOneById(id)).thenReturn(item);
        when(attributeItemService.viewByItemId(anyLong())).thenReturn(attributeItems);

        MerchantItemResponseDTO merchantItemResponseDTO = customerService.itemDetail(id);
        assertNotNull(merchantItemResponseDTO);
        assertEquals(item.getName(), merchantItemResponseDTO.getName());
        assertEquals(endpointUrl + "/" + bucketName + "/" + filename, merchantItemResponseDTO.getImage());

        verify(itemRepository).findOneById(id);
    }

    @DisplayName("Test Mock Detail Merchant")
    @Test
    void testDetailMerchant() {
        String username = "merchanttest";
        String filename = "test.jpg";

        Merchant merchant = new Merchant();
        merchant.setUsername(username);
        merchant.setPhone("62");
        merchant.setAddress("Indonesia");
        merchant.setPicture(filename);

        when(merchantRepository.findOneByUsername(username)).thenReturn(merchant);

        ResponseDTO responseDTO = customerService.merchantDetail(username);
        assertNotNull(responseDTO);
        assertEquals(merchant.getUsername(), responseDTO.getData().get("username"));
        assertEquals(endpointUrl + "/" + bucketName + "/" + filename, responseDTO.getData().get("picture"));

        verify(merchantRepository).findOneByUsername(username);
    }
}
