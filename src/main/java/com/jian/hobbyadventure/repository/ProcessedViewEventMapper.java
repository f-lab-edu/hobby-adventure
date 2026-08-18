package com.jian.hobbyadventure.repository;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ProcessedViewEventMapper {

    @Insert("INSERT IGNORE INTO processed_view_events (view_event_id) VALUES (#{viewEventId})")
    int insertIfAbsent(@Param("viewEventId") String viewEventId);
}
