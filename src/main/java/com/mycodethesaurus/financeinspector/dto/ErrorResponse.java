package com.mycodethesaurus.financeinspector.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@Schema(name = "ErrorResponse", description = "Standard error response structure for API errors")
public class ErrorResponse {

  @Schema(
      description = "Timestamp when the error occurred",
      example = "2024-01-15T10:30:00",
      format = "date-time")
  private LocalDateTime timestamp;

  @Schema(description = "HTTP status code", example = "404")
  private int status;

  @Schema(description = "Error type or category", example = "Resource Not Found")
  private String error;

  @Schema(description = "Detailed error message", example = "User not found with id: 1")
  private String message;

  @Schema(description = "API endpoint path where the error occurred", example = "/v1/users/1")
  private String path;
}
