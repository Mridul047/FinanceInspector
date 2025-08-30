package com.mycodethesaurus.financeinspector.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(
    name = "UserUpdateRequest",
    description = "Request payload for updating user account information")
public class UserUpdateRequest {

  @Schema(description = "Updated first name (optional)", example = "John", maxLength = 100)
  @Size(max = 100, message = "First name must not exceed 100 characters")
  private String firstName;

  @Schema(description = "Updated last name (optional)", example = "Doe", maxLength = 100)
  @Size(max = 100, message = "Last name must not exceed 100 characters")
  private String lastName;

  @Schema(
      description = "Updated email address (optional, must be unique)",
      example = "john.doe.updated@example.com",
      format = "email")
  @Email(message = "Email must be valid")
  private String email;
}
