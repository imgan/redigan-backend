package com.ecommerceapi.ecomerceapi.dto.request.Merchant;

import javax.validation.constraints.NotEmpty;

public class PresetUpdateDTO {

    private String presetMessage;

    public String getPresetMessage() {
        return presetMessage;
    }

    public void setPresetMessage(String presetMessage) {
        this.presetMessage = presetMessage;
    }
}
