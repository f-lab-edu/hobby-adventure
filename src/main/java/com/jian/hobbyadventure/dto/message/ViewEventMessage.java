package com.jian.hobbyadventure.dto.message;

import java.time.LocalDateTime;

public record ViewEventMessage(String viewEventId, Long explorationId, LocalDateTime viewedAt) {
}
