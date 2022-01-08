package com.ecommerceapi.ecomerceapi.dto.request.Customer;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public class CustomerViewItemListDTO {

    @NotEmpty
    @Size(max = 100, message = "username max character is 100")
    private String username;

    @Min(0)
    private Integer offset;

    @Min(1)
    private Integer limit;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }
}
