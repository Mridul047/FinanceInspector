package com.mycodethesaurus.financeinspector.persistence.repository;

import com.mycodethesaurus.financeinspector.persistence.entity.ExpenseEntity;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpenseRepository extends JpaRepository<ExpenseEntity, Long> {

  // Find expense by ID and user ID (for security)
  Optional<ExpenseEntity> findByIdAndUserId(Long id, Long userId);

  // Find all expenses for a user with pagination
  Page<ExpenseEntity> findByUserIdOrderByExpenseDateDesc(Long userId, Pageable pageable);

  // Find expenses by user and date range
  @Query(
      """
      SELECT e FROM ExpenseEntity e
      WHERE e.user.id = :userId
      AND e.expenseDate BETWEEN :startDate AND :endDate
      ORDER BY e.expenseDate DESC
      """)
  List<ExpenseEntity> findByUserIdAndDateRange(
      @Param("userId") Long userId,
      @Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate);

  // Find expenses by user and category
  Page<ExpenseEntity> findByUserIdAndCategoryIdOrderByExpenseDateDesc(
      Long userId, Long categoryId, Pageable pageable);

  // Find recent expenses for a user
  @Query(
      """
      SELECT e FROM ExpenseEntity e
      WHERE e.user.id = :userId
      ORDER BY e.createdOn DESC
      """)
  List<ExpenseEntity> findRecentExpensesByUserId(@Param("userId") Long userId, Pageable pageable);

  // Search expenses by description or merchant
  @Query(
      """
      SELECT e FROM ExpenseEntity e
      WHERE e.user.id = :userId
      AND (LOWER(e.description) LIKE LOWER(CONCAT('%', :searchText, '%'))
           OR LOWER(e.merchant) LIKE LOWER(CONCAT('%', :searchText, '%')))
      ORDER BY e.expenseDate DESC
      """)
  Page<ExpenseEntity> searchExpensesByText(
      @Param("userId") Long userId, @Param("searchText") String searchText, Pageable pageable);

  // Count expenses by user
  long countByUserId(Long userId);

  // Check if expense exists for user
  boolean existsByIdAndUserId(Long id, Long userId);

  // Delete all expenses for a user
  void deleteByUserId(Long userId);
}
