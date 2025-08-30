package com.mycodethesaurus.financeinspector.component;

import static org.junit.jupiter.api.Assertions.*;

import com.mycodethesaurus.financeinspector.dto.CategoryCreateRequest;
import com.mycodethesaurus.financeinspector.dto.CategoryResponse;
import com.mycodethesaurus.financeinspector.persistence.entity.ExpenseCategoryEntity;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("CategoryMapper Tests")
class CategoryMapperTest {

  @InjectMocks private CategoryMapper categoryMapper;

  private CategoryCreateRequest mockCreateRequest;
  private ExpenseCategoryEntity mockCategoryEntity;
  private ExpenseCategoryEntity mockParentEntity;

  @BeforeEach
  void setUp() {
    mockCreateRequest = createMockCreateRequest();
    mockCategoryEntity = createMockCategoryEntity();
    mockParentEntity = createMockParentEntity();
  }

  // ========== CREATE REQUEST TO GLOBAL ENTITY TESTS ==========

  @Test
  @DisplayName("Should map create request to global entity without parent")
  void shouldMapCreateRequestToGlobalEntityWithoutParent() {
    // When
    ExpenseCategoryEntity result =
        categoryMapper.createRequestToGlobalEntity(mockCreateRequest, null);

    // Then
    assertNotNull(result);
    assertEquals(mockCreateRequest.getName(), result.getName());
    assertEquals(mockCreateRequest.getDescription(), result.getDescription());
    assertEquals(mockCreateRequest.getColorCode(), result.getColorCode());
    assertEquals(mockCreateRequest.getIconName(), result.getIconName());
    assertEquals(mockCreateRequest.getSortOrder(), result.getSortOrder());
    assertTrue(result.getIsActive());
    assertNull(result.getParent());
    assertNull(result.getId()); // Should be null for new entities
  }

  @Test
  @DisplayName("Should map create request to global entity with parent")
  void shouldMapCreateRequestToGlobalEntityWithParent() {
    // When
    ExpenseCategoryEntity result =
        categoryMapper.createRequestToGlobalEntity(mockCreateRequest, mockParentEntity);

    // Then
    assertNotNull(result);
    assertEquals(mockCreateRequest.getName(), result.getName());
    assertEquals(mockCreateRequest.getDescription(), result.getDescription());
    assertEquals(mockCreateRequest.getColorCode(), result.getColorCode());
    assertEquals(mockCreateRequest.getIconName(), result.getIconName());
    assertEquals(mockCreateRequest.getSortOrder(), result.getSortOrder());
    assertTrue(result.getIsActive());
    assertEquals(mockParentEntity, result.getParent());
    assertNull(result.getId()); // Should be null for new entities
  }

  @Test
  @DisplayName("Should handle null create request gracefully")
  void shouldHandleNullCreateRequestGracefully() {
    // When & Then
    assertThrows(
        NullPointerException.class, () -> categoryMapper.createRequestToGlobalEntity(null, null));
  }

  // ========== ENTITY TO RESPONSE TESTS ==========

  @Test
  @DisplayName("Should map entity to response without parent and subcategories")
  void shouldMapEntityToResponseWithoutParentAndSubcategories() {
    // When
    CategoryResponse result = categoryMapper.entityToResponse(mockCategoryEntity);

    // Then
    assertNotNull(result);
    assertEquals(mockCategoryEntity.getId(), result.getId());
    assertEquals(mockCategoryEntity.getName(), result.getName());
    assertEquals(mockCategoryEntity.getDescription(), result.getDescription());
    assertEquals(mockCategoryEntity.getColorCode(), result.getColorCode());
    assertEquals(mockCategoryEntity.getIconName(), result.getIconName());
    assertEquals(mockCategoryEntity.getSortOrder(), result.getSortOrder());
    assertEquals(mockCategoryEntity.getIsActive(), result.getIsActive());
    assertEquals(mockCategoryEntity.getCreatedOn(), result.getCreatedOn());
    assertEquals(mockCategoryEntity.getUpdatedOn(), result.getUpdatedOn());
    assertNull(result.getUserId()); // Should always be null for global categories
    assertNull(result.getParent());
    // Note: subcategories may be null for minimal response, which is expected
    if (result.getSubcategories() != null) {
      assertTrue(result.getSubcategories().isEmpty());
    }
  }

  @Test
  @DisplayName("Should map entity to response with parent")
  void shouldMapEntityToResponseWithParent() {
    // Given
    mockCategoryEntity.setParent(mockParentEntity);

    // When
    CategoryResponse result = categoryMapper.entityToResponse(mockCategoryEntity);

    // Then
    assertNotNull(result);
    assertNotNull(result.getParent());
    assertEquals(mockParentEntity.getId(), result.getParent().getId());
    assertEquals(mockParentEntity.getName(), result.getParent().getName());
    assertEquals(mockParentEntity.getColorCode(), result.getParent().getColorCode());
    assertEquals(mockParentEntity.getIconName(), result.getParent().getIconName());
  }

  @Test
  @DisplayName("Should map entity to response with active subcategories")
  void shouldMapEntityToResponseWithActiveSubcategories() {
    // Given
    ExpenseCategoryEntity activeSubcategory = createMockSubcategoryEntity(true);
    ExpenseCategoryEntity inactiveSubcategory = createMockSubcategoryEntity(false);
    mockCategoryEntity.setSubcategories(Arrays.asList(activeSubcategory, inactiveSubcategory));

    // When
    CategoryResponse result = categoryMapper.entityToResponse(mockCategoryEntity);

    // Then
    assertNotNull(result);
    assertNotNull(result.getSubcategories());
    assertEquals(1, result.getSubcategories().size()); // Only active subcategory should be included

    CategoryResponse.CategorySummary subcategory = result.getSubcategories().get(0);
    assertEquals(activeSubcategory.getId(), subcategory.getId());
    assertEquals(activeSubcategory.getName(), subcategory.getName());
    assertEquals(activeSubcategory.getColorCode(), subcategory.getColorCode());
    assertEquals(activeSubcategory.getIconName(), subcategory.getIconName());
  }

  @Test
  @DisplayName("Should handle null entity gracefully")
  void shouldHandleNullEntityGracefully() {
    // When & Then
    assertThrows(NullPointerException.class, () -> categoryMapper.entityToResponse(null));
  }

  // ========== ENTITIES TO RESPONSES TESTS ==========

  @Test
  @DisplayName("Should map entities list to responses list")
  void shouldMapEntitiesListToResponsesList() {
    // Given
    ExpenseCategoryEntity secondEntity = createMockCategoryEntity();
    secondEntity.setId(2L);
    secondEntity.setName("Transportation");
    List<ExpenseCategoryEntity> entities = Arrays.asList(mockCategoryEntity, secondEntity);

    // When
    List<CategoryResponse> result = categoryMapper.entitiesToResponses(entities);

    // Then
    assertNotNull(result);
    assertEquals(2, result.size());

    CategoryResponse firstResponse = result.get(0);
    assertEquals(mockCategoryEntity.getId(), firstResponse.getId());
    assertEquals(mockCategoryEntity.getName(), firstResponse.getName());

    CategoryResponse secondResponse = result.get(1);
    assertEquals(secondEntity.getId(), secondResponse.getId());
    assertEquals(secondEntity.getName(), secondResponse.getName());
  }

  @Test
  @DisplayName("Should handle empty entities list")
  void shouldHandleEmptyEntitiesList() {
    // Given
    List<ExpenseCategoryEntity> emptyList = Arrays.asList();

    // When
    List<CategoryResponse> result = categoryMapper.entitiesToResponses(emptyList);

    // Then
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName("Should handle null entities list gracefully")
  void shouldHandleNullEntitiesListGracefully() {
    // When & Then
    assertThrows(NullPointerException.class, () -> categoryMapper.entitiesToResponses(null));
  }

  // ========== ENTITY TO RESPONSE MINIMAL TESTS ==========

  @Test
  @DisplayName("Should map entity to minimal response without subcategories")
  void shouldMapEntityToMinimalResponseWithoutSubcategories() {
    // When
    CategoryResponse result = categoryMapper.entityToResponseMinimal(mockCategoryEntity);

    // Then
    assertNotNull(result);
    assertEquals(mockCategoryEntity.getId(), result.getId());
    assertEquals(mockCategoryEntity.getName(), result.getName());
    assertEquals(mockCategoryEntity.getDescription(), result.getDescription());
    assertEquals(mockCategoryEntity.getColorCode(), result.getColorCode());
    assertEquals(mockCategoryEntity.getIconName(), result.getIconName());
    assertEquals(mockCategoryEntity.getSortOrder(), result.getSortOrder());
    assertEquals(mockCategoryEntity.getIsActive(), result.getIsActive());
    assertEquals(mockCategoryEntity.getCreatedOn(), result.getCreatedOn());
    assertEquals(mockCategoryEntity.getUpdatedOn(), result.getUpdatedOn());
    assertNull(result.getUserId()); // Should always be null for global categories
    assertNull(result.getSubcategories()); // Minimal response should not include subcategories
  }

  @Test
  @DisplayName("Should map entity to minimal response with parent")
  void shouldMapEntityToMinimalResponseWithParent() {
    // Given
    mockCategoryEntity.setParent(mockParentEntity);

    // When
    CategoryResponse result = categoryMapper.entityToResponseMinimal(mockCategoryEntity);

    // Then
    assertNotNull(result);
    assertNotNull(result.getParent());
    assertEquals(mockParentEntity.getId(), result.getParent().getId());
    assertEquals(mockParentEntity.getName(), result.getParent().getName());
    assertEquals(mockParentEntity.getColorCode(), result.getParent().getColorCode());
    assertEquals(mockParentEntity.getIconName(), result.getParent().getIconName());
    assertNull(result.getSubcategories()); // Minimal response should not include subcategories
  }

  // ========== DEPRECATED METHOD TESTS ==========

  @Test
  @DisplayName("Should handle deprecated createRequestToEntity method")
  void shouldHandleDeprecatedCreateRequestToEntityMethod() {
    // When
    ExpenseCategoryEntity result =
        categoryMapper.createRequestToEntity(mockCreateRequest, new Object(), mockParentEntity);

    // Then
    assertNotNull(result);
    assertEquals(mockCreateRequest.getName(), result.getName());
    assertEquals(mockCreateRequest.getDescription(), result.getDescription());
    assertEquals(mockCreateRequest.getColorCode(), result.getColorCode());
    assertEquals(mockCreateRequest.getIconName(), result.getIconName());
    assertEquals(mockCreateRequest.getSortOrder(), result.getSortOrder());
    assertTrue(result.getIsActive());
    assertEquals(mockParentEntity, result.getParent());
  }

  // ========== HELPER METHODS ==========

  private CategoryCreateRequest createMockCreateRequest() {
    CategoryCreateRequest request = new CategoryCreateRequest();
    request.setName("Food & Dining");
    request.setDescription("Restaurant meals and grocery expenses");
    request.setColorCode("#FF5722");
    request.setIconName("restaurant");
    request.setSortOrder(1);
    return request;
  }

  private ExpenseCategoryEntity createMockCategoryEntity() {
    ExpenseCategoryEntity entity = new ExpenseCategoryEntity();
    entity.setId(1L);
    entity.setName("Food & Dining");
    entity.setDescription("Restaurant meals and grocery expenses");
    entity.setColorCode("#FF5722");
    entity.setIconName("restaurant");
    entity.setSortOrder(1);
    entity.setIsActive(true);
    entity.setCreatedOn(LocalDateTime.now());
    entity.setUpdatedOn(LocalDateTime.now());
    return entity;
  }

  private ExpenseCategoryEntity createMockParentEntity() {
    ExpenseCategoryEntity entity = new ExpenseCategoryEntity();
    entity.setId(2L);
    entity.setName("Shopping");
    entity.setDescription("Retail purchases and consumer goods");
    entity.setColorCode("#9C27B0");
    entity.setIconName("shopping_bag");
    entity.setSortOrder(3);
    entity.setIsActive(true);
    entity.setCreatedOn(LocalDateTime.now());
    entity.setUpdatedOn(LocalDateTime.now());
    return entity;
  }

  private ExpenseCategoryEntity createMockSubcategoryEntity(boolean isActive) {
    ExpenseCategoryEntity entity = new ExpenseCategoryEntity();
    entity.setId(isActive ? 3L : 4L);
    entity.setName(isActive ? "Groceries" : "Inactive Category");
    entity.setDescription(
        isActive ? "Supermarket and grocery shopping" : "This category is inactive");
    entity.setColorCode("#FF5722");
    entity.setIconName("grocery_store");
    entity.setSortOrder(1);
    entity.setIsActive(isActive);
    entity.setCreatedOn(LocalDateTime.now());
    entity.setUpdatedOn(LocalDateTime.now());
    return entity;
  }
}
