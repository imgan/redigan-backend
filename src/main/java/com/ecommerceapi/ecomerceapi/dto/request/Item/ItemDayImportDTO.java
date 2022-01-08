package com.ecommerceapi.ecomerceapi.dto.request.Item;

import com.ecommerceapi.ecomerceapi.model.ItemAvailableDay;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ItemDayImportDTO {
    private Integer id;
    private String name;
    private Date createdAt;
    private List<Integer> itemDayList = new ArrayList<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public List<Integer> getItemDayList() {
        return itemDayList;
    }

    public void setItemDayList(List<Integer> itemDayList) {
        this.itemDayList = itemDayList;
    }
}
