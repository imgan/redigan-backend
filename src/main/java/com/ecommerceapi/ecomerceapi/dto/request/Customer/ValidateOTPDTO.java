package com.ecommerceapi.ecomerceapi.dto.request.Customer;

import javax.validation.constraints.NotEmpty;

public class ValidateOTPDTO {

    @NotEmpty
    private String otp;

    @NotEmpty
    private String phoneNumber;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}
