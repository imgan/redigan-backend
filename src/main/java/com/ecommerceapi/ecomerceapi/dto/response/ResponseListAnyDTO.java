package com.ecommerceapi.ecomerceapi.dto.response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ResponseListAnyDTO<Any> {

    private Integer code;

    private String info;

    private Map detail;

    private List<Any> data = new ArrayList<Any>();

    /** Constructor */
    public ResponseListAnyDTO(Integer code, String info, Map detail, List<Any> data) {
        this.code = code;
        this.info = info;
        this.detail = detail;
        this.data = data;
    }

    /** Getter Setter */
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

    public List<Any> getData() {
        return data;
    }

    public void setData(List<Any> data) {
        this.data = data;
    }
    /** End Getter Setter */
}