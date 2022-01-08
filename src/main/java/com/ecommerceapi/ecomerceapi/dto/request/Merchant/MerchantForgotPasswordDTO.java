package com.ecommerceapi.ecomerceapi.dto.request.Merchant;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public class MerchantForgotPasswordDTO {

    @NotEmpty
    @Size(max = 50, message = "email has maximum characters")
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
