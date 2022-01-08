package com.ecommerceapi.ecomerceapi.model;

import org.hibernate.annotations.ColumnDefault;
import org.springframework.beans.factory.annotation.Value;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "item")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ITEM_SEQ")
    @SequenceGenerator(sequenceName = "_item_id_seq", allocationSize = 1, name = "ITEM_SEQ")
    private Long id;

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "price", nullable = false)
    @ColumnDefault("0")
    private Long price;

    @Column(name = "max_item", nullable = false)
    @ColumnDefault("0")
    private Integer maxItem;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "assembly_time", nullable = false)
    @ColumnDefault("0")
    private Integer assemblyTime;

    @Column(name = "picture", length = 255)
    private String picture;

    @ManyToOne(targetEntity = Merchant.class, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id", referencedColumnName = "id")
    private Merchant merchant;

    @Column(name = "enabled", nullable = false)
    @ColumnDefault("true")
    private Boolean enabled;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "created_date")
    private Date createdDate;

    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    @Column(name = "updated_date")
    private Date updatedDate;

    @Column(name = "out_stock", nullable = false)
    @ColumnDefault("true")
    private Boolean outStock;

    @Column(name = "type_order", nullable = false)
    @ColumnDefault("1")
    private Short type;

    @Column(name = "is_miscellaneous")
    @ColumnDefault("false")
    private Boolean isMiscellaneous = false;

    /** Join Relation */
    @OneToMany(targetEntity = ItemAvailableDay.class, cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "item_id", referencedColumnName = "id")
    private List<ItemAvailableDay> itemDayList = new ArrayList<>();

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

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public Merchant getMerchant() {
        return merchant;
    }

    public void setMerchant(Merchant merchant) {
        this.merchant = merchant;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
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
        return isMiscellaneous;
    }

    public void setMiscellaneous(Boolean miscellaneous) {
        isMiscellaneous = miscellaneous;
    }

    public List<ItemAvailableDay> getItemDayList() {
        return itemDayList;
    }

    public void setItemDayList(List<ItemAvailableDay> itemDayList) {
        this.itemDayList = itemDayList;
    }
    /** End Getter Setter */

    /** Custom Mapping Data */
    public List<Integer> getDayIndexList() {
        List<Integer> dayData = new ArrayList<>();
        for (ItemAvailableDay Day : this.getItemDayList()) {
            if (Day.getAvailable()) dayData.add(Day.getDayIndex());
        }
        return dayData;
    }
}
