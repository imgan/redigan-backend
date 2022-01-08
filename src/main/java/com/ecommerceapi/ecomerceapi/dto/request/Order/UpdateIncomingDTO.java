package com.ecommerceapi.ecomerceapi.dto.request.Order;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

public class UpdateIncomingDTO {

    @Min(value = 0)
    private Integer deliveryFee;

    private String additionalInfo;

    @Min(value = 0)
    private Integer additionalFee;

    @NotEmpty
    private String orderNumber;

    private Integer pin;

    private String reason;

    /** Admin */
    private String userType = "none";
    private String merchant;

    public Integer getPin() {
        return pin;
    }

    public void setPin(Integer pin) {
        this.pin = pin;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Integer getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(Integer deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public Integer getAdditionalFee() {
        return additionalFee;
    }

    public void setAdditionalFee(Integer additionalFee) {
        this.additionalFee = additionalFee;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getMerchant() {
        return merchant;
    }

    public void setMerchant(String merchant) {
        this.merchant = merchant;
    }
}
