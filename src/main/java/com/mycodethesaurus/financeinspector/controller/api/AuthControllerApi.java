package com.mycodethesaurus.financeinspector.controller.api;

import com.mycodethesaurus.financeinspector.dto.ErrorResponse;
import com.mycodethesaurus.financeinspector.dto.JwtResponse;
import com.mycodethesaurus.financeinspector.dto.LoginRequest;
import com.mycodethesaurus.financeinspector.dto.RefreshTokenRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Authentication Controller API specification for JWT-based authentication.
 *
 * <p>This interface defines the authentication endpoints for the Finance Inspector application. It
 * provides comprehensive OpenAPI documentation for all authentication operations including user
 * login, token refresh, and logout functionality.
 *
 * <p>Authentication Flow:
 *
 * <ol>
 *   <li>Client sends login credentials to /v1/auth/login
 *   <li>Server validates credentials and returns JWT tokens
 *   <li>Client includes access token in Authorization header for API requests
 *   <li>When access token expires, client uses refresh token to get new tokens
 *   <li>Client calls logout endpoint to invalidate tokens when done
 * </ol>
 *
 * <p>Security Features:
 *
 * <ul>
 *   <li>JWT access tokens with configurable expiration
 *   <li>Refresh tokens for seamless token renewal
 *   <li>Token rotation for enhanced security
 *   <li>Comprehensive audit logging
 *   <li>Standardized error responses
 * </ul>
 */
@Tag(
    name = "Authentication",
    description =
        "Authentication and authorization operations for user login, token management, and logout")
public interface AuthControllerApi {

  /**
   * Authenticates user credentials and returns JWT tokens.
   *
   * @param loginRequest the user login credentials
   * @return JWT response with access and refresh tokens
   */
  @Operation(
      summary = "Authenticate user and obtain JWT tokens",
      description =
          """
            Validates user credentials and returns JWT access and refresh tokens upon successful authentication.

            The access token should be included in the Authorization header for subsequent API requests:
            `Authorization: Bearer {access_token}`

            The refresh token can be used to obtain new access tokens when they expire without requiring
            the user to re-authenticate.

            **Authentication Process:**
            1. Validate username and password format
            2. Authenticate credentials against user database
            3. Generate JWT access token (short-lived)
            4. Generate JWT refresh token (long-lived)
            5. Return user details and tokens
            """)
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Authentication successful",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = JwtResponse.class),
                    examples =
                        @ExampleObject(
                            name = "Successful Authentication",
                            value =
                                """
                        {
                          "token": "eyJhbGciOiJIUzUxMiJ9...",
                          "refreshToken": "eyJhbGciOiJIUzUxMiJ9...",
                          "type": "Bearer",
                          "expiresIn": 3600,
                          "userId": 123,
                          "username": "john.doe",
                          "email": "john.doe@example.com",
                          "role": "REGULAR_USER"
                        }
                        """))),
        @ApiResponse(
            responseCode = "401",
            description = "Authentication failed - invalid credentials",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples =
                        @ExampleObject(
                            name = "Invalid Credentials",
                            value =
                                """
                        {
                          "message": "Invalid username or password",
                          "error": "Unauthorized",
                          "status": 401,
                          "timestamp": "2024-01-15T10:30:00",
                          "path": "/v1/auth/login"
                        }
                        """))),
        @ApiResponse(
            responseCode = "400",
            description = "Bad request - invalid input format",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
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
                          "path": "/v1/auth/login",
                          "details": "Username is required"
                        }
                        """))),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
      })
  ResponseEntity<JwtResponse> login(
      @Parameter(
              description = "User login credentials",
              required = true,
              schema = @Schema(implementation = LoginRequest.class))
          @Valid
          @RequestBody
          LoginRequest loginRequest);

  /**
   * Refreshes an expired access token using a valid refresh token.
   *
   * @param refreshRequest the refresh token request
   * @return new JWT tokens
   */
  @Operation(
      summary = "Refresh JWT access token",
      description =
          """
            Generates a new access token using a valid refresh token. This allows users to obtain
            new access tokens without re-authentication when their current access token expires.

            **Token Refresh Process:**
            1. Validate refresh token format and signature
            2. Check token expiration and authenticity
            3. Extract user information from token
            4. Generate new access token
            5. Optionally rotate refresh token for enhanced security

            **Security Note:** For enhanced security, this endpoint implements refresh token rotation,
            meaning a new refresh token is also generated and the old one is invalidated.
            """)
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Token refreshed successfully",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = JwtResponse.class),
                    examples =
                        @ExampleObject(
                            name = "Token Refresh Success",
                            value =
                                """
                        {
                          "token": "eyJhbGciOiJIUzUxMiJ9...",
                          "refreshToken": "eyJhbGciOiJIUzUxMiJ9...",
                          "type": "Bearer",
                          "expiresIn": 3600,
                          "userId": 123,
                          "username": "john.doe",
                          "email": "john.doe@example.com",
                          "role": "REGULAR_USER"
                        }
                        """))),
        @ApiResponse(
            responseCode = "401",
            description = "Invalid or expired refresh token",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples =
                        @ExampleObject(
                            name = "Invalid Refresh Token",
                            value =
                                """
                        {
                          "message": "Invalid or expired JWT token. Please login again.",
                          "error": "Unauthorized",
                          "status": 401,
                          "timestamp": "2024-01-15T10:30:00",
                          "path": "/v1/auth/refresh"
                        }
                        """))),
        @ApiResponse(
            responseCode = "400",
            description = "Bad request - missing or invalid refresh token format",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error during token refresh",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
      })
  ResponseEntity<JwtResponse> refreshToken(
      @Parameter(
              description = "Refresh token request containing the refresh token",
              required = true,
              schema = @Schema(implementation = RefreshTokenRequest.class))
          @Valid
          @RequestBody
          RefreshTokenRequest refreshRequest);

  /**
   * Logs out the current user and invalidates JWT tokens.
   *
   * @param request the HTTP servlet request
   * @return success response
   */
  @Operation(
      summary = "Logout user and invalidate tokens",
      description =
          """
            Logs out the currently authenticated user and invalidates their JWT tokens.

            **Logout Process:**
            1. Validate current authentication status
            2. Clear security context
            3. Log logout event for audit purposes
            4. Optionally add tokens to blacklist (if implemented)

            **Client Responsibilities:**
            After calling this endpoint, the client should:
            - Remove all stored JWT tokens from local storage
            - Clear any cached user data
            - Redirect to login page or public area

            **Note:** While JWT tokens are stateless by nature, this endpoint provides a clean
            logout mechanism and can be extended to implement token blacklisting if required.
            """)
  @SecurityRequirement(name = "bearerAuth")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Logout successful",
            content =
                @Content(
                    examples =
                        @ExampleObject(
                            name = "Logout Success",
                            value = "No content - logout successful"))),
        @ApiResponse(
            responseCode = "401",
            description = "No valid authentication found",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples =
                        @ExampleObject(
                            name = "Not Authenticated",
                            value =
                                """
                        {
                          "message": "Authentication required. Please provide a valid JWT token.",
                          "error": "Unauthorized",
                          "status": 401,
                          "timestamp": "2024-01-15T10:30:00",
                          "path": "/v1/auth/logout"
                        }
                        """))),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error during logout",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
      })
  ResponseEntity<Void> logout(HttpServletRequest request);
}
