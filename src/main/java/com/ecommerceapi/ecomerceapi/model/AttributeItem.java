package com.ecommerceapi.ecomerceapi.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "attribute_item")
public class AttributeItem {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ATTRIBUTE_SEQ")
    @SequenceGenerator(sequenceName = "_attribute_id_seq", allocationSize = 1, name = "ATTRIBUTE_SEQ")
    private Long id;

    @ManyToOne(targetEntity = Item.class, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", referencedColumnName = "id")
    private Item item;

    @Column(name = "price", nullable = false)
    private Long Price;

    @Column(name = "name", nullable = false)
    private String Name;

    @Column(name = "stock", nullable = false)
    private Long Stock;

    private Date createdAt;

    private Date updateAt;

    private Boolean enabled;

    public Date getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(Date updateAt) {
        this.updateAt = updateAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Long getPrice() {
        return Price;
    }

    public void setPrice(Long price) {
        Price = price;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public Long getStock() {
        return Stock;
    }

    public void setStock(Long stock) {
        Stock = stock;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
