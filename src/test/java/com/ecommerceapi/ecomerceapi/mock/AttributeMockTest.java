package com.ecommerceapi.ecomerceapi.mock;

import com.ecommerceapi.ecomerceapi.dto.request.AttributeItem.AttributeCreateDTO;
import com.ecommerceapi.ecomerceapi.dto.request.AttributeItem.AttributeUpdateDTO;
import com.ecommerceapi.ecomerceapi.dto.response.ResponseDTO;
import com.ecommerceapi.ecomerceapi.model.AttributeItem;
import com.ecommerceapi.ecomerceapi.model.Item;
import com.ecommerceapi.ecomerceapi.repositories.AttributeItemRepository;
import com.ecommerceapi.ecomerceapi.repositories.ItemRepository;
import com.ecommerceapi.ecomerceapi.services.AttributeItemService;
import com.ecommerceapi.ecomerceapi.services.impl.AttributeItemImplements;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class AttributeMockTest {
    @Mock
    ItemRepository itemRepository;

    @Mock
    AttributeItemRepository attributeItemRepository;

    @InjectMocks
    AttributeItemService attributeItemService = new AttributeItemImplements();

    @BeforeEach
    void setupTest() {

    }

    @DisplayName("Test Mock Create Attribute Item")
    @Test
    void testCreateAttributeItem() {
        AttributeCreateDTO attributeCreateDTO = new AttributeCreateDTO();
        attributeCreateDTO.setItemId(14L);
        attributeCreateDTO.setName("TestAttribute");
        attributeCreateDTO.setQty(1L);
        attributeCreateDTO.setPrice(100L);

        Item item = new Item();
        item.setId(14L);

        when(itemRepository.findOneById(anyLong())).thenReturn(item);
        when(attributeItemRepository.saveAndFlush(any(AttributeItem.class))).thenReturn(new AttributeItem());

        ResponseDTO responseDTO = attributeItemService.createAttributeItem(attributeCreateDTO);
        assertNotNull(responseDTO);
        assertNotNull(responseDTO.getData());
        assertEquals(attributeCreateDTO.getName(), responseDTO.getData().get("name"));

        verify(itemRepository).findOneById(anyLong());
        verify(attributeItemRepository).saveAndFlush(any(AttributeItem.class));
    }

    @DisplayName("Test Mock Update Attribute Item")
    @Test
    void testUpdateAttributeItem() {
        AttributeUpdateDTO attributeUpdateDTO = new AttributeUpdateDTO();
        attributeUpdateDTO.setId(14L);
        attributeUpdateDTO.setName("TestAttribute");
        attributeUpdateDTO.setQty(1L);
        attributeUpdateDTO.setPrice(100L);

        Item item = new Item();
        item.setId(14L);
        AttributeItem attributeItem = new AttributeItem();
        attributeItem.setId(14L);
        attributeItem.setItem(item);

        when(attributeItemRepository.findOneById(anyLong())).thenReturn(attributeItem);
        when(itemRepository.findOneById(anyLong())).thenReturn(item);
        when(attributeItemRepository.saveAndFlush(any(AttributeItem.class))).thenReturn(attributeItem);

        ResponseDTO responseDTO = attributeItemService.updateAttributeItem(attributeUpdateDTO);
        assertNotNull(responseDTO);
        assertNotNull(responseDTO.getData());
        assertEquals(attributeUpdateDTO.getName(), responseDTO.getData().get("name"));

        verify(attributeItemRepository).findOneById(anyLong());
        verify(itemRepository).findOneById(anyLong());
        verify(attributeItemRepository).saveAndFlush(any(AttributeItem.class));
    }

    @DisplayName("Test Mock View Attribute Item")
    @Test
    void testViewAttributeItem() {
        Long itemId = 14L;

        AttributeItem attributeItem = new AttributeItem();
        List<AttributeItem> attributeItems = new ArrayList<>();
        attributeItems.add(attributeItem);

        when(attributeItemRepository.findAllByItemIdAndEnabledAtt(itemId, true)).thenReturn(attributeItems);

        List<AttributeItem> attributeItemsService = attributeItemService.viewByItemId(itemId);
        assertNotNull(attributeItemsService);
    }
}
