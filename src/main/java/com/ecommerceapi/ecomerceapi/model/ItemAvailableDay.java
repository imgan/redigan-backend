package com.ecommerceapi.ecomerceapi.model;

import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "item_available_day")
public class ItemAvailableDay {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DAY_SEQ")
    @SequenceGenerator(sequenceName = "_day_id_seq", allocationSize = 1, name = "DAY_SEQ")
    private Long id;

    @Column(name = "item_id", nullable = false)
    private Long itemId;

    @Column(name = "day_index")
    private Integer dayIndex;

    @Column(name = "is_available")
    @ColumnDefault("false")
    private Boolean isAvailable;

    private Date createdAt;

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    /** Getter Setter */


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public Integer getDayIndex() {
        return dayIndex;
    }

    public void setDayIndex(Integer dayIndex) {
        this.dayIndex = dayIndex;
    }

    public Boolean getAvailable() {
        return isAvailable;
    }

    public void setAvailable(Boolean available) {
        isAvailable = available;
    }
    /** End Getter Setter */
}
