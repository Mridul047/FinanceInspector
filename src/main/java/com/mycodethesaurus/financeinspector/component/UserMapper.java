package com.mycodethesaurus.financeinspector.component;

import com.mycodethesaurus.financeinspector.dto.UserCreateRequest;
import com.mycodethesaurus.financeinspector.dto.UserResponse;
import com.mycodethesaurus.financeinspector.dto.UserUpdateRequest;
import com.mycodethesaurus.financeinspector.persistence.entity.UserEntity;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserMapper {

  public UserEntity createRequestToEntity(UserCreateRequest request) {
    UserEntity entity = new UserEntity();
    entity.setUserName(request.getUserName());
    entity.setPassword(request.getPassword()); // Note: In production, this should be hashed
    entity.setFirstName(request.getFirstName());
    entity.setLastName(request.getLastName());
    entity.setEmail(request.getEmail());
    entity.setCreatedOn(LocalDateTime.now());
    entity.setUpdatedOn(LocalDateTime.now());
    return entity;
  }

  public UserResponse entityToResponse(UserEntity entity) {
    return new UserResponse(
        entity.getId(),
        entity.getUserName(),
        entity.getFirstName(),
        entity.getLastName(),
        entity.getEmail(),
        entity.getCreatedOn(),
        entity.getUpdatedOn());
  }

  public List<UserResponse> entityListToResponseList(List<UserEntity> entities) {
    return entities.stream().map(this::entityToResponse).toList();
  }

  public void updateEntityFromRequest(UserEntity entity, UserUpdateRequest request) {
    if (request.getFirstName() != null && !request.getFirstName().trim().isEmpty()) {
      entity.setFirstName(request.getFirstName());
    }
    if (request.getLastName() != null && !request.getLastName().trim().isEmpty()) {
      entity.setLastName(request.getLastName());
    }
    if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
      entity.setEmail(request.getEmail());
    }
    entity.setUpdatedOn(LocalDateTime.now());
  }
}
