package com.jian.hobbyadventure.service;

import com.jian.hobbyadventure.common.exception.BusinessException;
import com.jian.hobbyadventure.common.exception.ErrorCode;
import com.jian.hobbyadventure.common.response.PageResponse;
import com.jian.hobbyadventure.domain.Category;
import com.jian.hobbyadventure.domain.Exploration;
import com.jian.hobbyadventure.domain.ExplorationStatus;
import com.jian.hobbyadventure.domain.UserExploration;
import com.jian.hobbyadventure.domain.Waypoint;
import com.jian.hobbyadventure.dto.response.CompleteExplorationResponse;
import com.jian.hobbyadventure.dto.response.ExplorationCountResponse;
import com.jian.hobbyadventure.dto.response.MyExplorationDetailResponse;
import com.jian.hobbyadventure.dto.response.MyExplorationListItemResponse;
import com.jian.hobbyadventure.repository.CategoryMapper;
import com.jian.hobbyadventure.repository.ExplorationMapper;
import com.jian.hobbyadventure.repository.RecordMapper;
import com.jian.hobbyadventure.repository.UserExplorationCountRow;
import com.jian.hobbyadventure.repository.UserExplorationMapper;
import com.jian.hobbyadventure.repository.WaypointImageMapper;
import com.jian.hobbyadventure.repository.WaypointMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    @Mock
    private WaypointMapper waypointMapper;
    @Mock
    private WaypointImageMapper waypointImageMapper;
    @Mock
    private ImageService imageService;

    @InjectMocks
    private MyExplorationService myExplorationService;

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
        when(userExplorationMapper.findAllByCondition(anyLong(), any(), any(), any(), anyInt(), anyInt())).thenReturn(List.of(ue));
        when(userExplorationMapper.countByCondition(anyLong(), any(), any(), any())).thenReturn(1L);
        when(explorationMapper.findByIdIn(any())).thenReturn(List.of(e));
        when(categoryMapper.findAll()).thenReturn(List.of(createCategory(1L, "운동")));
        when(recordMapper.findUserExplorationIdsByUserExplorationIdIn(any())).thenReturn(List.of());

        PageResponse<MyExplorationListItemResponse> result = myExplorationService.getMyExplorations(1L, ExplorationStatus.STARTED, null, null, null, 1, 10);

        assertThat(result.getData()).hasSize(1);
        assertThat(result.getData().get(0).getCategoryName()).isEqualTo("운동");
    }

    @Test
    void getMyExplorations_categoryId가_있으면_해당_카테고리만_반환한다() {
        UserExploration ue = createUserExploration(1L, 1L, 10L, ExplorationStatus.STARTED);
        Exploration e = createExploration(10L, 1L);
        when(explorationMapper.findIdsByCategoryId(1L)).thenReturn(List.of(10L));
        when(userExplorationMapper.findAllByCondition(anyLong(), any(), any(), any(), anyInt(), anyInt())).thenReturn(List.of(ue));
        when(userExplorationMapper.countByCondition(anyLong(), any(), any(), any())).thenReturn(1L);
        when(explorationMapper.findByIdIn(any())).thenReturn(List.of(e));
        when(categoryMapper.findAll()).thenReturn(List.of(createCategory(1L, "운동")));
        when(recordMapper.findUserExplorationIdsByUserExplorationIdIn(any())).thenReturn(List.of());

        PageResponse<MyExplorationListItemResponse> result = myExplorationService.getMyExplorations(1L, ExplorationStatus.STARTED, 1L, null, null, 1, 10);

        assertThat(result.getData()).hasSize(1);
    }

    @Test
    void getMyExplorations_explorationId가_있으면_categoryId보다_우선한다() {
        UserExploration ue = createUserExploration(1L, 1L, 10L, ExplorationStatus.COMPLETED);
        Exploration e = createExploration(10L, 1L);
        when(userExplorationMapper.findAllByCondition(anyLong(), any(), any(), any(), anyInt(), anyInt())).thenReturn(List.of(ue));
        when(userExplorationMapper.countByCondition(anyLong(), any(), any(), any())).thenReturn(1L);
        when(explorationMapper.findByIdIn(any())).thenReturn(List.of(e));
        when(categoryMapper.findAll()).thenReturn(List.of(createCategory(1L, "운동")));
        when(recordMapper.findUserExplorationIdsByUserExplorationIdIn(any())).thenReturn(List.of());

        myExplorationService.getMyExplorations(1L, ExplorationStatus.COMPLETED, 999L, 10L, null, 1, 10);

        verify(userExplorationMapper).findAllByCondition(1L, ExplorationStatus.COMPLETED, List.of(10L), null, 10, 0);
        verify(explorationMapper, org.mockito.Mockito.never()).findIdsByCategoryId(any());
    }

    @Test
    void getMyExplorations_hasRecord_false면_기록없는_completed만_반환한다() {
        UserExploration ue = createUserExploration(2L, 1L, 10L, ExplorationStatus.COMPLETED);
        Exploration e = createExploration(10L, 1L);
        when(userExplorationMapper.findIdsByUserIdAndStatus(1L, ExplorationStatus.COMPLETED)).thenReturn(List.of(1L, 2L));
        when(recordMapper.findUserExplorationIdsByUserExplorationIdIn(List.of(1L, 2L))).thenReturn(List.of(1L));
        when(userExplorationMapper.findAllByCondition(anyLong(), any(), any(), any(), anyInt(), anyInt())).thenReturn(List.of(ue));
        when(userExplorationMapper.countByCondition(anyLong(), any(), any(), any())).thenReturn(1L);
        when(explorationMapper.findByIdIn(any())).thenReturn(List.of(e));
        when(categoryMapper.findAll()).thenReturn(List.of(createCategory(1L, "운동")));
        when(recordMapper.findUserExplorationIdsByUserExplorationIdIn(List.of(2L))).thenReturn(List.of());

        PageResponse<MyExplorationListItemResponse> result =
                myExplorationService.getMyExplorations(1L, ExplorationStatus.COMPLETED, null, null, false, 1, 10);

        assertThat(result.getData()).hasSize(1);
        verify(userExplorationMapper).findAllByCondition(1L, ExplorationStatus.COMPLETED, null, List.of(2L), 10, 0);
    }

    @Test
    void getMyExplorations_hasRecord_false인데_전부_기록있으면_DB조회없이_빈목록을_반환한다() {
        when(userExplorationMapper.findIdsByUserIdAndStatus(1L, ExplorationStatus.COMPLETED)).thenReturn(List.of(1L));
        when(recordMapper.findUserExplorationIdsByUserExplorationIdIn(List.of(1L))).thenReturn(List.of(1L));

        PageResponse<MyExplorationListItemResponse> result =
                myExplorationService.getMyExplorations(1L, ExplorationStatus.COMPLETED, null, null, false, 1, 10);

        assertThat(result.getData()).isEmpty();
        assertThat(result.getMeta().getTotalElements()).isZero();
        verify(userExplorationMapper, org.mockito.Mockito.never()).findAllByCondition(any(), any(), any(), any(), anyInt(), anyInt());
    }

    @Test
    void getMyExplorations_COMPLETED도_마지막_여정_정보를_채운다() {
        UserExploration ue = createUserExploration(2L, 1L, 10L, ExplorationStatus.COMPLETED);
        Exploration e = createExploration(10L, 1L);
        Waypoint waypoint = Waypoint.create(2L, "완주함, 뿌듯했다", "한강공원", LocalDateTime.now());
        when(userExplorationMapper.findAllByCondition(anyLong(), any(), any(), any(), anyInt(), anyInt())).thenReturn(List.of(ue));
        when(userExplorationMapper.countByCondition(anyLong(), any(), any(), any())).thenReturn(1L);
        when(explorationMapper.findByIdIn(any())).thenReturn(List.of(e));
        when(categoryMapper.findAll()).thenReturn(List.of(createCategory(1L, "운동")));
        when(recordMapper.findUserExplorationIdsByUserExplorationIdIn(any())).thenReturn(List.of());
        when(waypointMapper.findLatestByUserExplorationIdIn(List.of(2L))).thenReturn(List.of(waypoint));
        when(waypointImageMapper.findAllByWaypointIds(any())).thenReturn(List.of());

        PageResponse<MyExplorationListItemResponse> result =
                myExplorationService.getMyExplorations(1L, ExplorationStatus.COMPLETED, null, null, null, 1, 10);

        assertThat(result.getData()).hasSize(1);
        assertThat(result.getData().get(0).getLastWaypointMemo()).isEqualTo("완주함, 뿌듯했다");
        assertThat(result.getData().get(0).getLastWaypointCheckedAt()).isNotNull();
    }

    @Test
    void getCompletedExplorationCounts_탐험별로_묶어서_개수를_반환한다() {
        UserExplorationCountRow row = new UserExplorationCountRow();
        row.setExplorationId(10L);
        row.setCount(3);
        Exploration e = createExploration(10L, 1L);
        when(userExplorationMapper.countGroupByExplorationId(1L, ExplorationStatus.COMPLETED)).thenReturn(List.of(row));
        when(explorationMapper.findByIdIn(List.of(10L))).thenReturn(List.of(e));

        List<ExplorationCountResponse> result = myExplorationService.getCompletedExplorationCounts(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getExplorationId()).isEqualTo(10L);
        assertThat(result.get(0).getTitle()).isEqualTo("탐험 제목");
        assertThat(result.get(0).getCount()).isEqualTo(3);
    }

    @Test
    void getCompletedExplorationCounts_완료한_탐험이_없으면_빈목록을_반환한다() {
        when(userExplorationMapper.countGroupByExplorationId(1L, ExplorationStatus.COMPLETED)).thenReturn(List.of());

        List<ExplorationCountResponse> result = myExplorationService.getCompletedExplorationCounts(1L);

        assertThat(result).isEmpty();
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
