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
import com.mycodethesaurus.financeinspector.service.CategoryService;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AdminCategoryController.class)
@DisplayName("AdminCategoryController Tests")
class AdminCategoryControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private CategoryService categoryService;

  @MockBean private Principal mockPrincipal;

  @Autowired private ObjectMapper objectMapper;

  private CategoryResponse mockCategoryResponse;
  private CategoryCreateRequest mockCreateRequest;
  private List<CategoryResponse> mockCategoryList;

  @BeforeEach
  void setUp() {
    mockCategoryResponse = createMockCategoryResponse();
    mockCreateRequest = createMockCreateRequest();
    mockCategoryList = Arrays.asList(mockCategoryResponse);

    // Setup mock principal to return "admin" as name
    when(mockPrincipal.getName()).thenReturn("admin");
  }

  // Note: AdminCategoryController doesn't have getAllCategories endpoint
  // It's focused on create, update, delete, and activate operations

  // ========== CREATE CATEGORY TESTS ==========

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
            post("/v1/admin/categories")
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
            post("/v1/admin/categories")
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
        .thenThrow(new DuplicateResourceException("Category name 'Food & Dining' already exists"));

    // When & Then
    mockMvc
        .perform(
            post("/v1/admin/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mockCreateRequest))
                .principal(mockPrincipal))
        .andExpect(status().isConflict());

    verify(categoryService).createGlobalCategory(any(CategoryCreateRequest.class), eq(adminUser));
  }

  // ========== UPDATE CATEGORY TESTS ==========

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
            put("/v1/admin/categories/{id}", categoryId)
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
            put("/v1/admin/categories/{id}", categoryId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mockCreateRequest))
                .principal(mockPrincipal))
        .andExpect(status().isNotFound());

    verify(categoryService)
        .updateGlobalCategory(eq(categoryId), any(CategoryCreateRequest.class), eq(adminUser));
  }

  // ========== DELETE CATEGORY TESTS ==========

  @Test
  @DisplayName("Should delete category successfully")
  void shouldDeleteCategorySuccessfully() throws Exception {
    // Given
    Long categoryId = 1L;
    String adminUser = "admin";
    doNothing().when(categoryService).deleteGlobalCategory(categoryId, adminUser);

    // When & Then
    mockMvc
        .perform(delete("/v1/admin/categories/{id}", categoryId).principal(mockPrincipal))
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
        .perform(delete("/v1/admin/categories/{id}", categoryId).principal(mockPrincipal))
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
        .perform(delete("/v1/admin/categories/{id}", categoryId).principal(mockPrincipal))
        .andExpect(status().isBadRequest());

    verify(categoryService).deleteGlobalCategory(categoryId, adminUser);
  }

  // ========== ACTIVATE CATEGORY TESTS ==========

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
        .perform(put("/v1/admin/categories/{id}/activate", categoryId).principal(mockPrincipal))
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
        .perform(put("/v1/admin/categories/{id}/activate", categoryId).principal(mockPrincipal))
        .andExpect(status().isNotFound());

    verify(categoryService).activateGlobalCategory(categoryId, adminUser);
  }

  // ========== VALIDATION TESTS ==========

  @Test
  @DisplayName("Should handle missing principal (authentication)")
  void shouldHandleMissingPrincipal() throws Exception {
    // When & Then
    mockMvc
        .perform(
            post("/v1/admin/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mockCreateRequest)))
        .andExpect(status().is5xxServerError()); // 500 when principal is null

    verifyNoInteractions(categoryService);
  }

  @Test
  @DisplayName("Should handle invalid ID parameter")
  void shouldHandleInvalidIdParameter() throws Exception {
    // When & Then
    mockMvc
        .perform(put("/v1/admin/categories/invalid/activate").principal(mockPrincipal))
        .andExpect(
            status()
                .isInternalServerError()); // Spring returns 500 when path variable conversion fails

    verifyNoInteractions(categoryService);
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
