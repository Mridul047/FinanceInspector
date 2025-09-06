package com.mycodethesaurus.financeinspector.dto;

import com.mycodethesaurus.financeinspector.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for JWT authentication response.
 *
 * <p>This DTO contains the authentication response after successful login:
 *
 * <ul>
 *   <li><strong>token</strong> - The JWT access token for API requests
 *   <li><strong>refreshToken</strong> - The refresh token for token renewal
 *   <li><strong>type</strong> - Token type (always "Bearer")
 *   <li><strong>user</strong> - Basic user information
 *   <li><strong>expiresIn</strong> - Token expiration time in seconds
 * </ul>
 *
 * <p>Security considerations:
 *
 * <ul>
 *   <li>Contains all information needed for client-side authentication state
 *   <li>Tokens should be stored securely on the client side
 *   <li>Refresh token can be used to obtain new access tokens
 * </ul>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "JwtResponse", description = "JWT authentication response with user details")
public class JwtResponse {

  /** The JWT access token for authenticating API requests. */
  @Schema(
      description = "JWT access token for API authentication",
      example =
          "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJqb2huZG9lIiwidXNlcklkIjoxLCJyb2xlIjoiUk9MRV9VU0VSIiwiaWF0IjoxNjQwOTk1MjAwLCJleHAiOjE2NDEwODE2MDB9.signature",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private String token;

  /** The refresh token for obtaining new access tokens. */
  @Schema(
      description = "Refresh token for obtaining new access tokens",
      example =
          "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJqb2huZG9lIiwidHlwZSI6InJlZnJlc2giLCJpYXQiOjE2NDA5OTUyMDAsImV4cCI6MTY0MTYwMDAwMH0.signature")
  private String refreshToken;

  /** Token type (always "Bearer" for JWT tokens). */
  @Schema(description = "Token type", example = "Bearer", defaultValue = "Bearer")
  @Builder.Default
  private String type = "Bearer";

  /** User ID of the authenticated user. */
  @Schema(
      description = "Unique identifier of the authenticated user",
      example = "1",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private Long userId;

  /** Username of the authenticated user. */
  @Schema(
      description = "Username of the authenticated user",
      example = "johndoe",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private String username;

  /** Email address of the authenticated user. */
  @Schema(
      description = "Email address of the authenticated user",
      example = "john.doe@example.com",
      format = "email",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private String email;

  /** Role of the authenticated user. */
  @Schema(
      description = "Role of the authenticated user",
      example = "REGULAR_USER",
      allowableValues = {"REGULAR_USER", "ADMIN_USER"},
      requiredMode = Schema.RequiredMode.REQUIRED)
  private UserRole role;

  /** Token expiration time in seconds from now. */
  @Schema(
      description = "Access token expiration time in seconds",
      example = "86400",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private Long expiresIn;

  /** Refresh token expiration time in seconds from now. */
  @Schema(description = "Refresh token expiration time in seconds", example = "604800")
  private Long refreshExpiresIn;

  /**
   * Constructor for creating response with basic token information.
   *
   * @param token the JWT access token
   * @param userId the user's ID
   * @param username the username
   * @param email the user's email
   * @param role the user's role
   * @param expiresIn token expiration time in seconds
   */
  public JwtResponse(
      String token, Long userId, String username, String email, UserRole role, Long expiresIn) {
    this.token = token;
    this.userId = userId;
    this.username = username;
    this.email = email;
    this.role = role;
    this.expiresIn = expiresIn;
    this.type = "Bearer";
  }

  /**
   * Constructor for creating response with both access and refresh tokens.
   *
   * @param token the JWT access token
   * @param refreshToken the refresh token
   * @param userId the user's ID
   * @param username the username
   * @param email the user's email
   * @param role the user's role
   * @param expiresIn access token expiration time in seconds
   * @param refreshExpiresIn refresh token expiration time in seconds
   */
  public JwtResponse(
      String token,
      String refreshToken,
      Long userId,
      String username,
      String email,
      UserRole role,
      Long expiresIn,
      Long refreshExpiresIn) {
    this.token = token;
    this.refreshToken = refreshToken;
    this.userId = userId;
    this.username = username;
    this.email = email;
    this.role = role;
    this.expiresIn = expiresIn;
    this.refreshExpiresIn = refreshExpiresIn;
    this.type = "Bearer";
  }

  /**
   * Checks if the user has administrative privileges.
   *
   * @return true if the user is an admin, false otherwise
   */
  public boolean isAdmin() {
    return role != null && role.isAdmin();
  }

  /**
   * Checks if the user is a regular user.
   *
   * @return true if the user is a regular user, false otherwise
   */
  public boolean isRegularUser() {
    return role != null && role.isRegularUser();
  }
}
