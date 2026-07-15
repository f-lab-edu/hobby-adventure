package com.jian.hobbyadventure.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.cloudfront.CloudFrontUtilities;
import software.amazon.awssdk.services.cloudfront.model.CannedSignerRequest;
import software.amazon.awssdk.services.cloudfront.url.SignedUrl;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final CloudFrontUtilities cloudFrontUtilities;

    @Value("${aws.s3.bucket}")
    private String bucket;

    @Value("${aws.cloudfront.domain}")
    private String cloudFrontDomain;

    @Value("${aws.cloudfront.private-key-path}")
    private String cloudFrontPrivateKeyPath;

    @Value("${aws.cloudfront.key-pair-id}")
    private String cloudFrontKeyPairId;

    public List<String> saveImages(Long recordId, List<MultipartFile> files) {
        List<String> keys = new ArrayList<>();
        for (MultipartFile file : files) {
            String ext = extractExtension(file.getOriginalFilename());
            String key = "records/" + recordId + "/" + UUID.randomUUID() + ext;
            try {
                s3Client.putObject(
                        PutObjectRequest.builder()
                                .bucket(bucket)
                                .key(key)
                                .contentType(file.getContentType())
                                .build(),
                        RequestBody.fromInputStream(file.getInputStream(), file.getSize())
                );
            } catch (IOException e) {
                throw new RuntimeException("S3 업로드 실패: " + key, e);
            }
            keys.add(key);
        }
        return keys;
    }

    public void deleteImages(List<String> keys) {
        if (keys.isEmpty()) return;

        List<ObjectIdentifier> objects = keys.stream()
                .map(key -> ObjectIdentifier.builder().key(key).build())
                .toList();

        s3Client.deleteObjects(DeleteObjectsRequest.builder()
                .bucket(bucket)
                .delete(Delete.builder().objects(objects).build())
                .build());
    }

    public String generatePresignedUrl(String key) {
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(30))
                .getObjectRequest(GetObjectRequest.builder()
                        .bucket(bucket)
                        .key(key)
                        .build())
                .build();

        return s3Presigner.presignGetObject(presignRequest).url().toString();
    }

    public String generatePublicCloudFrontUrl(String key) {
        return "https://" + cloudFrontDomain + "/" + key;
    }

    public String generateSignedCloudFrontUrl(String key) {
        CannedSignerRequest request;
        try {
            request = CannedSignerRequest.builder()
                    .resourceUrl("https://" + cloudFrontDomain + "/" + key)
                    .privateKey(Paths.get(cloudFrontPrivateKeyPath))
                    .keyPairId(cloudFrontKeyPairId)
                    .expirationDate(Instant.now().plus(Duration.ofMinutes(30)))
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("CloudFront 서명 URL 생성 실패: " + key, e);
        }

        SignedUrl signedUrl = cloudFrontUtilities.getSignedUrlWithCannedPolicy(request);
        return signedUrl.url();
    }

    private String extractExtension(String originalFilename) {
        if (originalFilename == null || !originalFilename.contains(".")) return "";
        return originalFilename.substring(originalFilename.lastIndexOf('.'));
    }
}
