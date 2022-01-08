package com.ecommerceapi.ecomerceapi.services.impl;

import com.ecommerceapi.ecomerceapi.model.Admin;
import com.ecommerceapi.ecomerceapi.model.Merchant;
import com.ecommerceapi.ecomerceapi.repositories.MerchantTokenRepository;
import com.ecommerceapi.ecomerceapi.services.EmailService;
import com.ecommerceapi.ecomerceapi.util.ConstantUtil;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class EmailServiceImplements implements EmailService {

    @Autowired
    MerchantTokenRepository merchantTokenRepository;

    @Autowired
    @Qualifier("emailSender")
    private JavaMailSender emailSender;

    @Autowired
    @Qualifier("emailConfigBean")
    private Configuration emailConfig;

    @Value("${app.merchant.url}")
    private String merchantUrl;

    @Value("${app.admin.url}")
    private String adminUrl;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void sendForgotMessage(Merchant merchant) throws MessagingException, TemplateException, IOException {
        try{
            Map model = new HashMap();
            model.put("customerName", merchant.getStoreName());
            model.put("token", merchant.getToken_update());
            model.put("url", merchantUrl);

            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());

            Template template = emailConfig.getTemplate("forgotpassword.ftl");
            String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);

            mimeMessageHelper.setTo(merchant.getEmail());
            mimeMessageHelper.setText(html, true);
            mimeMessageHelper.setSubject("Reset Password");
            mimeMessageHelper.setFrom(ConstantUtil.NO_REPLY_EMAIL_ADMIN);
            emailSender.send(message);
        }catch(Exception e){
            logger.error(e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void sendForgotMessageAdmin(Admin admin) throws MessagingException, TemplateException, IOException {
        try{
            Map model = new HashMap();
            model.put("customerName", admin.getOfficerName());
            model.put("token", admin.getToken_update());
            model.put("url", adminUrl);

            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());

            Template template = emailConfig.getTemplate("forgotpassword.ftl");
            String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);

            mimeMessageHelper.setTo(admin.getEmail());
            mimeMessageHelper.setText(html, true);
            mimeMessageHelper.setSubject("Reset Password");
            mimeMessageHelper.setFrom(ConstantUtil.NO_REPLY_EMAIL_ADMIN);
            emailSender.send(message);
        }catch(Exception e){
            logger.error(e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void sendRegisMessage(Merchant merchant) throws MessagingException, TemplateException, IOException {
        try{
            Map model = new HashMap();
            model.put("customerName", merchant.getStoreName());

            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());

            Template template = emailConfig.getTemplate("registrationstore.ftl");
            String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);

            mimeMessageHelper.setTo(merchant.getEmail());
            mimeMessageHelper.setText(html, true);
            mimeMessageHelper.setSubject("Account Successfully Created");
            mimeMessageHelper.setFrom(ConstantUtil.NO_REPLY_EMAIL_ADMIN);
            emailSender.send(message);
        }catch(Exception e){
            logger.error(e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void sendRegisMessageAdmin(Admin admin) throws MessagingException, TemplateException, IOException {
        try{
            Map model = new HashMap();
            model.put("customerName", admin.getOfficerName());

            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());

            Template template = emailConfig.getTemplate("registrationadmin.ftl");
            String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);

            mimeMessageHelper.setTo(admin.getEmail());
            mimeMessageHelper.setText(html, true);
            mimeMessageHelper.setSubject("Account Successfully Created");
            mimeMessageHelper.setFrom(ConstantUtil.NO_REPLY_EMAIL_ADMIN);
            emailSender.send(message);
        }catch(Exception e){
            logger.error(e.getMessage(), e);
            throw e;
        }
    }

    /** AUTO GENERATE TOKEN*/
    @Override
    public String generateToken(Merchant merchant) {
        long timestamp = System.currentTimeMillis();
        String token = Jwts.builder().signWith(SignatureAlgorithm.HS256, ConstantUtil.API_SECRET_KEY)
                .setIssuedAt(new Date(timestamp))
                .setExpiration(new Date(timestamp + ConstantUtil.TOKEN_VALIDTY))
                .claim("merchantId", merchant.getId())
                .claim("email", merchant.getEmail())
                .claim("username", merchant.getUsername())
                .claim("phone", merchant.getPhone())
                .claim("type", "merchant").compact();
        return token;
    }

    @Override
    public void sendMessageWithAttachment(String to, String subject, String text, String pathToAttachment) throws MessagingException {
     return;
    }
}
