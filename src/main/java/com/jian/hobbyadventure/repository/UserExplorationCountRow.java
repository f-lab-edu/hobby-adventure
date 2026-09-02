package com.jian.hobbyadventure.repository;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// user_explorations 테이블 하나만으로 GROUP BY한 결과 — JOIN 없이 explorationId별 개수만 담음(제목은 서비스 레이어에서 배치 조회해 합침)
@Getter
@Setter
@NoArgsConstructor
public class UserExplorationCountRow {

    private Long explorationId;
    private long count;
}
