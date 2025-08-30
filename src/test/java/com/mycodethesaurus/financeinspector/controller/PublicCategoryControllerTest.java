package com.mycodethesaurus.financeinspector.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycodethesaurus.financeinspector.dto.CategoryResponse;
import com.mycodethesaurus.financeinspector.exception.ResourceNotFoundException;
import com.mycodethesaurus.financeinspector.service.CategoryService;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

@WebMvcTest(PublicCategoryController.class)
@DisplayName("PublicCategoryController Tests")
class PublicCategoryControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private CategoryService categoryService;

  @Autowired private ObjectMapper objectMapper;

  private CategoryResponse mockCategoryResponse;
  private List<CategoryResponse> mockCategoryList;

  @BeforeEach
  void setUp() {
    mockCategoryResponse = createMockCategoryResponse();
    mockCategoryList = Arrays.asList(mockCategoryResponse);
  }

  // ========== GET ALL CATEGORIES TESTS ==========

  @Test
  @DisplayName("Should get all categories successfully")
  void shouldGetAllCategoriesSuccessfully() throws Exception {
    // Given
    when(categoryService.getAllGlobalCategories()).thenReturn(mockCategoryList);

    // When & Then
    mockMvc
        .perform(get("/v1/public/categories").accept(MediaType.APPLICATION_JSON))
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

  // ========== SEARCH CATEGORIES TESTS ==========

  @Test
  @DisplayName("Should search categories with query parameter")
  void shouldSearchCategoriesWithQueryParameter() throws Exception {
    // Given
    String searchQuery = "food";
    when(categoryService.searchGlobalCategories(searchQuery, false)).thenReturn(mockCategoryList);

    // When & Then
    mockMvc
        .perform(
            get("/v1/public/categories/search")
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
            get("/v1/public/categories/search")
                .param("query", searchQuery)
                .param("parentOnly", "true")
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0].id").value(1L));

    verify(categoryService).searchGlobalCategories(searchQuery, true);
  }

  // ========== GET CATEGORY BY ID TESTS ==========

  @Test
  @DisplayName("Should get category by ID successfully")
  void shouldGetCategoryByIdSuccessfully() throws Exception {
    // Given
    Long categoryId = 1L;
    when(categoryService.getGlobalCategoryById(categoryId)).thenReturn(mockCategoryResponse);

    // When & Then
    mockMvc
        .perform(get("/v1/public/categories/{id}", categoryId).accept(MediaType.APPLICATION_JSON))
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
        .perform(get("/v1/public/categories/{id}", categoryId).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());

    verify(categoryService).getGlobalCategoryById(categoryId);
  }

  // ========== GET TOP LEVEL CATEGORIES TESTS ==========

  @Test
  @DisplayName("Should get top level categories successfully")
  void shouldGetTopLevelCategoriesSuccessfully() throws Exception {
    // Given
    when(categoryService.getGlobalTopLevelCategories()).thenReturn(mockCategoryList);

    // When & Then
    mockMvc
        .perform(get("/v1/public/categories/top-level").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0].id").value(1L))
        .andExpect(jsonPath("$[0].name").value("Food & Dining"));

    verify(categoryService).getGlobalTopLevelCategories();
  }

  // ========== GET SUBCATEGORIES TESTS ==========

  @Test
  @DisplayName("Should get subcategories successfully")
  void shouldGetSubcategoriesSuccessfully() throws Exception {
    // Given
    Long parentId = 1L;
    when(categoryService.getGlobalSubcategories(parentId)).thenReturn(mockCategoryList);

    // When & Then
    mockMvc
        .perform(
            get("/v1/public/categories/{id}/subcategories", parentId)
                .accept(MediaType.APPLICATION_JSON))
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
        .thenThrow(new ResourceNotFoundException("Parent category not found with id: " + parentId));

    // When & Then
    mockMvc
        .perform(
            get("/v1/public/categories/{id}/subcategories", parentId)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());

    verify(categoryService).getGlobalSubcategories(parentId);
  }

  // ========== VALIDATION TESTS ==========

  @Test
  @DisplayName("Should handle invalid ID parameter")
  void shouldHandleInvalidIdParameter() throws Exception {
    // When & Then
    mockMvc
        .perform(get("/v1/public/categories/invalid").accept(MediaType.APPLICATION_JSON))
        .andExpect(
            status()
                .isInternalServerError()); // Spring returns 500 when path variable conversion fails

    verifyNoInteractions(categoryService);
  }

  @Test
  @DisplayName("Should handle empty search query")
  void shouldHandleEmptySearchQuery() throws Exception {
    // Given
    when(categoryService.searchGlobalCategories("", false)).thenReturn(new ArrayList<>());

    // When & Then
    mockMvc
        .perform(
            get("/v1/public/categories/search")
                .param("query", "")
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

    verify(categoryService).searchGlobalCategories("", false);
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
}
