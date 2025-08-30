package com.mycodethesaurus.financeinspector.controller;

import com.mycodethesaurus.financeinspector.dto.ErrorResponse;
import com.mycodethesaurus.financeinspector.dto.UserCreateRequest;
import com.mycodethesaurus.financeinspector.dto.UserResponse;
import com.mycodethesaurus.financeinspector.dto.UserUpdateRequest;
import com.mycodethesaurus.financeinspector.dto.ValidationErrorResponse;
import com.mycodethesaurus.financeinspector.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/users")
@Slf4j
@Tag(
    name = "User Management",
    description =
        "APIs for managing user accounts, including creation, retrieval, updates, and deletion operations")
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping
  @Operation(
      summary = "Create a new user",
      description =
          "Creates a new user account with the provided information. Username and email must be unique.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "User created successfully",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserResponse.class),
                    examples =
                        @ExampleObject(
                            name = "Created User Response",
                            value =
                                """
                      {
                        "id": 1,
                        "userName": "johndoe",
                        "firstName": "John",
                        "lastName": "Doe",
                        "email": "john.doe@example.com",
                        "createdOn": "2024-01-15T10:30:00",
                        "updatedOn": "2024-01-15T10:30:00"
                      }"""))),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input data or validation errors",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ValidationErrorResponse.class))),
        @ApiResponse(
            responseCode = "409",
            description = "User with username or email already exists",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
      })
  public ResponseEntity<UserResponse> createUser(
      @Parameter(description = "User creation details", required = true) @Valid @RequestBody
          UserCreateRequest request) {
    log.info("Received request to create user: {}", request.getUserName());
    UserResponse response = userService.createUser(request);
    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }

  @GetMapping("/{userId}")
  @Operation(
      summary = "Get user by ID",
      description = "Retrieves a specific user by their unique identifier")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "User found and returned successfully",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserResponse.class),
                    examples =
                        @ExampleObject(
                            name = "User Details Response",
                            value =
                                """
                      {
                        "id": 1,
                        "userName": "johndoe",
                        "firstName": "John",
                        "lastName": "Doe",
                        "email": "john.doe@example.com",
                        "createdOn": "2024-01-15T10:30:00",
                        "updatedOn": "2024-01-15T10:30:00"
                      }"""))),
        @ApiResponse(
            responseCode = "404",
            description = "User not found with the specified ID",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
      })
  public ResponseEntity<UserResponse> getUserById(
      @Parameter(description = "User ID", required = true, example = "1") @PathVariable
          Long userId) {
    log.info("Received request to get user by id: {}", userId);
    UserResponse response = userService.getUserById(userId);
    return ResponseEntity.ok(response);
  }

  @GetMapping
  @Operation(
      summary = "Get all users",
      description = "Retrieves a list of all registered users in the system")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Users retrieved successfully",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserResponse.class),
                    examples =
                        @ExampleObject(
                            name = "Users List Response",
                            value =
                                """
                      [
                        {
                          "id": 1,
                          "userName": "johndoe",
                          "firstName": "John",
                          "lastName": "Doe",
                          "email": "john.doe@example.com",
                          "createdOn": "2024-01-15T10:30:00",
                          "updatedOn": "2024-01-15T10:30:00"
                        },
                        {
                          "id": 2,
                          "userName": "janedoe",
                          "firstName": "Jane",
                          "lastName": "Doe",
                          "email": "jane.doe@example.com",
                          "createdOn": "2024-01-15T11:00:00",
                          "updatedOn": "2024-01-15T11:00:00"
                        }
                      ]""")))
      })
  public ResponseEntity<List<UserResponse>> getAllUsers() {
    log.info("Received request to get all users");
    List<UserResponse> response = userService.getAllUsers();
    return ResponseEntity.ok(response);
  }

  @PutMapping("/{userId}")
  @Operation(
      summary = "Update user information",
      description =
          "Updates an existing user's information. Username and email must remain unique.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "User updated successfully",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserResponse.class),
                    examples =
                        @ExampleObject(
                            name = "Updated User Response",
                            value =
                                """
                      {
                        "id": 1,
                        "userName": "johndoe_updated",
                        "firstName": "John",
                        "lastName": "Doe",
                        "email": "john.doe.updated@example.com",
                        "createdOn": "2024-01-15T10:30:00",
                        "updatedOn": "2024-01-15T14:45:00"
                      }"""))),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input data or validation errors",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ValidationErrorResponse.class))),
        @ApiResponse(
            responseCode = "404",
            description = "User not found with the specified ID",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(
            responseCode = "409",
            description = "Username or email already exists for another user",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
      })
  public ResponseEntity<UserResponse> updateUser(
      @Parameter(description = "User ID", required = true, example = "1") @PathVariable Long userId,
      @Parameter(description = "Updated user information", required = true) @Valid @RequestBody
          UserUpdateRequest request) {
    log.info("Received request to update user with id: {}", userId);
    UserResponse response = userService.updateUser(userId, request);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{userId}")
  @Operation(
      summary = "Delete user",
      description = "Permanently deletes a user and all associated data from the system")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "204", description = "User deleted successfully"),
        @ApiResponse(
            responseCode = "404",
            description = "User not found with the specified ID",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
      })
  public ResponseEntity<Void> deleteUser(
      @Parameter(description = "User ID", required = true, example = "1") @PathVariable
          Long userId) {
    log.info("Received request to delete user with id: {}", userId);
    userService.deleteUser(userId);
    return ResponseEntity.noContent().build();
  }
}
