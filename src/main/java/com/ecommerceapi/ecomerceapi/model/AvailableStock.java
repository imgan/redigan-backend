package com.ecommerceapi.ecomerceapi.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "available_stock")
public class AvailableStock {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "AVAILABLE_SEQ")
    @SequenceGenerator(sequenceName = "_available_id_seq", allocationSize = 1, name = "AVAILABLE_SEQ")
    private Long id;

    @Column(name = "date", nullable = false)
    private Date date;

    @Column(name = "qty", nullable = false)
    private Integer stockQty;

    @Column(name = "order_number", nullable = false)
    private String orderNumber;

    @OneToOne(targetEntity = Item.class, cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "item_id", referencedColumnName = "id")
    private Item item;

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getStockQty() {
        return stockQty;
    }

    public void setStockQty(Integer stockQty) {
        this.stockQty = stockQty;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }
}
