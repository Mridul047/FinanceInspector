package com.mycodethesaurus.financeinspector.service;

import com.mycodethesaurus.financeinspector.component.CategoryMapper;
import com.mycodethesaurus.financeinspector.dto.CategoryCreateRequest;
import com.mycodethesaurus.financeinspector.dto.CategoryResponse;
import com.mycodethesaurus.financeinspector.exception.DuplicateResourceException;
import com.mycodethesaurus.financeinspector.exception.ResourceNotFoundException;
import com.mycodethesaurus.financeinspector.persistence.entity.ExpenseCategoryEntity;
import com.mycodethesaurus.financeinspector.persistence.repository.ExpenseCategoryRepository;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
public class CategoryService {

  private final ExpenseCategoryRepository categoryRepository;
  private final CategoryMapper categoryMapper;

  public CategoryService(
      ExpenseCategoryRepository categoryRepository, CategoryMapper categoryMapper) {
    this.categoryRepository = categoryRepository;
    this.categoryMapper = categoryMapper;
  }

  // ========== GLOBAL CATEGORY METHODS ==========

  @Transactional(readOnly = true)
  public List<CategoryResponse> getAllGlobalCategories() {
    log.info("Fetching all global categories");
    List<ExpenseCategoryEntity> categories = categoryRepository.findAllActiveCategories();
    return categoryMapper.entitiesToResponses(categories);
  }

  @Transactional(readOnly = true)
  public CategoryResponse getGlobalCategoryById(Long categoryId) {
    log.info("Fetching global category with id: {}", categoryId);
    ExpenseCategoryEntity entity =
        categoryRepository
            .findActiveCategoryById(categoryId)
            .orElseThrow(
                () -> new ResourceNotFoundException("Category not found with id: " + categoryId));
    return categoryMapper.entityToResponse(entity);
  }

  @Transactional(readOnly = true)
  public List<CategoryResponse> getGlobalTopLevelCategories() {
    log.info("Fetching global top-level categories");
    List<ExpenseCategoryEntity> categories = categoryRepository.findAllRootCategories();
    return categoryMapper.entitiesToResponses(categories);
  }

  @Transactional(readOnly = true)
  public List<CategoryResponse> getGlobalSubcategories(Long parentId) {
    log.info("Fetching global subcategories for parent: {}", parentId);

    // Validate parent category exists
    categoryRepository
        .findActiveCategoryById(parentId)
        .orElseThrow(
            () -> new ResourceNotFoundException("Parent category not found with id: " + parentId));

    List<ExpenseCategoryEntity> subcategories =
        categoryRepository.findSubcategoriesByParentId(parentId);
    return categoryMapper.entitiesToResponses(subcategories);
  }

  @Transactional(readOnly = true)
  public List<CategoryResponse> searchGlobalCategories(String query, Boolean parentOnly) {
    log.info("Searching global categories with query: '{}', parentOnly: {}", query, parentOnly);

    List<ExpenseCategoryEntity> categories =
        Boolean.TRUE.equals(parentOnly)
            ? categoryRepository.searchTopLevelByNameOrDescription(query)
            : categoryRepository.searchByNameOrDescription(query);

    return categoryMapper.entitiesToResponses(categories);
  }

  // ========== ADMIN-ONLY METHODS ==========

  public CategoryResponse createGlobalCategory(CategoryCreateRequest request, String adminUser) {
    log.info("Creating global category '{}' by admin: {}", request.getName(), adminUser);

    // Validate category name uniqueness
    validateGlobalCategoryUniqueness(request.getName());

    // Get parent category if specified
    ExpenseCategoryEntity parent = null;
    if (request.getParentId() != null) {
      parent =
          categoryRepository
              .findActiveCategoryById(request.getParentId())
              .orElseThrow(
                  () ->
                      new ResourceNotFoundException(
                          "Parent category not found with id: " + request.getParentId()));
    }

    // Create entity
    ExpenseCategoryEntity entity = categoryMapper.createRequestToGlobalEntity(request, parent);
    entity.setCreatedBy(adminUser);
    entity.setUpdatedBy(adminUser);

    // Save entity
    ExpenseCategoryEntity savedEntity = categoryRepository.save(entity);

    log.info(
        "Global category created successfully with id: {} by admin: {}",
        savedEntity.getId(),
        adminUser);
    return categoryMapper.entityToResponse(savedEntity);
  }

  public CategoryResponse updateGlobalCategory(
      Long categoryId, CategoryCreateRequest request, String adminUser) {
    log.info("Updating global category with id: {} by admin: {}", categoryId, adminUser);

    ExpenseCategoryEntity entity =
        categoryRepository
            .findActiveCategoryById(categoryId)
            .orElseThrow(
                () -> new ResourceNotFoundException("Category not found with id: " + categoryId));

    // Get parent category if specified
    ExpenseCategoryEntity parent = null;
    if (request.getParentId() != null) {
      parent =
          categoryRepository
              .findActiveCategoryById(request.getParentId())
              .orElseThrow(
                  () ->
                      new ResourceNotFoundException(
                          "Parent category not found with id: " + request.getParentId()));

      // Prevent circular reference
      if (parent.getId().equals(categoryId)) {
        throw new IllegalArgumentException("Category cannot be its own parent");
      }

      // Check if new parent would create a cycle
      validateNoCyclicReference(entity, parent);
    }

    // Validate category name uniqueness (excluding current category)
    validateGlobalCategoryUniquenessForUpdate(categoryId, request.getName());

    // Update entity fields
    entity.setName(request.getName());
    entity.setDescription(request.getDescription());
    entity.setColorCode(request.getColorCode());
    entity.setIconName(request.getIconName());
    entity.setSortOrder(request.getSortOrder());
    entity.setParent(parent);
    entity.setUpdatedBy(adminUser);

    // Save updated entity
    ExpenseCategoryEntity savedEntity = categoryRepository.save(entity);

    log.info(
        "Global category updated successfully with id: {} by admin: {}",
        savedEntity.getId(),
        adminUser);
    return categoryMapper.entityToResponse(savedEntity);
  }

  public void deleteGlobalCategory(Long categoryId, String adminUser) {
    log.info("Deleting global category with id: {} by admin: {}", categoryId, adminUser);

    ExpenseCategoryEntity entity =
        categoryRepository
            .findActiveCategoryById(categoryId)
            .orElseThrow(
                () -> new ResourceNotFoundException("Category not found with id: " + categoryId));

    // Check if category has subcategories
    List<ExpenseCategoryEntity> subcategories =
        categoryRepository.findSubcategoriesByParentId(categoryId);
    if (!subcategories.isEmpty()) {
      throw new IllegalArgumentException("Cannot delete category with active subcategories");
    }

    // Check if category has expenses
    boolean hasExpenses = categoryRepository.hasExpenses(categoryId);
    if (hasExpenses) {
      // Soft delete - mark as inactive
      entity.setIsActive(false);
      entity.setUpdatedBy(adminUser);
      categoryRepository.save(entity);
      log.info(
          "Global category soft deleted (marked inactive) with id: {} by admin: {}",
          categoryId,
          adminUser);
    } else {
      // Hard delete
      categoryRepository.delete(entity);
      log.info("Global category hard deleted with id: {} by admin: {}", categoryId, adminUser);
    }
  }

  public CategoryResponse activateGlobalCategory(Long categoryId, String adminUser) {
    log.info("Activating global category with id: {} by admin: {}", categoryId, adminUser);

    ExpenseCategoryEntity entity =
        categoryRepository
            .findById(categoryId)
            .orElseThrow(
                () -> new ResourceNotFoundException("Category not found with id: " + categoryId));

    entity.setIsActive(true);
    entity.setUpdatedBy(adminUser);

    ExpenseCategoryEntity savedEntity = categoryRepository.save(entity);

    log.info(
        "Global category activated successfully with id: {} by admin: {}",
        savedEntity.getId(),
        adminUser);
    return categoryMapper.entityToResponse(savedEntity);
  }

  // ========== HELPER METHODS ==========

  private void validateNoCyclicReference(
      ExpenseCategoryEntity category, ExpenseCategoryEntity newParent) {
    ExpenseCategoryEntity current = newParent;
    while (current != null) {
      if (current.getId().equals(category.getId())) {
        throw new IllegalArgumentException("Moving category would create a circular reference");
      }
      current = current.getParent();
    }
  }

  private void validateGlobalCategoryUniqueness(String name) {
    if (categoryRepository.existsByNameIgnoreCase(name)) {
      throw new DuplicateResourceException("Category name '" + name + "' already exists");
    }
  }

  private void validateGlobalCategoryUniquenessForUpdate(Long categoryId, String name) {
    Optional<ExpenseCategoryEntity> existing = categoryRepository.findByNameIgnoreCase(name);
    if (existing.isPresent() && !existing.get().getId().equals(categoryId)) {
      throw new DuplicateResourceException("Category name '" + name + "' already exists");
    }
  }
}
