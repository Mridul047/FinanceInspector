package com.mycodethesaurus.financeinspector.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.mycodethesaurus.financeinspector.component.CategoryMapper;
import com.mycodethesaurus.financeinspector.dto.CategoryCreateRequest;
import com.mycodethesaurus.financeinspector.dto.CategoryResponse;
import com.mycodethesaurus.financeinspector.exception.DuplicateResourceException;
import com.mycodethesaurus.financeinspector.exception.ResourceNotFoundException;
import com.mycodethesaurus.financeinspector.persistence.entity.ExpenseCategoryEntity;
import com.mycodethesaurus.financeinspector.persistence.repository.ExpenseCategoryRepository;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("CategoryService Tests")
class CategoryServiceTest {

  @Mock private ExpenseCategoryRepository categoryRepository;

  @Mock private CategoryMapper categoryMapper;

  @InjectMocks private CategoryService categoryService;

  private ExpenseCategoryEntity mockCategoryEntity;
  private CategoryResponse mockCategoryResponse;
  private CategoryCreateRequest mockCreateRequest;

  @BeforeEach
  void setUp() {
    mockCategoryEntity = createMockCategoryEntity();
    mockCategoryResponse = createMockCategoryResponse();
    mockCreateRequest = createMockCreateRequest();
  }

  // ========== GET ALL GLOBAL CATEGORIES TESTS ==========

  @Test
  @DisplayName("Should get all global categories successfully")
  void shouldGetAllGlobalCategoriesSuccessfully() {
    // Given
    List<ExpenseCategoryEntity> mockEntities = Arrays.asList(mockCategoryEntity);
    List<CategoryResponse> expectedResponses = Arrays.asList(mockCategoryResponse);

    when(categoryRepository.findAllActiveCategories()).thenReturn(mockEntities);
    when(categoryMapper.entitiesToResponses(mockEntities)).thenReturn(expectedResponses);

    // When
    List<CategoryResponse> result = categoryService.getAllGlobalCategories();

    // Then
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(expectedResponses, result);
    verify(categoryRepository).findAllActiveCategories();
    verify(categoryMapper).entitiesToResponses(mockEntities);
  }

  // ========== GET GLOBAL CATEGORY BY ID TESTS ==========

  @Test
  @DisplayName("Should get global category by ID successfully")
  void shouldGetGlobalCategoryByIdSuccessfully() {
    // Given
    Long categoryId = 1L;
    when(categoryRepository.findActiveCategoryById(categoryId))
        .thenReturn(Optional.of(mockCategoryEntity));
    when(categoryMapper.entityToResponse(mockCategoryEntity)).thenReturn(mockCategoryResponse);

    // When
    CategoryResponse result = categoryService.getGlobalCategoryById(categoryId);

    // Then
    assertNotNull(result);
    assertEquals(mockCategoryResponse, result);
    verify(categoryRepository).findActiveCategoryById(categoryId);
    verify(categoryMapper).entityToResponse(mockCategoryEntity);
  }

  @Test
  @DisplayName("Should throw ResourceNotFoundException when category not found by ID")
  void shouldThrowResourceNotFoundExceptionWhenCategoryNotFoundById() {
    // Given
    Long categoryId = 999L;
    when(categoryRepository.findActiveCategoryById(categoryId)).thenReturn(Optional.empty());

    // When & Then
    ResourceNotFoundException exception =
        assertThrows(
            ResourceNotFoundException.class,
            () -> categoryService.getGlobalCategoryById(categoryId));

    assertEquals("Category not found with id: " + categoryId, exception.getMessage());
    verify(categoryRepository).findActiveCategoryById(categoryId);
    verifyNoInteractions(categoryMapper);
  }

  // ========== GET TOP LEVEL CATEGORIES TESTS ==========

  @Test
  @DisplayName("Should get global top level categories successfully")
  void shouldGetGlobalTopLevelCategoriesSuccessfully() {
    // Given
    List<ExpenseCategoryEntity> mockEntities = Arrays.asList(mockCategoryEntity);
    List<CategoryResponse> expectedResponses = Arrays.asList(mockCategoryResponse);

    when(categoryRepository.findAllRootCategories()).thenReturn(mockEntities);
    when(categoryMapper.entitiesToResponses(mockEntities)).thenReturn(expectedResponses);

    // When
    List<CategoryResponse> result = categoryService.getGlobalTopLevelCategories();

    // Then
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(expectedResponses, result);
    verify(categoryRepository).findAllRootCategories();
    verify(categoryMapper).entitiesToResponses(mockEntities);
  }

  // ========== GET SUBCATEGORIES TESTS ==========

  @Test
  @DisplayName("Should get global subcategories successfully")
  void shouldGetGlobalSubcategoriesSuccessfully() {
    // Given
    Long parentId = 1L;
    List<ExpenseCategoryEntity> mockSubcategories = Arrays.asList(mockCategoryEntity);
    List<CategoryResponse> expectedResponses = Arrays.asList(mockCategoryResponse);

    when(categoryRepository.findActiveCategoryById(parentId))
        .thenReturn(Optional.of(mockCategoryEntity));
    when(categoryRepository.findSubcategoriesByParentId(parentId)).thenReturn(mockSubcategories);
    when(categoryMapper.entitiesToResponses(mockSubcategories)).thenReturn(expectedResponses);

    // When
    List<CategoryResponse> result = categoryService.getGlobalSubcategories(parentId);

    // Then
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(expectedResponses, result);
    verify(categoryRepository).findActiveCategoryById(parentId);
    verify(categoryRepository).findSubcategoriesByParentId(parentId);
    verify(categoryMapper).entitiesToResponses(mockSubcategories);
  }

  @Test
  @DisplayName("Should throw ResourceNotFoundException when parent category not found")
  void shouldThrowResourceNotFoundExceptionWhenParentCategoryNotFound() {
    // Given
    Long parentId = 999L;
    when(categoryRepository.findActiveCategoryById(parentId)).thenReturn(Optional.empty());

    // When & Then
    ResourceNotFoundException exception =
        assertThrows(
            ResourceNotFoundException.class,
            () -> categoryService.getGlobalSubcategories(parentId));

    assertEquals("Parent category not found with id: " + parentId, exception.getMessage());
    verify(categoryRepository).findActiveCategoryById(parentId);
    verify(categoryRepository, never()).findSubcategoriesByParentId(any());
    verifyNoInteractions(categoryMapper);
  }

  // ========== SEARCH CATEGORIES TESTS ==========

  @Test
  @DisplayName("Should search global categories successfully")
  void shouldSearchGlobalCategoriesSuccessfully() {
    // Given
    String query = "food";
    Boolean parentOnly = false;
    List<ExpenseCategoryEntity> mockEntities = Arrays.asList(mockCategoryEntity);
    List<CategoryResponse> expectedResponses = Arrays.asList(mockCategoryResponse);

    when(categoryRepository.searchByNameOrDescription(query)).thenReturn(mockEntities);
    when(categoryMapper.entitiesToResponses(mockEntities)).thenReturn(expectedResponses);

    // When
    List<CategoryResponse> result = categoryService.searchGlobalCategories(query, parentOnly);

    // Then
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(expectedResponses, result);
    verify(categoryRepository).searchByNameOrDescription(query);
    verify(categoryMapper).entitiesToResponses(mockEntities);
  }

  @Test
  @DisplayName("Should search global top-level categories only when parentOnly is true")
  void shouldSearchGlobalTopLevelCategoriesOnlyWhenParentOnlyIsTrue() {
    // Given
    String query = "food";
    Boolean parentOnly = true;
    List<ExpenseCategoryEntity> mockEntities = Arrays.asList(mockCategoryEntity);
    List<CategoryResponse> expectedResponses = Arrays.asList(mockCategoryResponse);

    when(categoryRepository.searchTopLevelByNameOrDescription(query)).thenReturn(mockEntities);
    when(categoryMapper.entitiesToResponses(mockEntities)).thenReturn(expectedResponses);

    // When
    List<CategoryResponse> result = categoryService.searchGlobalCategories(query, parentOnly);

    // Then
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(expectedResponses, result);
    verify(categoryRepository).searchTopLevelByNameOrDescription(query);
    verify(categoryMapper).entitiesToResponses(mockEntities);
  }

  // ========== CREATE GLOBAL CATEGORY TESTS ==========

  @Test
  @DisplayName("Should create global category successfully")
  void shouldCreateGlobalCategorySuccessfully() {
    // Given
    String adminUser = "admin";
    ExpenseCategoryEntity savedEntity = createMockCategoryEntity();
    savedEntity.setCreatedBy(adminUser);
    savedEntity.setUpdatedBy(adminUser);

    when(categoryRepository.existsByNameIgnoreCase(mockCreateRequest.getName())).thenReturn(false);
    when(categoryMapper.createRequestToGlobalEntity(mockCreateRequest, null))
        .thenReturn(mockCategoryEntity);
    when(categoryRepository.save(any(ExpenseCategoryEntity.class))).thenReturn(savedEntity);
    when(categoryMapper.entityToResponse(savedEntity)).thenReturn(mockCategoryResponse);

    // When
    CategoryResponse result = categoryService.createGlobalCategory(mockCreateRequest, adminUser);

    // Then
    assertNotNull(result);
    assertEquals(mockCategoryResponse, result);
    verify(categoryRepository).existsByNameIgnoreCase(mockCreateRequest.getName());
    verify(categoryMapper).createRequestToGlobalEntity(mockCreateRequest, null);
    verify(categoryRepository).save(any(ExpenseCategoryEntity.class));
    verify(categoryMapper).entityToResponse(savedEntity);
  }

  @Test
  @DisplayName("Should throw DuplicateResourceException when creating category with existing name")
  void shouldThrowDuplicateResourceExceptionWhenCreatingCategoryWithExistingName() {
    // Given
    String adminUser = "admin";
    when(categoryRepository.existsByNameIgnoreCase(mockCreateRequest.getName())).thenReturn(true);

    // When & Then
    DuplicateResourceException exception =
        assertThrows(
            DuplicateResourceException.class,
            () -> categoryService.createGlobalCategory(mockCreateRequest, adminUser));

    assertEquals(
        "Category name '" + mockCreateRequest.getName() + "' already exists",
        exception.getMessage());
    verify(categoryRepository).existsByNameIgnoreCase(mockCreateRequest.getName());
    verifyNoInteractions(categoryMapper);
    verify(categoryRepository, never()).save(any());
  }

  @Test
  @DisplayName("Should create global category with parent successfully")
  void shouldCreateGlobalCategoryWithParentSuccessfully() {
    // Given
    String adminUser = "admin";
    Long parentId = 2L;
    mockCreateRequest.setParentId(parentId);

    ExpenseCategoryEntity parentEntity = createMockCategoryEntity();
    parentEntity.setId(parentId);

    ExpenseCategoryEntity savedEntity = createMockCategoryEntity();
    savedEntity.setCreatedBy(adminUser);
    savedEntity.setUpdatedBy(adminUser);

    when(categoryRepository.existsByNameIgnoreCase(mockCreateRequest.getName())).thenReturn(false);
    when(categoryRepository.findActiveCategoryById(parentId)).thenReturn(Optional.of(parentEntity));
    when(categoryMapper.createRequestToGlobalEntity(mockCreateRequest, parentEntity))
        .thenReturn(mockCategoryEntity);
    when(categoryRepository.save(any(ExpenseCategoryEntity.class))).thenReturn(savedEntity);
    when(categoryMapper.entityToResponse(savedEntity)).thenReturn(mockCategoryResponse);

    // When
    CategoryResponse result = categoryService.createGlobalCategory(mockCreateRequest, adminUser);

    // Then
    assertNotNull(result);
    assertEquals(mockCategoryResponse, result);
    verify(categoryRepository).existsByNameIgnoreCase(mockCreateRequest.getName());
    verify(categoryRepository).findActiveCategoryById(parentId);
    verify(categoryMapper).createRequestToGlobalEntity(mockCreateRequest, parentEntity);
    verify(categoryRepository).save(any(ExpenseCategoryEntity.class));
    verify(categoryMapper).entityToResponse(savedEntity);
  }

  @Test
  @DisplayName(
      "Should throw ResourceNotFoundException when creating category with non-existent parent")
  void shouldThrowResourceNotFoundExceptionWhenCreatingCategoryWithNonExistentParent() {
    // Given
    String adminUser = "admin";
    Long parentId = 999L;
    mockCreateRequest.setParentId(parentId);

    when(categoryRepository.existsByNameIgnoreCase(mockCreateRequest.getName())).thenReturn(false);
    when(categoryRepository.findActiveCategoryById(parentId)).thenReturn(Optional.empty());

    // When & Then
    ResourceNotFoundException exception =
        assertThrows(
            ResourceNotFoundException.class,
            () -> categoryService.createGlobalCategory(mockCreateRequest, adminUser));

    assertEquals("Parent category not found with id: " + parentId, exception.getMessage());
    verify(categoryRepository).existsByNameIgnoreCase(mockCreateRequest.getName());
    verify(categoryRepository).findActiveCategoryById(parentId);
    verify(categoryRepository, never()).save(any());
    verifyNoInteractions(categoryMapper);
  }

  // ========== UPDATE GLOBAL CATEGORY TESTS ==========

  @Test
  @DisplayName("Should update global category successfully")
  void shouldUpdateGlobalCategorySuccessfully() {
    // Given
    Long categoryId = 1L;
    String adminUser = "admin";
    ExpenseCategoryEntity updatedEntity = createMockCategoryEntity();
    updatedEntity.setUpdatedBy(adminUser);

    when(categoryRepository.findActiveCategoryById(categoryId))
        .thenReturn(Optional.of(mockCategoryEntity));
    when(categoryRepository.findByNameIgnoreCase(mockCreateRequest.getName()))
        .thenReturn(Optional.empty());
    when(categoryRepository.save(any(ExpenseCategoryEntity.class))).thenReturn(updatedEntity);
    when(categoryMapper.entityToResponse(updatedEntity)).thenReturn(mockCategoryResponse);

    // When
    CategoryResponse result =
        categoryService.updateGlobalCategory(categoryId, mockCreateRequest, adminUser);

    // Then
    assertNotNull(result);
    assertEquals(mockCategoryResponse, result);
    verify(categoryRepository).findActiveCategoryById(categoryId);
    verify(categoryRepository).findByNameIgnoreCase(mockCreateRequest.getName());
    verify(categoryRepository).save(any(ExpenseCategoryEntity.class));
    verify(categoryMapper).entityToResponse(updatedEntity);
  }

  @Test
  @DisplayName("Should throw ResourceNotFoundException when updating non-existent category")
  void shouldThrowResourceNotFoundExceptionWhenUpdatingNonExistentCategory() {
    // Given
    Long categoryId = 999L;
    String adminUser = "admin";
    when(categoryRepository.findActiveCategoryById(categoryId)).thenReturn(Optional.empty());

    // When & Then
    ResourceNotFoundException exception =
        assertThrows(
            ResourceNotFoundException.class,
            () -> categoryService.updateGlobalCategory(categoryId, mockCreateRequest, adminUser));

    assertEquals("Category not found with id: " + categoryId, exception.getMessage());
    verify(categoryRepository).findActiveCategoryById(categoryId);
    verify(categoryRepository, never()).save(any());
    verifyNoInteractions(categoryMapper);
  }

  @Test
  @DisplayName("Should update global category with parent successfully")
  void shouldUpdateGlobalCategoryWithParentSuccessfully() {
    // Given
    Long categoryId = 1L;
    Long parentId = 2L;
    String adminUser = "admin";
    mockCreateRequest.setParentId(parentId);

    ExpenseCategoryEntity parentEntity = createMockCategoryEntity();
    parentEntity.setId(parentId);

    ExpenseCategoryEntity updatedEntity = createMockCategoryEntity();
    updatedEntity.setUpdatedBy(adminUser);

    when(categoryRepository.findActiveCategoryById(categoryId))
        .thenReturn(Optional.of(mockCategoryEntity));
    when(categoryRepository.findActiveCategoryById(parentId)).thenReturn(Optional.of(parentEntity));
    when(categoryRepository.findByNameIgnoreCase(mockCreateRequest.getName()))
        .thenReturn(Optional.empty());
    when(categoryRepository.save(any(ExpenseCategoryEntity.class))).thenReturn(updatedEntity);
    when(categoryMapper.entityToResponse(updatedEntity)).thenReturn(mockCategoryResponse);

    // When
    CategoryResponse result =
        categoryService.updateGlobalCategory(categoryId, mockCreateRequest, adminUser);

    // Then
    assertNotNull(result);
    assertEquals(mockCategoryResponse, result);
    verify(categoryRepository).findActiveCategoryById(categoryId);
    verify(categoryRepository).findActiveCategoryById(parentId);
    verify(categoryRepository).findByNameIgnoreCase(mockCreateRequest.getName());
    verify(categoryRepository).save(any(ExpenseCategoryEntity.class));
    verify(categoryMapper).entityToResponse(updatedEntity);
  }

  @Test
  @DisplayName("Should throw ResourceNotFoundException when updating with non-existent parent")
  void shouldThrowResourceNotFoundExceptionWhenUpdatingWithNonExistentParent() {
    // Given
    Long categoryId = 1L;
    Long parentId = 999L;
    String adminUser = "admin";
    mockCreateRequest.setParentId(parentId);

    when(categoryRepository.findActiveCategoryById(categoryId))
        .thenReturn(Optional.of(mockCategoryEntity));
    when(categoryRepository.findActiveCategoryById(parentId)).thenReturn(Optional.empty());

    // When & Then
    ResourceNotFoundException exception =
        assertThrows(
            ResourceNotFoundException.class,
            () -> categoryService.updateGlobalCategory(categoryId, mockCreateRequest, adminUser));

    assertEquals("Parent category not found with id: " + parentId, exception.getMessage());
    verify(categoryRepository).findActiveCategoryById(categoryId);
    verify(categoryRepository).findActiveCategoryById(parentId);
    verify(categoryRepository, never()).save(any());
    verifyNoInteractions(categoryMapper);
  }

  @Test
  @DisplayName("Should throw IllegalArgumentException when category tries to be its own parent")
  void shouldThrowIllegalArgumentExceptionWhenCategoryTriesToBeItsOwnParent() {
    // Given
    Long categoryId = 1L;
    String adminUser = "admin";
    mockCreateRequest.setParentId(categoryId); // Same as categoryId

    when(categoryRepository.findActiveCategoryById(categoryId))
        .thenReturn(Optional.of(mockCategoryEntity));

    // When & Then
    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> categoryService.updateGlobalCategory(categoryId, mockCreateRequest, adminUser));

    assertEquals("Category cannot be its own parent", exception.getMessage());
    // Called twice - once for the category and once for the parent (same ID)
    verify(categoryRepository, times(2)).findActiveCategoryById(categoryId);
    verify(categoryRepository, never()).save(any());
    verifyNoInteractions(categoryMapper);
  }

  @Test
  @DisplayName("Should throw IllegalArgumentException when update would create cyclic reference")
  void shouldThrowIllegalArgumentExceptionWhenUpdateWouldCreateCyclicReference() {
    // Given
    Long categoryId = 1L;
    Long parentId = 2L;
    String adminUser = "admin";
    mockCreateRequest.setParentId(parentId);

    // Create a scenario where parent category has our category as its parent (cycle)
    ExpenseCategoryEntity parentEntity = createMockCategoryEntity();
    parentEntity.setId(parentId);
    parentEntity.setParent(
        mockCategoryEntity); // Parent's parent is the category we're trying to update

    when(categoryRepository.findActiveCategoryById(categoryId))
        .thenReturn(Optional.of(mockCategoryEntity));
    when(categoryRepository.findActiveCategoryById(parentId)).thenReturn(Optional.of(parentEntity));

    // When & Then
    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> categoryService.updateGlobalCategory(categoryId, mockCreateRequest, adminUser));

    assertEquals("Moving category would create a circular reference", exception.getMessage());
    verify(categoryRepository).findActiveCategoryById(categoryId);
    verify(categoryRepository).findActiveCategoryById(parentId);
    verify(categoryRepository, never()).save(any());
    verifyNoInteractions(categoryMapper);
  }

  @Test
  @DisplayName("Should throw DuplicateResourceException when updating with existing name")
  void shouldThrowDuplicateResourceExceptionWhenUpdatingWithExistingName() {
    // Given
    Long categoryId = 1L;
    Long existingCategoryId = 2L;
    String adminUser = "admin";

    ExpenseCategoryEntity existingCategory = createMockCategoryEntity();
    existingCategory.setId(existingCategoryId);

    when(categoryRepository.findActiveCategoryById(categoryId))
        .thenReturn(Optional.of(mockCategoryEntity));
    when(categoryRepository.findByNameIgnoreCase(mockCreateRequest.getName()))
        .thenReturn(Optional.of(existingCategory));

    // When & Then
    DuplicateResourceException exception =
        assertThrows(
            DuplicateResourceException.class,
            () -> categoryService.updateGlobalCategory(categoryId, mockCreateRequest, adminUser));

    assertEquals(
        "Category name '" + mockCreateRequest.getName() + "' already exists",
        exception.getMessage());
    verify(categoryRepository).findActiveCategoryById(categoryId);
    verify(categoryRepository).findByNameIgnoreCase(mockCreateRequest.getName());
    verify(categoryRepository, never()).save(any());
    verifyNoInteractions(categoryMapper);
  }

  // ========== DELETE GLOBAL CATEGORY TESTS ==========

  @Test
  @DisplayName("Should hard delete global category when no expenses")
  void shouldHardDeleteGlobalCategoryWhenNoExpenses() {
    // Given
    Long categoryId = 1L;
    String adminUser = "admin";

    when(categoryRepository.findActiveCategoryById(categoryId))
        .thenReturn(Optional.of(mockCategoryEntity));
    when(categoryRepository.findSubcategoriesByParentId(categoryId)).thenReturn(Arrays.asList());
    when(categoryRepository.hasExpenses(categoryId)).thenReturn(false);

    // When
    categoryService.deleteGlobalCategory(categoryId, adminUser);

    // Then
    verify(categoryRepository).findActiveCategoryById(categoryId);
    verify(categoryRepository).findSubcategoriesByParentId(categoryId);
    verify(categoryRepository).hasExpenses(categoryId);
    verify(categoryRepository).delete(mockCategoryEntity);
    verify(categoryRepository, never()).save(any());
  }

  @Test
  @DisplayName("Should soft delete global category when has expenses")
  void shouldSoftDeleteGlobalCategoryWhenHasExpenses() {
    // Given
    Long categoryId = 1L;
    String adminUser = "admin";

    when(categoryRepository.findActiveCategoryById(categoryId))
        .thenReturn(Optional.of(mockCategoryEntity));
    when(categoryRepository.findSubcategoriesByParentId(categoryId)).thenReturn(Arrays.asList());
    when(categoryRepository.hasExpenses(categoryId)).thenReturn(true);
    when(categoryRepository.save(any(ExpenseCategoryEntity.class))).thenReturn(mockCategoryEntity);

    // When
    categoryService.deleteGlobalCategory(categoryId, adminUser);

    // Then
    verify(categoryRepository).findActiveCategoryById(categoryId);
    verify(categoryRepository).findSubcategoriesByParentId(categoryId);
    verify(categoryRepository).hasExpenses(categoryId);
    verify(categoryRepository).save(any(ExpenseCategoryEntity.class));
    verify(categoryRepository, never()).delete(any());
  }

  @Test
  @DisplayName("Should throw IllegalArgumentException when deleting category with subcategories")
  void shouldThrowIllegalArgumentExceptionWhenDeletingCategoryWithSubcategories() {
    // Given
    Long categoryId = 1L;
    String adminUser = "admin";
    List<ExpenseCategoryEntity> subcategories = Arrays.asList(createMockCategoryEntity());

    when(categoryRepository.findActiveCategoryById(categoryId))
        .thenReturn(Optional.of(mockCategoryEntity));
    when(categoryRepository.findSubcategoriesByParentId(categoryId)).thenReturn(subcategories);

    // When & Then
    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> categoryService.deleteGlobalCategory(categoryId, adminUser));

    assertEquals("Cannot delete category with active subcategories", exception.getMessage());
    verify(categoryRepository).findActiveCategoryById(categoryId);
    verify(categoryRepository).findSubcategoriesByParentId(categoryId);
    verify(categoryRepository, never()).hasExpenses(any());
    verify(categoryRepository, never()).delete(any());
    verify(categoryRepository, never()).save(any());
  }

  // ========== ACTIVATE GLOBAL CATEGORY TESTS ==========

  @Test
  @DisplayName("Should activate global category successfully")
  void shouldActivateGlobalCategorySuccessfully() {
    // Given
    Long categoryId = 1L;
    String adminUser = "admin";
    ExpenseCategoryEntity inactiveEntity = createMockCategoryEntity();
    inactiveEntity.setIsActive(false);
    ExpenseCategoryEntity activatedEntity = createMockCategoryEntity();
    activatedEntity.setIsActive(true);
    activatedEntity.setUpdatedBy(adminUser);

    when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(inactiveEntity));
    when(categoryRepository.save(any(ExpenseCategoryEntity.class))).thenReturn(activatedEntity);
    when(categoryMapper.entityToResponse(activatedEntity)).thenReturn(mockCategoryResponse);

    // When
    CategoryResponse result = categoryService.activateGlobalCategory(categoryId, adminUser);

    // Then
    assertNotNull(result);
    assertEquals(mockCategoryResponse, result);
    verify(categoryRepository).findById(categoryId);
    verify(categoryRepository).save(any(ExpenseCategoryEntity.class));
    verify(categoryMapper).entityToResponse(activatedEntity);
  }

  @Test
  @DisplayName("Should throw ResourceNotFoundException when activating non-existent category")
  void shouldThrowResourceNotFoundExceptionWhenActivatingNonExistentCategory() {
    // Given
    Long categoryId = 999L;
    String adminUser = "admin";
    when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

    // When & Then
    ResourceNotFoundException exception =
        assertThrows(
            ResourceNotFoundException.class,
            () -> categoryService.activateGlobalCategory(categoryId, adminUser));

    assertEquals("Category not found with id: " + categoryId, exception.getMessage());
    verify(categoryRepository).findById(categoryId);
    verify(categoryRepository, never()).save(any());
    verifyNoInteractions(categoryMapper);
  }

  // ========== HELPER METHODS ==========

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

  private CategoryResponse createMockCategoryResponse() {
    CategoryResponse response = new CategoryResponse();
    response.setId(1L);
    response.setName("Food & Dining");
    response.setDescription("Restaurant meals and grocery expenses");
    response.setColorCode("#FF5722");
    response.setIconName("restaurant");
    response.setSortOrder(1);
    response.setIsActive(true);
    response.setUserId(null);
    response.setCreatedOn(LocalDateTime.now());
    response.setUpdatedOn(LocalDateTime.now());
    return response;
  }

  private CategoryCreateRequest createMockCreateRequest() {
    CategoryCreateRequest request = new CategoryCreateRequest();
    request.setName("Food & Dining");
    request.setDescription("Restaurant meals and grocery expenses");
    request.setColorCode("#FF5722");
    request.setIconName("restaurant");
    request.setSortOrder(1);
    return request;
  }
}
