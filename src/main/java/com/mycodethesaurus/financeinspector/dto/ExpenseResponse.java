package com.mycodethesaurus.financeinspector.dto;

import com.mycodethesaurus.financeinspector.enums.PaymentMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ExpenseResponse", description = "Expense details response")
public class ExpenseResponse {

  @Schema(description = "Expense ID", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
  private Long id;

  @Schema(description = "User ID who created the expense", example = "1")
  private Long userId;

  @Schema(description = "Category information")
  private CategorySummary category;

  @Schema(description = "Expense amount", example = "25.99")
  private BigDecimal amount;

  @Schema(description = "Currency code", example = "USD")
  private String currencyCode;

  @Schema(description = "Expense description", example = "Lunch at downtown restaurant")
  private String description;

  @Schema(description = "Date when expense occurred", example = "2024-01-15", format = "date")
  private LocalDate expenseDate;

  @Schema(description = "Location where expense occurred", example = "Downtown Mall")
  private String location;

  @Schema(description = "Merchant name", example = "McDonald's")
  private String merchant;

  @Schema(description = "Payment method", example = "CREDIT_CARD")
  private PaymentMethod paymentMethod;

  @Schema(description = "Receipt URL", example = "https://receipts.example.com/receipt123.jpg")
  private String receiptUrl;

  @Schema(description = "Additional notes", example = "Business lunch with client")
  private String notes;

  @Schema(description = "Expense tags", example = "[\"business\", \"meal\", \"client\"]")
  private Set<String> tags;

  @Schema(
      description = "Creation timestamp",
      example = "2024-01-15T10:30:00",
      format = "date-time",
      accessMode = Schema.AccessMode.READ_ONLY)
  private LocalDateTime createdOn;

  @Schema(
      description = "Last update timestamp",
      example = "2024-01-15T14:45:00",
      format = "date-time",
      accessMode = Schema.AccessMode.READ_ONLY)
  private LocalDateTime updatedOn;

  @Schema(name = "CategorySummary", description = "Category summary information")
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class CategorySummary {

    @Schema(description = "Category ID", example = "1")
    private Long id;

    @Schema(description = "Category name", example = "Food & Dining")
    private String name;

    @Schema(description = "Category color code", example = "#FF5722")
    private String colorCode;

    @Schema(description = "Category icon name", example = "restaurant")
    private String iconName;

    @Schema(description = "Parent category information")
    private CategorySummary parent;
  }
}
