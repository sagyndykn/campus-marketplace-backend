package com.campus.marketplace.service.impl;

import com.campus.marketplace.service.MinioService;
import io.minio.*;
import io.minio.http.Method;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MinioServiceImpl implements MinioService {

    private final MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucket;

    @Value("${minio.endpoint}")
    private String endpoint;

    @PostConstruct
    public void initBucket() {
        try {
            boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
            if (!exists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
                // public read policy
                String policy = """
                        {
                          "Version":"2012-10-17",
                          "Statement":[{
                            "Effect":"Allow",
                            "Principal":{"AWS":["*"]},
                            "Action":["s3:GetObject"],
                            "Resource":["arn:aws:s3:::%s/*"]
                          }]
                        }
                        """.formatted(bucket);
                minioClient.setBucketPolicy(SetBucketPolicyArgs.builder()
                        .bucket(bucket)
                        .config(policy)
                        .build());
            }
        } catch (Exception e) {
            throw new RuntimeException("MinIO bucket initialization failed: " + e.getMessage(), e);
        }
    }

    @Override
    public String upload(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new RuntimeException("Только изображения разрешены (JPEG, PNG, WEBP)");
        }

        String ext = getExtension(file.getOriginalFilename());
        String objectName = "photos/" + UUID.randomUUID() + ext;

        try {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(contentType)
                    .build());
        } catch (Exception e) {
            throw new RuntimeException("Ошибка загрузки файла: " + e.getMessage(), e);
        }

        return endpoint + "/" + bucket + "/" + objectName;
    }

    @Override
    public void delete(String url) {
        // extract objectName from full URL: http://localhost:9000/listings/photos/uuid.jpg
        String objectName = url.replace(endpoint + "/" + bucket + "/", "");
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .build());
        } catch (Exception e) {
            throw new RuntimeException("Ошибка удаления файла: " + e.getMessage(), e);
        }
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return ".jpg";
        return filename.substring(filename.lastIndexOf('.'));
    }
}