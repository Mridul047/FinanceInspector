package com.mycodethesaurus.financeinspector.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(
    name = "SalaryIncomeDto",
    description =
        "Salary income record with comprehensive salary breakdown including allowances and deductions")
public class SalaryIncomeDto {

  @Schema(
      description = "Unique identifier for the salary income record",
      example = "1",
      accessMode = Schema.AccessMode.READ_ONLY)
  private Long id;

  @Schema(description = "ID of the user this salary record belongs to", example = "1")
  private Long userId;

  @Schema(description = "Three-letter ISO currency code", example = "USD", pattern = "^[A-Z]{3}$")
  private String currencyCode;

  @Schema(
      description = "Basic salary amount before allowances",
      example = "5000.0000",
      type = "number",
      format = "decimal")
  private BigDecimal basicAmount;

  @Schema(
      description = "House Rent Allowance (HRA) amount",
      example = "1000.0000",
      type = "number",
      format = "decimal")
  private BigDecimal hraAmount;

  @Schema(
      description = "Other allowances (transport, meal, etc.)",
      example = "500.0000",
      type = "number",
      format = "decimal")
  private BigDecimal otherAllowanceAmount;

  @Schema(
      description = "Bonus or incentive amount",
      example = "2000.0000",
      type = "number",
      format = "decimal")
  private BigDecimal bonusAmount;

  @Schema(
      description = "Employee Provident Fund (PF) contribution amount",
      example = "600.0000",
      type = "number",
      format = "decimal")
  private BigDecimal empPfAmount;

  @Schema(
      description = "Professional tax deduction amount",
      example = "200.0000",
      type = "number",
      format = "decimal")
  private BigDecimal professionTaxAmount;

  @Schema(
      description = "Income tax deduction amount",
      example = "1500.0000",
      type = "number",
      format = "decimal")
  private BigDecimal incomeTaxAmount;

  @Schema(
      description = "Timestamp when the record was created",
      example = "2024-01-15T10:30:00",
      format = "date-time",
      accessMode = Schema.AccessMode.READ_ONLY)
  private LocalDateTime createdOn;

  @Schema(
      description = "Timestamp when the record was last updated",
      example = "2024-01-15T14:45:00",
      format = "date-time",
      accessMode = Schema.AccessMode.READ_ONLY)
  private LocalDateTime updatedOn;
}
