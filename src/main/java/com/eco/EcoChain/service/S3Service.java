package com.eco.EcoChain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
public class S3Service {
    @Value("${cloud.aws.bucket.name}")
    private String bucketName;

    @Value("${cloud.aws.credentials.access-key}")
    private String accessKeyId;

    @Value("${cloud.aws.credentials.secret-key}")
    private String secretAccessKey;

    public String uploadFile(MultipartFile file) {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKeyId, secretAccessKey);
        S3Client s3 = S3Client.builder()
                .region(Region.EU_NORTH_1)
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();

        String key = UUID.randomUUID() + "_" + file.getOriginalFilename();

        try {
            PutObjectRequest putOb = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            s3.putObject(putOb, RequestBody.fromBytes(file.getBytes()));

            return "https://" + bucketName + ".s3." + Region.EU_NORTH_1 + ".amazonaws.com/" + key;

        } catch (IOException e) {
            throw new RuntimeException("Ошибка загрузки файла на S3", e);
        }
    }
}

