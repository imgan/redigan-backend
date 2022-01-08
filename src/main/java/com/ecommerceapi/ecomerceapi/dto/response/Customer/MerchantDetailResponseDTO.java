package com.ecommerceapi.ecomerceapi.dto.response.Customer;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MerchantDetailResponseDTO {

    private String username;

    @JsonProperty("phone_number")
    private String phone;

    private String address;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
