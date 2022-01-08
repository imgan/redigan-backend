package com.ecommerceapi.ecomerceapi.dto.request.PaymentGateway;

import com.amazonaws.services.dynamodbv2.xspec.B;

import javax.validation.constraints.NotEmpty;

public class NotificationRequestDTO {

    @NotEmpty
    private String orderNumber;

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    @Override
    public String toString() {
        return "NotificationRequestDTO{" +
                "orderNumber='" + orderNumber + '\'' +
                '}';
    }
}
