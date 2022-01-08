package com.ecommerceapi.ecomerceapi.dto.request.Merchant;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public class MerchantUpdateDTO {

    @NotEmpty
    @Size(max = 100, message = "usename must be lees than 100 characters")
    private String username;

    @NotEmpty
    @Size(max = 255, message = "storename has maximum characters")
    private String storeName;

    private Integer pin;

    @NotEmpty
    @Size(max = 50, message = "email has maximum characters")
    private String email;

    @NotEmpty
    @Size(max = 18, message = "phone_number has maximum characters")
    private String phoneNumber;

    @NotEmpty
    @Size(max = 500, message = "address has maximum characters")
    private String address;

    @NotEmpty
    @Size(max = 100, message = "bankaccount has maximum characters")
    private String bankAccount;

    @NotEmpty
    @Size(max = 100, message = "bankaccountaame has maximum characters")
    private String bankAccountName;

    @NotEmpty
    @Size(max = 100, message = "bankname has maximum characters")
    private String bankName;

    private Boolean isDeleted;

    @Size(max = 100, message = "ipAddress has maximum characters")
    private String ipAddress;

    @Size(max = 100, message = "password has maximum characters")
    private String password;

    private String oldPassword;

    @Size(max = 100, message = "passwordConfirm has maximum characters")
    private String passwordConfirm;

    @Size(max = 100, message = "createdBy has maximum characters")
    private String createdBy;

    @NotEmpty
    @Size(max = 20, message = "operation number has maximum characters")
    private String operationNumber;

    private String about;

    private String openHour;

    private String closeHour;

    @Size(max = 100, message = "working day has maximum characters")
    private String workingDay;

    private String userType;

    private String token;

    private Integer status;

    private String city;

    private String postalCode;

    private String availableDelivery;

    public String getAvailableDelivery() {
        return availableDelivery;
    }

    public void setAvailableDelivery(String availableDelivery) {
        this.availableDelivery = availableDelivery;
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

    public String getWorkingDay() {
        return workingDay;
    }

    public void setWorkingDay(String workingDay) {
        this.workingDay = workingDay;
    }
    public String getUsername() {
        return username;
    }

    public Integer getPin() {
        return pin;
    }

    public void setPin(Integer pin) {
        this.pin = pin;
    }

    public String getPasswordConfirm() {
        return passwordConfirm;
    }

    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getBankAccountName() {
        return bankAccountName;
    }

    public void setBankAccountName(String bankAccountName) {
        this.bankAccountName = bankAccountName;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getOperationNumber() {
        return operationNumber;
    }

    public void setOperationNumber(String operationNumber) {
        this.operationNumber = operationNumber;
    }

    public String getOpenHour() {
        return openHour;
    }

    public void setOpenHour(String openHour) {
        this.openHour = openHour;
    }

    public String getCloseHour() {
        return closeHour;
    }

    public void setCloseHour(String closeHour) {
        this.closeHour = closeHour;
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

    public Integer getStatus() {
        return status;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
