package com.jian.hobbyadventure.repository;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// records 테이블 하나만으로 GROUP BY한 결과 — JOIN 없이 월별 개수만 담음
@Getter
@Setter
@NoArgsConstructor
public class RecordMonthCountRow {

    private String month;
    private long count;
}
