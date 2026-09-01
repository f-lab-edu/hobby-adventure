package com.jian.hobbyadventure.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateWaypointRequest {

    private String memo;
    private String placeName;
    private LocalDateTime checkedAt;

    // 삭제할 기존 사진 id 목록 (선택 사항, 없으면 삭제 없음)
    private List<Long> deleteImageIds;
}
