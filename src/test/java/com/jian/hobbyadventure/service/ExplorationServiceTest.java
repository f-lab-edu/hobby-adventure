package com.jian.hobbyadventure.service;

import com.jian.hobbyadventure.common.exception.BusinessException;
import com.jian.hobbyadventure.common.exception.ErrorCode;
import com.jian.hobbyadventure.common.response.PageResponse;
import com.jian.hobbyadventure.domain.Category;
import com.jian.hobbyadventure.domain.Exploration;
import com.jian.hobbyadventure.dto.response.ExplorationDetailResponse;
import com.jian.hobbyadventure.dto.response.ExplorationListItemResponse;
import com.jian.hobbyadventure.repository.CategoryMapper;
import com.jian.hobbyadventure.repository.ExplorationMapper;
import com.jian.hobbyadventure.repository.UserExplorationMapper;
import com.jian.hobbyadventure.repository.UserMapper;
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
class ExplorationServiceTest {

    @Mock
    private ExplorationMapper explorationMapper;
    @Mock
    private CategoryMapper categoryMapper;
    @Mock
    private UserMapper userMapper;
    @Mock
    private UserExplorationMapper userExplorationMapper;

    @InjectMocks
    private ExplorationService explorationService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(explorationService, "imageBaseUrl", "/images/");
    }

    private Exploration createExploration(Long id, Long categoryId) {
        Exploration exploration = new Exploration();
        exploration.setId(id);
        exploration.setCategoryId(categoryId);
        exploration.setTitle("탐험 제목");
        exploration.setThumbnailUrl("explorations/test.jpg");
        exploration.setShortDescription("짧은 설명");
        exploration.setDescription("상세 설명");
        exploration.setCreatedAt(LocalDateTime.now());
        return exploration;
    }

    private Category createCategory(Long id, String code, String name, int displayOrder) {
        Category category = new Category();
        category.setCategoryId(id);
        category.setCode(code);
        category.setName(name);
        category.setDisplayOrder(displayOrder);
        return category;
    }

    @Test
    void getExplorations_categoryId가_null이면_전체_탐험_목록을_반환한다() {
        List<Category> categories = List.of(createCategory(1L, "EXERCISE", "운동", 1));
        List<Exploration> explorations = List.of(createExploration(1L, 1L));
        when(categoryMapper.findAll()).thenReturn(categories);
        when(explorationMapper.findAll(anyInt(), anyInt())).thenReturn(explorations);
        when(explorationMapper.countAll()).thenReturn(1L);

        PageResponse<ExplorationListItemResponse> result = explorationService.getExplorations(null, 1, 10);

        assertThat(result.getData()).hasSize(1);
        assertThat(result.getData().get(0).getCategoryName()).isEqualTo("운동");
    }

    @Test
    void getExplorations_categoryId가_있으면_해당_카테고리의_탐험만_반환한다() {
        List<Category> categories = List.of(createCategory(1L, "EXERCISE", "운동", 1));
        List<Exploration> explorations = List.of(createExploration(1L, 1L));
        when(categoryMapper.findAll()).thenReturn(categories);
        when(explorationMapper.findAllByCategoryId(anyLong(), anyInt(), anyInt())).thenReturn(explorations);
        when(explorationMapper.countByCategoryId(anyLong())).thenReturn(1L);

        PageResponse<ExplorationListItemResponse> result = explorationService.getExplorations(1L, 1, 10);

        assertThat(result.getData()).hasSize(1);
        assertThat(result.getMeta().getTotalElements()).isEqualTo(1L);
    }

    @Test
    void getExplorations_존재하지_않는_categoryId_시_BusinessException을_던진다() {
        when(categoryMapper.findAll()).thenReturn(List.of());

        assertThatThrownBy(() -> explorationService.getExplorations(99L, 1, 10))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.NOT_FOUND);
    }

    @Test
    void getExploration_성공_시_탐험_상세를_반환한다() {
        Exploration exploration = createExploration(1L, 1L);
        Category category = createCategory(1L, "EXERCISE", "운동", 1);
        when(explorationMapper.findById(1L)).thenReturn(Optional.of(exploration));
        when(categoryMapper.findById(1L)).thenReturn(category);

        ExplorationDetailResponse result = explorationService.getExploration(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getCategoryName()).isEqualTo("운동");
        assertThat(result.getThumbnailUrl()).isEqualTo("/images/explorations/test.jpg");
    }

    @Test
    void getExploration_존재하지_않는_id_시_BusinessException을_던진다() {
        when(explorationMapper.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> explorationService.getExploration(1L))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.NOT_FOUND);
    }

    @Test
    void startExploration_성공_시_userExplorationMapper_insert가_호출된다() {
        when(explorationMapper.findById(1L)).thenReturn(Optional.of(createExploration(1L, 1L)));
        when(userMapper.existsById(1L)).thenReturn(true);

        explorationService.startExploration(1L, 1L);

        verify(userExplorationMapper).insert(any());
    }

    @Test
    void startExploration_존재하지_않는_탐험_시_BusinessException을_던진다() {
        when(explorationMapper.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> explorationService.startExploration(1L, 1L))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.NOT_FOUND);
    }

    @Test
    void startExploration_존재하지_않는_사용자_시_BusinessException을_던진다() {
        when(explorationMapper.findById(1L)).thenReturn(Optional.of(createExploration(1L, 1L)));
        when(userMapper.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> explorationService.startExploration(1L, 1L))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.NOT_FOUND);
    }
}
