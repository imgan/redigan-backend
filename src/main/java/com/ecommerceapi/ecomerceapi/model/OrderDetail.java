package com.ecommerceapi.ecomerceapi.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "order_detail")
public class OrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ORDER_DETAIL_SEQ")
    @SequenceGenerator(sequenceName = "_order_detail_id_seq", allocationSize = 1, name = "ORDER_DETAIL_SEQ")
    private Long id;

    @Column(name = "order_number", nullable = false)
    private String  orderNumber;

    @Column(name = "item_id", nullable = false)
    private Long itemId;

    @Column(name = "qty" , nullable = false)
    private Integer qty;

    @Column(name = "additional_info" )
    private String additionalInfo;

    @Column(name="created_date")
    private Date createdDate;

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
}
