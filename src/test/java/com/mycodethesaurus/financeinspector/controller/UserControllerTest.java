package com.mycodethesaurus.financeinspector.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycodethesaurus.financeinspector.dto.UserCreateRequest;
import com.mycodethesaurus.financeinspector.dto.UserResponse;
import com.mycodethesaurus.financeinspector.dto.UserUpdateRequest;
import com.mycodethesaurus.financeinspector.exception.DuplicateResourceException;
import com.mycodethesaurus.financeinspector.exception.ResourceNotFoundException;
import com.mycodethesaurus.financeinspector.exception.handler.CustomGlobalExceptionHandler;
import com.mycodethesaurus.financeinspector.service.UserService;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(UserController.class)
@DisplayName("UserController Unit Tests")
class UserControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private UserService userService;

  @Autowired private ObjectMapper objectMapper;

  private UserCreateRequest createRequest;
  private UserUpdateRequest updateRequest;
  private UserResponse userResponse;

  @BeforeEach
  void setUp() {
    mockMvc =
        MockMvcBuilders.standaloneSetup(new UserController(userService))
            .setControllerAdvice(new CustomGlobalExceptionHandler())
            .build();

    createRequest = new UserCreateRequest();
    createRequest.setUserName("testuser");
    createRequest.setPassword("password123");
    createRequest.setFirstName("John");
    createRequest.setLastName("Doe");
    createRequest.setEmail("john.doe@example.com");

    updateRequest = new UserUpdateRequest();
    updateRequest.setFirstName("Jane");
    updateRequest.setLastName("Smith");
    updateRequest.setEmail("jane.smith@example.com");

    userResponse = new UserResponse();
    userResponse.setId(1L);
    userResponse.setUserName("testuser");
    userResponse.setFirstName("John");
    userResponse.setLastName("Doe");
    userResponse.setEmail("john.doe@example.com");
    userResponse.setCreatedOn(LocalDateTime.now());
    userResponse.setUpdatedOn(LocalDateTime.now());
  }

  @Test
  @DisplayName("Should create user successfully")
  void shouldCreateUserSuccessfully() throws Exception {
    // Given
    when(userService.createUser(any(UserCreateRequest.class))).thenReturn(userResponse);

    // When & Then
    mockMvc
        .perform(
            post("/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(1L))
        .andExpect(jsonPath("$.userName").value("testuser"))
        .andExpect(jsonPath("$.firstName").value("John"))
        .andExpect(jsonPath("$.lastName").value("Doe"))
        .andExpect(jsonPath("$.email").value("john.doe@example.com"));
  }

  @Test
  @DisplayName("Should return 400 when creating user with invalid data")
  void shouldReturn400WhenCreatingUserWithInvalidData() throws Exception {
    // Given
    UserCreateRequest invalidRequest = new UserCreateRequest();
    invalidRequest.setUserName(""); // Invalid empty username
    invalidRequest.setPassword("123"); // Too short password
    invalidRequest.setEmail("invalid-email"); // Invalid email format

    // When & Then
    mockMvc
        .perform(
            post("/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("Validation Failed"));
  }

  @Test
  @DisplayName("Should return 409 when creating user with duplicate username")
  void shouldReturn409WhenCreatingUserWithDuplicateUsername() throws Exception {
    // Given
    when(userService.createUser(any(UserCreateRequest.class)))
        .thenThrow(new DuplicateResourceException("Username already exists: testuser"));

    // When & Then
    mockMvc
        .perform(
            post("/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.error").value("Duplicate Resource"))
        .andExpect(jsonPath("$.message").value("Username already exists: testuser"));
  }

  @Test
  @DisplayName("Should get user by ID successfully")
  void shouldGetUserByIdSuccessfully() throws Exception {
    // Given
    when(userService.getUserById(1L)).thenReturn(userResponse);

    // When & Then
    mockMvc
        .perform(get("/v1/users/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1L))
        .andExpect(jsonPath("$.userName").value("testuser"))
        .andExpect(jsonPath("$.email").value("john.doe@example.com"));
  }

  @Test
  @DisplayName("Should return 404 when user not found")
  void shouldReturn404WhenUserNotFound() throws Exception {
    // Given
    when(userService.getUserById(1L))
        .thenThrow(new ResourceNotFoundException("User not found with id: 1"));

    // When & Then
    mockMvc
        .perform(get("/v1/users/1"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error").value("Resource Not Found"))
        .andExpect(jsonPath("$.message").value("User not found with id: 1"));
  }

  @Test
  @DisplayName("Should get all users successfully")
  void shouldGetAllUsersSuccessfully() throws Exception {
    // Given
    List<UserResponse> users = Arrays.asList(userResponse);
    when(userService.getAllUsers()).thenReturn(users);

    // When & Then
    mockMvc
        .perform(get("/v1/users"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0].id").value(1L))
        .andExpect(jsonPath("$[0].userName").value("testuser"));
  }

  @Test
  @DisplayName("Should update user successfully")
  void shouldUpdateUserSuccessfully() throws Exception {
    // Given
    UserResponse updatedResponse = new UserResponse();
    updatedResponse.setId(1L);
    updatedResponse.setUserName("testuser");
    updatedResponse.setFirstName("Jane");
    updatedResponse.setLastName("Smith");
    updatedResponse.setEmail("jane.smith@example.com");
    updatedResponse.setCreatedOn(LocalDateTime.now());
    updatedResponse.setUpdatedOn(LocalDateTime.now());

    when(userService.updateUser(eq(1L), any(UserUpdateRequest.class))).thenReturn(updatedResponse);

    // When & Then
    mockMvc
        .perform(
            put("/v1/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1L))
        .andExpect(jsonPath("$.firstName").value("Jane"))
        .andExpect(jsonPath("$.lastName").value("Smith"))
        .andExpect(jsonPath("$.email").value("jane.smith@example.com"));
  }

  @Test
  @DisplayName("Should return 400 when updating user with invalid email")
  void shouldReturn400WhenUpdatingUserWithInvalidEmail() throws Exception {
    // Given
    UserUpdateRequest invalidRequest = new UserUpdateRequest();
    invalidRequest.setEmail("invalid-email");

    // When & Then
    mockMvc
        .perform(
            put("/v1/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("Validation Failed"));
  }

  @Test
  @DisplayName("Should return 409 when updating to duplicate email")
  void shouldReturn409WhenUpdatingToDuplicateEmail() throws Exception {
    // Given
    when(userService.updateUser(eq(1L), any(UserUpdateRequest.class)))
        .thenThrow(new DuplicateResourceException("Email already exists: jane.smith@example.com"));

    // When & Then
    mockMvc
        .perform(
            put("/v1/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.error").value("Duplicate Resource"))
        .andExpect(jsonPath("$.message").value("Email already exists: jane.smith@example.com"));
  }

  @Test
  @DisplayName("Should delete user successfully")
  void shouldDeleteUserSuccessfully() throws Exception {
    // When & Then
    mockMvc.perform(delete("/v1/users/1")).andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("Should return 404 when deleting non-existent user")
  void shouldReturn404WhenDeletingNonExistentUser() throws Exception {
    // Given
    doThrow(new ResourceNotFoundException("User not found with id: 1"))
        .when(userService)
        .deleteUser(1L);

    // When & Then
    mockMvc
        .perform(delete("/v1/users/1"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error").value("Resource Not Found"))
        .andExpect(jsonPath("$.message").value("User not found with id: 1"));
  }
}
