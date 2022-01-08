package com.ecommerceapi.ecomerceapi.dto.request.Order;

import javax.validation.constraints.NotEmpty;

public class UpdateOngoingDTO {

    @NotEmpty
    private String orderNumber;

    private String trackingLink;

    private Integer pin;

    private Boolean isPaid;

    /** Admin */
    private String userType = "none";
    private String merchant;

    public Integer getPin() {
        return pin;
    }

    public void setPin(Integer pin) {
        this.pin = pin;
    }

    public Boolean getIsPaid() {
        return isPaid;
    }

    public void setIsPaid(Boolean isPaid) {
        this.isPaid = isPaid;
    }

    public String getTrackingLink() {
        return trackingLink;
    }

    public void setTrackingLink(String trackingLink) {
        this.trackingLink = trackingLink;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
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

    @Override
    public String toString() {
        return "UpdateOngoingDTO{" +
                "orderNumber='" + orderNumber + '\'' +
                ", trackingLink='" + trackingLink + '\'' +
                ", pin=" + pin +
                ", isPaid=" + isPaid +
                ", userType='" + userType + '\'' +
                ", merchant='" + merchant + '\'' +
                '}';
    }
}
