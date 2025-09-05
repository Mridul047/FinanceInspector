package com.mycodethesaurus.financeinspector.component;

import com.mycodethesaurus.financeinspector.dto.UserCreateRequest;
import com.mycodethesaurus.financeinspector.dto.UserResponse;
import com.mycodethesaurus.financeinspector.dto.UserUpdateRequest;
import com.mycodethesaurus.financeinspector.enums.UserRole;
import com.mycodethesaurus.financeinspector.persistence.entity.UserEntity;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Mapper component for converting between UserEntity and DTOs.
 *
 * <p>This mapper handles the conversion between different representations of user data:
 *
 * <ul>
 *   <li>UserCreateRequest to UserEntity (for user creation)
 *   <li>UserEntity to UserResponse (for API responses)
 *   <li>UserUpdateRequest to UserEntity updates (for user updates)
 * </ul>
 *
 * <p>Security considerations:
 *
 * <ul>
 *   <li>Passwords are automatically encoded using BCrypt
 *   <li>New users are assigned REGULAR_USER role by default
 *   <li>Security-sensitive fields are not exposed in responses
 * </ul>
 */
@Component
@Slf4j
public class UserMapper {

  @Autowired private PasswordEncoder passwordEncoder;

  /**
   * Converts a UserCreateRequest to a UserEntity for database storage.
   *
   * @param request the user creation request
   * @return a new UserEntity with encoded password and default security settings
   */
  public UserEntity createRequestToEntity(UserCreateRequest request) {
    UserEntity entity = new UserEntity();
    entity.setUserName(request.getUserName());

    // Encode password for security
    entity.setPassword(passwordEncoder.encode(request.getPassword()));

    entity.setFirstName(request.getFirstName());
    entity.setLastName(request.getLastName());
    entity.setEmail(request.getEmail());

    // Set default security values
    entity.setRole(UserRole.REGULAR_USER);
    entity.setEnabled(true);
    entity.setAccountNonExpired(true);
    entity.setAccountNonLocked(true);
    entity.setCredentialsNonExpired(true);
    entity.setFailedLoginAttempts(0);

    // Set audit timestamps manually for consistency
    LocalDateTime now = LocalDateTime.now();
    entity.setCreatedOn(now);
    entity.setUpdatedOn(now);

    log.debug("Converted UserCreateRequest to UserEntity for user: {}", request.getUserName());
    return entity;
  }

  /**
   * Converts a UserEntity to a UserResponse for API responses.
   *
   * @param entity the user entity from the database
   * @return a UserResponse DTO with public user information
   */
  public UserResponse entityToResponse(UserEntity entity) {
    return new UserResponse(
        entity.getId(),
        entity.getUsername(), // Use getUsername() from UserDetails interface
        entity.getFirstName(),
        entity.getLastName(),
        entity.getEmail(),
        entity.getCreatedOn(),
        entity.getUpdatedOn());
  }

  /**
   * Converts a list of UserEntity objects to a list of UserResponse objects.
   *
   * @param entities the list of user entities
   * @return a list of UserResponse DTOs
   */
  public List<UserResponse> entityListToResponseList(List<UserEntity> entities) {
    return entities.stream().map(this::entityToResponse).toList();
  }

  /**
   * Updates an existing UserEntity with values from a UserUpdateRequest. Only non-null and
   * non-empty values are updated.
   *
   * @param entity the existing user entity to update
   * @param request the update request containing new values
   */
  public void updateEntityFromRequest(UserEntity entity, UserUpdateRequest request) {
    boolean updated = false;

    if (request.getFirstName() != null && !request.getFirstName().trim().isEmpty()) {
      entity.setFirstName(request.getFirstName());
      updated = true;
    }
    if (request.getLastName() != null && !request.getLastName().trim().isEmpty()) {
      entity.setLastName(request.getLastName());
      updated = true;
    }
    if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
      entity.setEmail(request.getEmail());
      updated = true;
    }

    // Always update timestamp - this will work in both unit tests and JPA context
    entity.setUpdatedOn(LocalDateTime.now());

    if (updated) {
      log.debug("Updated UserEntity for user: {}", entity.getUsername());
    }
  }

  /**
   * Creates a UserEntity for administrative purposes with specified role.
   *
   * @param request the user creation request
   * @param role the role to assign to the user
   * @return a new UserEntity with the specified role
   */
  public UserEntity createRequestToEntityWithRole(UserCreateRequest request, UserRole role) {
    UserEntity entity = createRequestToEntity(request);
    entity.setRole(role);
    log.debug("Created UserEntity with role {} for user: {}", role, request.getUserName());
    return entity;
  }
}
