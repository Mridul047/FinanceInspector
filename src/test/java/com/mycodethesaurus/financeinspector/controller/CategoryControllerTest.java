package com.mycodethesaurus.financeinspector.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycodethesaurus.financeinspector.dto.CategoryCreateRequest;
import com.mycodethesaurus.financeinspector.dto.CategoryResponse;
import com.mycodethesaurus.financeinspector.exception.DuplicateResourceException;
import com.mycodethesaurus.financeinspector.exception.ResourceNotFoundException;
import com.mycodethesaurus.financeinspector.exception.handler.CustomGlobalExceptionHandler;
import com.mycodethesaurus.financeinspector.service.CategoryService;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
@DisplayName("CategoryController Tests")
class CategoryControllerTest {

  private MockMvc mockMvc;

  @Mock private CategoryService categoryService;

  @Mock private Principal mockPrincipal;

  private ObjectMapper objectMapper;

  private CategoryResponse mockCategoryResponse;
  private CategoryCreateRequest mockCreateRequest;
  private List<CategoryResponse> mockCategoryList;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();
    mockMvc =
        MockMvcBuilders.standaloneSetup(new CategoryController(categoryService))
            .setControllerAdvice(new CustomGlobalExceptionHandler())
            .build();

    mockCategoryResponse = createMockCategoryResponse();
    mockCreateRequest = createMockCreateRequest();
    mockCategoryList = Arrays.asList(mockCategoryResponse);

    // Setup mock principal to return "admin" as name (using lenient to avoid
    // UnnecessaryStubbingException)
    lenient().when(mockPrincipal.getName()).thenReturn("admin");
  }

  // ========== PUBLIC CATEGORY OPERATIONS TESTS ==========

  @Nested
  @DisplayName("Public Category Operations")
  class PublicCategoryOperations {

    @Test
    @DisplayName("Should get all categories successfully")
    void shouldGetAllCategoriesSuccessfully() throws Exception {
      // Given
      when(categoryService.getAllGlobalCategories()).thenReturn(mockCategoryList);

      // When & Then
      mockMvc
          .perform(get("/v1/categories").accept(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$").isArray())
          .andExpect(jsonPath("$[0].id").value(1L))
          .andExpect(jsonPath("$[0].name").value("Food & Dining"))
          .andExpect(jsonPath("$[0].description").value("Restaurant meals and grocery expenses"))
          .andExpect(jsonPath("$[0].colorCode").value("#FF5722"))
          .andExpect(jsonPath("$[0].iconName").value("restaurant"))
          .andExpect(jsonPath("$[0].isActive").value(true))
          .andExpect(jsonPath("$[0].userId").doesNotExist());

      verify(categoryService).getAllGlobalCategories();
    }

    @Test
    @DisplayName("Should get category by ID successfully")
    void shouldGetCategoryByIdSuccessfully() throws Exception {
      // Given
      Long categoryId = 1L;
      when(categoryService.getGlobalCategoryById(categoryId)).thenReturn(mockCategoryResponse);

      // When & Then
      mockMvc
          .perform(get("/v1/categories/{id}", categoryId).accept(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.id").value(1L))
          .andExpect(jsonPath("$.name").value("Food & Dining"))
          .andExpect(jsonPath("$.description").value("Restaurant meals and grocery expenses"))
          .andExpect(jsonPath("$.colorCode").value("#FF5722"))
          .andExpect(jsonPath("$.iconName").value("restaurant"))
          .andExpect(jsonPath("$.isActive").value(true));

      verify(categoryService).getGlobalCategoryById(categoryId);
    }

    @Test
    @DisplayName("Should return 404 when category not found by ID")
    void shouldReturn404WhenCategoryNotFoundById() throws Exception {
      // Given
      Long categoryId = 999L;
      when(categoryService.getGlobalCategoryById(categoryId))
          .thenThrow(new ResourceNotFoundException("Category not found with id: " + categoryId));

      // When & Then
      mockMvc
          .perform(get("/v1/categories/{id}", categoryId).accept(MediaType.APPLICATION_JSON))
          .andExpect(status().isNotFound());

      verify(categoryService).getGlobalCategoryById(categoryId);
    }

    @Test
    @DisplayName("Should get top level categories successfully")
    void shouldGetTopLevelCategoriesSuccessfully() throws Exception {
      // Given
      when(categoryService.getGlobalTopLevelCategories()).thenReturn(mockCategoryList);

      // When & Then
      mockMvc
          .perform(get("/v1/categories/top-level").accept(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$").isArray())
          .andExpect(jsonPath("$[0].id").value(1L))
          .andExpect(jsonPath("$[0].name").value("Food & Dining"));

      verify(categoryService).getGlobalTopLevelCategories();
    }

    @Test
    @DisplayName("Should get subcategories successfully")
    void shouldGetSubcategoriesSuccessfully() throws Exception {
      // Given
      Long parentId = 1L;
      when(categoryService.getGlobalSubcategories(parentId)).thenReturn(mockCategoryList);

      // When & Then
      mockMvc
          .perform(
              get("/v1/categories/{id}/subcategories", parentId).accept(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$").isArray())
          .andExpect(jsonPath("$[0].id").value(1L));

      verify(categoryService).getGlobalSubcategories(parentId);
    }

    @Test
    @DisplayName("Should return 404 when parent category not found for subcategories")
    void shouldReturn404WhenParentCategoryNotFoundForSubcategories() throws Exception {
      // Given
      Long parentId = 999L;
      when(categoryService.getGlobalSubcategories(parentId))
          .thenThrow(
              new ResourceNotFoundException("Parent category not found with id: " + parentId));

      // When & Then
      mockMvc
          .perform(
              get("/v1/categories/{id}/subcategories", parentId).accept(MediaType.APPLICATION_JSON))
          .andExpect(status().isNotFound());

      verify(categoryService).getGlobalSubcategories(parentId);
    }

    @Test
    @DisplayName("Should search categories with query parameter")
    void shouldSearchCategoriesWithQueryParameter() throws Exception {
      // Given
      String searchQuery = "food";
      when(categoryService.searchGlobalCategories(searchQuery, false)).thenReturn(mockCategoryList);

      // When & Then
      mockMvc
          .perform(
              get("/v1/categories/search")
                  .param("query", searchQuery)
                  .accept(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$").isArray())
          .andExpect(jsonPath("$[0].id").value(1L))
          .andExpect(jsonPath("$[0].name").value("Food & Dining"));

      verify(categoryService).searchGlobalCategories(searchQuery, false);
    }

    @Test
    @DisplayName("Should search categories with parentOnly parameter")
    void shouldSearchCategoriesWithParentOnlyParameter() throws Exception {
      // Given
      String searchQuery = "food";
      when(categoryService.searchGlobalCategories(searchQuery, true)).thenReturn(mockCategoryList);

      // When & Then
      mockMvc
          .perform(
              get("/v1/categories/search")
                  .param("query", searchQuery)
                  .param("parentOnly", "true")
                  .accept(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$").isArray())
          .andExpect(jsonPath("$[0].id").value(1L));

      verify(categoryService).searchGlobalCategories(searchQuery, true);
    }

    @Test
    @DisplayName("Should handle empty search query")
    void shouldHandleEmptySearchQuery() throws Exception {
      // Given
      when(categoryService.searchGlobalCategories("", false)).thenReturn(new ArrayList<>());

      // When & Then
      mockMvc
          .perform(
              get("/v1/categories/search").param("query", "").accept(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk());

      verify(categoryService).searchGlobalCategories("", false);
    }
  }

  // ========== ADMIN CATEGORY OPERATIONS TESTS ==========

  @Nested
  @DisplayName("Admin Category Operations")
  class AdminCategoryOperations {

    @Test
    @DisplayName("Should create category successfully")
    void shouldCreateCategorySuccessfully() throws Exception {
      // Given
      String adminUser = "admin";
      when(categoryService.createGlobalCategory(any(CategoryCreateRequest.class), eq(adminUser)))
          .thenReturn(mockCategoryResponse);

      // When & Then
      mockMvc
          .perform(
              post("/v1/categories")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(mockCreateRequest))
                  .principal(mockPrincipal))
          .andExpect(status().isCreated())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.id").value(1L))
          .andExpect(jsonPath("$.name").value("Food & Dining"));

      verify(categoryService).createGlobalCategory(any(CategoryCreateRequest.class), eq(adminUser));
    }

    @Test
    @DisplayName("Should return 400 when creating category with invalid data")
    void shouldReturn400WhenCreatingCategoryWithInvalidData() throws Exception {
      // Given
      CategoryCreateRequest invalidRequest = new CategoryCreateRequest();
      // Leave name empty to trigger validation error

      // When & Then
      mockMvc
          .perform(
              post("/v1/categories")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(invalidRequest))
                  .principal(mockPrincipal))
          .andExpect(status().isBadRequest());

      verifyNoInteractions(categoryService);
    }

    @Test
    @DisplayName("Should return 409 when creating category with duplicate name")
    void shouldReturn409WhenCreatingCategoryWithDuplicateName() throws Exception {
      // Given
      String adminUser = "admin";
      when(categoryService.createGlobalCategory(any(CategoryCreateRequest.class), eq(adminUser)))
          .thenThrow(
              new DuplicateResourceException("Category name 'Food & Dining' already exists"));

      // When & Then
      mockMvc
          .perform(
              post("/v1/categories")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(mockCreateRequest))
                  .principal(mockPrincipal))
          .andExpect(status().isConflict());

      verify(categoryService).createGlobalCategory(any(CategoryCreateRequest.class), eq(adminUser));
    }

    @Test
    @DisplayName("Should update category successfully")
    void shouldUpdateCategorySuccessfully() throws Exception {
      // Given
      Long categoryId = 1L;
      String adminUser = "admin";
      when(categoryService.updateGlobalCategory(
              eq(categoryId), any(CategoryCreateRequest.class), eq(adminUser)))
          .thenReturn(mockCategoryResponse);

      // When & Then
      mockMvc
          .perform(
              put("/v1/categories/{id}", categoryId)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(mockCreateRequest))
                  .principal(mockPrincipal))
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.id").value(1L))
          .andExpect(jsonPath("$.name").value("Food & Dining"));

      verify(categoryService)
          .updateGlobalCategory(eq(categoryId), any(CategoryCreateRequest.class), eq(adminUser));
    }

    @Test
    @DisplayName("Should return 404 when updating non-existent category")
    void shouldReturn404WhenUpdatingNonExistentCategory() throws Exception {
      // Given
      Long categoryId = 999L;
      String adminUser = "admin";
      when(categoryService.updateGlobalCategory(
              eq(categoryId), any(CategoryCreateRequest.class), eq(adminUser)))
          .thenThrow(new ResourceNotFoundException("Category not found with id: " + categoryId));

      // When & Then
      mockMvc
          .perform(
              put("/v1/categories/{id}", categoryId)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(mockCreateRequest))
                  .principal(mockPrincipal))
          .andExpect(status().isNotFound());

      verify(categoryService)
          .updateGlobalCategory(eq(categoryId), any(CategoryCreateRequest.class), eq(adminUser));
    }

    @Test
    @DisplayName("Should delete category successfully")
    void shouldDeleteCategorySuccessfully() throws Exception {
      // Given
      Long categoryId = 1L;
      String adminUser = "admin";
      doNothing().when(categoryService).deleteGlobalCategory(categoryId, adminUser);

      // When & Then
      mockMvc
          .perform(delete("/v1/categories/{id}", categoryId).principal(mockPrincipal))
          .andExpect(status().isNoContent());

      verify(categoryService).deleteGlobalCategory(categoryId, adminUser);
    }

    @Test
    @DisplayName("Should return 404 when deleting non-existent category")
    void shouldReturn404WhenDeletingNonExistentCategory() throws Exception {
      // Given
      Long categoryId = 999L;
      String adminUser = "admin";
      doThrow(new ResourceNotFoundException("Category not found with id: " + categoryId))
          .when(categoryService)
          .deleteGlobalCategory(categoryId, adminUser);

      // When & Then
      mockMvc
          .perform(delete("/v1/categories/{id}", categoryId).principal(mockPrincipal))
          .andExpect(status().isNotFound());

      verify(categoryService).deleteGlobalCategory(categoryId, adminUser);
    }

    @Test
    @DisplayName("Should return 400 when deleting category with subcategories")
    void shouldReturn400WhenDeletingCategoryWithSubcategories() throws Exception {
      // Given
      Long categoryId = 1L;
      String adminUser = "admin";
      doThrow(new IllegalArgumentException("Cannot delete category with active subcategories"))
          .when(categoryService)
          .deleteGlobalCategory(categoryId, adminUser);

      // When & Then
      mockMvc
          .perform(delete("/v1/categories/{id}", categoryId).principal(mockPrincipal))
          .andExpect(status().isBadRequest());

      verify(categoryService).deleteGlobalCategory(categoryId, adminUser);
    }

    @Test
    @DisplayName("Should activate category successfully")
    void shouldActivateCategorySuccessfully() throws Exception {
      // Given
      Long categoryId = 1L;
      String adminUser = "admin";
      when(categoryService.activateGlobalCategory(categoryId, adminUser))
          .thenReturn(mockCategoryResponse);

      // When & Then
      mockMvc
          .perform(put("/v1/categories/{id}/activate", categoryId).principal(mockPrincipal))
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.id").value(1L))
          .andExpect(jsonPath("$.isActive").value(true));

      verify(categoryService).activateGlobalCategory(categoryId, adminUser);
    }

    @Test
    @DisplayName("Should return 404 when activating non-existent category")
    void shouldReturn404WhenActivatingNonExistentCategory() throws Exception {
      // Given
      Long categoryId = 999L;
      String adminUser = "admin";
      when(categoryService.activateGlobalCategory(categoryId, adminUser))
          .thenThrow(new ResourceNotFoundException("Category not found with id: " + categoryId));

      // When & Then
      mockMvc
          .perform(put("/v1/categories/{id}/activate", categoryId).principal(mockPrincipal))
          .andExpect(status().isNotFound());

      verify(categoryService).activateGlobalCategory(categoryId, adminUser);
    }

    @Test
    @DisplayName("Should handle missing principal (authentication)")
    void shouldHandleMissingPrincipal() throws Exception {
      // When & Then
      mockMvc
          .perform(
              post("/v1/categories")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(mockCreateRequest)))
          .andExpect(status().is5xxServerError()); // 500 when principal is null

      verifyNoInteractions(categoryService);
    }
  }

  // ========== HELPER METHODS ==========

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
    response.setSubcategories(new ArrayList<>());
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
