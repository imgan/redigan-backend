package com.ecommerceapi.ecomerceapi.dto.request.Merchant;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

public class UpdatePinDTO {

    private Integer oldPin;

    private Integer pin;

    private Integer pinConfirm;

    public Integer getOldPin() {
        return oldPin;
    }

    public void setOldPin(Integer oldPin) {
        this.oldPin = oldPin;
    }

    public Integer getPin() {
        return pin;
    }

    public void setPin(Integer pin) {
        this.pin = pin;
    }

    public Integer getPinConfirm() {
        return pinConfirm;
    }

    public void setPinConfirm(Integer pinConfirm) {
        this.pinConfirm = pinConfirm;
    }
}
