package com.jian.hobbyadventure.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExplorationViewEventPublisher {

    private final SqsAsyncClient sqsAsyncClient;
    private final ObjectMapper objectMapper;

    @Value("${aws.sqs.view-event-queue-url}")
    private String queueUrl;

    public void publish(Long explorationId) {
        ViewEventMessage message = new ViewEventMessage(
                UUID.randomUUID().toString(),
                explorationId,
                LocalDateTime.now()
        );

        String body;
        try {
            body = objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            log.error("조회 이벤트 메시지 직렬화 실패, explorationId={}", explorationId, e);
            return;
        }

        sqsAsyncClient.sendMessage(SendMessageRequest.builder()
                        .queueUrl(queueUrl)
                        .messageBody(body)
                        .build())
                .exceptionally(ex -> {
                    log.error("조회 이벤트 발행 실패, explorationId={}", explorationId, ex);
                    return null;
                });
    }

    private record ViewEventMessage(String viewEventId, Long explorationId, LocalDateTime viewedAt) {
    }
}
