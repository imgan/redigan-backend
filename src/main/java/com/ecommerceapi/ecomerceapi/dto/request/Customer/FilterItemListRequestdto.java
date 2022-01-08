package com.ecommerceapi.ecomerceapi.dto.request.Customer;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public class FilterItemListRequestdto {

    @NotEmpty
    @Size(max = 100, message = "Username must be lees than 100 characters")
    private String username;

    @Min(value = 0, message = "Limit must be greater than or equal to 0")
    private Integer limit = 15;

    @Min(value = 0, message = "Limit must be greater than or equal to 0")
    private Integer offset = 0;

    @Min(value = 0, message = "Page must be greater than or equal to 0")
    private Integer page = 1;

    private String search = "";

    /** Getter Setter */
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

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
    /** End Getter Setter */
}
