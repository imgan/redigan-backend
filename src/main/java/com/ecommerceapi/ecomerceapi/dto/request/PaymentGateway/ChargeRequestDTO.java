package com.ecommerceapi.ecomerceapi.dto.request.PaymentGateway;

import javax.validation.constraints.NotEmpty;

public class ChargeRequestDTO {

    @NotEmpty
    private String transaction_id;

    @NotEmpty
    private Integer amount;

    @NotEmpty
    private Long merchant_id;

    public String getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Long getMerchant_id() {
        return merchant_id;
    }

    public void setMerchant_id(Long merchant_id) {
        this.merchant_id = merchant_id;
    }
}
