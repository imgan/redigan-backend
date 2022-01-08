package com.ecommerceapi.ecomerceapi.dto.request.Merchant;

import org.springframework.web.multipart.MultipartFile;

public class PhotoUpdateDTO {

    private MultipartFile image;

    public MultipartFile getImage() {
        return image;
    }

    public void setImage(MultipartFile image) {
        this.image = image;
    }
}
