package com.mycodethesaurus.financeinspector.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "UserResponse", description = "User account information response")
public class UserResponse {

  @Schema(
      description = "Unique identifier for the user",
      example = "1",
      accessMode = Schema.AccessMode.READ_ONLY)
  private Long id;

  @Schema(description = "Unique username for the user account", example = "johndoe")
  private String userName;

  @Schema(description = "User's first name", example = "John")
  private String firstName;

  @Schema(description = "User's last name", example = "Doe")
  private String lastName;

  @Schema(description = "User's email address", example = "john.doe@example.com", format = "email")
  private String email;

  @Schema(
      description = "Timestamp when the user account was created",
      example = "2024-01-15T10:30:00",
      format = "date-time",
      accessMode = Schema.AccessMode.READ_ONLY)
  private LocalDateTime createdOn;

  @Schema(
      description = "Timestamp when the user account was last updated",
      example = "2024-01-15T14:45:00",
      format = "date-time",
      accessMode = Schema.AccessMode.READ_ONLY)
  private LocalDateTime updatedOn;
}
