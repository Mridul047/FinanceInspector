package com.mycodethesaurus.financeinspector.controller;

import com.mycodethesaurus.financeinspector.dto.CategoryResponse;
import com.mycodethesaurus.financeinspector.dto.ErrorResponse;
import com.mycodethesaurus.financeinspector.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/public/categories")
@Slf4j
@Tag(
    name = "Public Category Management",
    description =
        "Public APIs for accessing global expense categories (no authentication required)")
public class PublicCategoryController {

  private final CategoryService categoryService;

  public PublicCategoryController(CategoryService categoryService) {
    this.categoryService = categoryService;
  }

  @GetMapping
  @Operation(
      summary = "Get all global categories",
      description = "Retrieves all active global expense categories available system-wide")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Categories retrieved successfully",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = CategoryResponse.class),
                    examples =
                        @ExampleObject(
                            name = "Global Categories Response",
                            value =
                                """
                    [
                      {
                        "id": 1,
                        "name": "Food & Dining",
                        "description": "Restaurant meals and grocery expenses",
                        "colorCode": "#FF5722",
                        "iconName": "restaurant",
                        "parent": null,
                        "subcategories": [
                          {
                            "id": 6,
                            "name": "Groceries",
                            "colorCode": "#FF5722",
                            "iconName": "grocery_store"
                          }
                        ],
                        "userId": null,
                        "isActive": true,
                        "sortOrder": 1,
                        "createdOn": "2024-01-01T12:00:00",
                        "updatedOn": "2024-01-01T12:00:00"
                      }
                    ]"""))),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
      })
  public ResponseEntity<List<CategoryResponse>> getAllCategories() {
    log.info("Public request to get all global categories");
    List<CategoryResponse> response = categoryService.getAllGlobalCategories();
    return ResponseEntity.ok(response);
  }

  @GetMapping("/{categoryId}")
  @Operation(
      summary = "Get category by ID",
      description = "Retrieves a specific global expense category by its unique identifier")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Category found and returned successfully",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = CategoryResponse.class),
                    examples =
                        @ExampleObject(
                            name = "Category Details Response",
                            value =
                                """
                    {
                      "id": 1,
                      "name": "Food & Dining",
                      "description": "Restaurant meals and grocery expenses",
                      "colorCode": "#FF5722",
                      "iconName": "restaurant",
                      "parent": null,
                      "subcategories": [
                        {
                          "id": 6,
                          "name": "Groceries",
                          "colorCode": "#FF5722",
                          "iconName": "grocery_store"
                        },
                        {
                          "id": 7,
                          "name": "Restaurants",
                          "colorCode": "#FF5722",
                          "iconName": "restaurant"
                        }
                      ],
                      "userId": null,
                      "isActive": true,
                      "sortOrder": 1,
                      "createdOn": "2024-01-01T12:00:00",
                      "updatedOn": "2024-01-01T12:00:00"
                    }"""))),
        @ApiResponse(
            responseCode = "404",
            description = "Category not found with the specified ID",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
      })
  public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable Long categoryId) {
    log.info("Public request to get category with id: {}", categoryId);
    CategoryResponse response = categoryService.getGlobalCategoryById(categoryId);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/top-level")
  @Operation(
      summary = "Get top-level categories",
      description = "Retrieves all top-level (root) global categories without parent categories")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Top-level categories retrieved successfully",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = CategoryResponse.class),
                    examples =
                        @ExampleObject(
                            name = "Top-Level Categories Response",
                            value =
                                """
                    [
                      {
                        "id": 1,
                        "name": "Food & Dining",
                        "description": "Restaurant meals and grocery expenses",
                        "colorCode": "#FF5722",
                        "iconName": "restaurant",
                        "parent": null,
                        "subcategories": [],
                        "userId": null,
                        "isActive": true,
                        "sortOrder": 1
                      },
                      {
                        "id": 2,
                        "name": "Transportation",
                        "description": "Vehicle and travel related expenses",
                        "colorCode": "#2196F3",
                        "iconName": "directions_car",
                        "parent": null,
                        "subcategories": [],
                        "userId": null,
                        "isActive": true,
                        "sortOrder": 2
                      }
                    ]"""))),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
      })
  public ResponseEntity<List<CategoryResponse>> getTopLevelCategories() {
    log.info("Public request to get top-level categories");
    List<CategoryResponse> response = categoryService.getGlobalTopLevelCategories();
    return ResponseEntity.ok(response);
  }

  @GetMapping("/{parentId}/subcategories")
  @Operation(
      summary = "Get subcategories",
      description = "Retrieves all subcategories under the specified parent category")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Subcategories retrieved successfully",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = CategoryResponse.class),
                    examples =
                        @ExampleObject(
                            name = "Subcategories Response",
                            value =
                                """
                    [
                      {
                        "id": 6,
                        "name": "Groceries",
                        "description": "Supermarket and grocery shopping",
                        "colorCode": "#FF5722",
                        "iconName": "grocery_store",
                        "parent": {
                          "id": 1,
                          "name": "Food & Dining",
                          "colorCode": "#FF5722",
                          "iconName": "restaurant"
                        },
                        "subcategories": [],
                        "userId": null,
                        "isActive": true,
                        "sortOrder": 1
                      },
                      {
                        "id": 7,
                        "name": "Restaurants",
                        "description": "Dining out at restaurants",
                        "colorCode": "#FF5722",
                        "iconName": "restaurant",
                        "parent": {
                          "id": 1,
                          "name": "Food & Dining",
                          "colorCode": "#FF5722",
                          "iconName": "restaurant"
                        },
                        "subcategories": [],
                        "userId": null,
                        "isActive": true,
                        "sortOrder": 2
                      }
                    ]"""))),
        @ApiResponse(
            responseCode = "404",
            description = "Parent category not found",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
      })
  public ResponseEntity<List<CategoryResponse>> getSubcategories(@PathVariable Long parentId) {
    log.info("Public request to get subcategories for parent: {}", parentId);
    List<CategoryResponse> response = categoryService.getGlobalSubcategories(parentId);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/search")
  @Operation(
      summary = "Search categories",
      description = "Search global categories by name or description with optional filtering")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Search results retrieved successfully",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = CategoryResponse.class),
                    examples =
                        @ExampleObject(
                            name = "Search Results Response",
                            value =
                                """
                    [
                      {
                        "id": 1,
                        "name": "Food & Dining",
                        "description": "Restaurant meals and grocery expenses",
                        "colorCode": "#FF5722",
                        "iconName": "restaurant",
                        "parent": null,
                        "subcategories": [],
                        "userId": null,
                        "isActive": true,
                        "sortOrder": 1
                      },
                      {
                        "id": 8,
                        "name": "Fast Food",
                        "description": "Quick service restaurants",
                        "colorCode": "#FF5722",
                        "iconName": "fastfood",
                        "parent": {
                          "id": 1,
                          "name": "Food & Dining",
                          "colorCode": "#FF5722",
                          "iconName": "restaurant"
                        },
                        "subcategories": [],
                        "userId": null,
                        "isActive": true,
                        "sortOrder": 3
                      }
                    ]"""))),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid search parameters",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
      })
  public ResponseEntity<List<CategoryResponse>> searchCategories(
      @Parameter(
              description = "Search query to match category names or descriptions",
              example = "food")
          @RequestParam
          String query,
      @Parameter(description = "Search only top-level categories (no parent)", example = "false")
          @RequestParam(defaultValue = "false")
          Boolean parentOnly) {
    log.info(
        "Public request to search categories with query: '{}', parentOnly: {}", query, parentOnly);
    List<CategoryResponse> response = categoryService.searchGlobalCategories(query, parentOnly);
    return ResponseEntity.ok(response);
  }
}
