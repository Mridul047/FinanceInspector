package com.mycodethesaurus.financeinspector.integration;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycodethesaurus.financeinspector.config.AbstractIntegrationTest;
import com.mycodethesaurus.financeinspector.dto.CategoryCreateRequest;
import com.mycodethesaurus.financeinspector.dto.CategoryResponse;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

@AutoConfigureMockMvc
@Transactional
@DisplayName("Category Integration Tests")
class CategoryIntegrationTest extends AbstractIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  // ========== PUBLIC CATEGORY API TESTS ==========

  @Nested
  @DisplayName("Public Category Operations")
  class PublicCategoryOperations {

    @Test
    @DisplayName("Should get all global categories from unified API")
    void shouldGetAllGlobalCategoriesFromUnifiedApi() throws Exception {
      // When & Then
      MvcResult result =
          mockMvc
              .perform(get("/v1/categories").accept(MediaType.APPLICATION_JSON))
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
    @DisplayName("Should get top-level categories from unified API")
    void shouldGetTopLevelCategoriesFromUnifiedApi() throws Exception {
      // When & Then
      MvcResult result =
          mockMvc
              .perform(get("/v1/categories/top-level").accept(MediaType.APPLICATION_JSON))
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
    @DisplayName("Should get specific category by ID from unified API")
    void shouldGetSpecificCategoryByIdFromUnifiedApi() throws Exception {
      // First, get all categories to find a valid ID
      MvcResult categoriesResult =
          mockMvc
              .perform(get("/v1/categories").accept(MediaType.APPLICATION_JSON))
              .andExpect(status().isOk())
              .andReturn();

      String categoriesContent = categoriesResult.getResponse().getContentAsString();
      CategoryResponse[] categories =
          objectMapper.readValue(categoriesContent, CategoryResponse[].class);
      assertTrue(categories.length > 0);

      Long categoryId = categories[0].getId();

      // When & Then
      mockMvc
          .perform(get("/v1/categories/{id}", categoryId).accept(MediaType.APPLICATION_JSON))
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
    @DisplayName("Should return 404 for non-existent category from unified API")
    void shouldReturn404ForNonExistentCategoryFromUnifiedApi() throws Exception {
      // When & Then
      mockMvc
          .perform(get("/v1/categories/99999").accept(MediaType.APPLICATION_JSON))
          .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should search categories from unified API")
    void shouldSearchCategoriesFromUnifiedApi() throws Exception {
      // When & Then
      MvcResult result =
          mockMvc
              .perform(
                  get("/v1/categories/search")
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
    @DisplayName("Should get subcategories from unified API")
    void shouldGetSubcategoriesFromUnifiedApi() throws Exception {
      // First, find a category with subcategories
      MvcResult topLevelResult =
          mockMvc
              .perform(get("/v1/categories/top-level").accept(MediaType.APPLICATION_JSON))
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
                  get("/v1/categories/{id}/subcategories", parentCategory.getId())
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
  }

  // ========== ADMIN CATEGORY API TESTS ==========

  @Nested
  @DisplayName("Admin Category Operations")
  class AdminCategoryOperations {

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should create new category via unified admin API")
    void shouldCreateNewCategoryViaUnifiedAdminApi() throws Exception {
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
                  post("/v1/categories")
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(objectMapper.writeValueAsString(createRequest))
                      .with(user("admin").roles("ADMIN"))
                      .with(csrf()))
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
    @DisplayName("Should return 403 for admin operations without authentication")
    void shouldReturn403ForAdminOperationsWithoutAuthentication() throws Exception {
      // Given
      CategoryCreateRequest createRequest = new CategoryCreateRequest();
      createRequest.setName("Test Category");
      createRequest.setDescription("Test");
      createRequest.setColorCode("#123456");
      createRequest.setIconName("test");
      createRequest.setSortOrder(1);

      // When & Then - should be forbidden without proper role
      mockMvc
          .perform(
              post("/v1/categories")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(createRequest))
                  .with(csrf()))
          .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should return 403 for admin operations with USER role")
    void shouldReturn403ForAdminOperationsWithUserRole() throws Exception {
      // Given
      CategoryCreateRequest createRequest = new CategoryCreateRequest();
      createRequest.setName("Test Category");
      createRequest.setDescription("Test");
      createRequest.setColorCode("#123456");
      createRequest.setIconName("test");
      createRequest.setSortOrder(1);

      // When & Then - should be forbidden with USER role
      mockMvc
          .perform(
              post("/v1/categories")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(createRequest))
                  .with(user("user").roles("USER"))
                  .with(csrf()))
          .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should update category via unified admin API")
    void shouldUpdateCategoryViaUnifiedAdminApi() throws Exception {
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
                  post("/v1/categories")
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(objectMapper.writeValueAsString(createRequest))
                      .with(user("admin").roles("ADMIN"))
                      .with(csrf()))
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
              put("/v1/categories/{id}", createdCategory.getId())
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(updateRequest))
                  .with(user("admin").roles("ADMIN"))
                  .with(csrf()))
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
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should delete category via unified admin API")
    void shouldDeleteCategoryViaUnifiedAdminApi() throws Exception {
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
                  post("/v1/categories")
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(objectMapper.writeValueAsString(createRequest))
                      .with(user("admin").roles("ADMIN"))
                      .with(csrf()))
              .andExpect(status().isCreated())
              .andReturn();

      String createContent = createResult.getResponse().getContentAsString();
      CategoryResponse createdCategory =
          objectMapper.readValue(createContent, CategoryResponse.class);

      // Delete the category
      mockMvc
          .perform(
              delete("/v1/categories/{id}", createdCategory.getId())
                  .with(user("admin").roles("ADMIN"))
                  .with(csrf()))
          .andExpect(status().isNoContent());

      // Verify category is deleted
      mockMvc
          .perform(
              get("/v1/categories/{id}", createdCategory.getId())
                  .accept(MediaType.APPLICATION_JSON))
          .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should activate category via unified admin API")
    void shouldActivateCategoryViaUnifiedAdminApi() throws Exception {
      // First, create a category
      CategoryCreateRequest createRequest = new CategoryCreateRequest();
      createRequest.setName("Category to Activate");
      createRequest.setDescription("This category will be activated");
      createRequest.setColorCode("#444444");
      createRequest.setIconName("activate_icon");
      createRequest.setSortOrder(1);

      MvcResult createResult =
          mockMvc
              .perform(
                  post("/v1/categories")
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(objectMapper.writeValueAsString(createRequest))
                      .with(user("admin").roles("ADMIN"))
                      .with(csrf()))
              .andExpect(status().isCreated())
              .andReturn();

      String createContent = createResult.getResponse().getContentAsString();
      CategoryResponse createdCategory =
          objectMapper.readValue(createContent, CategoryResponse.class);

      // Activate the category (should work even if already active)
      mockMvc
          .perform(
              put("/v1/categories/{id}/activate", createdCategory.getId())
                  .with(user("admin").roles("ADMIN"))
                  .with(csrf()))
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.id").value(createdCategory.getId()))
          .andExpect(jsonPath("$.isActive").value(true));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
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
              post("/v1/categories")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(createRequest))
                  .with(user("admin").roles("ADMIN"))
                  .with(csrf()))
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
              post("/v1/categories")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(duplicateRequest))
                  .with(user("admin").roles("ADMIN"))
                  .with(csrf()))
          .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return 400 for invalid admin requests")
    void shouldReturn400ForInvalidAdminRequests() throws Exception {
      // Test invalid request body
      CategoryCreateRequest invalidRequest = new CategoryCreateRequest();
      // Missing required fields

      mockMvc
          .perform(
              post("/v1/categories")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(invalidRequest))
                  .with(user("admin").roles("ADMIN"))
                  .with(csrf()))
          .andExpect(status().isBadRequest());
    }
  }

  // ========== CROSS-FUNCTIONALITY TESTS ==========

  @Nested
  @DisplayName("Cross-Functionality Tests")
  class CrossFunctionalityTests {

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should ensure admin operations reflect in public API")
    void shouldEnsureAdminOperationsReflectInPublicApi() throws Exception {
      // Create a category via admin API
      CategoryCreateRequest createRequest = new CategoryCreateRequest();
      createRequest.setName("Cross-Function Test Category");
      createRequest.setDescription("Testing cross-functionality");
      createRequest.setColorCode("#999999");
      createRequest.setIconName("cross_test");
      createRequest.setSortOrder(1000);

      MvcResult adminResult =
          mockMvc
              .perform(
                  post("/v1/categories")
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(objectMapper.writeValueAsString(createRequest))
                      .with(user("admin").roles("ADMIN"))
                      .with(csrf()))
              .andExpect(status().isCreated())
              .andReturn();

      String adminContent = adminResult.getResponse().getContentAsString();
      CategoryResponse adminCategory = objectMapper.readValue(adminContent, CategoryResponse.class);

      // Verify it's accessible via public API
      mockMvc
          .perform(
              get("/v1/categories/{id}", adminCategory.getId()).accept(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value(adminCategory.getId()))
          .andExpect(jsonPath("$.name").value("Cross-Function Test Category"))
          .andExpect(jsonPath("$.description").value("Testing cross-functionality"))
          .andExpect(jsonPath("$.isActive").value(true));

      // Verify it appears in public category list
      MvcResult publicResult =
          mockMvc
              .perform(get("/v1/categories").accept(MediaType.APPLICATION_JSON))
              .andExpect(status().isOk())
              .andReturn();

      String publicContent = publicResult.getResponse().getContentAsString();
      CategoryResponse[] publicCategories =
          objectMapper.readValue(publicContent, CategoryResponse[].class);

      boolean foundCategory = false;
      for (CategoryResponse category : publicCategories) {
        if (category.getId().equals(adminCategory.getId())) {
          foundCategory = true;
          assertEquals("Cross-Function Test Category", category.getName());
          break;
        }
      }
      assertTrue(foundCategory, "Category created via admin API should appear in public API");
    }
  }
}
