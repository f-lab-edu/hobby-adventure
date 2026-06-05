package com.jian.hobbyadventure.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RecordImage extends BaseEntity {

    private Long id;
    private Long recordId;
    private String imageUrl;
    private int imageOrder;
}
