package com.ecommerceapi.ecomerceapi.dto.request.Admin;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public class AdminForgotPasswordDTO {

    @NotEmpty
    @Size(max = 100, message = "email must be less than 100 characters")
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
