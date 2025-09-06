package com.mycodethesaurus.financeinspector.controller;

import com.mycodethesaurus.financeinspector.controller.api.CategoryControllerApi;
import com.mycodethesaurus.financeinspector.dto.CategoryCreateRequest;
import com.mycodethesaurus.financeinspector.dto.CategoryResponse;
import com.mycodethesaurus.financeinspector.service.CategoryService;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/categories")
@Slf4j
public class CategoryController implements CategoryControllerApi {

  private final CategoryService categoryService;

  public CategoryController(CategoryService categoryService) {
    this.categoryService = categoryService;
  }

  // ========== PUBLIC CATEGORY OPERATIONS ==========

  @GetMapping
  @Override
  public ResponseEntity<List<CategoryResponse>> getAllCategories() {
    log.info("Public request to get all global categories");
    List<CategoryResponse> response = categoryService.getAllGlobalCategories();
    return ResponseEntity.ok(response);
  }

  @GetMapping("/{categoryId}")
  @Override
  public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable Long categoryId) {
    log.info("Public request to get category with id: {}", categoryId);
    CategoryResponse response = categoryService.getGlobalCategoryById(categoryId);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/top-level")
  @Override
  public ResponseEntity<List<CategoryResponse>> getTopLevelCategories() {
    log.info("Public request to get top-level categories");
    List<CategoryResponse> response = categoryService.getGlobalTopLevelCategories();
    return ResponseEntity.ok(response);
  }

  @GetMapping("/{parentId}/subcategories")
  @Override
  public ResponseEntity<List<CategoryResponse>> getSubcategories(@PathVariable Long parentId) {
    log.info("Public request to get subcategories for parent: {}", parentId);
    List<CategoryResponse> response = categoryService.getGlobalSubcategories(parentId);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/search")
  @Override
  public ResponseEntity<List<CategoryResponse>> searchCategories(
      @RequestParam String query, @RequestParam(defaultValue = "false") Boolean parentOnly) {
    log.info(
        "Public request to search categories with query: '{}', parentOnly: {}", query, parentOnly);
    List<CategoryResponse> response = categoryService.searchGlobalCategories(query, parentOnly);
    return ResponseEntity.ok(response);
  }

  // ========== ADMIN CATEGORY OPERATIONS ==========

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  @Override
  public ResponseEntity<CategoryResponse> createGlobalCategory(
      @Valid @RequestBody CategoryCreateRequest request, Principal principal) {
    String adminUser = principal.getName();
    log.info("Admin '{}' creating global category: {}", adminUser, request.getName());
    CategoryResponse response = categoryService.createGlobalCategory(request, adminUser);
    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }

  @PutMapping("/{categoryId}")
  @PreAuthorize("hasRole('ADMIN')")
  @Override
  public ResponseEntity<CategoryResponse> updateGlobalCategory(
      @PathVariable Long categoryId,
      @Valid @RequestBody CategoryCreateRequest request,
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
  @PreAuthorize("hasRole('ADMIN')")
  @Override
  public ResponseEntity<Void> deleteGlobalCategory(
      @PathVariable Long categoryId, Principal principal) {
    String adminUser = principal.getName();
    log.info("Admin '{}' deleting global category: {}", adminUser, categoryId);
    categoryService.deleteGlobalCategory(categoryId, adminUser);
    return ResponseEntity.noContent().build();
  }

  @PutMapping("/{categoryId}/activate")
  @PreAuthorize("hasRole('ADMIN')")
  @Override
  public ResponseEntity<CategoryResponse> activateGlobalCategory(
      @PathVariable Long categoryId, Principal principal) {
    String adminUser = principal.getName();
    log.info("Admin '{}' activating global category: {}", adminUser, categoryId);
    CategoryResponse response = categoryService.activateGlobalCategory(categoryId, adminUser);
    return ResponseEntity.ok(response);
  }
}
