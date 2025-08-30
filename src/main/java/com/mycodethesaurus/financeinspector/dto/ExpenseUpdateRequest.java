package com.mycodethesaurus.financeinspector.dto;

import com.mycodethesaurus.financeinspector.enums.PaymentMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(
    name = "ExpenseUpdateRequest",
    description = "Request payload for updating an existing expense")
public class ExpenseUpdateRequest {

  @NotNull(message = "Category ID is required")
  @Schema(
      description = "Updated category ID for the expense",
      example = "1",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private Long categoryId;

  @NotNull(message = "Amount is required")
  @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
  @Schema(
      description = "Updated expense amount",
      example = "29.99",
      minimum = "0.01",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private BigDecimal amount;

  @NotBlank(message = "Currency code is required")
  @Size(min = 3, max = 3, message = "Currency code must be exactly 3 characters")
  @Schema(
      description = "Updated ISO 4217 currency code",
      example = "USD",
      pattern = "^[A-Z]{3}$",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private String currencyCode;

  @NotBlank(message = "Description is required")
  @Size(max = 255, message = "Description must not exceed 255 characters")
  @Schema(
      description = "Updated expense description",
      example = "Dinner at downtown restaurant",
      maxLength = 255,
      requiredMode = Schema.RequiredMode.REQUIRED)
  private String description;

  @NotNull(message = "Expense date is required")
  @Schema(
      description = "Updated date when expense occurred",
      example = "2024-01-16",
      format = "date",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private LocalDate expenseDate;

  @Size(max = 100, message = "Location must not exceed 100 characters")
  @Schema(
      description = "Updated location where expense occurred",
      example = "City Center",
      maxLength = 100)
  private String location;

  @Size(max = 100, message = "Merchant name must not exceed 100 characters")
  @Schema(
      description = "Updated merchant or vendor name",
      example = "Olive Garden",
      maxLength = 100)
  private String merchant;

  @Schema(description = "Updated payment method used", example = "DEBIT_CARD")
  private PaymentMethod paymentMethod;

  @Schema(
      description = "Updated URL to receipt image",
      example = "https://receipts.example.com/receipt456.jpg")
  private String receiptUrl;

  @Size(max = 1000, message = "Notes must not exceed 1000 characters")
  @Schema(
      description = "Updated additional notes",
      example = "Business dinner with team",
      maxLength = 1000)
  private String notes;

  @Schema(
      description = "Updated tags for categorization",
      example = "[\"business\", \"dinner\", \"team\"]")
  private Set<String> tags = new HashSet<>();
}
