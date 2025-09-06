package com.mycodethesaurus.financeinspector.controller.api;

import com.mycodethesaurus.financeinspector.controller.api.common.ApiParameters;
import com.mycodethesaurus.financeinspector.dto.CategoryCreateRequest;
import com.mycodethesaurus.financeinspector.dto.CategoryResponse;
import com.mycodethesaurus.financeinspector.dto.ErrorResponse;
import com.mycodethesaurus.financeinspector.dto.ValidationErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import org.springframework.http.ResponseEntity;

/**
 * API interface for category management operations. Contains all OpenAPI documentation and method
 * signatures for both public and admin category operations.
 */
@Tag(
    name = "Category Management",
    description =
        "APIs for managing expense categories, including public read operations and admin write operations")
public interface CategoryControllerApi {

  // ========== PUBLIC CATEGORY OPERATIONS ==========

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
  ResponseEntity<List<CategoryResponse>> getAllCategories();

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
  ResponseEntity<CategoryResponse> getCategoryById(
      @Parameter(
              description = ApiParameters.CATEGORY_ID_DESC,
              required = true,
              example = ApiParameters.CATEGORY_ID_EXAMPLE)
          Long categoryId);

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
  ResponseEntity<List<CategoryResponse>> getTopLevelCategories();

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
  ResponseEntity<List<CategoryResponse>> getSubcategories(
      @Parameter(
              description =
                  "Unique identifier of the parent category to retrieve subcategories for",
              required = true,
              example = ApiParameters.CATEGORY_ID_EXAMPLE,
              schema = @Schema(type = "integer", format = "int64", minimum = "1"))
          Long parentId);

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
  ResponseEntity<List<CategoryResponse>> searchCategories(
      @Parameter(
              description = "Search query text to match against category names or descriptions",
              required = true,
              example = ApiParameters.QUERY_EXAMPLE,
              schema = @Schema(type = "string", minLength = 1, maxLength = 100))
          String query,
      @Parameter(
              description =
                  "Filter to return only parent categories (true) or all matching categories (false/null)",
              example = ApiParameters.PARENT_ONLY_EXAMPLE,
              schema = @Schema(type = "boolean", defaultValue = "false"))
          Boolean parentOnly);

  // ========== ADMIN CATEGORY OPERATIONS ==========

  @Operation(
      summary = "Create new global category",
      description = "Creates a new global expense category. Requires admin authentication.")
  @SecurityRequirement(name = "bearerAuth")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "Global category created successfully",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = CategoryResponse.class),
                    examples =
                        @ExampleObject(
                            name = "Created Category Response",
                            value =
                                """
                            {
                              "id": 31,
                              "name": "Entertainment",
                              "description": "Movies, games, and entertainment expenses",
                              "colorCode": "#E91E63",
                              "iconName": "local_movies",
                              "parent": null,
                              "subcategories": [],
                              "userId": null,
                              "isActive": true,
                              "sortOrder": 6,
                              "createdOn": "2024-01-15T10:30:00",
                              "updatedOn": "2024-01-15T10:30:00"
                            }"""))),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input data or validation errors",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ValidationErrorResponse.class))),
        @ApiResponse(
            responseCode = "401",
            description = "Authentication required",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Admin access required",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(
            responseCode = "404",
            description = "Parent category not found",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(
            responseCode = "409",
            description = "Category name already exists",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
      })
  ResponseEntity<CategoryResponse> createGlobalCategory(
      @Parameter(
              description =
                  "Category creation details including name, description, color, icon, and optional parent",
              required = true,
              schema = @Schema(implementation = CategoryCreateRequest.class),
              examples =
                  @ExampleObject(
                      name = "Category Creation Request",
                      value =
                          """
                      {
                        "name": "Entertainment",
                        "description": "Movies, games, and entertainment expenses",
                        "colorCode": "#E91E63",
                        "iconName": "local_movies",
                        "parentId": null,
                        "sortOrder": 6
                      }"""))
          @Valid
          CategoryCreateRequest request,
      Principal principal);

  @Operation(
      summary = "Update global category",
      description = "Updates an existing global expense category. Requires admin authentication.")
  @SecurityRequirement(name = "bearerAuth")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Global category updated successfully",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = CategoryResponse.class),
                    examples =
                        @ExampleObject(
                            name = "Updated Category Response",
                            value =
                                """
                            {
                              "id": 1,
                              "name": "Food & Dining Updated",
                              "description": "Updated description for restaurants and groceries",
                              "colorCode": "#FF6722",
                              "iconName": "restaurant",
                              "parent": null,
                              "subcategories": [],
                              "userId": null,
                              "isActive": true,
                              "sortOrder": 1,
                              "createdOn": "2024-01-01T12:00:00",
                              "updatedOn": "2024-01-15T14:45:00"
                            }"""))),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input data, validation errors, or circular reference",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ValidationErrorResponse.class))),
        @ApiResponse(
            responseCode = "401",
            description = "Authentication required",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Admin access required",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(
            responseCode = "404",
            description = "Category or parent category not found",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(
            responseCode = "409",
            description = "Category name already exists",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
      })
  ResponseEntity<CategoryResponse> updateGlobalCategory(
      @Parameter(
              description = ApiParameters.CATEGORY_ID_DESC,
              required = true,
              example = ApiParameters.CATEGORY_ID_EXAMPLE)
          Long categoryId,
      @Parameter(
              description =
                  "Updated category information including name, description, color, icon, and optional parent",
              required = true,
              schema = @Schema(implementation = CategoryCreateRequest.class),
              examples =
                  @ExampleObject(
                      name = "Category Update Request",
                      value =
                          """
                      {
                        "name": "Food & Dining Updated",
                        "description": "Updated description for restaurants and groceries",
                        "colorCode": "#FF6722",
                        "iconName": "restaurant",
                        "parentId": null,
                        "sortOrder": 1
                      }"""))
          @Valid
          CategoryCreateRequest request,
      Principal principal);

  @Operation(
      summary = "Delete global category",
      description =
          "Deletes a global expense category. Categories with subcategories or expenses will be soft-deleted (marked inactive). Requires admin authentication.")
  @SecurityRequirement(name = "bearerAuth")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "204", description = "Category deleted successfully"),
        @ApiResponse(
            responseCode = "400",
            description = "Cannot delete category with active subcategories",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(
            responseCode = "401",
            description = "Authentication required",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Admin access required",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(
            responseCode = "404",
            description = "Category not found with the specified ID",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
      })
  ResponseEntity<Void> deleteGlobalCategory(
      @Parameter(
              description = ApiParameters.CATEGORY_ID_DESC,
              required = true,
              example = ApiParameters.CATEGORY_ID_EXAMPLE)
          Long categoryId,
      Principal principal);

  @Operation(
      summary = "Activate global category",
      description =
          "Reactivates a previously deactivated global category. Requires admin authentication.")
  @SecurityRequirement(name = "bearerAuth")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Category activated successfully",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = CategoryResponse.class),
                    examples =
                        @ExampleObject(
                            name = "Activated Category Response",
                            value =
                                """
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
                              "sortOrder": 1,
                              "createdOn": "2024-01-01T12:00:00",
                              "updatedOn": "2024-01-15T16:20:00"
                            }"""))),
        @ApiResponse(
            responseCode = "401",
            description = "Authentication required",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Admin access required",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(
            responseCode = "404",
            description = "Category not found with the specified ID",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
      })
  ResponseEntity<CategoryResponse> activateGlobalCategory(
      @Parameter(
              description = ApiParameters.CATEGORY_ID_DESC,
              required = true,
              example = ApiParameters.CATEGORY_ID_EXAMPLE)
          Long categoryId,
      Principal principal);
}
