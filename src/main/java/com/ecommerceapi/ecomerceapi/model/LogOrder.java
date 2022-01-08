package com.ecommerceapi.ecomerceapi.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "log_order")
public class LogOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "LOG_ORDER_SEQ")
    @SequenceGenerator(sequenceName = "_log_order_id_seq", allocationSize = 1, name = "LOG_ORDER_SEQ")
    private Long id;

    @Column(name = "data", columnDefinition = "text")
    private String data;

    @Column(name = "created_date")
    private Date createdDate;

    @Column(name = "status")
    private Boolean status;

    @Column(name = "order_number")
    private String orderNumber;

    public Long getId() {
        return id;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

}
