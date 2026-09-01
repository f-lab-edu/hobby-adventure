package com.jian.hobbyadventure.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateWaypointRequest {

    @NotNull(message = "내 탐험 ID를 입력해주세요.")
    private Long userExplorationId;

    private String memo;
    private String placeName;

    @NotNull(message = "시각을 입력해주세요.")
    private LocalDateTime checkedAt;
}
