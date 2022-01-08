package com.ecommerceapi.ecomerceapi.services;

import com.ecommerceapi.ecomerceapi.model.Admin;
import com.ecommerceapi.ecomerceapi.model.Merchant;
import freemarker.template.TemplateException;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.IOException;

@Service
public interface EmailService {

    public void sendForgotMessage(Merchant merchant) throws MessagingException, TemplateException, IOException;
    public void sendForgotMessageAdmin(Admin admin) throws MessagingException, TemplateException, IOException;
    public void sendRegisMessage(Merchant merchant) throws MessagingException, TemplateException, IOException;
    public void sendRegisMessageAdmin(Admin admin) throws MessagingException, TemplateException, IOException;

    String generateToken(Merchant merchant);

    void sendMessageWithAttachment(
            String to, String subject, String text, String pathToAttachment) throws MessagingException;
}
