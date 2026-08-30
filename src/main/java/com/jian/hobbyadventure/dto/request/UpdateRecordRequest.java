package com.jian.hobbyadventure.dto.request;

import com.jian.hobbyadventure.domain.Emotion;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRecordRequest {

    private String title;
    private LocalDate visitedDate;

    @Min(value = 1, message = "별점은 1 이상이어야 합니다.")
    @Max(value = 5, message = "별점은 5 이하여야 합니다.")
    private Integer rating;

    private Emotion emotionCode;
    private String placeName;
    private String content;

    // 삭제할 기존 사진 id 목록 (선택 사항, 없으면 삭제 없음)
    private List<Long> deleteImageIds;
}
