package com.jian.hobbyadventure.dto.request;

import com.jian.hobbyadventure.common.exception.BusinessException;
import com.jian.hobbyadventure.common.exception.ErrorCode;

public record RecordSearchCondition(
        Long categoryId,
        Long explorationId
) {
    public RecordSearchCondition {
        if (categoryId != null && explorationId != null) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST);
        }
    }

    public boolean hasCategoryFilter() {
        return categoryId != null;
    }

    public boolean hasExplorationFilter() {
        return explorationId != null;
    }
}
