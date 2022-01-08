package com.ecommerceapi.ecomerceapi.dto.request.AttributeItem;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public class AttributeCreateDTO {

    @NotEmpty
    @Size(max = 255, message = "name max character is 255")
    private String name;

    @Min(0)
    private Long price;

    @Min(0)
    private Long qty;

    private Long itemId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public Long getQty() {
        return qty;
    }

    public void setQty(Long qty) {
        this.qty = qty;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }
}
