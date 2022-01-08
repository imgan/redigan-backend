package com.ecommerceapi.ecomerceapi.dto.response;

import java.util.Map;

public class ResponseAnyDTO<Any> {

    private Integer code;
    private String info;
    private Any data;

    /** Constructor */
    public ResponseAnyDTO(Integer code, String info, Any data) {
        this.code = code;
        this.info = info;
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

    public Any getData() {
        return data;
    }

    public void setData(Any data) {
        this.data = data;
    }
    /** End Getter Setter */
}
