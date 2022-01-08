package com.ecommerceapi.ecomerceapi.dto.request.Qontak;

import javax.validation.constraints.NotEmpty;
import java.util.HashMap;
import java.util.Map;

public class SendBroadCastMessageDTO {

    @NotEmpty
    private String to_number;

    @NotEmpty
    private String to_name;

    @NotEmpty
    private String message_template_id;

    @NotEmpty
    private String channel_integration_id;

    @NotEmpty
    private HashMap language;

    public void setLanguage(HashMap language) {
        this.language = language;
    }

    @NotEmpty
    private Map parameters;

    public String getTo_number() {
        return to_number;
    }

    public void setTo_number(String to_number) {
        this.to_number = to_number;
    }

    public String getTo_name() {
        return to_name;
    }

    public void setTo_name(String to_name) {
        this.to_name = to_name;
    }

    public String getMessage_template_id() {
        return message_template_id;
    }

    public void setMessage_template_id(String message_template_id) {
        this.message_template_id = message_template_id;
    }

    public String getChannel_integration_id() {
        return channel_integration_id;
    }

    public void setChannel_integration_id(String channel_integration_id) {
        this.channel_integration_id = channel_integration_id;
    }

    public HashMap getLanguage() {
        return language;
    }

    public Map getParameters() {
        return parameters;
    }

    public void setParameters(String s, Map parameters) {
        this.parameters = parameters;
    }

}
