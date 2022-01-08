package com.ecommerceapi.ecomerceapi.model;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "merchant")
public class Merchant {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MERCHANT_SEQ")
    @SequenceGenerator(sequenceName = "_merchant_id_seq", allocationSize = 1, name = "MERCHANT_SEQ")
    @Column(name = "id")
    private Long id;

    @Column(name = "store_name", length = 100)
    private String storeName;

    @Column(name = "username", length = 100)
    private String username;

    @Column(name = "password", length = 255)
    private String password;

    @Column(name = "status")
    private Integer status = 0;

    @OneToMany(targetEntity = Item.class , cascade = CascadeType.ALL)
    @JoinColumn(name = "merchant_id", referencedColumnName = "id")
    private List<Item> itemList;

    @Column(name = "working_day")
    private String workingday;  

    @Column(name = "open_hour")
    private String openhour;

    @Column(name = "close_hour")
    private String closeHour;

    @Column(name = "email",length = 100)
    private String email;

    @Column(name = "last_login")
    private Date lastLogin;

    @Column(name = "pin", length = 6 )
    private Integer pin;

    @Column(name = "phone", length = 18)
    private String phone;

    @Column(name = "address", length = 500)
    private String address;

    @Column(name = "bankaccount", length = 50)
    private String bankAccount;

    @Column(name = "bankaccountname", length = 50)
    private String bankAccountName;

    @Column(name = "bankname", length = 50)
    private String bankName;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @Column(name = "picture",length = 255)
    private String picture;

    @Column(name = "created_by",length = 100)
    private String createdBy;

    @Column(name = "created_date")
    private Date createdDate;

    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    @Column(name = "updated_date")
    private Date updatedDate;

    @Column(name = "operation_number", length = 20)
    private String operationNumber;

    @Column(columnDefinition="TEXT")
    private String about;

    @Column(columnDefinition="TEXT")
    private String token_update;

    @Column(name = "preset_message",columnDefinition="TEXT")
    private String presetMessage;

    private String city;

    private String postalCode;

    @Column(name = "available_delivery", length = 20)
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

    public String getPresetMessage() {
        return presetMessage;
    }

    public void setPresetMessage(String presetMessage) {
        this.presetMessage = presetMessage;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getCloseHour() {
        return closeHour;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public void setCloseHour(String closeHour) {
        this.closeHour = closeHour;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    public String getBankAccountName() {
        return bankAccountName;
    }

    public void setBankAccountName(String bankAccountName) {
        this.bankAccountName = bankAccountName;
    }

    public String getOperationNumber() {
        return operationNumber;
    }

    public void setOperationNumber(String operationNumber) {
        this.operationNumber = operationNumber;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        this.password = passwordEncoder.encode(password);
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getWorkingday() {
        return workingday;
    }

    public void setWorkingday(String workingday) {
        this.workingday = workingday;
    }

    public String getOpenhour() {
        return openhour;
    }

    public void setOpenhour(String openhour) {
        this.openhour = openhour;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }

    public Integer getPin() {
        return pin;
    }

    public void setPin(Integer pin) {
        this.pin = pin;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    public String getToken_update() {
        return token_update;
    }

    public void setToken_update(String token_update) {
        this.token_update = token_update;
    }
}
