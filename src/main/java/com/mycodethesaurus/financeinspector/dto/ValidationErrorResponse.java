package com.mycodethesaurus.financeinspector.dto;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class ValidationErrorResponse extends ErrorResponse {
  private List<ValidationError> validationErrors;
}
