package com.ecommerceapi.ecomerceapi.dto.request.Admin;

import org.hibernate.annotations.ColumnDefault;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Column;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public class AdminFormRequestDTO {

    @NotEmpty
    @Size(max = 100, message = "username must be less than 100 characters")
    private String username;

    @Size(max = 100, message = "email must be less than 100 characters")
    private String email;

    @NotEmpty
    @Size(max = 100, message = "name must be less than 100 characters")
    private String officerName;

    @Size(max = 20, message = "phone has maximum characters")
    private String phone;

    @Size(max = 100, message = "password has maximum characters")
    private String password;

    @Size(max = 100, message = "confirm password has maximum characters")
    private String passwordConfirm;

    @Size(max = 100, message = "old password has maximum characters")
    private String passwordOld;

    @Min(value = 1, message = "role must be set")
    private Integer roleId;

    private Integer status;

    private String ipAddress;

    private Integer pin;

    /** Getter Setter */
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOfficerName() {
        return officerName;
    }

    public void setOfficerName(String officerName) {
        this.officerName = officerName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public String getPasswordOld() {
        return passwordOld;
    }

    public void setPasswordOld(String passwordOld) {
        this.passwordOld = passwordOld;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Integer getPin() {
        return pin;
    }

    public void setPin(Integer pin) {
        this.pin = pin;
    }
    /** End Getter Setter */
}
