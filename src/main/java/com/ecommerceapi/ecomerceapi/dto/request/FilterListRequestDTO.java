package com.ecommerceapi.ecomerceapi.dto.request;

import javax.validation.constraints.Min;

public class FilterListRequestDTO {

    @Min(value = 0, message = "Limit must be greater than or equal to 0")
    private Integer limit = 999999999;

    @Min(value = 0, message = "Limit must be greater than or equal to 0")
    private Integer offset = 0;

    @Min(value = 0, message = "Page must be greater than or equal to 0")
    private Integer page = 1;

    private String search = "";

    private String startDate;
    private String endDate;

    private Integer paid =0;
    /** Auth */
    private String userType = "none";
    private String merchant;
    private Integer pin;

    public Integer getPaid() {
        return paid;
    }

    public void setPaid(Integer paid) {
        this.paid = paid;
    }

    /** Getter Setter */

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getMerchant() {
        return merchant;
    }

    public void setMerchant(String merchant) {
        this.merchant = merchant;
    }

    public Integer getPin() {
        return pin;
    }

    public void setPin(Integer pin) {
        this.pin = pin;
    }

    /** End Getter Setter */
}
