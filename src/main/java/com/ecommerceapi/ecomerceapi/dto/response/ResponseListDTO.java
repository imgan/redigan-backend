package com.ecommerceapi.ecomerceapi.dto.response;

import java.util.List;
import java.util.Map;

public class ResponseListDTO {
    private Integer code;
    private String info;
    private Map detail;
    private List<Map> data;

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

    public Map getDetail() {
        return detail;
    }

    public void setDetail(Map detail) {
        this.detail = detail;
    }

    public List<Map> getData() {
        return data;
    }

    public void setData(List<Map> data) {
        this.data = data;
    }
}
