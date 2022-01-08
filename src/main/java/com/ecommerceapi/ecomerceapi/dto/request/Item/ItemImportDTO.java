package com.ecommerceapi.ecomerceapi.dto.request.Item;

import org.springframework.web.multipart.MultipartFile;

public class ItemImportDTO {

    private MultipartFile excelFile;

    public MultipartFile getExcelFile() {
        return excelFile;
    }

    public void setExcelFile(MultipartFile excelFile) {
        this.excelFile = excelFile;
    }
}
