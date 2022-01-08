package com.ecommerceapi.ecomerceapi.services.impl;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.ecommerceapi.ecomerceapi.dto.params.FileNameDTO;
import com.ecommerceapi.ecomerceapi.exception.ResultServiceException;
import com.ecommerceapi.ecomerceapi.services.AmazonS3Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Component
public class AmazonS3ServiceImplements extends BaseServices implements AmazonS3Service {

    private AmazonS3 s3client;

    @Value("${amazonProperties.endpointUrl}")
    private String endpointUrl;

    @Value("${amazonProperties.bucketName}")
    private String bucketName;

    @Value("${amazonProperties.accessKey}")
    private String accessKey;

    @Value("${amazonProperties.secretKey}")
    private String secretKey;

    @PostConstruct
    private void initializeAmazon() {
        AWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);
        this.s3client = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.AP_SOUTHEAST_1).build();
    }

    public String uploadFile(FileNameDTO fileNameDTO) {
        String fileUrl = "";
        try {
            File file = convertMultiPartToFile(fileNameDTO.getImage());
            String fileName = generateFileName(fileNameDTO);
            fileUrl = endpointUrl + "/" + bucketName + "/" + fileName;
            uploadFileTos3bucket(fileName, file);
            file.delete();
        } catch (Exception e) {
            logger.error("[FATAL]" + e);
            throw new ResultServiceException(e.getMessage());
        }
        return fileUrl;
    }

    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }

    private String generateFileName(FileNameDTO fileNameDTO) {
        MultipartFile multiPart = fileNameDTO.getImage();
        String typeFile = multiPart.getOriginalFilename().substring(multiPart.getOriginalFilename().lastIndexOf(".") + 1);
        LocalDate localDate = LocalDate.now(ZoneId.of("GMT+07:00"));
        String formatLocalDate = localDate.format(DateTimeFormatter.ofPattern("dd_MM_yyyy"));
        Instant instant = Instant.now();
        long timeStampMillis = instant.toEpochMilli();
        return fileNameDTO.getFileModel() + "_" + fileNameDTO.getId().toString() + "_" + formatLocalDate + "_" + timeStampMillis + "." + typeFile;
    }

    private void uploadFileTos3bucket(String fileName, File file) {
        s3client.putObject(new PutObjectRequest(bucketName, fileName, file)
                .withCannedAcl(CannedAccessControlList.PublicRead));
    }

    public String deleteFileFromS3Bucket(String fileUrl) {
        try {
            String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
            s3client.deleteObject(new DeleteObjectRequest(bucketName, fileName));
        } catch (Exception e) {
            logger.error("[FATAL]" + e);
            return "File not found";
        }
        return "Successfully deleted";
    }

}
