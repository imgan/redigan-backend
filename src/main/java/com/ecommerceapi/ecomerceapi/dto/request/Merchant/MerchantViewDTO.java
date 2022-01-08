package com.ecommerceapi.ecomerceapi.dto.request.Merchant;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public class MerchantViewDTO {

    @NotEmpty
    @Size(max = 100, message = "max character username")
    private String username;

    private String userType;

    private String token;

    public String getUsername() {
        return username;
    }

    public void setUsername(String storename) {
        this.username = storename;
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
