package com.ecommerceapi.ecomerceapi.controllers;

import com.ecommerceapi.ecomerceapi.util.DateTimeUtil;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("ALL")
public class BaseController {

    protected String getIpAddress(HttpServletRequest request) {
        return request.getHeader("X-FORWARDED-FOR") == null ? request.getRemoteAddr() : request.getHeader("X-FORWARDED-FOR");
    }

    public boolean isExistingData(Object data) {
        return !(data == null || data.toString().isEmpty());
    }

    protected boolean isExistingDataAndStringValue(Object data) {
        if (isExistingData(data)) {
            return (data instanceof String);
        }
        return false;
    }

    protected boolean isExistingDataAndMapValue(Object data) {
        if (isExistingData(data)) {
            if (data instanceof Map) {
                return !((Map) data).isEmpty();
            }
        }
        return false;
    }

    protected boolean isExistingDataAndListValue(Object data) {
        if (isExistingData(data)) {
            if (data instanceof List) {
                return !((List) data).isEmpty();
            }
        }
        return false;
    }

    protected boolean isExistingDataAndIntegerValue(Object data) {
        if (isExistingData(data)) {
            return (data instanceof Integer);
        }
        return false;
    }

    protected boolean isExistingDataAndLongValue(Object data) {
        if (isExistingData(data)) {
            return (data instanceof Long);
        }
        return false;
    }

    protected boolean isExistingDataAndDateValue(Object data) {
        if (isExistingData(data)) {
            return (DateTimeUtil.convertStringToDateCustomized(data.toString(), DateTimeUtil.API_ECM) != null);
        }
        return false;
    }

    protected Map errorResponseIncompleteData(List<String> field) {
        String fields = "";
        for (int i = 0; i < field.size() ; i++) {
            if(i > 0) fields += ", ";
            fields += field.get(i);
        }
        return responseFormat(400, fields, null);
    }


    protected Map responseFormat(int codeId, String field, Object data) {
        String info;
        switch (codeId) {
            case 200:
                info = "successfully loaded : " + field;
                break;
            case 202:
                info = "login success";
                if (field != null) {
                    info += " : " + field;
                }
                break;
            case 422:
                info = "incomplete data (errorResponse Base): " + field;
                break;
            case 400:
                info = "invalid data format : " + field;
                break;
            case 401:
                info = "invalid access";
                if (field != null) {
                    info += " : " + field;
                }
                break;
            case 409:
                info = "Existing request : " + field;
                break;
            case 406:
                info = "Invalid request : " + field;
                break;
            case 404:
                info = "data not found : " + field;
                break;
            case 500:
                info = "internal server error";
                if (field != null) {
                    info += " : " + field;
                }
                break;
            case 100:
                info = "token invalid";
                if (field != null) {
                    info += " : " + field;
                }
                break;
            case 101:
                info = "token expired";
                if (field != null) {
                    info += " : " + field;
                }
                break;
            default:
                info = "Unknown error : " + field;
                break;
        }

        Map resultMap = new HashMap();
        resultMap.put("code", codeId);
        resultMap.put("info", info);

        if (data != null) {
            if(data instanceof String) {
                Map map = new HashMap();
                map.put("description", data);
                data = map;
            }
        }

        resultMap.put("data", data);
        return resultMap;
    }

    protected HttpStatus httpStatusCode(Map data, boolean isTrx) {
        Integer code = (Integer) data.get("code");
        if(code == null){
            code = ((Long) data.get("Code")).intValue();
        }
        HttpStatus httpStatus;
        switch (code) {
            case 201:
                if(isTrx){
                    httpStatus = HttpStatus.CREATED;
                }else{
                    httpStatus = HttpStatus.OK;
                }
                break;
            case 200:
                httpStatus = HttpStatus.OK;
                break;
            case 400:
                httpStatus = HttpStatus.BAD_REQUEST;
                break;
            case 401:
                httpStatus = HttpStatus.CONFLICT;
                break;
            case 404:
                httpStatus = HttpStatus.NOT_FOUND;
                break;
            case 403:
                httpStatus = HttpStatus.FORBIDDEN;
                break;
            case 500:
                httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                break;
            default:
                httpStatus = HttpStatus.OK;
                break;
        }

        return httpStatus;
    }
}
