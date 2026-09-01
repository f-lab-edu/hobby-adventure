package com.jian.hobbyadventure.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class WaypointImage extends BaseEntity {

    private Long id;
    private Long waypointId;
    private String imageUrl;
    private int imageOrder;
}
