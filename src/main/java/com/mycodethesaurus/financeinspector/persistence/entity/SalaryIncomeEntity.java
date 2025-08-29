package com.mycodethesaurus.financeinspector.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "salary_income", schema = "fip")
public class SalaryIncomeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private UserEntity userEntity;

  @Column(name = "currency_code", nullable = false, length = 3)
  private String currencyCode;

  @Column(name = "basic_amount", nullable = false, precision = 15, scale = 4)
  private BigDecimal basicAmount;

  @Column(name = "hra_amount", nullable = false, precision = 15, scale = 4)
  private BigDecimal hraAmount;

  @Column(name = "other_allowance_amount", nullable = false, precision = 15, scale = 4)
  private BigDecimal otherAllowanceAmount;

  @Column(name = "bonus_amount", nullable = false, precision = 15, scale = 4)
  private BigDecimal bonusAmount;

  @Column(name = "emp_pf_amount", nullable = false, precision = 15, scale = 4)
  private BigDecimal empPfAmount;

  @Column(name = "profession_tax_amount", nullable = false, precision = 15, scale = 4)
  private BigDecimal professionTaxAmount;

  @Column(name = "income_tax_amount", nullable = false, precision = 15, scale = 4)
  private BigDecimal incomeTaxAmount;

  @Column(name = "created_on", nullable = false)
  LocalDateTime createdOn;

  @Column(name = "updated_on", nullable = false)
  LocalDateTime updatedOn;
}
