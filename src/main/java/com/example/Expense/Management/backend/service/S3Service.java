package com.example.Expense.Management.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.core.sync.RequestBody;

import java.io.IOException;
import java.net.URL;

@Service
public class S3Service {

    private final S3Client s3Client;
    private final String bucketName;

    public S3Service(@Value("${aws.s3.access-key-id}") String accessKeyId,
                     @Value("${aws.s3.secret-access-key}") String secretAccessKey,
                     @Value("${aws.s3.region}") String region,
                     @Value("${aws.s3.bucket-name}") String bucketName) {
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessKeyId, secretAccessKey);
        this.s3Client = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .build();
        this.bucketName = bucketName;
    }

    public String uploadFile(String key, byte[] fileBytes, String contentType) throws IOException {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(contentType)
                .build();

        PutObjectResponse response = s3Client.putObject(putObjectRequest, RequestBody.fromBytes(fileBytes));

        // Generate the public URL
        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, s3Client.serviceClientConfiguration().region().id(), key);
    }
}
