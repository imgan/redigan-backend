package com.ecommerceapi.ecomerceapi.dto.request.Order;

import javax.validation.constraints.Min;

public class FilterListAllRequestDTO {
    @Min(value = 0, message = "Limit must be greater than or equal to 0")
    private Integer limit = 15;

    @Min(value = 0, message = "Limit must be greater than or equal to 0")
    private Integer offset = 0;

    @Min(value = 0, message = "Page must be greater than or equal to 0")
    private Integer page = 1;

    private String search = "";

    private String merchant = "";

    private String status = "all";

    private String startDate;
    private String endDate;

    private String userType = "none";

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

    public String getMerchant() {
        return merchant;
    }

    public void setMerchant(String merchant) {
        this.merchant = merchant;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
}
