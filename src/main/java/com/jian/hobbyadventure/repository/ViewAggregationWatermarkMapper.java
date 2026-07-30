package com.jian.hobbyadventure.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ViewAggregationWatermarkMapper {

    @Select("SELECT last_processed_id FROM view_aggregation_watermark WHERE id = 1 FOR UPDATE")
    Long findLastProcessedIdForUpdate();

    @Update("UPDATE view_aggregation_watermark SET last_processed_id = #{lastProcessedId} WHERE id = 1")
    void updateLastProcessedId(@Param("lastProcessedId") Long lastProcessedId);
}
