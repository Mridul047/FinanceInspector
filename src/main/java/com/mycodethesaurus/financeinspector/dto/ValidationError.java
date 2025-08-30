package com.mycodethesaurus.financeinspector.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(name = "ValidationError", description = "Individual field validation error details")
public class ValidationError {

  @Schema(description = "Name of the field that failed validation", example = "email")
  private String field;

  @Schema(description = "Validation error message for the field", example = "Email must be valid")
  private String message;
}
