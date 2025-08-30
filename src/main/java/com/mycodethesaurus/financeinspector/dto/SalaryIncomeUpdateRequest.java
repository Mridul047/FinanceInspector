package com.mycodethesaurus.financeinspector.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(
    name = "SalaryIncomeUpdateRequest",
    description =
        "Request payload for updating an existing salary income record with new salary breakdown")
public class SalaryIncomeUpdateRequest {

  @Schema(
      description = "Updated three-letter ISO currency code",
      example = "USD",
      pattern = "^[A-Z]{3}$",
      minLength = 3,
      maxLength = 3,
      requiredMode = Schema.RequiredMode.REQUIRED)
  @NotBlank(message = "Currency code is required")
  @Size(min = 3, max = 3, message = "Currency code must be exactly 3 characters")
  private String currencyCode;

  @Schema(
      description = "Updated basic salary amount before allowances",
      example = "5500.00",
      minimum = "0",
      type = "number",
      format = "decimal",
      requiredMode = Schema.RequiredMode.REQUIRED)
  @NotNull(message = "Basic amount is required")
  @DecimalMin(value = "0.0", message = "Basic amount must be greater than or equal to 0")
  private BigDecimal basicAmount;

  @Schema(
      description = "Updated House Rent Allowance (HRA) amount",
      example = "1100.00",
      minimum = "0",
      type = "number",
      format = "decimal",
      requiredMode = Schema.RequiredMode.REQUIRED)
  @NotNull(message = "HRA amount is required")
  @DecimalMin(value = "0.0", message = "HRA amount must be greater than or equal to 0")
  private BigDecimal hraAmount;

  @Schema(
      description = "Updated other allowances (transport, meal, etc.)",
      example = "600.00",
      minimum = "0",
      type = "number",
      format = "decimal",
      requiredMode = Schema.RequiredMode.REQUIRED)
  @NotNull(message = "Other allowance amount is required")
  @DecimalMin(value = "0.0", message = "Other allowance amount must be greater than or equal to 0")
  private BigDecimal otherAllowanceAmount;

  @Schema(
      description = "Updated bonus or incentive amount",
      example = "2500.00",
      minimum = "0",
      type = "number",
      format = "decimal",
      requiredMode = Schema.RequiredMode.REQUIRED)
  @NotNull(message = "Bonus amount is required")
  @DecimalMin(value = "0.0", message = "Bonus amount must be greater than or equal to 0")
  private BigDecimal bonusAmount;

  @Schema(
      description = "Updated Employee Provident Fund (PF) contribution amount",
      example = "660.00",
      minimum = "0",
      type = "number",
      format = "decimal",
      requiredMode = Schema.RequiredMode.REQUIRED)
  @NotNull(message = "Employee PF amount is required")
  @DecimalMin(value = "0.0", message = "Employee PF amount must be greater than or equal to 0")
  private BigDecimal empPfAmount;

  @Schema(
      description = "Updated professional tax deduction amount",
      example = "200.00",
      minimum = "0",
      type = "number",
      format = "decimal",
      requiredMode = Schema.RequiredMode.REQUIRED)
  @NotNull(message = "Profession tax amount is required")
  @DecimalMin(value = "0.0", message = "Profession tax amount must be greater than or equal to 0")
  private BigDecimal professionTaxAmount;

  @Schema(
      description = "Updated income tax deduction amount",
      example = "1650.00",
      minimum = "0",
      type = "number",
      format = "decimal",
      requiredMode = Schema.RequiredMode.REQUIRED)
  @NotNull(message = "Income tax amount is required")
  @DecimalMin(value = "0.0", message = "Income tax amount must be greater than or equal to 0")
  private BigDecimal incomeTaxAmount;
}
