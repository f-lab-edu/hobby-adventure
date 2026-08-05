package com.jian.hobbyadventure.scheduler;

import com.jian.hobbyadventure.dto.message.ViewEventMessage;
import com.jian.hobbyadventure.repository.ExplorationMapper;
import com.jian.hobbyadventure.repository.ProcessedViewEventMapper;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.json.JsonMapper;

@Component
@RequiredArgsConstructor
public class ExplorationViewIngestionWorker {

    private final ExplorationMapper explorationMapper;
    private final ProcessedViewEventMapper processedViewEventMapper;
    private final JsonMapper jsonMapper;

    @SqsListener(queueNames = "${aws.sqs.view-event-queue-url}", maxConcurrentMessages = "1", maxMessagesPerPoll = "1")
    @Transactional
    public void receive(String messageBody) {
        ViewEventMessage event = jsonMapper.readValue(messageBody, ViewEventMessage.class);

        if (processedViewEventMapper.insertIfAbsent(event.viewEventId()) > 0) {
            explorationMapper.incrementViewCount(event.explorationId(), 1L);
        }
    }
}
