package com.mycodethesaurus.financeinspector.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "CategoryResponse", description = "Expense category response")
public class CategoryResponse {

  @Schema(description = "Category ID", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
  private Long id;

  @Schema(description = "Category name", example = "Food & Dining")
  private String name;

  @Schema(
      description = "Category description",
      example = "Restaurants, groceries, and dining expenses")
  private String description;

  @Schema(description = "Category color code", example = "#FF5722")
  private String colorCode;

  @Schema(description = "Category icon name", example = "restaurant")
  private String iconName;

  @Schema(description = "Parent category information")
  private CategorySummary parent;

  @Schema(description = "Subcategories list")
  private List<CategorySummary> subcategories;

  @Schema(description = "User ID (null for global categories)", example = "1")
  private Long userId;

  @Schema(description = "Whether category is active", example = "true")
  private Boolean isActive;

  @Schema(description = "Sort order", example = "1")
  private Integer sortOrder;

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
  }
}
