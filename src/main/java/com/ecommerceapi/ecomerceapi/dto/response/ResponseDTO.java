package com.ecommerceapi.ecomerceapi.dto.response;

import java.util.Map;

public class ResponseDTO {

    private Integer code;
    private String info;
    private Map data;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Map getData() {
        return data;
    }

    public void setData(Map data) {
        this.data = data;
    }
}
