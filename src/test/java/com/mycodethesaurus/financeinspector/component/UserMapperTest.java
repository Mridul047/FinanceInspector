package com.mycodethesaurus.financeinspector.component;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.mycodethesaurus.financeinspector.dto.UserCreateRequest;
import com.mycodethesaurus.financeinspector.dto.UserResponse;
import com.mycodethesaurus.financeinspector.dto.UserUpdateRequest;
import com.mycodethesaurus.financeinspector.persistence.entity.UserEntity;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserMapper Unit Tests")
class UserMapperTest {

  @InjectMocks private UserMapper userMapper;

  @Mock private PasswordEncoder passwordEncoder;

  private UserCreateRequest createRequest;
  private UserUpdateRequest updateRequest;
  private UserEntity userEntity;

  @BeforeEach
  void setUp() {
    LocalDateTime now = LocalDateTime.now();

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

    userEntity = new UserEntity();
    userEntity.setId(1L);
    userEntity.setUserName("testuser");
    userEntity.setPassword("password123");
    userEntity.setFirstName("John");
    userEntity.setLastName("Doe");
    userEntity.setEmail("john.doe@example.com");
    userEntity.setCreatedOn(now);
    userEntity.setUpdatedOn(now);
  }

  @Test
  @DisplayName("Should map UserCreateRequest to UserEntity correctly")
  void shouldMapCreateRequestToEntityCorrectly() {
    // Given
    when(passwordEncoder.encode("password123")).thenReturn("encoded_password123");

    // When
    UserEntity result = userMapper.createRequestToEntity(createRequest);

    // Then
    assertNotNull(result);
    assertEquals("testuser", result.getUsername());
    assertEquals("encoded_password123", result.getPassword());
    assertEquals("John", result.getFirstName());
    assertEquals("Doe", result.getLastName());
    assertEquals("john.doe@example.com", result.getEmail());
    assertNotNull(result.getCreatedOn());
    assertNotNull(result.getUpdatedOn());
  }

  @Test
  @DisplayName("Should handle null values in createRequestToEntity")
  void shouldHandleNullValuesInCreateRequestToEntity() {
    // Given
    UserCreateRequest nullRequest = null;

    // When & Then
    assertThrows(
        NullPointerException.class,
        () -> {
          userMapper.createRequestToEntity(nullRequest);
        });
  }

  @Test
  @DisplayName("Should map UserEntity to UserResponse correctly")
  void shouldMapEntityToResponseCorrectly() {
    // When
    UserResponse result = userMapper.entityToResponse(userEntity);

    // Then
    assertNotNull(result);
    assertEquals(1L, result.getId());
    assertEquals("testuser", result.getUserName());
    assertEquals("John", result.getFirstName());
    assertEquals("Doe", result.getLastName());
    assertEquals("john.doe@example.com", result.getEmail());
    assertEquals(userEntity.getCreatedOn(), result.getCreatedOn());
    assertEquals(userEntity.getUpdatedOn(), result.getUpdatedOn());
  }

  @Test
  @DisplayName("Should handle null entity in entityToResponse")
  void shouldHandleNullEntityInEntityToResponse() {
    // Given
    UserEntity nullEntity = null;

    // When & Then
    assertThrows(
        NullPointerException.class,
        () -> {
          userMapper.entityToResponse(nullEntity);
        });
  }

  @Test
  @DisplayName("Should map list of entities to list of responses correctly")
  void shouldMapEntityListToResponseListCorrectly() {
    // Given
    UserEntity entity2 = new UserEntity();
    entity2.setId(2L);
    entity2.setUserName("testuser2");
    entity2.setFirstName("Jane");
    entity2.setLastName("Smith");
    entity2.setEmail("jane.smith@example.com");
    entity2.setCreatedOn(LocalDateTime.now());
    entity2.setUpdatedOn(LocalDateTime.now());

    List<UserEntity> entities = Arrays.asList(userEntity, entity2);

    // When
    List<UserResponse> result = userMapper.entityListToResponseList(entities);

    // Then
    assertNotNull(result);
    assertEquals(2, result.size());

    UserResponse response1 = result.get(0);
    assertEquals(1L, response1.getId());
    assertEquals("testuser", response1.getUserName());
    assertEquals("John", response1.getFirstName());

    UserResponse response2 = result.get(1);
    assertEquals(2L, response2.getId());
    assertEquals("testuser2", response2.getUserName());
    assertEquals("Jane", response2.getFirstName());
  }

  @Test
  @DisplayName("Should handle empty list in entityListToResponseList")
  void shouldHandleEmptyListInEntityListToResponseList() {
    // Given
    List<UserEntity> emptyList = Arrays.asList();

    // When
    List<UserResponse> result = userMapper.entityListToResponseList(emptyList);

    // Then
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName("Should handle null list in entityListToResponseList")
  void shouldHandleNullListInEntityListToResponseList() {
    // Given
    List<UserEntity> nullList = null;

    // When & Then
    assertThrows(
        NullPointerException.class,
        () -> {
          userMapper.entityListToResponseList(nullList);
        });
  }

  @Test
  @DisplayName("Should update entity from request with all fields")
  void shouldUpdateEntityFromRequestWithAllFields() throws InterruptedException {
    // Given
    LocalDateTime originalCreatedOn = userEntity.getCreatedOn();
    LocalDateTime originalUpdatedOn = userEntity.getUpdatedOn();

    // Add a small delay to ensure timestamp difference
    Thread.sleep(10);

    // When
    userMapper.updateEntityFromRequest(userEntity, updateRequest);

    // Then
    assertEquals("Jane", userEntity.getFirstName());
    assertEquals("Smith", userEntity.getLastName());
    assertEquals("jane.smith@example.com", userEntity.getEmail());
    assertEquals(originalCreatedOn, userEntity.getCreatedOn()); // Should remain unchanged
    assertNotEquals(originalUpdatedOn, userEntity.getUpdatedOn()); // Should be updated
    assertTrue(userEntity.getUpdatedOn().isAfter(originalUpdatedOn));
  }

  @Test
  @DisplayName("Should update entity from request with partial fields")
  void shouldUpdateEntityFromRequestWithPartialFields() {
    // Given
    UserUpdateRequest partialRequest = new UserUpdateRequest();
    partialRequest.setFirstName("UpdatedFirstName");
    // Leave lastName and email as null

    String originalLastName = userEntity.getLastName();
    String originalEmail = userEntity.getEmail();

    // When
    userMapper.updateEntityFromRequest(userEntity, partialRequest);

    // Then
    assertEquals("UpdatedFirstName", userEntity.getFirstName());
    assertEquals(originalLastName, userEntity.getLastName()); // Should remain unchanged
    assertEquals(originalEmail, userEntity.getEmail()); // Should remain unchanged
  }

  @Test
  @DisplayName("Should not update entity when request fields are empty strings")
  void shouldNotUpdateEntityWhenRequestFieldsAreEmptyStrings() {
    // Given
    UserUpdateRequest emptyRequest = new UserUpdateRequest();
    emptyRequest.setFirstName("   "); // Whitespace only
    emptyRequest.setLastName(""); // Empty string
    emptyRequest.setEmail("valid@email.com");

    String originalFirstName = userEntity.getFirstName();
    String originalLastName = userEntity.getLastName();

    // When
    userMapper.updateEntityFromRequest(userEntity, emptyRequest);

    // Then
    assertEquals(
        originalFirstName, userEntity.getFirstName()); // Should remain unchanged (whitespace)
    assertEquals(originalLastName, userEntity.getLastName()); // Should remain unchanged (empty)
    assertEquals("valid@email.com", userEntity.getEmail()); // Should be updated
  }

  @Test
  @DisplayName("Should handle null update request")
  void shouldHandleNullUpdateRequest() {
    // Given
    UserUpdateRequest nullRequest = null;

    // When & Then
    assertThrows(
        NullPointerException.class,
        () -> {
          userMapper.updateEntityFromRequest(userEntity, nullRequest);
        });
  }

  @Test
  @DisplayName("Should handle null entity in updateEntityFromRequest")
  void shouldHandleNullEntityInUpdateEntityFromRequest() {
    // Given
    UserEntity nullEntity = null;

    // When & Then
    assertThrows(
        NullPointerException.class,
        () -> {
          userMapper.updateEntityFromRequest(nullEntity, updateRequest);
        });
  }

  @Test
  @DisplayName("Should preserve original values when update request has all null fields")
  void shouldPreserveOriginalValuesWhenUpdateRequestHasAllNullFields() throws InterruptedException {
    // Given
    UserUpdateRequest nullFieldsRequest = new UserUpdateRequest();
    // All fields are null by default

    String originalFirstName = userEntity.getFirstName();
    String originalLastName = userEntity.getLastName();
    String originalEmail = userEntity.getEmail();
    LocalDateTime originalUpdatedOn = userEntity.getUpdatedOn();

    // Add a small delay to ensure timestamp difference
    Thread.sleep(50);

    // When
    userMapper.updateEntityFromRequest(userEntity, nullFieldsRequest);

    // Then
    assertEquals(originalFirstName, userEntity.getFirstName());
    assertEquals(originalLastName, userEntity.getLastName());
    assertEquals(originalEmail, userEntity.getEmail());
    assertTrue(
        userEntity.getUpdatedOn().isAfter(originalUpdatedOn)); // updatedOn should still be updated
  }
}
