package com.jian.hobbyadventure.scheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jian.hobbyadventure.domain.ExplorationView;
import com.jian.hobbyadventure.dto.message.ViewEventMessage;
import com.jian.hobbyadventure.repository.ExplorationViewMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageBatchRequestEntry;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExplorationViewIngestionWorker {

    private static final int MAX_NUMBER_OF_MESSAGES = 10;
    private static final int WAIT_TIME_SECONDS = 20;

    private final SqsClient sqsClient;
    private final ExplorationViewMapper explorationViewMapper;
    private final ObjectMapper objectMapper;

    @Value("${aws.sqs.view-event-queue-url}")
    private String queueUrl;

    @Scheduled(fixedDelay = 1000)
    public void ingest() {
        ReceiveMessageResponse response = sqsClient.receiveMessage(ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .maxNumberOfMessages(MAX_NUMBER_OF_MESSAGES)
                .waitTimeSeconds(WAIT_TIME_SECONDS)
                .build());

        List<Message> messages = response.messages();
        if (messages.isEmpty()) {
            return;
        }

        List<ExplorationView> views = new ArrayList<>();
        List<Message> parsedMessages = new ArrayList<>();

        for (Message message : messages) {
            try {
                ViewEventMessage event = objectMapper.readValue(message.body(), ViewEventMessage.class);
                views.add(new ExplorationView(event.viewEventId(), event.explorationId(), event.viewedAt()));
                parsedMessages.add(message);
            } catch (Exception e) {
                log.error("조회 이벤트 메시지 파싱 실패, messageId={}", message.messageId(), e);
            }
        }

        if (views.isEmpty()) {
            return;
        }

        try {
            explorationViewMapper.insertIgnoreBatch(views);
        } catch (Exception e) {
            log.error("조회 이벤트 배치 삽입 실패", e);
            return;
        }

        deleteMessages(parsedMessages);
    }

    private void deleteMessages(List<Message> messages) {
        List<DeleteMessageBatchRequestEntry> entries = messages.stream()
                .map(message -> DeleteMessageBatchRequestEntry.builder()
                        .id(message.messageId())
                        .receiptHandle(message.receiptHandle())
                        .build())
                .toList();

        sqsClient.deleteMessageBatch(builder -> builder.queueUrl(queueUrl).entries(entries));
    }
}
