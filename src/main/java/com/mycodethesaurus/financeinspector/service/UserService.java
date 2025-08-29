package com.mycodethesaurus.financeinspector.service;

import com.mycodethesaurus.financeinspector.component.UserMapper;
import com.mycodethesaurus.financeinspector.dto.UserCreateRequest;
import com.mycodethesaurus.financeinspector.dto.UserResponse;
import com.mycodethesaurus.financeinspector.dto.UserUpdateRequest;
import com.mycodethesaurus.financeinspector.exception.DuplicateResourceException;
import com.mycodethesaurus.financeinspector.exception.ResourceNotFoundException;
import com.mycodethesaurus.financeinspector.persistence.entity.UserEntity;
import com.mycodethesaurus.financeinspector.persistence.repository.UserRepository;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
public class UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;

  public UserService(UserRepository userRepository, UserMapper userMapper) {
    this.userRepository = userRepository;
    this.userMapper = userMapper;
  }

  public UserResponse createUser(UserCreateRequest request) {
    log.info("Creating user with username: {}", request.getUserName());

    // Validate unique constraints
    validateUserUniqueness(request);

    // Map request to entity
    UserEntity entity = userMapper.createRequestToEntity(request);

    // Save entity
    UserEntity savedEntity = userRepository.save(entity);

    log.info("User created successfully with id: {}", savedEntity.getId());
    return userMapper.entityToResponse(savedEntity);
  }

  @Transactional(readOnly = true)
  public UserResponse getUserById(Long userId) {
    log.info("Fetching user with id: {}", userId);

    UserEntity entity =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

    return userMapper.entityToResponse(entity);
  }

  @Transactional(readOnly = true)
  public List<UserResponse> getAllUsers() {
    log.info("Fetching all users");

    List<UserEntity> entities = userRepository.findAll();
    return userMapper.entityListToResponseList(entities);
  }

  public UserResponse updateUser(Long userId, UserUpdateRequest request) {
    log.info("Updating user with id: {}", userId);

    UserEntity entity =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

    // Validate unique constraints for updates
    validateUserUniquenessForUpdate(userId, request);

    // Update entity
    userMapper.updateEntityFromRequest(entity, request);

    // Save updated entity
    UserEntity savedEntity = userRepository.save(entity);

    log.info("User updated successfully with id: {}", savedEntity.getId());
    return userMapper.entityToResponse(savedEntity);
  }

  public void deleteUser(Long userId) {
    log.info("Deleting user with id: {}", userId);

    if (!userRepository.existsById(userId)) {
      throw new ResourceNotFoundException("User not found with id: " + userId);
    }

    userRepository.deleteById(userId);
    log.info("User deleted successfully with id: {}", userId);
  }

  private void validateUserUniqueness(UserCreateRequest request) {
    if (userRepository.existsByUserName(request.getUserName())) {
      throw new DuplicateResourceException("Username already exists: " + request.getUserName());
    }

    if (userRepository.existsByEmail(request.getEmail())) {
      throw new DuplicateResourceException("Email already exists: " + request.getEmail());
    }
  }

  private void validateUserUniquenessForUpdate(Long userId, UserUpdateRequest request) {
    if (request.getEmail() != null) {
      userRepository
          .findByEmailExcludingId(request.getEmail(), userId)
          .ifPresent(
              user -> {
                throw new DuplicateResourceException("Email already exists: " + request.getEmail());
              });
    }
  }
}
