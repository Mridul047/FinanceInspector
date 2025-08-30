package com.mycodethesaurus.financeinspector.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Schema(
    name = "ValidationErrorResponse",
    description =
        "Extended error response for validation failures with detailed field-level errors")
public class ValidationErrorResponse extends ErrorResponse {

  @Schema(
      description = "List of individual field validation errors",
      implementation = ValidationError.class)
  private List<ValidationError> validationErrors;
}
