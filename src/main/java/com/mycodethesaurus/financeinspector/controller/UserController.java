package com.mycodethesaurus.financeinspector.controller;

import com.mycodethesaurus.financeinspector.dto.UserCreateRequest;
import com.mycodethesaurus.financeinspector.dto.UserResponse;
import com.mycodethesaurus.financeinspector.dto.UserUpdateRequest;
import com.mycodethesaurus.financeinspector.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/users")
@Slf4j
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping
  public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserCreateRequest request) {
    log.info("Received request to create user: {}", request.getUserName());
    UserResponse response = userService.createUser(request);
    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }

  @GetMapping("/{userId}")
  public ResponseEntity<UserResponse> getUserById(@PathVariable Long userId) {
    log.info("Received request to get user by id: {}", userId);
    UserResponse response = userService.getUserById(userId);
    return ResponseEntity.ok(response);
  }

  @GetMapping
  public ResponseEntity<List<UserResponse>> getAllUsers() {
    log.info("Received request to get all users");
    List<UserResponse> response = userService.getAllUsers();
    return ResponseEntity.ok(response);
  }

  @PutMapping("/{userId}")
  public ResponseEntity<UserResponse> updateUser(
      @PathVariable Long userId, @Valid @RequestBody UserUpdateRequest request) {
    log.info("Received request to update user with id: {}", userId);
    UserResponse response = userService.updateUser(userId, request);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{userId}")
  public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
    log.info("Received request to delete user with id: {}", userId);
    userService.deleteUser(userId);
    return ResponseEntity.noContent().build();
  }
}
