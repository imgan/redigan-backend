package com.ecommerceapi.ecomerceapi.dto.request.Customer;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public class CustomerViewDTO {

    @NotEmpty
    @Size(max = 100, message = "username must be lees than 100 characters")
    private String phoneNumber;

    private String userType = "none";
    private String token;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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
