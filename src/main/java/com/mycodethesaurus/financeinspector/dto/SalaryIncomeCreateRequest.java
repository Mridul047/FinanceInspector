package com.mycodethesaurus.financeinspector.dto;

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
public class SalaryIncomeCreateRequest {

  @NotNull(message = "User ID is required")
  private Long userId;

  @NotBlank(message = "Currency code is required")
  @Size(min = 3, max = 3, message = "Currency code must be exactly 3 characters")
  private String currencyCode;

  @NotNull(message = "Basic amount is required")
  @DecimalMin(value = "0.0", message = "Basic amount must be greater than or equal to 0")
  private BigDecimal basicAmount;

  @NotNull(message = "HRA amount is required")
  @DecimalMin(value = "0.0", message = "HRA amount must be greater than or equal to 0")
  private BigDecimal hraAmount;

  @NotNull(message = "Other allowance amount is required")
  @DecimalMin(value = "0.0", message = "Other allowance amount must be greater than or equal to 0")
  private BigDecimal otherAllowanceAmount;

  @NotNull(message = "Bonus amount is required")
  @DecimalMin(value = "0.0", message = "Bonus amount must be greater than or equal to 0")
  private BigDecimal bonusAmount;

  @NotNull(message = "Employee PF amount is required")
  @DecimalMin(value = "0.0", message = "Employee PF amount must be greater than or equal to 0")
  private BigDecimal empPfAmount;

  @NotNull(message = "Profession tax amount is required")
  @DecimalMin(value = "0.0", message = "Profession tax amount must be greater than or equal to 0")
  private BigDecimal professionTaxAmount;

  @NotNull(message = "Income tax amount is required")
  @DecimalMin(value = "0.0", message = "Income tax amount must be greater than or equal to 0")
  private BigDecimal incomeTaxAmount;
}
