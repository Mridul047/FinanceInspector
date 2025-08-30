package com.mycodethesaurus.financeinspector.component;

import com.mycodethesaurus.financeinspector.dto.CategoryCreateRequest;
import com.mycodethesaurus.financeinspector.dto.CategoryResponse;
import com.mycodethesaurus.financeinspector.persistence.entity.ExpenseCategoryEntity;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

  /**
   * @deprecated Use createRequestToGlobalEntity instead - all categories are now global
   */
  @Deprecated
  public ExpenseCategoryEntity createRequestToEntity(
      CategoryCreateRequest request, Object user, ExpenseCategoryEntity parent) {
    return createRequestToGlobalEntity(request, parent);
  }

  public ExpenseCategoryEntity createRequestToGlobalEntity(
      CategoryCreateRequest request, ExpenseCategoryEntity parent) {
    ExpenseCategoryEntity category = new ExpenseCategoryEntity();
    category.setName(request.getName());
    category.setDescription(request.getDescription());
    category.setColorCode(request.getColorCode());
    category.setIconName(request.getIconName());
    category.setSortOrder(request.getSortOrder());
    category.setIsActive(true);
    category.setParent(parent);
    return category;
  }

  public CategoryResponse entityToResponse(ExpenseCategoryEntity category) {
    CategoryResponse response = new CategoryResponse();
    response.setId(category.getId());
    response.setName(category.getName());
    response.setDescription(category.getDescription());
    response.setColorCode(category.getColorCode());
    response.setIconName(category.getIconName());
    response.setIsActive(category.getIsActive());
    response.setSortOrder(category.getSortOrder());
    response.setUserId(null); // All categories are now global (no user association)

    // Add parent information if exists
    if (category.getParent() != null) {
      response.setParent(buildCategorySummary(category.getParent()));
    }

    // Add active subcategories list
    if (category.getSubcategories() != null) {
      List<CategoryResponse.CategorySummary> subcategories =
          category.getSubcategories().stream()
              .filter(ExpenseCategoryEntity::getIsActive)
              .map(this::buildCategorySummary)
              .collect(Collectors.toList());
      response.setSubcategories(subcategories);
    }

    response.setCreatedOn(category.getCreatedOn());
    response.setUpdatedOn(category.getUpdatedOn());
    return response;
  }

  public List<CategoryResponse> entitiesToResponses(List<ExpenseCategoryEntity> categories) {
    return categories.stream().map(this::entityToResponse).collect(Collectors.toList());
  }

  public CategoryResponse entityToResponseMinimal(ExpenseCategoryEntity category) {
    CategoryResponse response = new CategoryResponse();
    response.setId(category.getId());
    response.setName(category.getName());
    response.setDescription(category.getDescription());
    response.setColorCode(category.getColorCode());
    response.setIconName(category.getIconName());
    response.setIsActive(category.getIsActive());
    response.setSortOrder(category.getSortOrder());
    response.setUserId(null); // All categories are now global (no user association)

    if (category.getParent() != null) {
      response.setParent(buildCategorySummary(category.getParent()));
    }

    response.setCreatedOn(category.getCreatedOn());
    response.setUpdatedOn(category.getUpdatedOn());
    return response;
  }

  private CategoryResponse.CategorySummary buildCategorySummary(ExpenseCategoryEntity category) {
    CategoryResponse.CategorySummary summary = new CategoryResponse.CategorySummary();
    summary.setId(category.getId());
    summary.setName(category.getName());
    summary.setColorCode(category.getColorCode());
    summary.setIconName(category.getIconName());
    return summary;
  }
}
