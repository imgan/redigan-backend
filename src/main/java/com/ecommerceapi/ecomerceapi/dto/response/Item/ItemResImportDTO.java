package com.ecommerceapi.ecomerceapi.dto.response.Item;

import com.ecommerceapi.ecomerceapi.dto.request.AttributeItem.AttributeItemDTO;
import com.ecommerceapi.ecomerceapi.dto.request.Item.ItemDayImportDTO;
import com.ecommerceapi.ecomerceapi.model.AttributeItem;
import com.ecommerceapi.ecomerceapi.model.Item;

import java.util.ArrayList;
import java.util.List;

public class ItemResImportDTO {
    private List<Long> itemIds = new ArrayList<>();
    private List<Item> items = new ArrayList<>();
    private List<ItemDayImportDTO> itemDayImports = new ArrayList<>();
    private List<AttributeItemDTO> attributeItems = new ArrayList<>();

    public List<Long> getItemIds() {
        return itemIds;
    }

    public void setItemIds(List<Long> itemIds) {
        this.itemIds = itemIds;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public List<ItemDayImportDTO> getItemDayImports() {
        return itemDayImports;
    }

    public void setItemDayImports(List<ItemDayImportDTO> itemDayImports) {
        this.itemDayImports = itemDayImports;
    }

    public List<AttributeItemDTO> getAttributeItems() {
        return attributeItems;
    }

    public void setAttributeItems(List<AttributeItemDTO> attributeItems) {
        this.attributeItems = attributeItems;
    }
}
