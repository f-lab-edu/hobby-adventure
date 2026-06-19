package com.jian.hobbyadventure.service;

import com.jian.hobbyadventure.common.exception.BusinessException;
import com.jian.hobbyadventure.common.exception.ErrorCode;
import com.jian.hobbyadventure.domain.Category;
import com.jian.hobbyadventure.domain.Emotion;
import com.jian.hobbyadventure.domain.Exploration;
import com.jian.hobbyadventure.domain.ExplorationStatus;
import com.jian.hobbyadventure.domain.Record;
import com.jian.hobbyadventure.domain.UserExploration;
import com.jian.hobbyadventure.dto.request.CreateRecordRequest;
import com.jian.hobbyadventure.dto.request.UpdateRecordRequest;
import com.jian.hobbyadventure.dto.response.RecordDetailResponse;
import com.jian.hobbyadventure.dto.response.UpdateRecordResponse;
import com.jian.hobbyadventure.repository.CategoryMapper;
import com.jian.hobbyadventure.repository.ExplorationMapper;
import com.jian.hobbyadventure.repository.RecordImageMapper;
import com.jian.hobbyadventure.repository.RecordMapper;
import com.jian.hobbyadventure.repository.UserExplorationMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecordServiceTest {

    @Mock private RecordMapper recordMapper;
    @Mock private RecordImageMapper recordImageMapper;
    @Mock private UserExplorationMapper userExplorationMapper;
    @Mock private ExplorationMapper explorationMapper;
    @Mock private CategoryMapper categoryMapper;
    @Mock private ImageService imageService;

    @InjectMocks
    private RecordService recordService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(recordService, "imageBaseUrl", "/images/");
    }

    private UserExploration createUserExploration(Long id, Long userId, Long explorationId, ExplorationStatus status) {
        UserExploration ue = new UserExploration();
        ue.setId(id);
        ue.setUserId(userId);
        ue.setExplorationId(explorationId);
        ue.setStatus(status);
        return ue;
    }

    private Record createRecord(Long id, Long userExplorationId) {
        Record record = Record.create(userExplorationId, "title", LocalDate.of(2025, 6, 1), 4, Emotion.HAPPY, null, "content");
        record.setId(id);
        return record;
    }

    private Exploration createExploration(Long id, Long categoryId) {
        Exploration exploration = new Exploration();
        exploration.setId(id);
        exploration.setCategoryId(categoryId);
        exploration.setTitle("탐험 제목");
        return exploration;
    }

    private Category createCategory(Long id, String name) {
        Category category = new Category();
        category.setCategoryId(id);
        category.setName(name);
        return category;
    }

    @Test
    void createRecord_성공_시_recordMapper_insert가_호출된다() {
        CreateRecordRequest request = new CreateRecordRequest(1L, "title", LocalDate.of(2025, 6, 1), 4, Emotion.HAPPY, null, "content");
        when(userExplorationMapper.findById(1L)).thenReturn(Optional.of(createUserExploration(1L, 1L, 10L, ExplorationStatus.COMPLETED)));
        when(recordMapper.existsByUserExplorationId(1L)).thenReturn(false);

        recordService.createRecord(1L, request, null);

        verify(recordMapper).insert(any());
    }

    @Test
    void createRecord_존재하지_않는_userExploration_시_NOT_FOUND를_던진다() {
        CreateRecordRequest request = new CreateRecordRequest(1L, "title", LocalDate.of(2025, 6, 1), 4, Emotion.HAPPY, null, "content");
        when(userExplorationMapper.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> recordService.createRecord(1L, request, null))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.NOT_FOUND);
    }

    @Test
    void createRecord_다른_유저의_탐험_시_FORBIDDEN을_던진다() {
        CreateRecordRequest request = new CreateRecordRequest(1L, "title", LocalDate.of(2025, 6, 1), 4, Emotion.HAPPY, null, "content");
        when(userExplorationMapper.findById(1L)).thenReturn(Optional.of(createUserExploration(1L, 2L, 10L, ExplorationStatus.COMPLETED)));

        assertThatThrownBy(() -> recordService.createRecord(1L, request, null))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.FORBIDDEN);
    }

    @Test
    void createRecord_COMPLETED가_아닌_탐험_시_INVALID_STATE를_던진다() {
        CreateRecordRequest request = new CreateRecordRequest(1L, "title", LocalDate.of(2025, 6, 1), 4, Emotion.HAPPY, null, "content");
        when(userExplorationMapper.findById(1L)).thenReturn(Optional.of(createUserExploration(1L, 1L, 10L, ExplorationStatus.STARTED)));

        assertThatThrownBy(() -> recordService.createRecord(1L, request, null))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.INVALID_STATE);
    }

    @Test
    void createRecord_이미_기록이_있으면_DUPLICATED를_던진다() {
        CreateRecordRequest request = new CreateRecordRequest(1L, "title", LocalDate.of(2025, 6, 1), 4, Emotion.HAPPY, null, "content");
        when(userExplorationMapper.findById(1L)).thenReturn(Optional.of(createUserExploration(1L, 1L, 10L, ExplorationStatus.COMPLETED)));
        when(recordMapper.existsByUserExplorationId(1L)).thenReturn(true);

        assertThatThrownBy(() -> recordService.createRecord(1L, request, null))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.DUPLICATED);
    }

    @Test
    void getRecord_성공_시_기록_상세를_반환한다() {
        when(recordMapper.findById(1L)).thenReturn(Optional.of(createRecord(1L, 1L)));
        when(userExplorationMapper.findById(1L)).thenReturn(Optional.of(createUserExploration(1L, 1L, 10L, ExplorationStatus.COMPLETED)));
        when(explorationMapper.findById(10L)).thenReturn(Optional.of(createExploration(10L, 5L)));
        when(categoryMapper.findById(5L)).thenReturn(createCategory(5L, "학습"));
        when(recordImageMapper.findAllByRecordId(1L)).thenReturn(List.of());

        RecordDetailResponse result = recordService.getRecord(1L, 1L);

        assertThat(result.getRecordId()).isEqualTo(1L);
        assertThat(result.getCategoryName()).isEqualTo("학습");
        assertThat(result.getImageUrls()).isEmpty();
    }

    @Test
    void getRecord_존재하지_않는_기록_시_NOT_FOUND를_던진다() {
        when(recordMapper.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> recordService.getRecord(1L, 1L))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.NOT_FOUND);
    }

    @Test
    void getRecord_다른_유저의_기록_시_FORBIDDEN을_던진다() {
        when(recordMapper.findById(1L)).thenReturn(Optional.of(createRecord(1L, 1L)));
        when(userExplorationMapper.findById(1L)).thenReturn(Optional.of(createUserExploration(1L, 2L, 10L, ExplorationStatus.COMPLETED)));

        assertThatThrownBy(() -> recordService.getRecord(1L, 1L))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.FORBIDDEN);
    }

    @Test
    void updateRecord_성공_시_recordMapper_update가_호출된다() {
        Record record = createRecord(1L, 1L);
        when(recordMapper.findById(1L)).thenReturn(Optional.of(record));
        when(userExplorationMapper.findById(1L)).thenReturn(Optional.of(createUserExploration(1L, 1L, 10L, ExplorationStatus.COMPLETED)));

        UpdateRecordResponse result = recordService.updateRecord(1L, 1L, new UpdateRecordRequest("new title", null, null, null, null, null), null);

        assertThat(result.getRecordId()).isEqualTo(1L);
        verify(recordMapper).update(record);
    }

    @Test
    void updateRecord_존재하지_않는_기록_시_NOT_FOUND를_던진다() {
        when(recordMapper.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> recordService.updateRecord(1L, 1L, new UpdateRecordRequest(null, null, null, null, null, null), null))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.NOT_FOUND);
    }

    @Test
    void updateRecord_다른_유저의_기록_시_FORBIDDEN을_던진다() {
        when(recordMapper.findById(1L)).thenReturn(Optional.of(createRecord(1L, 1L)));
        when(userExplorationMapper.findById(1L)).thenReturn(Optional.of(createUserExploration(1L, 2L, 10L, ExplorationStatus.COMPLETED)));

        assertThatThrownBy(() -> recordService.updateRecord(1L, 1L, new UpdateRecordRequest(null, null, null, null, null, null), null))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.FORBIDDEN);
    }

    @Test
    void deleteRecord_성공_시_파일과_DB가_순서대로_삭제된다() {
        when(recordMapper.findById(1L)).thenReturn(Optional.of(createRecord(1L, 1L)));
        when(userExplorationMapper.findById(1L)).thenReturn(Optional.of(createUserExploration(1L, 1L, 10L, ExplorationStatus.COMPLETED)));

        recordService.deleteRecord(1L, 1L);

        verify(imageService).deleteImages(1L);
        verify(recordImageMapper).deleteAllByRecordId(1L);
        verify(recordMapper).deleteById(1L);
    }

    @Test
    void deleteRecord_다른_유저의_기록_시_FORBIDDEN을_던진다() {
        when(recordMapper.findById(1L)).thenReturn(Optional.of(createRecord(1L, 1L)));
        when(userExplorationMapper.findById(1L)).thenReturn(Optional.of(createUserExploration(1L, 2L, 10L, ExplorationStatus.COMPLETED)));

        assertThatThrownBy(() -> recordService.deleteRecord(1L, 1L))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.FORBIDDEN);
    }
}
