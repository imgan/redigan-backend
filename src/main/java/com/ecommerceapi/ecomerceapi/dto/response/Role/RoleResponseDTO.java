package com.ecommerceapi.ecomerceapi.dto.response.Role;

import javax.validation.constraints.NotEmpty;

public class RoleResponseDTO {

    @NotEmpty
    private Integer id;

    @NotEmpty
    private String name;

    private Boolean status;

    /** Getter Setter */
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
    /** End Getter Setter */
}
