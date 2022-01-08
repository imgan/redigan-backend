package com.ecommerceapi.ecomerceapi.dto.request.Role;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public class RoleFormRequestDTO {

    private Integer id;

    @NotEmpty
    @Size(max = 50, message = "Name must be less than 50 characters")
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
