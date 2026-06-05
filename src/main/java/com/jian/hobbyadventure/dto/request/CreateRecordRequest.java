package com.jian.hobbyadventure.dto.request;

import com.jian.hobbyadventure.domain.Emotion;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateRecordRequest {

    @NotNull(message = "내 탐험 ID를 입력해주세요.")
    private Long userExplorationId;

    @NotBlank(message = "제목을 입력해주세요.")
    private String title;

    @NotNull(message = "방문 날짜를 입력해주세요.")
    private LocalDate visitedDate;

    @NotNull(message = "별점을 입력해주세요.")
    @Min(value = 1, message = "별점은 1 이상이어야 합니다.")
    @Max(value = 5, message = "별점은 5 이하여야 합니다.")
    private Integer rating;

    @NotNull(message = "감정을 선택해주세요.")
    private Emotion emotionCode;

    private String placeName;

    @NotBlank(message = "내용을 입력해주세요.")
    private String content;
}
