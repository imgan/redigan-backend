package com.ecommerceapi.ecomerceapi.dto.params;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

public class FileNameDTO {

    private Long id = 0L;

    private String fileModel = "-";

    private MultipartFile image;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileModel() {
        return fileModel;
    }

    public void setFileModel(String fileModel) {
        this.fileModel = fileModel;
    }

    public MultipartFile getImage() {
        return image;
    }

    public void setImage(MultipartFile image) {
        this.image = image;
    }
}
