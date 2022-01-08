package com.ecommerceapi.ecomerceapi.services;

import com.ecommerceapi.ecomerceapi.dto.params.FileNameDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface AmazonS3Service {
    String uploadFile(FileNameDTO fileNameDTO);

    String deleteFileFromS3Bucket(String fileUrl);
}
