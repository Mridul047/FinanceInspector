package com.mycodethesaurus.financeinspector.service;

import com.mycodethesaurus.financeinspector.component.ExpenseMapper;
import com.mycodethesaurus.financeinspector.dto.ExpenseCreateRequest;
import com.mycodethesaurus.financeinspector.dto.ExpenseResponse;
import com.mycodethesaurus.financeinspector.dto.ExpenseUpdateRequest;
import com.mycodethesaurus.financeinspector.exception.ResourceNotFoundException;
import com.mycodethesaurus.financeinspector.persistence.entity.ExpenseCategoryEntity;
import com.mycodethesaurus.financeinspector.persistence.entity.ExpenseEntity;
import com.mycodethesaurus.financeinspector.persistence.entity.UserEntity;
import com.mycodethesaurus.financeinspector.persistence.repository.ExpenseCategoryRepository;
import com.mycodethesaurus.financeinspector.persistence.repository.ExpenseRepository;
import com.mycodethesaurus.financeinspector.persistence.repository.UserRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
public class ExpenseService {

  private final ExpenseRepository expenseRepository;
  private final ExpenseCategoryRepository categoryRepository;
  private final UserRepository userRepository;
  private final ExpenseMapper expenseMapper;

  public ExpenseService(
      ExpenseRepository expenseRepository,
      ExpenseCategoryRepository categoryRepository,
      UserRepository userRepository,
      ExpenseMapper expenseMapper) {
    this.expenseRepository = expenseRepository;
    this.categoryRepository = categoryRepository;
    this.userRepository = userRepository;
    this.expenseMapper = expenseMapper;
  }

  public ExpenseResponse createExpense(Long userId, ExpenseCreateRequest request) {
    log.info("Creating expense for user: {} with amount: {}", userId, request.getAmount());

    // Validate user exists
    UserEntity user = getUserEntity(userId);

    // Validate and get category
    ExpenseCategoryEntity category = getCategoryEntity(request.getCategoryId(), userId);

    // Map request to entity
    ExpenseEntity entity = expenseMapper.createRequestToEntity(request, user, category);

    // Save entity
    ExpenseEntity savedEntity = expenseRepository.save(entity);

    log.info("Expense created successfully with id: {}", savedEntity.getId());
    return expenseMapper.entityToResponse(savedEntity);
  }

  @Transactional(readOnly = true)
  public ExpenseResponse getExpenseById(Long userId, Long expenseId) {
    log.info("Fetching expense with id: {} for user: {}", expenseId, userId);

    ExpenseEntity entity = getExpenseEntity(expenseId, userId);
    return expenseMapper.entityToResponse(entity);
  }

  @Transactional(readOnly = true)
  public Page<ExpenseResponse> getUserExpenses(Long userId, Pageable pageable) {
    log.info("Fetching expenses for user: {} with pagination: {}", userId, pageable);

    // Validate user exists
    getUserEntity(userId);

    Page<ExpenseEntity> entities =
        expenseRepository.findByUserIdOrderByExpenseDateDesc(userId, pageable);
    return entities.map(expenseMapper::entityToResponse);
  }

  @Transactional(readOnly = true)
  public Page<ExpenseResponse> searchExpenses(Long userId, String searchText, Pageable pageable) {
    log.info("Searching expenses for user: {} with text: {}", userId, searchText);

    // Validate user exists
    getUserEntity(userId);

    Page<ExpenseEntity> entities =
        expenseRepository.searchExpensesByText(userId, searchText, pageable);
    return entities.map(expenseMapper::entityToResponse);
  }

  @Transactional(readOnly = true)
  public Page<ExpenseResponse> getExpensesByCategory(
      Long userId, Long categoryId, Pageable pageable) {
    log.info("Fetching expenses by category: {} for user: {}", categoryId, userId);

    // Validate user exists
    getUserEntity(userId);

    // Validate category exists and belongs to user
    getCategoryEntity(categoryId, userId);

    Page<ExpenseEntity> entities =
        expenseRepository.findByUserIdAndCategoryIdOrderByExpenseDateDesc(
            userId, categoryId, pageable);
    return entities.map(expenseMapper::entityToResponse);
  }

  @Transactional(readOnly = true)
  public List<ExpenseResponse> getExpensesByDateRange(
      Long userId, LocalDate startDate, LocalDate endDate) {
    log.info("Fetching expenses for user: {} between {} and {}", userId, startDate, endDate);

    // Validate user exists
    getUserEntity(userId);

    List<ExpenseEntity> entities =
        expenseRepository.findByUserIdAndDateRange(userId, startDate, endDate);
    return expenseMapper.entitiesToResponses(entities);
  }

  @Transactional(readOnly = true)
  public long getExpenseCount(Long userId) {
    log.info("Getting expense count for user: {}", userId);

    // Validate user exists
    getUserEntity(userId);

    return expenseRepository.countByUserId(userId);
  }

  public ExpenseResponse updateExpense(Long userId, Long expenseId, ExpenseUpdateRequest request) {
    log.info("Updating expense with id: {} for user: {}", expenseId, userId);

    ExpenseEntity entity = getExpenseEntity(expenseId, userId);

    // Validate and get category if changed
    ExpenseCategoryEntity category = getCategoryEntity(request.getCategoryId(), userId);

    // Update entity
    expenseMapper.updateEntityFromRequest(entity, request, category);

    // Save updated entity
    ExpenseEntity savedEntity = expenseRepository.save(entity);

    log.info("Expense updated successfully with id: {}", savedEntity.getId());
    return expenseMapper.entityToResponse(savedEntity);
  }

  public void deleteExpense(Long userId, Long expenseId) {
    log.info("Deleting expense with id: {} for user: {}", expenseId, userId);

    ExpenseEntity entity = getExpenseEntity(expenseId, userId);

    expenseRepository.delete(entity);
    log.info("Expense deleted successfully with id: {}", expenseId);
  }

  @Transactional(readOnly = true)
  public List<ExpenseResponse> getRecentExpenses(Long userId, int limit) {
    log.info("Fetching {} recent expenses for user: {}", limit, userId);

    // Validate user exists
    getUserEntity(userId);

    Pageable pageable = Pageable.ofSize(limit);
    List<ExpenseEntity> entities = expenseRepository.findRecentExpensesByUserId(userId, pageable);
    return expenseMapper.entitiesToResponses(entities);
  }

  private UserEntity getUserEntity(Long userId) {
    return userRepository
        .findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
  }

  private ExpenseCategoryEntity getCategoryEntity(Long categoryId, Long userId) {
    return categoryRepository
        .findActiveCategoryById(categoryId)
        .orElseThrow(
            () -> new ResourceNotFoundException("Category not found with id: " + categoryId));
  }

  private ExpenseEntity getExpenseEntity(Long expenseId, Long userId) {
    return expenseRepository
        .findByIdAndUserId(expenseId, userId)
        .orElseThrow(
            () -> new ResourceNotFoundException("Expense not found with id: " + expenseId));
  }
}
