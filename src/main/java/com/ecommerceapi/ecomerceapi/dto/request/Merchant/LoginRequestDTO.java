package com.ecommerceapi.ecomerceapi.dto.request.Merchant;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public class LoginRequestDTO {

    @NotEmpty
    @Size(max = 100, message = "Username must be lees than 100 characters")
    private String username;

    @NotEmpty
    @Size(max = 255, message = "Password has maximum characters")
    private String password;

    private String ipAddress;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
}
