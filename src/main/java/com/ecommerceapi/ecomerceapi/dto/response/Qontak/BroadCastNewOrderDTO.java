package com.ecommerceapi.ecomerceapi.dto.response.Qontak;

import java.util.Map;

public class BroadCastNewOrderDTO {
    private  String status;
    private Map data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Map getData() {
        return data;
    }

    public void setData(Map data) {
        this.data = data;
    }
}
