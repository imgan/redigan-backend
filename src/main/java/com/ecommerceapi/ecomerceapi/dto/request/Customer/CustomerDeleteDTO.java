package com.ecommerceapi.ecomerceapi.dto.request.Customer;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public class CustomerDeleteDTO {

    @NotEmpty
    @Size(max = 18, message = "phone number max character is 18")
    private String phone;

    private String userType;

    private String token;

    private Integer pin;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public Integer getPin() {
        return pin;
    }

    public void setPin(Integer pin) {
        this.pin = pin;
    }
}
