package com.ecommerceapi.ecomerceapi.dto.request.Admin;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public class AdminConfirmPasswordDTO {

    @NotEmpty
    @Size(max = 100, message = "password has maximum characters")
    private String password;

    @NotEmpty
    @Size(max = 100, message = "password confirm has maximum characters")
    private String passwordConfirm;

    @NotEmpty
    private String token;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordConfirm() {
        return passwordConfirm;
    }

    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
