package com.ecommerceapi.ecomerceapi.dto.response.PaymentGateway;

import javax.validation.constraints.NotEmpty;

public class ChargeResponseDTO {

    @NotEmpty
    private String transaction_id;

    @NotEmpty
    private String amount;

    @NotEmpty
    private String unique_amount;

    @NotEmpty
    private String merchant_id;

    @NotEmpty
    private String expired_at;

    public String getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getUnique_amount() {
        return unique_amount;
    }

    public void setUnique_amount(String unique_amount) {
        this.unique_amount = unique_amount;
    }

    public String getMerchant_id() {
        return merchant_id;
    }

    public void setMerchant_id(String merchant_id) {
        this.merchant_id = merchant_id;
    }

    public String getExpired_at() {
        return expired_at;
    }

    public void setExpired_at(String expired_at) {
        this.expired_at = expired_at;
    }

    @Override
    public String toString() {
        return "ChargeResponseDTO{" +
                "transaction_id='" + transaction_id + '\'' +
                ", amount='" + amount + '\'' +
                ", unique_amount='" + unique_amount + '\'' +
                ", merchant_id='" + merchant_id + '\'' +
                ", expired_at='" + expired_at + '\'' +
                '}';
    }
}
