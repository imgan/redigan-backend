package com.ecommerceapi.ecomerceapi.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "order_attribute_detail")
public class OrderAttributeDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ORDER_ATTRIBUTE_DETAIL_SEQ")
    @SequenceGenerator(sequenceName = "_order_attribute_detail_id_seq", allocationSize = 1, name = "ORDER_ATTRIBUTE_DETAIL_SEQ")
    private Long id;

    @Column(name = "order_number", nullable = false)
    private String  orderNumber;

    @ManyToOne(targetEntity = Item.class, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", referencedColumnName = "id")
    private Item item;

    @ManyToOne(targetEntity = AttributeItem.class, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "attribute_id", referencedColumnName = "id")
    private AttributeItem attributeItem;

    @Column(name = "additional_info" )
    private String additionalInfo;

    @Column(name = "sequence" )
    private Integer sequence;

    @Column(name="created_date")
    private Date createdDate;

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
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

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public AttributeItem getAttributeItem() {
        return attributeItem;
    }

    public void setAttributeItem(AttributeItem attributeItem) {
        this.attributeItem = attributeItem;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
}
