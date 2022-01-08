package com.ecommerceapi.ecomerceapi.dto.request.Admin;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public class AdminKeyRequestDTO {

    @NotEmpty
    @Size(max = 100, message = "username must be less than 100 characters")
    private String username;

    private Integer pin;

    /** Getter Setter */
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getPin() {
        return pin;
    }

    public void setPin(Integer pin) {
        this.pin = pin;
    }
    /** End Getter Setter */
}
