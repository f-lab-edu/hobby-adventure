package com.jian.hobbyadventure.service;

import com.jian.hobbyadventure.dto.message.ViewEventMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@Slf4j
@Component
public class ExplorationViewEventPublisher {

    private static final int QUEUE_CAPACITY = 2000;

    private final BlockingQueue<ViewEventMessage> pendingEvents = new ArrayBlockingQueue<>(QUEUE_CAPACITY);

    public void publish(Long explorationId) {
        ViewEventMessage message = new ViewEventMessage(
                UUID.randomUUID().toString(),
                explorationId,
                LocalDateTime.now()
        );

        if (!pendingEvents.offer(message)) {
            log.warn("조회 이벤트 큐가 가득 차서 유실됨, explorationId={}", explorationId);
        }
    }

    public List<ViewEventMessage> drain(int maxElements) {
        List<ViewEventMessage> drained = new ArrayList<>(maxElements);
        pendingEvents.drainTo(drained, maxElements);
        return drained;
    }
}
