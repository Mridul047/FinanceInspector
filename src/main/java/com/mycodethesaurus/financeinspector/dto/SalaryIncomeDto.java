package com.mycodethesaurus.financeinspector.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalaryIncomeDto {
  private Long id;
  private Long userId;
  private String currencyCode;
  private BigDecimal basicAmount;
  private BigDecimal hraAmount;
  private BigDecimal otherAllowanceAmount;
  private BigDecimal bonusAmount;
  private BigDecimal empPfAmount;
  private BigDecimal professionTaxAmount;
  private BigDecimal incomeTaxAmount;
  private LocalDateTime createdOn;
  private LocalDateTime updatedOn;
}
