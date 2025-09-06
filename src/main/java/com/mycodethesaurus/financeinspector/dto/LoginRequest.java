package com.mycodethesaurus.financeinspector.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for user login requests.
 *
 * <p>This DTO contains the credentials required for user authentication:
 *
 * <ul>
 *   <li><strong>username</strong> - The user's unique username
 *   <li><strong>password</strong> - The user's password (will be validated against encoded
 *       password)
 * </ul>
 *
 * <p>Security considerations:
 *
 * <ul>
 *   <li>Password is not logged or serialized for security
 *   <li>Input validation ensures non-empty credentials
 *   <li>Compatible with Spring Security authentication
 * </ul>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "LoginRequest", description = "User login credentials")
public class LoginRequest {

  /** The username for authentication. Must be between 3 and 50 characters and cannot be blank. */
  @Schema(
      description = "Username for authentication",
      example = "johndoe",
      minLength = 3,
      maxLength = 50,
      requiredMode = Schema.RequiredMode.REQUIRED)
  @NotBlank(message = "Username is required")
  @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
  private String username;

  /** The password for authentication. Must be between 8 and 100 characters and cannot be blank. */
  @Schema(
      description = "Password for authentication",
      example = "SecurePass123!",
      minLength = 8,
      maxLength = 100,
      format = "password",
      requiredMode = Schema.RequiredMode.REQUIRED)
  @NotBlank(message = "Password is required")
  @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
  private String password;

  /** Override toString to avoid logging sensitive password information. */
  @Override
  public String toString() {
    return "LoginRequest{username='" + username + "', password='[PROTECTED]'}";
  }
}
