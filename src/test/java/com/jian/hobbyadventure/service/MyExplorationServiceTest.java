package com.jian.hobbyadventure.service;

import com.jian.hobbyadventure.common.exception.BusinessException;
import com.jian.hobbyadventure.common.exception.ErrorCode;
import com.jian.hobbyadventure.common.response.PageResponse;
import com.jian.hobbyadventure.domain.Category;
import com.jian.hobbyadventure.domain.Exploration;
import com.jian.hobbyadventure.domain.ExplorationStatus;
import com.jian.hobbyadventure.domain.UserExploration;
import com.jian.hobbyadventure.dto.response.CompleteExplorationResponse;
import com.jian.hobbyadventure.dto.response.MyExplorationDetailResponse;
import com.jian.hobbyadventure.dto.response.MyExplorationListItemResponse;
import com.jian.hobbyadventure.repository.CategoryMapper;
import com.jian.hobbyadventure.repository.ExplorationMapper;
import com.jian.hobbyadventure.repository.RecordMapper;
import com.jian.hobbyadventure.repository.UserExplorationMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MyExplorationServiceTest {

    @Mock
    private UserExplorationMapper userExplorationMapper;
    @Mock
    private ExplorationMapper explorationMapper;
    @Mock
    private CategoryMapper categoryMapper;
    @Mock
    private RecordMapper recordMapper;

    @InjectMocks
    private MyExplorationService myExplorationService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(myExplorationService, "imageBaseUrl", "/images/");
    }

    private UserExploration createUserExploration(Long id, Long userId, Long explorationId, ExplorationStatus status) {
        UserExploration ue = new UserExploration();
        ue.setId(id);
        ue.setUserId(userId);
        ue.setExplorationId(explorationId);
        ue.setStatus(status);
        ue.setCreatedAt(LocalDateTime.now());
        if (status == ExplorationStatus.COMPLETED) {
            ue.setCompletedAt(LocalDateTime.now());
        }
        return ue;
    }

    private Exploration createExploration(Long id, Long categoryId) {
        Exploration e = new Exploration();
        e.setId(id);
        e.setCategoryId(categoryId);
        e.setTitle("탐험 제목");
        e.setThumbnailUrl("explorations/test.jpg");
        e.setShortDescription("짧은 설명");
        return e;
    }

    private Category createCategory(Long id, String name) {
        Category c = new Category();
        c.setCategoryId(id);
        c.setName(name);
        return c;
    }

    @Test
    void getMyExplorations_categoryId가_null이면_전체_목록을_반환한다() {
        UserExploration ue = createUserExploration(1L, 1L, 10L, ExplorationStatus.STARTED);
        Exploration e = createExploration(10L, 1L);
        when(userExplorationMapper.findAllByCondition(anyLong(), any(), any(), anyInt(), anyInt())).thenReturn(List.of(ue));
        when(userExplorationMapper.countByCondition(anyLong(), any(), any())).thenReturn(1L);
        when(explorationMapper.findByIdIn(any())).thenReturn(List.of(e));
        when(categoryMapper.findAll()).thenReturn(List.of(createCategory(1L, "운동")));
        when(recordMapper.findUserExplorationIdsByUserExplorationIdIn(any())).thenReturn(List.of());

        PageResponse<MyExplorationListItemResponse> result = myExplorationService.getMyExplorations(1L, ExplorationStatus.STARTED, null, 1, 10);

        assertThat(result.getData()).hasSize(1);
        assertThat(result.getData().get(0).getCategoryName()).isEqualTo("운동");
    }

    @Test
    void getMyExplorations_categoryId가_있으면_해당_카테고리만_반환한다() {
        UserExploration ue = createUserExploration(1L, 1L, 10L, ExplorationStatus.STARTED);
        Exploration e = createExploration(10L, 1L);
        when(explorationMapper.findIdsByCategoryId(1L)).thenReturn(List.of(10L));
        when(userExplorationMapper.findAllByCondition(anyLong(), any(), any(), anyInt(), anyInt())).thenReturn(List.of(ue));
        when(userExplorationMapper.countByCondition(anyLong(), any(), any())).thenReturn(1L);
        when(explorationMapper.findByIdIn(any())).thenReturn(List.of(e));
        when(categoryMapper.findAll()).thenReturn(List.of(createCategory(1L, "운동")));
        when(recordMapper.findUserExplorationIdsByUserExplorationIdIn(any())).thenReturn(List.of());

        PageResponse<MyExplorationListItemResponse> result = myExplorationService.getMyExplorations(1L, ExplorationStatus.STARTED, 1L, 1, 10);

        assertThat(result.getData()).hasSize(1);
    }

    @Test
    void getMyExploration_성공_시_단건을_반환한다() {
        UserExploration ue = createUserExploration(1L, 1L, 10L, ExplorationStatus.STARTED);
        Exploration e = createExploration(10L, 1L);
        Category c = createCategory(1L, "운동");
        when(userExplorationMapper.findById(1L)).thenReturn(Optional.of(ue));
        when(explorationMapper.findById(10L)).thenReturn(Optional.of(e));
        when(categoryMapper.findById(1L)).thenReturn(c);
        when(recordMapper.findByUserExplorationId(1L)).thenReturn(Optional.empty());

        MyExplorationDetailResponse result = myExplorationService.getMyExploration(1L, 1L);

        assertThat(result.getUserExplorationId()).isEqualTo(1L);
        assertThat(result.getCategoryName()).isEqualTo("운동");
    }

    @Test
    void getMyExploration_존재하지_않는_id_시_NOT_FOUND를_던진다() {
        when(userExplorationMapper.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> myExplorationService.getMyExploration(1L, 1L))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.NOT_FOUND);
    }

    @Test
    void getMyExploration_다른_사용자의_탐험_접근_시_FORBIDDEN을_던진다() {
        UserExploration ue = createUserExploration(1L, 2L, 10L, ExplorationStatus.STARTED);
        when(userExplorationMapper.findById(1L)).thenReturn(Optional.of(ue));

        assertThatThrownBy(() -> myExplorationService.getMyExploration(1L, 1L))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.FORBIDDEN);
    }

    @Test
    void completeExploration_성공_시_complete가_호출된다() {
        UserExploration ue = createUserExploration(1L, 1L, 10L, ExplorationStatus.STARTED);
        when(userExplorationMapper.findById(1L)).thenReturn(Optional.of(ue));

        CompleteExplorationResponse result = myExplorationService.completeExploration(1L, 1L);

        verify(userExplorationMapper).complete(1L);
        assertThat(result.getUserExplorationId()).isEqualTo(1L);
    }

    @Test
    void completeExploration_존재하지_않는_id_시_NOT_FOUND를_던진다() {
        when(userExplorationMapper.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> myExplorationService.completeExploration(1L, 1L))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.NOT_FOUND);
    }

    @Test
    void completeExploration_다른_사용자의_탐험_시_FORBIDDEN을_던진다() {
        UserExploration ue = createUserExploration(1L, 2L, 10L, ExplorationStatus.STARTED);
        when(userExplorationMapper.findById(1L)).thenReturn(Optional.of(ue));

        assertThatThrownBy(() -> myExplorationService.completeExploration(1L, 1L))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.FORBIDDEN);
    }

    @Test
    void completeExploration_이미_완료된_탐험_시_INVALID_STATE를_던진다() {
        UserExploration ue = createUserExploration(1L, 1L, 10L, ExplorationStatus.COMPLETED);
        when(userExplorationMapper.findById(1L)).thenReturn(Optional.of(ue));

        assertThatThrownBy(() -> myExplorationService.completeExploration(1L, 1L))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.INVALID_STATE);
    }
}
