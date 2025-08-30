package com.mycodethesaurus.financeinspector.persistence.repository;

import com.mycodethesaurus.financeinspector.persistence.entity.ExpenseCategoryEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpenseCategoryRepository extends JpaRepository<ExpenseCategoryEntity, Long> {

  // ========== GLOBAL CATEGORY METHODS ==========

  // Find all active categories (all categories are now global)
  @Query(
      """
      SELECT c FROM ExpenseCategoryEntity c
      WHERE c.isActive = true
      ORDER BY c.sortOrder ASC, c.name ASC
      """)
  List<ExpenseCategoryEntity> findAllActiveCategories();

  // Find category by ID (active only)
  @Query(
      """
      SELECT c FROM ExpenseCategoryEntity c
      WHERE c.id = :categoryId
      AND c.isActive = true
      """)
  Optional<ExpenseCategoryEntity> findActiveCategoryById(@Param("categoryId") Long categoryId);

  // Find root categories (no parent)
  @Query(
      """
      SELECT c FROM ExpenseCategoryEntity c
      WHERE c.parent IS NULL
      AND c.isActive = true
      ORDER BY c.sortOrder ASC, c.name ASC
      """)
  List<ExpenseCategoryEntity> findAllRootCategories();

  // Find subcategories by parent ID
  @Query(
      """
      SELECT c FROM ExpenseCategoryEntity c
      WHERE c.parent.id = :parentId
      AND c.isActive = true
      ORDER BY c.sortOrder ASC, c.name ASC
      """)
  List<ExpenseCategoryEntity> findSubcategoriesByParentId(@Param("parentId") Long parentId);

  // Check if category name exists (global uniqueness)
  @Query(
      """
      SELECT COUNT(c) > 0 FROM ExpenseCategoryEntity c
      WHERE LOWER(c.name) = LOWER(:name)
      AND c.isActive = true
      """)
  boolean existsByNameIgnoreCase(@Param("name") String name);

  // Find category by name
  @Query(
      """
      SELECT c FROM ExpenseCategoryEntity c
      WHERE LOWER(c.name) = LOWER(:name)
      AND c.isActive = true
      """)
  Optional<ExpenseCategoryEntity> findByNameIgnoreCase(@Param("name") String name);

  // Search categories by name or description
  @Query(
      """
      SELECT c FROM ExpenseCategoryEntity c
      WHERE c.isActive = true
      AND (LOWER(c.name) LIKE LOWER(CONCAT('%', :query, '%'))
           OR LOWER(c.description) LIKE LOWER(CONCAT('%', :query, '%')))
      ORDER BY c.sortOrder ASC, c.name ASC
      """)
  List<ExpenseCategoryEntity> searchByNameOrDescription(@Param("query") String query);

  // Search top-level categories only
  @Query(
      """
      SELECT c FROM ExpenseCategoryEntity c
      WHERE c.isActive = true
      AND c.parent IS NULL
      AND (LOWER(c.name) LIKE LOWER(CONCAT('%', :query, '%'))
           OR LOWER(c.description) LIKE LOWER(CONCAT('%', :query, '%')))
      ORDER BY c.sortOrder ASC, c.name ASC
      """)
  List<ExpenseCategoryEntity> searchTopLevelByNameOrDescription(@Param("query") String query);

  // Check if category has expenses
  @Query(
      """
      SELECT COUNT(e) > 0 FROM ExpenseEntity e
      WHERE e.category.id = :categoryId
      """)
  boolean hasExpenses(@Param("categoryId") Long categoryId);

  // Count total active categories
  @Query(
      """
      SELECT COUNT(c) FROM ExpenseCategoryEntity c
      WHERE c.isActive = true
      """)
  long countActiveCategories();
}
