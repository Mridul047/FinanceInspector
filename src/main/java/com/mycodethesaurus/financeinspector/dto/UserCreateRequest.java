package com.mycodethesaurus.financeinspector.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "UserCreateRequest", description = "Request payload for creating a new user account")
public class UserCreateRequest {

  @Schema(
      description = "Unique username for the user account",
      example = "johndoe",
      minLength = 3,
      maxLength = 50,
      requiredMode = Schema.RequiredMode.REQUIRED)
  @NotBlank(message = "Username is required")
  @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
  private String userName;

  @Schema(
      description = "Secure password for the user account",
      example = "SecurePass123!",
      minLength = 8,
      maxLength = 100,
      requiredMode = Schema.RequiredMode.REQUIRED,
      format = "password")
  @NotBlank(message = "Password is required")
  @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
  private String password;

  @Schema(
      description = "User's first name",
      example = "John",
      maxLength = 100,
      requiredMode = Schema.RequiredMode.REQUIRED)
  @NotBlank(message = "First name is required")
  @Size(max = 100, message = "First name must not exceed 100 characters")
  private String firstName;

  @Schema(
      description = "User's last name",
      example = "Doe",
      maxLength = 100,
      requiredMode = Schema.RequiredMode.REQUIRED)
  @NotBlank(message = "Last name is required")
  @Size(max = 100, message = "Last name must not exceed 100 characters")
  private String lastName;

  @Schema(
      description = "User's email address (must be unique)",
      example = "john.doe@example.com",
      format = "email",
      requiredMode = Schema.RequiredMode.REQUIRED)
  @NotBlank(message = "Email is required")
  @Email(message = "Email must be valid")
  private String email;
}
