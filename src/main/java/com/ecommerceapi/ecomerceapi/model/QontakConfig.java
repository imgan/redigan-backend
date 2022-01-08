package com.ecommerceapi.ecomerceapi.model;

import javax.persistence.*;

@Entity
@Table(name = "qontak_config")
public class QontakConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "QONTAK_SEQ")
    @SequenceGenerator(sequenceName = "_qontak_id_seq", allocationSize = 1, name = "QONTAK_SEQ")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "category", nullable = false)
    private String category;

    @Column(name = "message_template_id", nullable = false)
    private String messageTemplateId;

    @Column(name = "channel_integration_id", nullable = false)
    private String channelIntegrationId;

    @Column(name = "token", nullable = false)
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getMessageTemplateId() {
        return messageTemplateId;
    }

    public void setMessageTemplateId(String messageTemplateId) {
        this.messageTemplateId = messageTemplateId;
    }

    public String getChannelIntegrationId() {
        return channelIntegrationId;
    }

    public void setChannelIntegrationId(String channelIntegrationId) {
        this.channelIntegrationId = channelIntegrationId;
    }
}
