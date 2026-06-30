package com.jian.hobbyadventure.common.init;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.BucketAlreadyOwnedByYouException;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;

@Component
@RequiredArgsConstructor
public class S3BucketInitializer implements ApplicationRunner {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucket;

    @Value("${aws.s3.create-bucket-on-startup:false}")
    private boolean createBucketOnStartup;

    @Override
    public void run(ApplicationArguments args) {
        if (!createBucketOnStartup) return;
        try {
            s3Client.createBucket(CreateBucketRequest.builder().bucket(bucket).build());
        } catch (BucketAlreadyOwnedByYouException e) {
            // 이미 존재하면 무시
        }
    }
}
