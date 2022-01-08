package com.ecommerceapi.ecomerceapi.services.impl;

import com.ecommerceapi.ecomerceapi.controllers.BaseController;
import com.ecommerceapi.ecomerceapi.model.Merchant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class BaseServices extends BaseController {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    public Boolean checkPin(Integer pin , Integer pinConfirm){
        Boolean status;
        if(pinConfirm != null){
            if( pinConfirm.equals(pin)){
                logger.info("pin found is same with pin input");
                status = true;
            } else {
                logger.info("wrong pin number");
                status = false;
            }
        } else {
            if(pinConfirm == null && pin == null){
                status = true;
            } else if (pinConfirm == null && pin != null){
               status = false;
            } else {
                if(pinConfirm.equals(pin)){
                    status = true;
                } else {
                    status = false;
                }
            }
        }
        return status;
    }

    protected boolean isExistingDataAndStringValue(Object data) {
        if (isExistingData(data)) {
            return (data instanceof String);
        }
        return false;
    }

    protected boolean isExistingDataAndMapValue(Object data) {
        if (isExistingData(data)) {
            if (data instanceof Map) {
                return !((Map) data).isEmpty();
            }
        }
        return false;
    }
}
