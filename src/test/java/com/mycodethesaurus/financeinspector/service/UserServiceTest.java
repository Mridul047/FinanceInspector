package com.mycodethesaurus.financeinspector.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.mycodethesaurus.financeinspector.component.UserMapper;
import com.mycodethesaurus.financeinspector.dto.UserCreateRequest;
import com.mycodethesaurus.financeinspector.dto.UserResponse;
import com.mycodethesaurus.financeinspector.dto.UserUpdateRequest;
import com.mycodethesaurus.financeinspector.exception.DuplicateResourceException;
import com.mycodethesaurus.financeinspector.exception.ResourceNotFoundException;
import com.mycodethesaurus.financeinspector.persistence.entity.UserEntity;
import com.mycodethesaurus.financeinspector.persistence.repository.UserRepository;
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
@DisplayName("UserService Unit Tests")
class UserServiceTest {

  @Mock private UserRepository userRepository;

  @Mock private UserMapper userMapper;

  @InjectMocks private UserService userService;

  private UserEntity userEntity;
  private UserCreateRequest createRequest;
  private UserUpdateRequest updateRequest;
  private UserResponse userResponse;

  @BeforeEach
  void setUp() {
    userEntity = new UserEntity();
    userEntity.setId(1L);
    userEntity.setUserName("testuser");
    userEntity.setPassword("password123");
    userEntity.setFirstName("John");
    userEntity.setLastName("Doe");
    userEntity.setEmail("john.doe@example.com");
    userEntity.setCreatedOn(LocalDateTime.now());
    userEntity.setUpdatedOn(LocalDateTime.now());

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
  void shouldCreateUserSuccessfully() {
    // Given
    when(userRepository.existsByUserName("testuser")).thenReturn(false);
    when(userRepository.existsByEmail("john.doe@example.com")).thenReturn(false);
    when(userMapper.createRequestToEntity(createRequest)).thenReturn(userEntity);
    when(userRepository.save(userEntity)).thenReturn(userEntity);
    when(userMapper.entityToResponse(userEntity)).thenReturn(userResponse);

    // When
    UserResponse result = userService.createUser(createRequest);

    // Then
    assertNotNull(result);
    assertEquals("testuser", result.getUserName());
    assertEquals("john.doe@example.com", result.getEmail());

    verify(userRepository).existsByUserName("testuser");
    verify(userRepository).existsByEmail("john.doe@example.com");
    verify(userRepository).save(userEntity);
    verify(userMapper).createRequestToEntity(createRequest);
    verify(userMapper).entityToResponse(userEntity);
  }

  @Test
  @DisplayName("Should throw exception when username already exists")
  void shouldThrowExceptionWhenUsernameExists() {
    // Given
    when(userRepository.existsByUserName("testuser")).thenReturn(true);

    // When & Then
    DuplicateResourceException exception =
        assertThrows(DuplicateResourceException.class, () -> userService.createUser(createRequest));

    assertEquals("Username already exists: testuser", exception.getMessage());
    verify(userRepository).existsByUserName("testuser");
    verify(userRepository, never()).save(any());
  }

  @Test
  @DisplayName("Should throw exception when email already exists")
  void shouldThrowExceptionWhenEmailExists() {
    // Given
    when(userRepository.existsByUserName("testuser")).thenReturn(false);
    when(userRepository.existsByEmail("john.doe@example.com")).thenReturn(true);

    // When & Then
    DuplicateResourceException exception =
        assertThrows(DuplicateResourceException.class, () -> userService.createUser(createRequest));

    assertEquals("Email already exists: john.doe@example.com", exception.getMessage());
    verify(userRepository).existsByUserName("testuser");
    verify(userRepository).existsByEmail("john.doe@example.com");
    verify(userRepository, never()).save(any());
  }

  @Test
  @DisplayName("Should get user by ID successfully")
  void shouldGetUserByIdSuccessfully() {
    // Given
    when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
    when(userMapper.entityToResponse(userEntity)).thenReturn(userResponse);

    // When
    UserResponse result = userService.getUserById(1L);

    // Then
    assertNotNull(result);
    assertEquals(1L, result.getId());
    assertEquals("testuser", result.getUserName());

    verify(userRepository).findById(1L);
    verify(userMapper).entityToResponse(userEntity);
  }

  @Test
  @DisplayName("Should throw exception when user not found by ID")
  void shouldThrowExceptionWhenUserNotFoundById() {
    // Given
    when(userRepository.findById(1L)).thenReturn(Optional.empty());

    // When & Then
    ResourceNotFoundException exception =
        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(1L));

    assertEquals("User not found with id: 1", exception.getMessage());
    verify(userRepository).findById(1L);
    verify(userMapper, never()).entityToResponse(any());
  }

  @Test
  @DisplayName("Should get all users successfully")
  void shouldGetAllUsersSuccessfully() {
    // Given
    List<UserEntity> entities = Arrays.asList(userEntity);
    List<UserResponse> responses = Arrays.asList(userResponse);

    when(userRepository.findAll()).thenReturn(entities);
    when(userMapper.entityListToResponseList(entities)).thenReturn(responses);

    // When
    List<UserResponse> result = userService.getAllUsers();

    // Then
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals("testuser", result.get(0).getUserName());

    verify(userRepository).findAll();
    verify(userMapper).entityListToResponseList(entities);
  }

  @Test
  @DisplayName("Should update user successfully")
  void shouldUpdateUserSuccessfully() {
    // Given
    when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
    when(userRepository.findByEmailExcludingId("jane.smith@example.com", 1L))
        .thenReturn(Optional.empty());
    when(userRepository.save(userEntity)).thenReturn(userEntity);
    when(userMapper.entityToResponse(userEntity)).thenReturn(userResponse);

    // When
    UserResponse result = userService.updateUser(1L, updateRequest);

    // Then
    assertNotNull(result);

    verify(userRepository).findById(1L);
    verify(userRepository).findByEmailExcludingId("jane.smith@example.com", 1L);
    verify(userMapper).updateEntityFromRequest(userEntity, updateRequest);
    verify(userRepository).save(userEntity);
    verify(userMapper).entityToResponse(userEntity);
  }

  @Test
  @DisplayName("Should throw exception when updating to existing email")
  void shouldThrowExceptionWhenUpdatingToExistingEmail() {
    // Given
    UserEntity existingUser = new UserEntity();
    existingUser.setId(2L);

    when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
    when(userRepository.findByEmailExcludingId("jane.smith@example.com", 1L))
        .thenReturn(Optional.of(existingUser));

    // When & Then
    DuplicateResourceException exception =
        assertThrows(
            DuplicateResourceException.class, () -> userService.updateUser(1L, updateRequest));

    assertEquals("Email already exists: jane.smith@example.com", exception.getMessage());
    verify(userRepository).findById(1L);
    verify(userRepository).findByEmailExcludingId("jane.smith@example.com", 1L);
    verify(userRepository, never()).save(any());
  }

  @Test
  @DisplayName("Should delete user successfully")
  void shouldDeleteUserSuccessfully() {
    // Given
    when(userRepository.existsById(1L)).thenReturn(true);

    // When
    assertDoesNotThrow(() -> userService.deleteUser(1L));

    // Then
    verify(userRepository).existsById(1L);
    verify(userRepository).deleteById(1L);
  }

  @Test
  @DisplayName("Should throw exception when deleting non-existent user")
  void shouldThrowExceptionWhenDeletingNonExistentUser() {
    // Given
    when(userRepository.existsById(1L)).thenReturn(false);

    // When & Then
    ResourceNotFoundException exception =
        assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser(1L));

    assertEquals("User not found with id: 1", exception.getMessage());
    verify(userRepository).existsById(1L);
    verify(userRepository, never()).deleteById(any());
  }
}
