package com.mycodethesaurus.financeinspector.controller;

import com.mycodethesaurus.financeinspector.dto.CategoryCreateRequest;
import com.mycodethesaurus.financeinspector.dto.CategoryResponse;
import com.mycodethesaurus.financeinspector.dto.ErrorResponse;
import com.mycodethesaurus.financeinspector.dto.ValidationErrorResponse;
import com.mycodethesaurus.financeinspector.service.CategoryService;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
// import org.springframework.security.access.prepost.PreAuthorize; // TODO: Add Spring Security
// dependency
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/admin/categories")
@Slf4j
@Tag(
    name = "Admin Category Management",
    description =
        "Administrative APIs for managing global expense categories (requires admin authentication)")
@SecurityRequirement(name = "bearerAuth")
public class AdminCategoryController {

  private final CategoryService categoryService;

  public AdminCategoryController(CategoryService categoryService) {
    this.categoryService = categoryService;
  }

  @PostMapping
  // @PreAuthorize("hasRole('ADMIN')") // TODO: Enable when Spring Security is configured
  @Operation(
      summary = "Create new global category",
      description = "Creates a new global expense category. Requires admin authentication.")
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
  public ResponseEntity<CategoryResponse> createGlobalCategory(
      @Parameter(description = "Category creation details", required = true) @Valid @RequestBody
          CategoryCreateRequest request,
      Principal principal) {
    String adminUser = principal.getName();
    log.info("Admin '{}' creating global category: {}", adminUser, request.getName());
    CategoryResponse response = categoryService.createGlobalCategory(request, adminUser);
    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }

  @PutMapping("/{categoryId}")
  // @PreAuthorize("hasRole('ADMIN')") // TODO: Enable when Spring Security is configured
  @Operation(
      summary = "Update global category",
      description = "Updates an existing global expense category. Requires admin authentication.")
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
  public ResponseEntity<CategoryResponse> updateGlobalCategory(
      @Parameter(description = "Category ID", required = true, example = "1") @PathVariable
          Long categoryId,
      @Parameter(description = "Updated category information", required = true) @Valid @RequestBody
          CategoryCreateRequest request,
      Principal principal) {
    String adminUser = principal.getName();
    log.info(
        "Admin '{}' updating global category {} with name: {}",
        adminUser,
        categoryId,
        request.getName());
    CategoryResponse response =
        categoryService.updateGlobalCategory(categoryId, request, adminUser);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{categoryId}")
  // @PreAuthorize("hasRole('ADMIN')") // TODO: Enable when Spring Security is configured
  @Operation(
      summary = "Delete global category",
      description =
          "Deletes a global expense category. Categories with subcategories or expenses will be soft-deleted (marked inactive). Requires admin authentication.")
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
  public ResponseEntity<Void> deleteGlobalCategory(
      @Parameter(description = "Category ID", required = true, example = "1") @PathVariable
          Long categoryId,
      Principal principal) {
    String adminUser = principal.getName();
    log.info("Admin '{}' deleting global category: {}", adminUser, categoryId);
    categoryService.deleteGlobalCategory(categoryId, adminUser);
    return ResponseEntity.noContent().build();
  }

  @PutMapping("/{categoryId}/activate")
  // @PreAuthorize("hasRole('ADMIN')") // TODO: Enable when Spring Security is configured
  @Operation(
      summary = "Activate global category",
      description =
          "Reactivates a previously deactivated global category. Requires admin authentication.")
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
  public ResponseEntity<CategoryResponse> activateGlobalCategory(
      @Parameter(description = "Category ID", required = true, example = "1") @PathVariable
          Long categoryId,
      Principal principal) {
    String adminUser = principal.getName();
    log.info("Admin '{}' activating global category: {}", adminUser, categoryId);
    CategoryResponse response = categoryService.activateGlobalCategory(categoryId, adminUser);
    return ResponseEntity.ok(response);
  }
}
