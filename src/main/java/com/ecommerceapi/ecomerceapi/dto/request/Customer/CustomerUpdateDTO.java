package com.ecommerceapi.ecomerceapi.dto.request.Customer;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public class CustomerUpdateDTO {

    @Min(0)
    private Long id;

    @NotEmpty
    @Size(max = 18, message = "phoneNumber must be lees than 18 characters")
    private String phoneNumber;

    @Size(max = 100, message = "email must be lees than 100 characters")
    private String email;

    @NotEmpty
    @Size(max = 100, message = "name must be lees than 100 characters")
    private String customerName;

    @NotEmpty
    @Size(max = 255, message = "address must be lees than 255 characters")
    private String address;

    private Boolean status;

    private String userType = "none";
    private String token;
    private Integer pin;

    private String city;

    private String password;

    private String postalCode;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
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

    public Integer getPin() {
        return pin;
    }

    public void setPin(Integer pin) {
        this.pin = pin;
    }
}
