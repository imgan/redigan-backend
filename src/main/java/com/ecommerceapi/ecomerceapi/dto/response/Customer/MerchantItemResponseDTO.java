package com.ecommerceapi.ecomerceapi.dto.response.Customer;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MerchantItemResponseDTO {

    @NotEmpty
    private Long id;

    @NotEmpty
    private String name;

    private Long price;

    @JsonProperty("max_item_day")
    private Integer maxItem;

    private String description;

    @JsonProperty("assembly_time")
    private Integer assemblyTime;

    private String image;

    @JsonProperty("out_stock")
    private Boolean outStock;

    private Short type;

    private List<Map> attributeItems;

    @JsonProperty("available_day")
    private List<Integer> availableDay = new ArrayList<Integer>();

    /** Getter Setter */

    public List<Map> getAttributeItems() {
        return attributeItems;
    }

    public void setAttributeItems(List<Map> attributeItems) {
        this.attributeItems = attributeItems;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Integer getMaxItem() {
        return maxItem;
    }

    public void setMaxItem(Integer maxItem) {
        this.maxItem = maxItem;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getAssemblyTime() {
        return assemblyTime;
    }

    public void setAssemblyTime(Integer assemblyTime) {
        this.assemblyTime = assemblyTime;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Boolean getOutStock() {
        return outStock;
    }

    public void setOutStock(Boolean outStock) {
        this.outStock = outStock;
    }

    public Short getType() {
        return type;
    }

    public void setType(Short type) {
        this.type = type;
    }

    public List<Integer> getAvailableDay() {
        return availableDay;
    }

    public void setAvailableDay(List<Integer> availableDay) {
        this.availableDay = availableDay;
    }
    /** End Getter Setter */
}
