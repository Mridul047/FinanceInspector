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
@Schema(name = "ExpenseCreateRequest", description = "Request payload for creating a new expense")
public class ExpenseCreateRequest {

  @NotNull(message = "Category ID is required")
  @Schema(
      description = "Category ID for the expense",
      example = "1",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private Long categoryId;

  @NotNull(message = "Amount is required")
  @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
  @Schema(
      description = "Expense amount",
      example = "25.99",
      minimum = "0.01",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private BigDecimal amount;

  @NotBlank(message = "Currency code is required")
  @Size(min = 3, max = 3, message = "Currency code must be exactly 3 characters")
  @Schema(
      description = "ISO 4217 currency code",
      example = "USD",
      pattern = "^[A-Z]{3}$",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private String currencyCode;

  @NotBlank(message = "Description is required")
  @Size(max = 255, message = "Description must not exceed 255 characters")
  @Schema(
      description = "Expense description",
      example = "Lunch at downtown restaurant",
      maxLength = 255,
      requiredMode = Schema.RequiredMode.REQUIRED)
  private String description;

  @NotNull(message = "Expense date is required")
  @Schema(
      description = "Date when expense occurred",
      example = "2024-01-15",
      format = "date",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private LocalDate expenseDate;

  @Size(max = 100, message = "Location must not exceed 100 characters")
  @Schema(
      description = "Location where expense occurred",
      example = "Downtown Mall",
      maxLength = 100)
  private String location;

  @Size(max = 100, message = "Merchant name must not exceed 100 characters")
  @Schema(description = "Merchant or vendor name", example = "McDonald's", maxLength = 100)
  private String merchant;

  @Schema(description = "Payment method used", example = "CREDIT_CARD")
  private PaymentMethod paymentMethod;

  @Schema(
      description = "URL to receipt image",
      example = "https://receipts.example.com/receipt123.jpg")
  private String receiptUrl;

  @Size(max = 1000, message = "Notes must not exceed 1000 characters")
  @Schema(
      description = "Additional notes",
      example = "Business lunch with client",
      maxLength = 1000)
  private String notes;

  @Schema(description = "Tags for categorization", example = "[\"business\", \"meal\", \"client\"]")
  private Set<String> tags = new HashSet<>();
}
