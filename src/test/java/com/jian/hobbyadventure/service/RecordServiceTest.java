package com.jian.hobbyadventure.service;

import com.jian.hobbyadventure.common.exception.BusinessException;
import com.jian.hobbyadventure.common.exception.ErrorCode;
import com.jian.hobbyadventure.domain.Category;
import com.jian.hobbyadventure.domain.Emotion;
import com.jian.hobbyadventure.domain.Exploration;
import com.jian.hobbyadventure.domain.ExplorationStatus;
import com.jian.hobbyadventure.domain.ImageSize;
import com.jian.hobbyadventure.domain.Record;
import com.jian.hobbyadventure.domain.RecordImage;
import com.jian.hobbyadventure.domain.UserExploration;
import com.jian.hobbyadventure.dto.request.CreateRecordRequest;
import com.jian.hobbyadventure.dto.request.RecordSearchCondition;
import com.jian.hobbyadventure.dto.request.UpdateRecordRequest;
import com.jian.hobbyadventure.common.response.PageResponse;
import com.jian.hobbyadventure.dto.response.RecordDetailResponse;
import com.jian.hobbyadventure.dto.response.RecordListItemResponse;
import com.jian.hobbyadventure.dto.response.UpdateRecordResponse;
import com.jian.hobbyadventure.repository.CategoryMapper;
import com.jian.hobbyadventure.repository.ExplorationMapper;
import com.jian.hobbyadventure.repository.RecordImageMapper;
import com.jian.hobbyadventure.repository.RecordMapper;
import com.jian.hobbyadventure.repository.UserExplorationMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecordServiceTest {

    private static final String TITLE = "title";
    private static final String CONTENT = "content";
    private static final String FALLBACK_THUMBNAIL_URL = "explorations/fallback.jpg";

    @Mock private RecordMapper recordMapper;
    @Mock private RecordImageMapper recordImageMapper;
    @Mock private UserExplorationMapper userExplorationMapper;
    @Mock private ExplorationMapper explorationMapper;
    @Mock private CategoryMapper categoryMapper;
    @Mock private ImageService imageService;

    @InjectMocks
    private RecordService recordService;

    private UserExploration createUserExploration(Long id, Long userId, Long explorationId, ExplorationStatus status) {
        UserExploration ue = new UserExploration();
        ue.setId(id);
        ue.setUserId(userId);
        ue.setExplorationId(explorationId);
        ue.setStatus(status);
        return ue;
    }

    private Record createRecord(Long id, Long userExplorationId) {
        Record savedRecord = Record.create(userExplorationId, TITLE, LocalDate.of(2025, Month.JUNE, 1), 4, Emotion.HAPPY, null, CONTENT);
        savedRecord.setId(id);
        return savedRecord;
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
        CreateRecordRequest request = new CreateRecordRequest(1L, TITLE, LocalDate.of(2025, Month.JUNE, 1), 4, Emotion.HAPPY, null, CONTENT);
        when(userExplorationMapper.findById(1L)).thenReturn(Optional.of(createUserExploration(1L, 1L, 10L, ExplorationStatus.COMPLETED)));
        when(recordMapper.existsByUserExplorationId(1L)).thenReturn(false);

        recordService.createRecord(1L, request, null);

        verify(recordMapper).insert(any());
    }

    @Test
    void createRecord_존재하지_않는_userExploration_시_NOT_FOUND를_던진다() {
        CreateRecordRequest request = new CreateRecordRequest(1L, TITLE, LocalDate.of(2025, Month.JUNE, 1), 4, Emotion.HAPPY, null, CONTENT);
        when(userExplorationMapper.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> recordService.createRecord(1L, request, null))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.NOT_FOUND);
    }

    @Test
    void createRecord_다른_유저의_탐험_시_FORBIDDEN을_던진다() {
        CreateRecordRequest request = new CreateRecordRequest(1L, TITLE, LocalDate.of(2025, Month.JUNE, 1), 4, Emotion.HAPPY, null, CONTENT);
        when(userExplorationMapper.findById(1L)).thenReturn(Optional.of(createUserExploration(1L, 2L, 10L, ExplorationStatus.COMPLETED)));

        assertThatThrownBy(() -> recordService.createRecord(1L, request, null))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.FORBIDDEN);
    }

    @Test
    void createRecord_COMPLETED가_아닌_탐험_시_INVALID_STATE를_던진다() {
        CreateRecordRequest request = new CreateRecordRequest(1L, TITLE, LocalDate.of(2025, Month.JUNE, 1), 4, Emotion.HAPPY, null, CONTENT);
        when(userExplorationMapper.findById(1L)).thenReturn(Optional.of(createUserExploration(1L, 1L, 10L, ExplorationStatus.STARTED)));

        assertThatThrownBy(() -> recordService.createRecord(1L, request, null))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.INVALID_STATE);
    }

    @Test
    void createRecord_이미_기록이_있으면_DUPLICATED를_던진다() {
        CreateRecordRequest request = new CreateRecordRequest(1L, TITLE, LocalDate.of(2025, Month.JUNE, 1), 4, Emotion.HAPPY, null, CONTENT);
        when(userExplorationMapper.findById(1L)).thenReturn(Optional.of(createUserExploration(1L, 1L, 10L, ExplorationStatus.COMPLETED)));
        when(recordMapper.existsByUserExplorationId(1L)).thenReturn(true);

        assertThatThrownBy(() -> recordService.createRecord(1L, request, null))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.DUPLICATED);
    }

    @Test
    void createRecord_이미지가_10장_초과면_IMAGE_LIMIT_EXCEEDED를_던진다() {
        CreateRecordRequest request = new CreateRecordRequest(1L, TITLE, LocalDate.of(2025, Month.JUNE, 1), 4, Emotion.HAPPY, null, CONTENT);
        List<MultipartFile> images = List.of(mock(MultipartFile.class), mock(MultipartFile.class), mock(MultipartFile.class),
                mock(MultipartFile.class), mock(MultipartFile.class), mock(MultipartFile.class), mock(MultipartFile.class),
                mock(MultipartFile.class), mock(MultipartFile.class), mock(MultipartFile.class), mock(MultipartFile.class));

        assertThatThrownBy(() -> recordService.createRecord(1L, request, images))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.IMAGE_LIMIT_EXCEEDED);
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
    void getRecords_record_자체_이미지가_있으면_그_이미지를_사용한다() {
        UserExploration ue = createUserExploration(1L, 1L, 10L, ExplorationStatus.COMPLETED);
        Exploration exploration = createExploration(10L, 5L);
        exploration.setThumbnailUrl(FALLBACK_THUMBNAIL_URL);
        Record savedRecord = createRecord(1L, 1L);
        RecordImage image = new RecordImage();
        image.setRecordId(1L);
        image.setImageUrl("records/1/own.jpg");

        when(userExplorationMapper.findIdsByUserId(1L)).thenReturn(List.of(1L));
        when(recordMapper.findAllByUserExplorationIds(List.of(1L), 10, 0)).thenReturn(List.of(savedRecord));
        when(recordMapper.countByUserExplorationIds(List.of(1L))).thenReturn(1L);
        when(recordImageMapper.findAllByRecordIds(List.of(1L))).thenReturn(List.of(image));
        when(userExplorationMapper.findByIdIn(List.of(1L))).thenReturn(List.of(ue));
        when(explorationMapper.findByIdIn(List.of(10L))).thenReturn(List.of(exploration));
        when(categoryMapper.findAll()).thenReturn(List.of(createCategory(5L, "학습")));
        when(imageService.generateSignedCloudFrontUrl("records/1/own.jpg", ImageSize.LIST)).thenReturn("https://signed/own.jpg");

        PageResponse<RecordListItemResponse> result = recordService.getRecords(1L, new RecordSearchCondition(null, null), 1, 10);

        assertThat(result.getData()).hasSize(1);
        assertThat(result.getData().get(0).getThumbnailUrl()).isEqualTo("https://signed/own.jpg");
    }

    @Test
    void getRecords_record_이미지가_없으면_exploration_썸네일로_대체된다() {
        UserExploration ue = createUserExploration(1L, 1L, 10L, ExplorationStatus.COMPLETED);
        Exploration exploration = createExploration(10L, 5L);
        exploration.setThumbnailUrl(FALLBACK_THUMBNAIL_URL);
        Record savedRecord = createRecord(1L, 1L);

        when(userExplorationMapper.findIdsByUserId(1L)).thenReturn(List.of(1L));
        when(recordMapper.findAllByUserExplorationIds(List.of(1L), 10, 0)).thenReturn(List.of(savedRecord));
        when(recordMapper.countByUserExplorationIds(List.of(1L))).thenReturn(1L);
        when(recordImageMapper.findAllByRecordIds(List.of(1L))).thenReturn(List.of());
        when(userExplorationMapper.findByIdIn(List.of(1L))).thenReturn(List.of(ue));
        when(explorationMapper.findByIdIn(List.of(10L))).thenReturn(List.of(exploration));
        when(categoryMapper.findAll()).thenReturn(List.of(createCategory(5L, "학습")));
        when(imageService.generatePublicCloudFrontUrl(FALLBACK_THUMBNAIL_URL, ImageSize.LIST)).thenReturn("https://public/fallback.jpg");

        PageResponse<RecordListItemResponse> result = recordService.getRecords(1L, new RecordSearchCondition(null, null), 1, 10);

        assertThat(result.getData()).hasSize(1);
        assertThat(result.getData().get(0).getThumbnailUrl()).isEqualTo("https://public/fallback.jpg");
    }

    @Test
    void updateRecord_성공_시_recordMapper_update가_호출된다() {
        Record savedRecord = createRecord(1L, 1L);
        when(recordMapper.findById(1L)).thenReturn(Optional.of(savedRecord));
        when(userExplorationMapper.findById(1L)).thenReturn(Optional.of(createUserExploration(1L, 1L, 10L, ExplorationStatus.COMPLETED)));

        UpdateRecordResponse result = recordService.updateRecord(1L, 1L, new UpdateRecordRequest("new title", null, null, null, null, null), null);

        assertThat(result.getRecordId()).isEqualTo(1L);
        verify(recordMapper).update(savedRecord);
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
    void updateRecord_이미지가_10장_초과면_IMAGE_LIMIT_EXCEEDED를_던진다() {
        List<MultipartFile> images = List.of(mock(MultipartFile.class), mock(MultipartFile.class), mock(MultipartFile.class),
                mock(MultipartFile.class), mock(MultipartFile.class), mock(MultipartFile.class), mock(MultipartFile.class),
                mock(MultipartFile.class), mock(MultipartFile.class), mock(MultipartFile.class), mock(MultipartFile.class));

        assertThatThrownBy(() -> recordService.updateRecord(1L, 1L, new UpdateRecordRequest(null, null, null, null, null, null), images))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.IMAGE_LIMIT_EXCEEDED);
    }

    @Test
    void updateRecord_이미지_교체_시_DB삭제와_새이미지_저장_이후에_옛이미지가_S3에서_삭제된다() {
        when(recordMapper.findById(1L)).thenReturn(Optional.of(createRecord(1L, 1L)));
        when(userExplorationMapper.findById(1L)).thenReturn(Optional.of(createUserExploration(1L, 1L, 10L, ExplorationStatus.COMPLETED)));
        RecordImage oldImage = new RecordImage();
        oldImage.setImageUrl("records/1/old.jpg");
        when(recordImageMapper.findAllByRecordId(1L)).thenReturn(List.of(oldImage));
        MultipartFile newFile = mock(MultipartFile.class);
        when(imageService.saveImages(eq(1L), anyList())).thenReturn(List.of("records/1/new.jpg"));

        recordService.updateRecord(1L, 1L, new UpdateRecordRequest(null, null, null, null, null, null), List.of(newFile));

        InOrder inOrder = inOrder(recordImageMapper, imageService);
        inOrder.verify(recordImageMapper).deleteAllByRecordId(1L);
        inOrder.verify(imageService).saveImages(eq(1L), anyList());
        inOrder.verify(imageService).deleteImages(List.of("records/1/old.jpg"));
    }

    @Test
    void deleteRecord_성공_시_DB가_S3보다_먼저_삭제된다() {
        when(recordMapper.findById(1L)).thenReturn(Optional.of(createRecord(1L, 1L)));
        when(userExplorationMapper.findById(1L)).thenReturn(Optional.of(createUserExploration(1L, 1L, 10L, ExplorationStatus.COMPLETED)));
        RecordImage image = new RecordImage();
        image.setImageUrl("records/1/a.jpg");
        when(recordImageMapper.findAllByRecordId(1L)).thenReturn(List.of(image));

        recordService.deleteRecord(1L, 1L);

        InOrder inOrder = inOrder(recordImageMapper, recordMapper, imageService);
        inOrder.verify(recordImageMapper).deleteAllByRecordId(1L);
        inOrder.verify(recordMapper).deleteById(1L);
        inOrder.verify(imageService).deleteImages(List.of("records/1/a.jpg"));
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
