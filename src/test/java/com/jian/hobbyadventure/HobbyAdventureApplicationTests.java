package com.jian.hobbyadventure;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
		"aws.s3.endpoint=http://localhost:4566",
		"aws.s3.presign-endpoint=http://localhost:4566",
		"aws.s3.bucket=test-bucket",
		"aws.s3.region=ap-northeast-2",
		"aws.s3.access-key=test",
		"aws.s3.secret-key=test",
		"aws.sqs.endpoint=http://localhost:4566",
		"aws.sqs.region=ap-northeast-2",
		"aws.sqs.access-key=test",
		"aws.sqs.secret-key=test",
		"aws.sqs.view-event-queue-url=http://localhost:4566/000000000000/test-queue"
})
class HobbyAdventureApplicationTests {

	@Test
	void contextLoads() {
	}

}
