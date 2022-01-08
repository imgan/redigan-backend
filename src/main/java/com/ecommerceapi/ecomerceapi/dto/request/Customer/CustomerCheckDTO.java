package com.ecommerceapi.ecomerceapi.dto.request.Customer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CustomerCheckDTO {

    private List<Integer> itemId = new ArrayList<Integer>();


    private List<Map> itemsId = new ArrayList<>();

    public List<Map> getItemsId() {
        return itemsId;
    }

    public void setItemsId(List<Map> itemsId) {
        this.itemsId = itemsId;
    }

    public List<Integer> getItemId() {
        return itemId;
    }

    public void setItemId(List<Integer> itemId) {
        this.itemId = itemId;
    }
}
