package com.ecommerceapi.ecomerceapi.dto.response.Item;

import com.ecommerceapi.ecomerceapi.model.AttributeItem;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ItemResponseDTO {

    @NotEmpty
    private Long id;

    private String merchantName;

    private String storeName;

    @NotEmpty
    private String name;

    private Long price;

    private Integer maxItemDay;

    private String description;

    private Integer assemblyTime;

    private String image;

    private Boolean enabled;

    private Boolean outStock;

    private Short type;

    private Boolean miscellaneous;

    private List<Integer> availableDay = new ArrayList<Integer>();

    private List<Map> attributeItems;

    public List<Map> getAttributeItems() {
        return attributeItems;
    }

    public void setAttributeItems(List<Map> attributeItems) {
        this.attributeItems = attributeItems;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    /** Getter Setter */


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

    public Integer getMaxItemDay() {
        return maxItemDay;
    }

    public void setMaxItemDay(Integer maxItemDay) {
        this.maxItemDay = maxItemDay;
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

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
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

    public Boolean getMiscellaneous() {
        return miscellaneous;
    }

    public void setMiscellaneous(Boolean miscellaneous) {
        this.miscellaneous = miscellaneous;
    }

    public List<Integer> getAvailableDay() {
        return availableDay;
    }

    public void setAvailableDay(List<Integer> availableDay) {
        this.availableDay = availableDay;
    }
    /** End Getter Setter */
}
