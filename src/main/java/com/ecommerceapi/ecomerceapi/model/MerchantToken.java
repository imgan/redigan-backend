package com.ecommerceapi.ecomerceapi.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "merchant_token")
public class MerchantToken {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TOKEN_SEQ")
    @SequenceGenerator(sequenceName = "_token_id_seq", allocationSize = 1, name = "TOKEN_SEQ")
    @Column(name = "id")
    private Long id;

    @Column(name="token")
    private String token;

    @Column(name = "is_use")
    private Boolean isUse;

    @Column(name = "createdAt")
    private Date createdAt;

    public Long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }

    @Column(name = "merchant_id")
    private Long merchantId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Boolean getUse() {
        return isUse;
    }

    public void setUse(Boolean use) {
        isUse = use;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
