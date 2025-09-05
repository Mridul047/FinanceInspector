package com.mycodethesaurus.financeinspector.integration;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycodethesaurus.financeinspector.config.AbstractIntegrationTest;
import com.mycodethesaurus.financeinspector.dto.UserCreateRequest;
import com.mycodethesaurus.financeinspector.dto.UserResponse;
import com.mycodethesaurus.financeinspector.dto.UserUpdateRequest;
import com.mycodethesaurus.financeinspector.persistence.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@AutoConfigureMockMvc
@Transactional
@DisplayName("User Management Integration Tests")
class UserManagementIntegrationTest extends AbstractIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private UserRepository userRepository;

  private UserCreateRequest validCreateRequest;

  @BeforeEach
  void setUp() {
    // Clean database before each test
    userRepository.deleteAll();

    // Setup valid test data
    validCreateRequest = new UserCreateRequest();
    validCreateRequest.setUserName("integrationtest");
    validCreateRequest.setPassword("password123");
    validCreateRequest.setFirstName("Integration");
    validCreateRequest.setLastName("Test");
    validCreateRequest.setEmail("integration.test@example.com");
  }

  @AfterEach
  void tearDown() {
    // Clean up after each test
    userRepository.deleteAll();
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  @DisplayName("Should create user successfully - Integration")
  void shouldCreateUserSuccessfully() throws Exception {
    // When & Then
    String responseContent =
        mockMvc
            .perform(
                post("/v1/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(validCreateRequest))
                    .with(csrf()))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.userName").value("integrationtest"))
            .andExpect(jsonPath("$.firstName").value("Integration"))
            .andExpect(jsonPath("$.lastName").value("Test"))
            .andExpect(jsonPath("$.email").value("integration.test@example.com"))
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.createdOn").exists())
            .andExpect(jsonPath("$.updatedOn").exists())
            .andReturn()
            .getResponse()
            .getContentAsString();

    // Verify in database
    assertEquals(1, userRepository.count());

    // Extract and verify response
    UserResponse response = objectMapper.readValue(responseContent, UserResponse.class);
    assertEquals("integrationtest", response.getUserName());
    assertEquals("integration.test@example.com", response.getEmail());
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  @DisplayName("Should get user by ID successfully - Integration")
  void shouldGetUserByIdSuccessfully() throws Exception {
    // Given - Create a user first
    String createResponse =
        mockMvc
            .perform(
                post("/v1/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(validCreateRequest))
                    .with(csrf()))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

    UserResponse createdUser = objectMapper.readValue(createResponse, UserResponse.class);

    // When & Then
    mockMvc
        .perform(get("/v1/users/" + createdUser.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(createdUser.getId()))
        .andExpect(jsonPath("$.userName").value("integrationtest"))
        .andExpect(jsonPath("$.email").value("integration.test@example.com"));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  @DisplayName("Should get all users - Integration")
  void shouldGetAllUsers() throws Exception {
    // Given - Create two users
    mockMvc
        .perform(
            post("/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validCreateRequest))
                .with(csrf()))
        .andExpect(status().isCreated());

    UserCreateRequest secondUser = new UserCreateRequest();
    secondUser.setUserName("seconduser");
    secondUser.setPassword("password456");
    secondUser.setFirstName("Second");
    secondUser.setLastName("User");
    secondUser.setEmail("second.user@example.com");

    mockMvc
        .perform(
            post("/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(secondUser))
                .with(csrf()))
        .andExpect(status().isCreated());

    // When & Then
    mockMvc
        .perform(get("/v1/users"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[?(@.userName == 'integrationtest')]").exists())
        .andExpect(jsonPath("$[?(@.userName == 'seconduser')]").exists());
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  @DisplayName("Should update user successfully - Integration")
  void shouldUpdateUserSuccessfully() throws Exception {
    // Given - Create a user first
    String createResponse =
        mockMvc
            .perform(
                post("/v1/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(validCreateRequest))
                    .with(csrf()))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

    UserResponse createdUser = objectMapper.readValue(createResponse, UserResponse.class);

    UserUpdateRequest updateRequest = new UserUpdateRequest();
    updateRequest.setFirstName("Updated");
    updateRequest.setLastName("User");
    updateRequest.setEmail("updated.user@example.com");

    // When & Then
    mockMvc
        .perform(
            put("/v1/users/" + createdUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest))
                .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(createdUser.getId()))
        .andExpect(jsonPath("$.firstName").value("Updated"))
        .andExpect(jsonPath("$.lastName").value("User"))
        .andExpect(jsonPath("$.email").value("updated.user@example.com"))
        .andExpect(jsonPath("$.userName").value("integrationtest")); // Should remain unchanged
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  @DisplayName("Should handle validation errors correctly - Integration")
  void shouldHandleValidationErrors() throws Exception {
    // Given - Invalid request with multiple validation errors
    UserCreateRequest invalidRequest = new UserCreateRequest();
    invalidRequest.setUserName("ab"); // Too short (min 3)
    invalidRequest.setPassword("123"); // Too short (min 8)
    invalidRequest.setFirstName(""); // Blank
    invalidRequest.setLastName(""); // Blank
    invalidRequest.setEmail("invalid-email"); // Invalid format
    // Missing required fields will trigger @NotBlank

    // When & Then
    mockMvc
        .perform(
            post("/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest))
                .with(csrf()))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("Validation Failed"))
        .andExpect(jsonPath("$.validationErrors").exists())
        .andExpect(
            jsonPath("$.validationErrors", hasSize(greaterThan(3)))); // Multiple validation errors
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  @DisplayName("Should handle duplicate username - Integration")
  void shouldHandleDuplicateUsername() throws Exception {
    // Given - Create a user first
    mockMvc
        .perform(
            post("/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validCreateRequest))
                .with(csrf()))
        .andExpect(status().isCreated());

    // Create duplicate username request
    UserCreateRequest duplicateRequest = new UserCreateRequest();
    duplicateRequest.setUserName("integrationtest"); // Duplicate username
    duplicateRequest.setPassword("password123");
    duplicateRequest.setFirstName("Duplicate");
    duplicateRequest.setLastName("User");
    duplicateRequest.setEmail("duplicate@example.com");

    // When & Then
    mockMvc
        .perform(
            post("/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateRequest))
                .with(csrf()))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.error").value("Duplicate Resource"))
        .andExpect(jsonPath("$.message").value("Username already exists: integrationtest"));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  @DisplayName("Should handle duplicate email - Integration")
  void shouldHandleDuplicateEmail() throws Exception {
    // Given - Create a user first
    mockMvc
        .perform(
            post("/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validCreateRequest))
                .with(csrf()))
        .andExpect(status().isCreated());

    // Create duplicate email request
    UserCreateRequest duplicateEmailRequest = new UserCreateRequest();
    duplicateEmailRequest.setUserName("uniqueusername");
    duplicateEmailRequest.setPassword("password123");
    duplicateEmailRequest.setFirstName("Duplicate");
    duplicateEmailRequest.setLastName("Email");
    duplicateEmailRequest.setEmail("integration.test@example.com"); // Duplicate email

    // When & Then
    mockMvc
        .perform(
            post("/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateEmailRequest))
                .with(csrf()))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.error").value("Duplicate Resource"))
        .andExpect(
            jsonPath("$.message").value("Email already exists: integration.test@example.com"));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  @DisplayName("Should handle non-existent user - Integration")
  void shouldHandleNonExistentUser() throws Exception {
    // When & Then
    mockMvc
        .perform(get("/v1/users/99999"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error").value("Resource Not Found"))
        .andExpect(jsonPath("$.message").value("User not found with id: 99999"));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  @DisplayName("Should delete user successfully - Integration")
  void shouldDeleteUserSuccessfully() throws Exception {
    // Given - Create a user first
    String createResponse =
        mockMvc
            .perform(
                post("/v1/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(validCreateRequest))
                    .with(csrf()))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

    UserResponse createdUser = objectMapper.readValue(createResponse, UserResponse.class);
    assertEquals(1, userRepository.count());

    // When & Then
    mockMvc
        .perform(delete("/v1/users/" + createdUser.getId()).with(csrf()))
        .andExpect(status().isNoContent());

    // Verify user is deleted
    mockMvc.perform(get("/v1/users/" + createdUser.getId())).andExpect(status().isNotFound());

    // Verify count decreased
    assertEquals(0, userRepository.count());
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  @DisplayName("Should handle delete non-existent user - Integration")
  void shouldHandleDeleteNonExistentUser() throws Exception {
    // When & Then
    mockMvc
        .perform(delete("/v1/users/99999").with(csrf()))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error").value("Resource Not Found"))
        .andExpect(jsonPath("$.message").value("User not found with id: 99999"));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  @DisplayName("Should handle update non-existent user - Integration")
  void shouldHandleUpdateNonExistentUser() throws Exception {
    // Given
    UserUpdateRequest updateRequest = new UserUpdateRequest();
    updateRequest.setFirstName("Non");
    updateRequest.setLastName("Existent");

    // When & Then
    mockMvc
        .perform(
            put("/v1/users/99999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest))
                .with(csrf()))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error").value("Resource Not Found"))
        .andExpect(jsonPath("$.message").value("User not found with id: 99999"));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  @DisplayName("Should handle partial update correctly - Integration")
  void shouldHandlePartialUpdateCorrectly() throws Exception {
    // Given - Create a user first
    String createResponse =
        mockMvc
            .perform(
                post("/v1/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(validCreateRequest))
                    .with(csrf()))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

    UserResponse createdUser = objectMapper.readValue(createResponse, UserResponse.class);

    // Partial update - only update first name
    UserUpdateRequest partialUpdateRequest = new UserUpdateRequest();
    partialUpdateRequest.setFirstName("PartiallyUpdated");
    // Leave lastName and email as null

    // When & Then
    mockMvc
        .perform(
            put("/v1/users/" + createdUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(partialUpdateRequest))
                .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.firstName").value("PartiallyUpdated"))
        .andExpect(jsonPath("$.lastName").value("Test")) // Should remain unchanged
        .andExpect(
            jsonPath("$.email").value("integration.test@example.com")); // Should remain unchanged
  }
}
