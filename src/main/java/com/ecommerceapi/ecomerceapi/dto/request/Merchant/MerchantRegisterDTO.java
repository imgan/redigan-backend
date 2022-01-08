package com.ecommerceapi.ecomerceapi.dto.request.Merchant;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public class MerchantRegisterDTO {
    @NotEmpty
    @Size(max = 100, message = "username must be less than 100 characters")
    private String username;

    @NotEmpty
    @Size(max = 255, message = "storename has maximum characters")
    private String storeName;

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

    private String availableDelivery;

    private String city;

    private String postalCode;

    @Size(max = 100, message = "ipAddress has maximum characters")
    private String ipAddress;

    @Size(max = 100, message = "password has maximum characters")
    private String password;

    @NotEmpty
    @Size(max = 20, message = "operation number has maximum characters")
    private String operationNumber;

    @Size(max = 100, message = "password_confirm has maximum characters")
    private String passwordConfirm;

    public String getAvailableDelivery() {
        return availableDelivery;
    }

    public void setAvailableDelivery(String availableDelivery) {
        this.availableDelivery = availableDelivery;
    }

    public String getOperationNumber() {
        return operationNumber;
    }


    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
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

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public void setOperationNumber(String operationNumber) {
        this.operationNumber = operationNumber;
    }

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

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getStorename() {
        return storeName;
    }

    public void setStorename(String storename) {
        this.storeName = storename;
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

    public String getBankaccount() {
        return bankAccount;
    }

    public void setBankaccount(String bankaccount) {
        this.bankAccount = bankaccount;
    }

    public String getBankaccountname() {
        return bankAccountName;
    }

    public void setBankAccountName(String bankaccountname) {
        this.bankAccountName = bankaccountname;
    }

    public String getBankname() {
        return bankName;
    }

    public void setBankname(String bankname) {
        this.bankName = bankname;
    }

    public void setAddress(String address) {
        this.address = address;
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
}
