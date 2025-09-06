package com.mycodethesaurus.financeinspector.controller.api;

import com.mycodethesaurus.financeinspector.controller.api.common.ApiParameters;
import com.mycodethesaurus.financeinspector.dto.ErrorResponse;
import com.mycodethesaurus.financeinspector.dto.UserCreateRequest;
import com.mycodethesaurus.financeinspector.dto.UserResponse;
import com.mycodethesaurus.financeinspector.dto.UserUpdateRequest;
import com.mycodethesaurus.financeinspector.dto.ValidationErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;

/**
 * API interface for user management operations. Contains all OpenAPI documentation and method
 * signatures.
 */
@Tag(
    name = "User Management",
    description =
        "APIs for managing user accounts, including creation, retrieval, updates, and deletion operations")
public interface UserControllerApi {

  @Operation(
      summary = "Create a new user",
      description =
          "Creates a new user account with the provided information. Username and email must be unique. Requires admin authentication.")
  @SecurityRequirement(name = "bearerAuth")
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
                    schema = @Schema(implementation = ValidationErrorResponse.class),
                    examples =
                        @ExampleObject(
                            name = "Validation Error",
                            value =
                                """
                            {
                              "message": "Validation failed. Please check your request data.",
                              "error": "Bad Request",
                              "status": 400,
                              "timestamp": "2024-01-15T10:30:00",
                              "path": "/v1/users",
                              "details": "Username must be between 3 and 50 characters"
                            }"""))),
        @ApiResponse(
            responseCode = "401",
            description = "Authentication required",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples =
                        @ExampleObject(
                            name = "Authentication Required",
                            value =
                                """
                            {
                              "message": "Authentication required. Please provide a valid JWT token.",
                              "error": "Unauthorized",
                              "status": 401,
                              "timestamp": "2024-01-15T10:30:00",
                              "path": "/v1/users"
                            }"""))),
        @ApiResponse(
            responseCode = "403",
            description = "Admin access required",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples =
                        @ExampleObject(
                            name = "Insufficient Privileges",
                            value =
                                """
                            {
                              "message": "Admin access required to perform this operation",
                              "error": "Forbidden",
                              "status": 403,
                              "timestamp": "2024-01-15T10:30:00",
                              "path": "/v1/users"
                            }"""))),
        @ApiResponse(
            responseCode = "409",
            description = "User with username or email already exists",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples =
                        @ExampleObject(
                            name = "Duplicate User",
                            value =
                                """
                            {
                              "message": "User with username 'johndoe' already exists",
                              "error": "Conflict",
                              "status": 409,
                              "timestamp": "2024-01-15T10:30:00",
                              "path": "/v1/users"
                            }"""))),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
      })
  ResponseEntity<UserResponse> createUser(
      @Parameter(
              description =
                  "User creation details with username, email, first name, last name, and password",
              required = true,
              schema = @Schema(implementation = UserCreateRequest.class),
              examples =
                  @ExampleObject(
                      name = "User Creation Request",
                      value =
                          """
                      {
                        "userName": "johndoe",
                        "firstName": "John",
                        "lastName": "Doe",
                        "email": "john.doe@example.com",
                        "password": "SecurePassword123!",
                        "role": "REGULAR_USER"
                      }"""))
          @Valid
          UserCreateRequest request);

  @Operation(
      summary = "Get user by ID",
      description =
          "Retrieves a specific user by their unique identifier. Requires authentication.")
  @SecurityRequirement(name = "bearerAuth")
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
            responseCode = "401",
            description = "Authentication required",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples =
                        @ExampleObject(
                            name = "Authentication Required",
                            value =
                                """
                            {
                              "message": "Authentication required. Please provide a valid JWT token.",
                              "error": "Unauthorized",
                              "status": 401,
                              "timestamp": "2024-01-15T10:30:00",
                              "path": "/v1/users/1"
                            }"""))),
        @ApiResponse(
            responseCode = "403",
            description = "Access forbidden - user can only access own data",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples =
                        @ExampleObject(
                            name = "Access Forbidden",
                            value =
                                """
                            {
                              "message": "Access forbidden. You can only access your own user data.",
                              "error": "Forbidden",
                              "status": 403,
                              "timestamp": "2024-01-15T10:30:00",
                              "path": "/v1/users/1"
                            }"""))),
        @ApiResponse(
            responseCode = "404",
            description = "User not found with the specified ID",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples =
                        @ExampleObject(
                            name = "User Not Found",
                            value =
                                """
                            {
                              "message": "User not found with ID: 1",
                              "error": "Not Found",
                              "status": 404,
                              "timestamp": "2024-01-15T10:30:00",
                              "path": "/v1/users/1"
                            }"""))),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
      })
  ResponseEntity<UserResponse> getUserById(
      @Parameter(
              description = ApiParameters.USER_ID_DESC,
              required = true,
              example = ApiParameters.USER_ID_EXAMPLE)
          Long userId);

  @Operation(
      summary = "Get all users",
      description =
          "Retrieves a list of all registered users in the system. Requires admin authentication.")
  @SecurityRequirement(name = "bearerAuth")
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
                            ]"""))),
        @ApiResponse(
            responseCode = "401",
            description = "Authentication required",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples =
                        @ExampleObject(
                            name = "Authentication Required",
                            value =
                                """
                           {
                             "message": "Authentication required. Please provide a valid JWT token.",
                             "error": "Unauthorized",
                             "status": 401,
                             "timestamp": "2024-01-15T10:30:00",
                             "path": "/v1/users"
                           }"""))),
        @ApiResponse(
            responseCode = "403",
            description = "Admin access required",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples =
                        @ExampleObject(
                            name = "Admin Access Required",
                            value =
                                """
                           {
                             "message": "Admin access required to view all users",
                             "error": "Forbidden",
                             "status": 403,
                             "timestamp": "2024-01-15T10:30:00",
                             "path": "/v1/users"
                           }"""))),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
      })
  ResponseEntity<List<UserResponse>> getAllUsers();

  @Operation(
      summary = "Update user information",
      description =
          "Updates an existing user's information. Username and email must remain unique. Users can update their own data or admins can update any user.")
  @SecurityRequirement(name = "bearerAuth")
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
                    schema = @Schema(implementation = ValidationErrorResponse.class),
                    examples =
                        @ExampleObject(
                            name = "Validation Error",
                            value =
                                """
                            {
                              "message": "Validation failed. Please check your request data.",
                              "error": "Bad Request",
                              "status": 400,
                              "timestamp": "2024-01-15T10:30:00",
                              "path": "/v1/users/1",
                              "details": "Email format is invalid"
                            }"""))),
        @ApiResponse(
            responseCode = "401",
            description = "Authentication required",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples =
                        @ExampleObject(
                            name = "Authentication Required",
                            value =
                                """
                            {
                              "message": "Authentication required. Please provide a valid JWT token.",
                              "error": "Unauthorized",
                              "status": 401,
                              "timestamp": "2024-01-15T10:30:00",
                              "path": "/v1/users/1"
                            }"""))),
        @ApiResponse(
            responseCode = "403",
            description = "Access forbidden - user can only update own data",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples =
                        @ExampleObject(
                            name = "Access Forbidden",
                            value =
                                """
                            {
                              "message": "Access forbidden. You can only update your own user data.",
                              "error": "Forbidden",
                              "status": 403,
                              "timestamp": "2024-01-15T10:30:00",
                              "path": "/v1/users/1"
                            }"""))),
        @ApiResponse(
            responseCode = "404",
            description = "User not found with the specified ID",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples =
                        @ExampleObject(
                            name = "User Not Found",
                            value =
                                """
                            {
                              "message": "User not found with ID: 1",
                              "error": "Not Found",
                              "status": 404,
                              "timestamp": "2024-01-15T10:30:00",
                              "path": "/v1/users/1"
                            }"""))),
        @ApiResponse(
            responseCode = "409",
            description = "Username or email already exists for another user",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples =
                        @ExampleObject(
                            name = "Duplicate Data",
                            value =
                                """
                            {
                              "message": "Email 'john.doe@example.com' is already in use by another user",
                              "error": "Conflict",
                              "status": 409,
                              "timestamp": "2024-01-15T10:30:00",
                              "path": "/v1/users/1"
                            }"""))),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
      })
  ResponseEntity<UserResponse> updateUser(
      @Parameter(
              description = ApiParameters.USER_ID_DESC,
              required = true,
              example = ApiParameters.USER_ID_EXAMPLE)
          Long userId,
      @Parameter(
              description =
                  "Updated user information including first name, last name, email, and optionally password",
              required = true,
              schema = @Schema(implementation = UserUpdateRequest.class),
              examples =
                  @ExampleObject(
                      name = "User Update Request",
                      value =
                          """
                      {
                        "firstName": "John",
                        "lastName": "Doe Updated",
                        "email": "john.doe.updated@example.com",
                        "password": "NewSecurePassword123!"
                      }"""))
          @Valid
          UserUpdateRequest request);

  @Operation(
      summary = "Delete user",
      description =
          "Permanently deletes a user and all associated data from the system. Requires admin authentication or user can delete own account.")
  @SecurityRequirement(name = "bearerAuth")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "204", description = "User deleted successfully"),
        @ApiResponse(
            responseCode = "401",
            description = "Authentication required",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples =
                        @ExampleObject(
                            name = "Authentication Required",
                            value =
                                """
                            {
                              "message": "Authentication required. Please provide a valid JWT token.",
                              "error": "Unauthorized",
                              "status": 401,
                              "timestamp": "2024-01-15T10:30:00",
                              "path": "/v1/users/1"
                            }"""))),
        @ApiResponse(
            responseCode = "403",
            description = "Access forbidden - insufficient privileges",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples =
                        @ExampleObject(
                            name = "Access Forbidden",
                            value =
                                """
                            {
                              "message": "Access forbidden. You can only delete your own account or admin privileges required.",
                              "error": "Forbidden",
                              "status": 403,
                              "timestamp": "2024-01-15T10:30:00",
                              "path": "/v1/users/1"
                            }"""))),
        @ApiResponse(
            responseCode = "404",
            description = "User not found with the specified ID",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples =
                        @ExampleObject(
                            name = "User Not Found",
                            value =
                                """
                            {
                              "message": "User not found with ID: 1",
                              "error": "Not Found",
                              "status": 404,
                              "timestamp": "2024-01-15T10:30:00",
                              "path": "/v1/users/1"
                            }"""))),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
      })
  ResponseEntity<Void> deleteUser(
      @Parameter(
              description = ApiParameters.USER_ID_DESC,
              required = true,
              example = ApiParameters.USER_ID_EXAMPLE)
          Long userId);
}
