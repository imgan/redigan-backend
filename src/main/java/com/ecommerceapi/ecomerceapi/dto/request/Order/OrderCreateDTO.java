package com.ecommerceapi.ecomerceapi.dto.request.Order;

import com.ecommerceapi.ecomerceapi.model.Item;

import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class OrderCreateDTO {


    @NotEmpty
    private String phoneNumber;

    @NotEmpty
    private String merchantUserName;

    private Date deliveryDate;

    @NotEmpty
    private String deliveryTime;

    private List<Map> itemDetail;

    /** Auth */
    private Integer pin;
    private String userType = "none";
    private String token;

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public List<Map> getItemDetail() {
        return itemDetail;
    }

    public void setItemDetail(List<Map> itemDetail) {
        this.itemDetail = itemDetail;
    }

    public String getMerchantUserName() {
        return merchantUserName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setMerchantUserName(String merchantUserName) {
        this.merchantUserName = merchantUserName;
    }

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public Integer getPin() {
        return pin;
    }

    public void setPin(Integer pin) {
        this.pin = pin;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
