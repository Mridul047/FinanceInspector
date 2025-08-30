package com.mycodethesaurus.financeinspector.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(
    name = "CategoryCreateRequest",
    description = "Request payload for creating a new expense category")
public class CategoryCreateRequest {

  @NotBlank(message = "Category name is required")
  @Size(max = 100, message = "Category name must not exceed 100 characters")
  @Schema(
      description = "Category name",
      example = "Food & Dining",
      maxLength = 100,
      requiredMode = Schema.RequiredMode.REQUIRED)
  private String name;

  @Size(max = 255, message = "Description must not exceed 255 characters")
  @Schema(
      description = "Category description",
      example = "Restaurants, groceries, and dining expenses",
      maxLength = 255)
  private String description;

  @Size(max = 7, message = "Color code must be in #RRGGBB format")
  @Schema(
      description = "Category color code in hex format",
      example = "#FF5722",
      pattern = "^#[0-9A-Fa-f]{6}$",
      maxLength = 7)
  private String colorCode;

  @Size(max = 50, message = "Icon name must not exceed 50 characters")
  @Schema(description = "Icon name for the category", example = "restaurant", maxLength = 50)
  private String iconName;

  @Schema(description = "Parent category ID for subcategories", example = "1")
  private Long parentId;

  @Schema(description = "Sort order for category display", example = "1")
  private Integer sortOrder;
}
