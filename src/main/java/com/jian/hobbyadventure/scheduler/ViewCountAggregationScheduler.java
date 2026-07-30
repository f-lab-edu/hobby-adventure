package com.jian.hobbyadventure.scheduler;

import com.jian.hobbyadventure.repository.ExplorationMapper;
import com.jian.hobbyadventure.repository.ExplorationViewMapper;
import com.jian.hobbyadventure.repository.ExplorationViewMapper.ExplorationViewCount;
import com.jian.hobbyadventure.repository.ViewAggregationWatermarkMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ViewCountAggregationScheduler {

    private static final long FIXED_DELAY_MILLIS = 10 * 60 * 1000;

    private final ViewAggregationWatermarkMapper watermarkMapper;
    private final ExplorationViewMapper explorationViewMapper;
    private final ExplorationMapper explorationMapper;

    @Scheduled(fixedDelay = FIXED_DELAY_MILLIS)
    @Transactional
    public void aggregate() {
        Long watermark = watermarkMapper.findLastProcessedIdForUpdate();
        Long maxId = explorationViewMapper.findMaxId();

        if (maxId == null || maxId <= watermark) {
            return;
        }

        List<ExplorationViewCount> counts = explorationViewMapper.countByExplorationIdBetween(watermark, maxId);
        for (ExplorationViewCount count : counts) {
            explorationMapper.incrementViewCount(count.explorationId(), count.count());
        }

        watermarkMapper.updateLastProcessedId(maxId);
    }
}
