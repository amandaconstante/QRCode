package com.application.qrcode.generator.infrastructure;

import com.application.qrcode.generator.ports.StoragePort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.ByteArrayInputStream;
@Component // essa anotação garante que quando usar StoragePort no service ele encontre essa implementação da interface na injeção de dependencias
public class S3StorageAdapter implements StoragePort {
    private final S3Client s3Client;
    private final String bucketName;
    private final String region;

    public S3StorageAdapter(@Value("${aws.region}") String region,
                            @Value("${aws.s3.bucket-name}") String bucketName) {
        this.region = region;
        this.bucketName = bucketName;
        this.s3Client = S3Client.builder().region(Region.of(this.region)).build();
    }

    @Override
    public String uploadFile(byte[] fileData, String fileName, String contentType) {
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .contentType(contentType)
                .build();
        s3Client.putObject(request, RequestBody.fromBytes(fileData));

        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, fileName);
    }
}
