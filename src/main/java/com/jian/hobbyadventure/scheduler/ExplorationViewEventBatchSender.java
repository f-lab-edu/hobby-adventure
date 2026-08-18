package com.jian.hobbyadventure.scheduler;

import com.jian.hobbyadventure.dto.message.ViewEventMessage;
import com.jian.hobbyadventure.service.ExplorationViewEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageBatchRequestEntry;
import tools.jackson.databind.json.JsonMapper;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExplorationViewEventBatchSender {

    private static final int MAX_BATCH_SIZE = 10;

    private final ExplorationViewEventPublisher eventPublisher;
    private final SqsClient sqsClient;
    private final JsonMapper jsonMapper;

    @Value("${aws.sqs.view-event-queue-url}")
    private String queueUrl;

    @Scheduled(fixedDelay = 50)
    public void flush() {
        List<ViewEventMessage> batch = eventPublisher.drain(MAX_BATCH_SIZE);
        if (batch.isEmpty()) {
            return;
        }

        try {
            List<SendMessageBatchRequestEntry> entries = new ArrayList<>(batch.size());
            for (int i = 0; i < batch.size(); i++) {
                entries.add(SendMessageBatchRequestEntry.builder()
                        .id(String.valueOf(i))
                        .messageBody(jsonMapper.writeValueAsString(batch.get(i)))
                        .build());
            }

            sqsClient.sendMessageBatch(builder -> builder.queueUrl(queueUrl).entries(entries));
        } catch (Exception e) {
            log.error("조회 이벤트 배치 발행 실패, size={}", batch.size(), e);
        }
    }
}
