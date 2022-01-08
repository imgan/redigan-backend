package com.ecommerceapi.ecomerceapi.dto.request.AttributeItem;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public class AttributeUpdateDTO {

    private Long id;

    @NotEmpty
    @Size(max = 255, message = "name max character is 255")
    private String name;

    private Long price;

    private Long qty;

    private Long itemId;

    private Boolean enabled;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

