package com.ecommerceapi.ecomerceapi.dto.request.Item;

import com.ecommerceapi.ecomerceapi.model.AttributeItem;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ItemFormRequestDTO {

    private Long id;

    @NotEmpty
    @Size(max = 100, message = "Name must be less than 100 characters")
    private String name;

    @Min(value = 0, message = "Price must be greater than or equal to 0")
    private Long price;

    @Min(value = 0, message = "Max item day must be greater than or equal to 0")
    private Integer maxItemDay;

    @Size(max = 255, message = "Description must be lees than 255 characters")
    private String description;

    @Min(value = 0, message = "Assembly time must be greater than or equal to 0")
    private Integer assemblyTime;

    private MultipartFile image;

    private Boolean enabled;

    private Boolean outStock;

    private Short type;

    private Boolean miscellaneous;

    private List<Integer> availableDay = new ArrayList<Integer>();

    /** Admin */
    private String userType = "none";
    private String merchant;
    private Integer pin;

    private List<Map> attributeItems = new ArrayList<>();

    public List<Map> getAttributeItems() {
        return attributeItems;
    }

    public void setAttributeItems(List<Map> attributeItems) {
        this.attributeItems = attributeItems;
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

    public MultipartFile getImage() {
        return image;
    }

    public void setImage(MultipartFile image) {
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
    @Override
    public String toString() {
        return "ItemFormRequestDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", maxItemDay=" + maxItemDay +
                ", description='" + description + '\'' +
                ", assemblyTime=" + assemblyTime +
                ", image=" + image +
                ", enabled=" + enabled +
                ", outStock=" + outStock +
                ", type=" + type +
                ", miscellaneous=" + miscellaneous +
                ", availableDay=" + availableDay +
                ", attributeItems=" + attributeItems +
                '}';
    }
}
