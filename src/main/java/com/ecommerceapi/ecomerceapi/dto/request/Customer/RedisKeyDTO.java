package com.ecommerceapi.ecomerceapi.dto.request.Customer;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public class RedisKeyDTO {
    @NotEmpty
    @Size(max = 18, message = "phoneNumber must be lees than 18 characters")
    private String phoneNumber;

    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
