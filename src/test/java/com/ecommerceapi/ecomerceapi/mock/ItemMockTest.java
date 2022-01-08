package com.ecommerceapi.ecomerceapi.mock;

import com.ecommerceapi.ecomerceapi.dto.params.FileNameDTO;
import com.ecommerceapi.ecomerceapi.dto.request.AttributeItem.AttributeCreateDTO;
import com.ecommerceapi.ecomerceapi.dto.request.AttributeItem.AttributeUpdateDTO;
import com.ecommerceapi.ecomerceapi.dto.request.Customer.FilterItemListRequestdto;
import com.ecommerceapi.ecomerceapi.dto.request.FilterListRequestDTO;
import com.ecommerceapi.ecomerceapi.dto.request.Item.ItemDetailDTO;
import com.ecommerceapi.ecomerceapi.dto.request.Item.ItemFilterListDTO;
import com.ecommerceapi.ecomerceapi.dto.request.Item.ItemFormRequestDTO;
import com.ecommerceapi.ecomerceapi.dto.response.Item.ItemResponseDTO;
import com.ecommerceapi.ecomerceapi.dto.response.ResponseDTO;
import com.ecommerceapi.ecomerceapi.dto.response.ResponseListDTO;
import com.ecommerceapi.ecomerceapi.filters.AuthFilter;
import com.ecommerceapi.ecomerceapi.model.*;
import com.ecommerceapi.ecomerceapi.repositories.AttributeItemRepository;
import com.ecommerceapi.ecomerceapi.repositories.ItemAvailableDayRepository;
import com.ecommerceapi.ecomerceapi.repositories.ItemRepository;
import com.ecommerceapi.ecomerceapi.repositories.MerchantRepository;
import com.ecommerceapi.ecomerceapi.services.AmazonS3Service;
import com.ecommerceapi.ecomerceapi.services.AttributeItemService;
import com.ecommerceapi.ecomerceapi.services.ItemServices;
import com.ecommerceapi.ecomerceapi.services.impl.AttributeItemImplements;
import com.ecommerceapi.ecomerceapi.services.impl.ItemServiceImplements;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ItemMockTest {
    String token;
    String filename;
    Item item;

    @Value("${amazonProperties.endpointUrl}")
    private String endpointUrl;

    @Value("${amazonProperties.bucketName}")
    private String bucketName;

    @Value("${app.max.sizeImage}")
    private Integer maximumSize;

    @Mock
    private AmazonS3Service amazonS3Service;

    @Mock
    ItemRepository itemRepository;

    @Mock
    MerchantRepository merchantRepository;

    @Mock
    ItemAvailableDayRepository itemAvailableDayRepository;

    @Mock
    AttributeItemRepository attributeItemRepository;

    @Mock
    Item itemMock = new Item();

    @Mock
    Merchant merchantMock = new Merchant();

    @Mock
    ItemAvailableDay itemAvailableDayMock = new ItemAvailableDay();

    @Mock
    AttributeItemService attributeItemService = new AttributeItemImplements();

    @Mock
    AuthFilter authFilter;

    @InjectMocks
    ItemServices itemServices = new ItemServiceImplements();

    @BeforeEach
    void setupTest() {
        token = "Test token";
        filename = "test.jpg";
        item = new Item();

        ReflectionTestUtils.setField(itemServices, "maximumSize", maximumSize);
        ReflectionTestUtils.setField(itemServices, "endpointUrl", endpointUrl);
        ReflectionTestUtils.setField(itemServices, "bucketName", bucketName);
    }

    @DisplayName("Test Mock Create a Item")
    @Test
    void testCreateItem() throws Exception {
        ItemFormRequestDTO itemFormRequestDTO = new ItemFormRequestDTO();
        itemFormRequestDTO.setName("TestItem");
        itemFormRequestDTO.setPrice(0L);
        itemFormRequestDTO.setMaxItemDay(0);
        itemFormRequestDTO.setDescription("Desc TestItem");
        itemFormRequestDTO.setAssemblyTime(0);
        itemFormRequestDTO.setOutStock(true);
        itemFormRequestDTO.setType((short) 2);
        itemFormRequestDTO.setMiscellaneous(false);
        itemFormRequestDTO.setAvailableDay(new ArrayList<>( List.of(1, 2) ));

        itemMock.setId(14L);
        itemMock.setType(itemFormRequestDTO.getType());

        /** Other File Method
         # Using File
        File file = new File("src/test/resources/images/"+filename);
        FileInputStream is = new FileInputStream(file);
         # OR
        InputStream is = new FileInputStream(file);
        MultipartFile image = new MockMultipartFile("file", file.getName(), MediaType.IMAGE_JPEG_VALUE, is);

         # Using Path
        Path path = Paths.get("src/test/resources/images", filename);
        InputStream is = Files.newInputStream(path);
        MultipartFile image = new MockMultipartFile("file", filename, MediaType.IMAGE_JPEG_VALUE, is);

         # Using byte
        // new byte[0], "Spring Framework".getBytes()
        MultipartFile image = new MockMultipartFile("image", filename, MediaType.IMAGE_JPEG_VALUE, new byte[0]);
        */

        MultipartFile image = new MockMultipartFile("image", filename, MediaType.IMAGE_JPEG_VALUE,
                new byte[128]);
        itemFormRequestDTO.setImage(image);

        Map attributeMap = new HashMap<>();
        attributeMap.put("name", "TestAttribute");
        attributeMap.put("qty", 0);
        attributeMap.put("price", 100);
        List<Map> attributeMaps = new ArrayList<>();
        attributeMaps.add(attributeMap);
        itemFormRequestDTO.setAttributeItems(attributeMaps);

        when(authFilter.getMerchantFromToken(token)).thenReturn(new Merchant());
        when(amazonS3Service.uploadFile(any(FileNameDTO.class))).thenReturn(endpointUrl + "/" + bucketName + "/" + filename);
        when(itemMock.getPicture()).thenReturn(filename);
        when(itemMock.getName()).thenReturn(itemFormRequestDTO.getName());
        when(itemMock.getType()).thenReturn(itemFormRequestDTO.getType());
        when(itemRepository.saveAndFlush(any(Item.class))).thenReturn(itemMock);
        when(itemAvailableDayRepository.save(any(ItemAvailableDay.class))).thenReturn(itemAvailableDayMock);
        when(attributeItemService.createAttributeItem(any(AttributeCreateDTO.class))).thenReturn(new ResponseDTO());

        ItemResponseDTO itemResponseDTO = itemServices.itemCreate(itemFormRequestDTO, token);
        assertNotNull(itemResponseDTO);
        assertNotNull(itemResponseDTO.getId());
        assertEquals(itemFormRequestDTO.getName(), itemResponseDTO.getName());
        assertEquals(endpointUrl + "/" + bucketName + "/" + filename, itemResponseDTO.getImage());

        verify(amazonS3Service).uploadFile(any(FileNameDTO.class));
        verify(itemRepository).saveAndFlush(any(Item.class));
        verify(itemAvailableDayRepository, times(7)).save(any(ItemAvailableDay.class));
        verify(attributeItemService).createAttributeItem(any(AttributeCreateDTO.class));
    }

    @DisplayName("Test Mock Update a Item")
    @Test
    void testUpdateItem() throws Exception {
        merchantMock.setId(14L);
        merchantMock.setStoreName("TestMerchant");
        merchantMock.setUsername("testmerchant");
        item.setId(14L);
        item.setMerchant(merchantMock);
        item.setPicture(filename);
        item.setType((short) 2);

        ItemFormRequestDTO itemFormRequestDTO = new ItemFormRequestDTO();
        itemFormRequestDTO.setId(item.getId());
        itemFormRequestDTO.setName("TestItem");
        itemFormRequestDTO.setPrice(0L);
        itemFormRequestDTO.setMaxItemDay(0);
        itemFormRequestDTO.setDescription("Desc TestItem");
        itemFormRequestDTO.setAssemblyTime(0);
        itemFormRequestDTO.setOutStock(true);
        itemFormRequestDTO.setType(item.getType());
        itemFormRequestDTO.setMiscellaneous(false);
        itemFormRequestDTO.setAvailableDay(new ArrayList<>( List.of(1, 2) ));

        MultipartFile image = new MockMultipartFile("image", filename, MediaType.IMAGE_JPEG_VALUE,
                new byte[128]);
        itemFormRequestDTO.setImage(image);

        Map attributeMap = new HashMap<>();
        attributeMap.put("id", 1);
        attributeMap.put("name", "TestAttribute");
        attributeMap.put("qty", 0);
        attributeMap.put("price", 100);
        attributeMap.put("enabled", true);
        List<Map> attributeMaps = new ArrayList<>();
        attributeMaps.add(attributeMap);
        itemFormRequestDTO.setAttributeItems(attributeMaps);

        AttributeItem attributeItem = new AttributeItem();
        ArrayList<AttributeItem> attributeItems = new ArrayList<>();
        attributeItem.setId(1L);
        attributeItem.setName("TestAttribute");
        attributeItem.setStock(0L);
        attributeItem.setPrice(100L);
        attributeItems.add(attributeItem);

        when(authFilter.getMerchantFromToken(token)).thenReturn(merchantMock);
        when(itemRepository.findOneByIdAndMerchantId(itemFormRequestDTO.getId(), merchantMock.getId())).thenReturn(item);
        when(amazonS3Service.deleteFileFromS3Bucket(endpointUrl + "/" + bucketName + "/" + filename)).thenReturn("");
        when(amazonS3Service.uploadFile(any(FileNameDTO.class))).thenReturn(endpointUrl + "/" + bucketName + "/" + filename);
        when(itemMock.getPicture()).thenReturn(filename);
        when(itemMock.getName()).thenReturn(itemFormRequestDTO.getName());
        when(itemMock.getType()).thenReturn(itemFormRequestDTO.getType());
        when(itemRepository.saveAndFlush(any(Item.class))).thenReturn(item);
        doNothing().when(itemAvailableDayRepository).deleteAllByItemId(anyLong());
        when(itemAvailableDayRepository.findOneByItemIdAndDayIndex(anyLong(), anyInt())).thenReturn(itemAvailableDayMock);
        when(itemAvailableDayRepository.save(any(ItemAvailableDay.class))).thenReturn(itemAvailableDayMock);
//        when(attributeItemService.createAttributeItem(any(AttributeCreateDTO.class))).thenReturn(new ResponseDTO());
        when(attributeItemService.updateAttributeItem(any(AttributeUpdateDTO.class))).thenReturn(new ResponseDTO());
        when(attributeItemService.viewByItemId(anyLong())).thenReturn(attributeItems);

        ItemResponseDTO itemResponseDTO = itemServices.itemUpdate(itemFormRequestDTO, token);
        assertNotNull(itemResponseDTO);
        assertNotNull(itemResponseDTO.getId());
        assertEquals(itemFormRequestDTO.getName(), itemResponseDTO.getName());
        assertEquals(endpointUrl + "/" + bucketName + "/" + filename, itemResponseDTO.getImage());

        verify(itemRepository).findOneByIdAndMerchantId(itemFormRequestDTO.getId(), merchantMock.getId());
        verify(amazonS3Service).deleteFileFromS3Bucket(endpointUrl + "/" + bucketName + "/" + filename);
        verify(amazonS3Service).uploadFile(any(FileNameDTO.class));
        verify(itemRepository).saveAndFlush(any(Item.class));
        verify(itemAvailableDayRepository, times(7)).findOneByItemIdAndDayIndex(anyLong(), anyInt());
        verify(itemAvailableDayRepository, times(7)).save(any(ItemAvailableDay.class));
//        verify(attributeItemService).createAttributeItem(any(AttributeCreateDTO.class));
        verify(attributeItemService).updateAttributeItem(any(AttributeUpdateDTO.class));
        verify(attributeItemService).viewByItemId(anyLong());
    }

    @DisplayName("Test Mock View a Item")
    @Test
    void testViewItem() throws Exception {
        ItemDetailDTO itemDetailDTO = new ItemDetailDTO();
        itemDetailDTO.setId(14L);

        merchantMock.setId(14L);
        merchantMock.setStoreName("TestMerchant");
        merchantMock.setUsername("testmerchant");

        item.setId(itemDetailDTO.getId());
        item.setName("TestItem");
        item.setMerchant(merchantMock);
        item.setPicture(filename);
        item.setType((short) 1);

        AttributeItem attributeItem = new AttributeItem();
        ArrayList<AttributeItem> attributeItems = new ArrayList<>();
        attributeItem.setId(1L);
        attributeItem.setName("TestAttribute");
        attributeItem.setStock(0L);
        attributeItem.setPrice(100L);
        attributeItems.add(attributeItem);

        when(authFilter.getMerchantFromToken(token)).thenReturn(merchantMock);
        when(itemRepository.findOneByIdAndMerchantId(item.getId(), merchantMock.getId())).thenReturn(item);
        when(attributeItemService.viewByItemId(anyLong())).thenReturn(attributeItems);

        ItemResponseDTO itemResponseDTO = itemServices.itemView(itemDetailDTO, token);
        assertNotNull(itemResponseDTO);
        assertNotNull(itemResponseDTO.getId());
        assertEquals(item.getId(), itemResponseDTO.getId());
        assertEquals(item.getName(), itemResponseDTO.getName());
        assertEquals(endpointUrl + "/" + bucketName + "/" + filename, itemResponseDTO.getImage());

        verify(itemRepository).findOneByIdAndMerchantId(item.getId(), merchantMock.getId());
        verify(attributeItemService).viewByItemId(anyLong());
    }

    @DisplayName("Test Mock List Items")
    @Test
    void testListItem() throws Exception {
        AttributeItem attributeItem = new AttributeItem();
        ArrayList<Item> items = new ArrayList<>();
        ArrayList<AttributeItem> attributeItems = new ArrayList<>();

        merchantMock.setId(14L);
        merchantMock.setStoreName("TestMerchant");
        merchantMock.setUsername("testmerchant");

        for (int i = 1; i <= 2; i++) {
            item.setId((long) i);
            item.setName("TestItem"+i);
            item.setMerchant(merchantMock);
            item.setPicture(i+filename);
            item.setType((short) 1);
            items.add(item);

            attributeItem.setId((long) i);
            attributeItem.setName("TestAttribute"+i);
            attributeItem.setStock(0L);
            attributeItem.setPrice(100L);
            attributeItems.add(attributeItem);
        }

        FilterListRequestDTO filterListRequestDTO = new FilterListRequestDTO();
        filterListRequestDTO.setSearch("Test");
        filterListRequestDTO.setLimit(2);
        filterListRequestDTO.setOffset(0);

        when(authFilter.getMerchantFromToken(token)).thenReturn(merchantMock);
        when(itemRepository.findAllOffsetLimitByMerchantId(merchantMock.getId(), filterListRequestDTO.getSearch(),
                filterListRequestDTO.getLimit(), filterListRequestDTO.getOffset())).thenReturn(items);
        when(attributeItemService.viewByItemId(anyLong())).thenReturn(attributeItems);

        List<ItemResponseDTO> listItemResponseDTO = itemServices.itemList(filterListRequestDTO, token);
        assertNotNull(listItemResponseDTO);
        assertEquals(2, listItemResponseDTO.size());

        verify(itemRepository).findAllOffsetLimitByMerchantId(merchantMock.getId(), filterListRequestDTO.getSearch(),
                filterListRequestDTO.getLimit(), filterListRequestDTO.getOffset());
        verify(attributeItemService, times(2)).viewByItemId(anyLong());
    }

    @DisplayName("Test Mock Delete a Item")
    @Test
    void testDeleteItem() throws Exception {
        ItemDetailDTO itemDetailDTO = new ItemDetailDTO();
        itemDetailDTO.setId(14L);

        item.setId(itemDetailDTO.getId());
        item.setName("TestItem");
        item.setPicture(filename);
        item.setType((short) 1);
        merchantMock.setId(14L);

        when(authFilter.getMerchantFromToken(token)).thenReturn(merchantMock);
        when(itemRepository.findOneByIdAndMerchantId(item.getId(), merchantMock.getId())).thenReturn(item);
        when(itemRepository.save(any(Item.class))).thenReturn(itemMock);

        ItemResponseDTO itemResponseDTO = itemServices.itemDelete(itemDetailDTO, token);
        assertNotNull(itemResponseDTO);
        assertNotNull(itemResponseDTO.getId());
        assertEquals(item.getId(), itemResponseDTO.getId());
        assertEquals(item.getName(), itemResponseDTO.getName());
        assertEquals(false, itemResponseDTO.getEnabled());

        verify(itemRepository).findOneByIdAndMerchantId(item.getId(), merchantMock.getId());
        verify(itemRepository).save(any(Item.class));
    }

    @DisplayName("Test Mock Total Page Items")
    @Test
    void testTotalPageItem() throws Exception {
        merchantMock.setId(14L);

        FilterListRequestDTO filterListRequestDTO = new FilterListRequestDTO();
        filterListRequestDTO.setSearch("Test");
        filterListRequestDTO.setLimit(2);
        filterListRequestDTO.setOffset(0);

        when(authFilter.getMerchantFromToken(token)).thenReturn(merchantMock);
        when(itemRepository.findAllCountByMerchantId(merchantMock.getId(), filterListRequestDTO.getSearch())).thenReturn(2);

        Map totalPage = itemServices.totalPage(filterListRequestDTO, token);
        assertNotNull(totalPage);
        assertEquals(2, totalPage.get("total"));
        assertEquals(1, totalPage.get("totalPage"));

        verify(itemRepository).findAllCountByMerchantId(merchantMock.getId(), filterListRequestDTO.getSearch());
    }

    @DisplayName("Test Mock Import Items")
    @Test
    void testImportItem() throws Exception {
        List<Item> items = new ArrayList<>();
        List<ItemAvailableDay> itemAvailableDays = new ArrayList<>();
        String excelfile = "ItemTestImport.xlsx";
        merchantMock.setId(14L);

        for (int i = 1; i <= 2; i++) {
            Item itemList = new Item();
            itemList.setId((long) i);
            itemList.setName("TestItem"+i);
            itemList.setPrice(1000L);
            itemList.setMaxItem(5);
            itemList.setAssemblyTime(1);
            itemList.setPicture(i+filename);
            itemList.setMerchant(merchantMock);
            itemList.setEnabled(true);
            itemList.setOutStock(false);
            itemList.setType((short) 1);
            itemList.setMiscellaneous(false);
            items.add(itemList);
        }

        ItemAvailableDay itemAvailableDay = new ItemAvailableDay();
        itemAvailableDay.setId(14L);
        itemAvailableDays.add(itemAvailableDay);

        Path path = Paths.get("src/test/resources/files", excelfile);
        InputStream is = Files.newInputStream(path);
        MultipartFile excel = new MockMultipartFile("file", excelfile, MediaType.MULTIPART_FORM_DATA_VALUE, is);

        when(itemRepository.saveAllAndFlush(anyList())).thenReturn(items);
        when(itemAvailableDayRepository.saveAll(anyList())).thenReturn(itemAvailableDays);

        Boolean itemImportResponseDTO = itemServices.itemImport(excel, merchantMock);
        assertNotNull(itemImportResponseDTO);
        assertEquals(true, itemImportResponseDTO);

        verify(itemRepository).saveAllAndFlush(anyList());
    }

    @DisplayName("Test Mock List Items")
    @Test
    void testItemsList() {
        ItemFilterListDTO itemFilterListDTO = new ItemFilterListDTO();
        itemFilterListDTO.setUsername("merchanttest");
        itemFilterListDTO.setLimit(2);
        itemFilterListDTO.setOffset(0);
        itemFilterListDTO.setMiscellaneous(0);

        Merchant merchant = new Merchant();
        merchant.setId(14L);
        merchant.setStoreName("TestMerchant");
        merchant.setUsername("testmerchant");

        AttributeItem attributeItem = new AttributeItem();
        List<Item> items = new ArrayList<>();
        ArrayList<AttributeItem> attributeItems = new ArrayList<>();

        for (int i = 1; i <= 2; i++) {
            Item item = new Item();
            item.setId((long) i);
            item.setName("TestItem"+i);
            item.setMerchant(merchant);
            item.setPrice(1000L);
            item.setMaxItem(5);
            item.setAssemblyTime(1);
            item.setMerchant(merchant);
            item.setEnabled(true);
            item.setOutStock(false);
            item.setType((short) 1);
            items.add(item);

            attributeItem.setId((long) i);
            attributeItem.setName("TestAttribute"+i);
            attributeItem.setStock(0L);
            attributeItem.setPrice(100L);
            attributeItems.add(attributeItem);
        }

        when(authFilter.getAdminFromToken(token)).thenReturn(new Admin());
        when(merchantRepository.findOneByUsername(itemFilterListDTO.getUsername())).thenReturn(merchant);
        when(itemRepository.findAllOffsetLimitAdmin(merchant.getId(), itemFilterListDTO.getSearch(),
                itemFilterListDTO.getLimit(), itemFilterListDTO.getOffset(), itemFilterListDTO.getMiscellaneous())).
                thenReturn(items);
        when(itemRepository.findAllOffsetLimitAdminCount(merchant.getId(), itemFilterListDTO.getSearch(),
                itemFilterListDTO.getMiscellaneous())).thenReturn(2);
        when(attributeItemService.viewByItemId(anyLong())).thenReturn(attributeItems);

        ResponseListDTO responseListDTO = itemServices.itemsList(itemFilterListDTO, token);
        assertNotNull(responseListDTO);
        assertEquals(2, responseListDTO.getData().size());

        verify(merchantRepository).findOneByUsername(itemFilterListDTO.getUsername());
        verify(itemRepository).findAllOffsetLimitAdmin(merchant.getId(), itemFilterListDTO.getSearch(),
                itemFilterListDTO.getLimit(), itemFilterListDTO.getOffset(), itemFilterListDTO.getMiscellaneous());
        verify(itemRepository).findAllOffsetLimitAdminCount(merchant.getId(), itemFilterListDTO.getSearch(),
                itemFilterListDTO.getMiscellaneous());
        verify(attributeItemService, times(2)).viewByItemId(anyLong());
    }

    @DisplayName("Test Mock Import Attribute Items")
    @Test
    void testImportAttrItem() throws Exception {
        List<Item> items = new ArrayList<>();
        List<AttributeItem> attributeItems = new ArrayList<>();
        String excelfile = "AttributeItemTestImport.xlsx";

        for (int i = 1; i <= 2; i++) {
            Item itemList = new Item();
            itemList.setId((long) i);
            itemList.setName("TestItem"+i);
            itemList.setPrice(1000L);
            itemList.setMaxItem(5);
            itemList.setAssemblyTime(1);
            itemList.setPicture(i+filename);
            itemList.setMerchant(merchantMock);
            itemList.setEnabled(true);
            itemList.setOutStock(false);
            itemList.setType((short) 1);
            itemList.setMiscellaneous(false);
            items.add(itemList);
        }

        AttributeItem attributeItem = new AttributeItem();
        attributeItem.setId(14L);
        attributeItems.add(attributeItem);

        Path path = Paths.get("src/test/resources/files", excelfile);
        InputStream is = Files.newInputStream(path);
        MultipartFile excel = new MockMultipartFile("file", excelfile, MediaType.MULTIPART_FORM_DATA_VALUE, is);

        when(itemRepository.findAllById(anyList())).thenReturn(items);
        when(attributeItemRepository.saveAll(anyList())).thenReturn(attributeItems);

        Boolean itemImportResponseDTO = itemServices.itemAttributeImport(excel);
        assertNotNull(itemImportResponseDTO);
        assertEquals(true, itemImportResponseDTO);

        verify(itemRepository).findAllById(anyList());
    }
}
