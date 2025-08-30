package com.mycodethesaurus.financeinspector.integration;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycodethesaurus.financeinspector.dto.CategoryCreateRequest;
import com.mycodethesaurus.financeinspector.dto.CategoryResponse;
import java.security.Principal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("integration-test")
@Sql(scripts = "/integration-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Transactional
@DisplayName("Global Category Integration Tests")
class GlobalCategoryIntegrationTest {

  @Autowired private WebApplicationContext webApplicationContext;

  @Autowired private ObjectMapper objectMapper;

  private MockMvc mockMvc;
  private Principal mockPrincipal;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    mockPrincipal = Mockito.mock(Principal.class);
    Mockito.when(mockPrincipal.getName()).thenReturn("integration-test-admin");
  }

  // ========== PUBLIC CATEGORY API TESTS ==========

  @Test
  @DisplayName("Should get all global categories from public API")
  void shouldGetAllGlobalCategoriesFromPublicApi() throws Exception {
    // When & Then
    MvcResult result =
        mockMvc
            .perform(get("/v1/public/categories").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray())
            .andReturn();

    // Verify we have the expected default categories
    String responseContent = result.getResponse().getContentAsString();
    CategoryResponse[] categories =
        objectMapper.readValue(responseContent, CategoryResponse[].class);

    assertTrue(categories.length >= 5); // Should have at least 5 top-level categories

    // Verify we have the expected top-level categories
    List<String> categoryNames =
        List.of(categories).stream().map(CategoryResponse::getName).toList();

    assertTrue(categoryNames.contains("Food & Dining"));
    assertTrue(categoryNames.contains("Transportation"));
    assertTrue(categoryNames.contains("Shopping"));
    assertTrue(categoryNames.contains("Bills & Utilities"));
    assertTrue(categoryNames.contains("Healthcare"));
  }

  @Test
  @DisplayName("Should get top-level categories from public API")
  void shouldGetTopLevelCategoriesFromPublicApi() throws Exception {
    // When & Then
    MvcResult result =
        mockMvc
            .perform(get("/v1/public/categories/top-level").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray())
            .andReturn();

    // Verify we have exactly 5 top-level categories
    String responseContent = result.getResponse().getContentAsString();
    CategoryResponse[] categories =
        objectMapper.readValue(responseContent, CategoryResponse[].class);

    assertEquals(5, categories.length); // Should have exactly 5 top-level categories

    // Verify all are top-level (no parent)
    for (CategoryResponse category : categories) {
      assertNull(category.getParent());
      assertNotNull(category.getSubcategories());
    }
  }

  @Test
  @DisplayName("Should get specific category by ID from public API")
  void shouldGetSpecificCategoryByIdFromPublicApi() throws Exception {
    // First, get all categories to find a valid ID
    MvcResult categoriesResult =
        mockMvc
            .perform(get("/v1/public/categories").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

    String categoriesContent = categoriesResult.getResponse().getContentAsString();
    CategoryResponse[] categories =
        objectMapper.readValue(categoriesContent, CategoryResponse[].class);
    assertTrue(categories.length > 0);

    Long categoryId = categories[0].getId();

    // When & Then
    mockMvc
        .perform(get("/v1/public/categories/{id}", categoryId).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(categoryId))
        .andExpect(jsonPath("$.name").exists())
        .andExpect(jsonPath("$.description").exists())
        .andExpect(jsonPath("$.colorCode").exists())
        .andExpect(jsonPath("$.iconName").exists())
        .andExpect(jsonPath("$.isActive").value(true))
        .andExpect(jsonPath("$.userId").doesNotExist());
  }

  @Test
  @DisplayName("Should return 404 for non-existent category from public API")
  void shouldReturn404ForNonExistentCategoryFromPublicApi() throws Exception {
    // When & Then
    mockMvc
        .perform(get("/v1/public/categories/99999").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Should search categories from public API")
  void shouldSearchCategoriesFromPublicApi() throws Exception {
    // When & Then
    MvcResult result =
        mockMvc
            .perform(
                get("/v1/public/categories/search")
                    .param("query", "food")
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray())
            .andReturn();

    // Verify search results contain food-related categories
    String responseContent = result.getResponse().getContentAsString();
    CategoryResponse[] categories =
        objectMapper.readValue(responseContent, CategoryResponse[].class);

    assertTrue(categories.length > 0);

    // At least one category should contain "food" in name or description
    boolean foundFoodCategory = false;
    for (CategoryResponse category : categories) {
      if (category.getName().toLowerCase().contains("food")
          || (category.getDescription() != null
              && category.getDescription().toLowerCase().contains("food"))) {
        foundFoodCategory = true;
        break;
      }
    }
    assertTrue(foundFoodCategory, "Should find at least one food-related category");
  }

  @Test
  @DisplayName("Should get subcategories from public API")
  void shouldGetSubcategoriesFromPublicApi() throws Exception {
    // First, find a category with subcategories
    MvcResult topLevelResult =
        mockMvc
            .perform(get("/v1/public/categories/top-level").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

    String topLevelContent = topLevelResult.getResponse().getContentAsString();
    CategoryResponse[] topLevelCategories =
        objectMapper.readValue(topLevelContent, CategoryResponse[].class);

    CategoryResponse parentCategory = null;
    for (CategoryResponse category : topLevelCategories) {
      if (category.getSubcategories() != null && !category.getSubcategories().isEmpty()) {
        parentCategory = category;
        break;
      }
    }

    assertNotNull(parentCategory, "Should find at least one category with subcategories");

    // When & Then
    MvcResult result =
        mockMvc
            .perform(
                get("/v1/public/categories/{id}/subcategories", parentCategory.getId())
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray())
            .andReturn();

    // Verify subcategories
    String responseContent = result.getResponse().getContentAsString();
    CategoryResponse[] subcategories =
        objectMapper.readValue(responseContent, CategoryResponse[].class);

    assertTrue(subcategories.length > 0);

    // All subcategories should have the parent category
    for (CategoryResponse subcategory : subcategories) {
      assertNotNull(subcategory.getParent());
      assertEquals(parentCategory.getId(), subcategory.getParent().getId());
    }
  }

  // ========== ADMIN CATEGORY API TESTS ==========
  // Note: AdminCategoryController doesn't have getAllCategories endpoint
  // It focuses on create, update, delete, and activate operations

  @Test
  @DisplayName("Should create new category via admin API")
  void shouldCreateNewCategoryViaAdminApi() throws Exception {
    // Given
    CategoryCreateRequest createRequest = new CategoryCreateRequest();
    createRequest.setName("Test Category");
    createRequest.setDescription("A test category for integration testing");
    createRequest.setColorCode("#123456");
    createRequest.setIconName("test_icon");
    createRequest.setSortOrder(999);

    // When & Then
    MvcResult result =
        mockMvc
            .perform(
                post("/v1/admin/categories")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createRequest))
                    .principal(mockPrincipal))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.name").value("Test Category"))
            .andExpect(jsonPath("$.description").value("A test category for integration testing"))
            .andExpect(jsonPath("$.colorCode").value("#123456"))
            .andExpect(jsonPath("$.iconName").value("test_icon"))
            .andExpect(jsonPath("$.sortOrder").value(999))
            .andExpect(jsonPath("$.isActive").value(true))
            .andExpect(jsonPath("$.userId").doesNotExist())
            .andReturn();

    // Verify the created category
    String responseContent = result.getResponse().getContentAsString();
    CategoryResponse createdCategory =
        objectMapper.readValue(responseContent, CategoryResponse.class);

    assertNotNull(createdCategory.getId());
    assertNotNull(createdCategory.getCreatedOn());
    assertNotNull(createdCategory.getUpdatedOn());
  }

  @Test
  @DisplayName("Should update category via admin API")
  void shouldUpdateCategoryViaAdminApi() throws Exception {
    // First, create a category
    CategoryCreateRequest createRequest = new CategoryCreateRequest();
    createRequest.setName("Original Category");
    createRequest.setDescription("Original description");
    createRequest.setColorCode("#111111");
    createRequest.setIconName("original_icon");
    createRequest.setSortOrder(1);

    MvcResult createResult =
        mockMvc
            .perform(
                post("/v1/admin/categories")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createRequest))
                    .principal(mockPrincipal))
            .andExpect(status().isCreated())
            .andReturn();

    String createContent = createResult.getResponse().getContentAsString();
    CategoryResponse createdCategory =
        objectMapper.readValue(createContent, CategoryResponse.class);

    // Update the category
    CategoryCreateRequest updateRequest = new CategoryCreateRequest();
    updateRequest.setName("Updated Category");
    updateRequest.setDescription("Updated description");
    updateRequest.setColorCode("#222222");
    updateRequest.setIconName("updated_icon");
    updateRequest.setSortOrder(2);

    // When & Then
    mockMvc
        .perform(
            put("/v1/admin/categories/{id}", createdCategory.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest))
                .principal(mockPrincipal))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(createdCategory.getId()))
        .andExpect(jsonPath("$.name").value("Updated Category"))
        .andExpect(jsonPath("$.description").value("Updated description"))
        .andExpect(jsonPath("$.colorCode").value("#222222"))
        .andExpect(jsonPath("$.iconName").value("updated_icon"))
        .andExpect(jsonPath("$.sortOrder").value(2));
  }

  @Test
  @DisplayName("Should delete category via admin API")
  void shouldDeleteCategoryViaAdminApi() throws Exception {
    // First, create a category
    CategoryCreateRequest createRequest = new CategoryCreateRequest();
    createRequest.setName("Category to Delete");
    createRequest.setDescription("This category will be deleted");
    createRequest.setColorCode("#333333");
    createRequest.setIconName("delete_icon");
    createRequest.setSortOrder(1);

    MvcResult createResult =
        mockMvc
            .perform(
                post("/v1/admin/categories")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createRequest))
                    .principal(mockPrincipal))
            .andExpect(status().isCreated())
            .andReturn();

    String createContent = createResult.getResponse().getContentAsString();
    CategoryResponse createdCategory =
        objectMapper.readValue(createContent, CategoryResponse.class);

    // Delete the category
    mockMvc
        .perform(
            delete("/v1/admin/categories/{id}", createdCategory.getId()).principal(mockPrincipal))
        .andExpect(status().isNoContent());

    // Verify category is deleted
    mockMvc
        .perform(
            get("/v1/public/categories/{id}", createdCategory.getId())
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Should activate category via admin API")
  void shouldActivateCategoryViaAdminApi() throws Exception {
    // First, create a category and then soft delete it (mark as inactive)
    CategoryCreateRequest createRequest = new CategoryCreateRequest();
    createRequest.setName("Category to Activate");
    createRequest.setDescription("This category will be activated");
    createRequest.setColorCode("#444444");
    createRequest.setIconName("activate_icon");
    createRequest.setSortOrder(1);

    MvcResult createResult =
        mockMvc
            .perform(
                post("/v1/admin/categories")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createRequest))
                    .principal(mockPrincipal))
            .andExpect(status().isCreated())
            .andReturn();

    String createContent = createResult.getResponse().getContentAsString();
    CategoryResponse createdCategory =
        objectMapper.readValue(createContent, CategoryResponse.class);

    // Note: In a real scenario, we would need to create an expense first to test soft delete
    // For this test, we'll just test the activate endpoint with an active category

    // Activate the category (should work even if already active)
    mockMvc
        .perform(
            put("/v1/admin/categories/{id}/activate", createdCategory.getId())
                .principal(mockPrincipal))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(createdCategory.getId()))
        .andExpect(jsonPath("$.isActive").value(true));
  }

  @Test
  @DisplayName("Should return 409 when creating category with duplicate name")
  void shouldReturn409WhenCreatingCategoryWithDuplicateName() throws Exception {
    // First, create a category
    CategoryCreateRequest createRequest = new CategoryCreateRequest();
    createRequest.setName("Duplicate Category Test");
    createRequest.setDescription("First category");
    createRequest.setColorCode("#555555");
    createRequest.setIconName("duplicate_icon");
    createRequest.setSortOrder(1);

    mockMvc
        .perform(
            post("/v1/admin/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest))
                .principal(mockPrincipal))
        .andExpect(status().isCreated());

    // Try to create another category with the same name
    CategoryCreateRequest duplicateRequest = new CategoryCreateRequest();
    duplicateRequest.setName("Duplicate Category Test"); // Same name
    duplicateRequest.setDescription("Second category with same name");
    duplicateRequest.setColorCode("#666666");
    duplicateRequest.setIconName("duplicate_icon2");
    duplicateRequest.setSortOrder(2);

    // When & Then
    mockMvc
        .perform(
            post("/v1/admin/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateRequest))
                .principal(mockPrincipal))
        .andExpect(status().isConflict());
  }

  @Test
  @DisplayName("Should return 500 for invalid admin requests")
  void shouldReturn500ForInvalidAdminRequests() throws Exception {
    // Test missing principal (authentication)
    CategoryCreateRequest createRequest = new CategoryCreateRequest();
    createRequest.setName("Test Category");
    createRequest.setDescription("Test");
    createRequest.setColorCode("#123456");
    createRequest.setIconName("test");
    createRequest.setSortOrder(1);

    mockMvc
        .perform(
            post("/v1/admin/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
        .andExpect(status().isInternalServerError()); // 500 when principal is null

    // Test invalid request body
    CategoryCreateRequest invalidRequest = new CategoryCreateRequest();
    // Missing required fields

    mockMvc
        .perform(
            post("/v1/admin/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest))
                .principal(mockPrincipal))
        .andExpect(status().isBadRequest());
  }
}
