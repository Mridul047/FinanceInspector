package com.mycodethesaurus.financeinspector.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.mycodethesaurus.financeinspector.component.ExpenseMapper;
import com.mycodethesaurus.financeinspector.dto.ExpenseCreateRequest;
import com.mycodethesaurus.financeinspector.dto.ExpenseResponse;
import com.mycodethesaurus.financeinspector.dto.ExpenseUpdateRequest;
import com.mycodethesaurus.financeinspector.enums.PaymentMethod;
import com.mycodethesaurus.financeinspector.exception.ResourceNotFoundException;
import com.mycodethesaurus.financeinspector.persistence.entity.ExpenseCategoryEntity;
import com.mycodethesaurus.financeinspector.persistence.entity.ExpenseEntity;
import com.mycodethesaurus.financeinspector.persistence.entity.UserEntity;
import com.mycodethesaurus.financeinspector.persistence.repository.ExpenseCategoryRepository;
import com.mycodethesaurus.financeinspector.persistence.repository.ExpenseRepository;
import com.mycodethesaurus.financeinspector.persistence.repository.UserRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
@DisplayName("ExpenseService Unit Tests")
class ExpenseServiceTest {

  @Mock private ExpenseRepository expenseRepository;

  @Mock private UserRepository userRepository;

  @Mock private ExpenseCategoryRepository categoryRepository;

  @Mock private ExpenseMapper expenseMapper;

  @InjectMocks private ExpenseService expenseService;

  private UserEntity userEntity;
  private ExpenseCategoryEntity categoryEntity;
  private ExpenseEntity expenseEntity;
  private ExpenseCreateRequest createRequest;
  private ExpenseUpdateRequest updateRequest;
  private ExpenseResponse expenseResponse;
  private ExpenseResponse.CategorySummary categorySummary;

  @BeforeEach
  void setUp() {
    userEntity = new UserEntity();
    userEntity.setId(1L);
    userEntity.setUserName("testuser");
    userEntity.setFirstName("John");
    userEntity.setLastName("Doe");
    userEntity.setEmail("john.doe@example.com");

    categoryEntity = new ExpenseCategoryEntity();
    categoryEntity.setId(1L);
    categoryEntity.setName("Food & Dining");
    categoryEntity.setDescription("Restaurant meals");
    categoryEntity.setIsActive(true);

    expenseEntity = new ExpenseEntity();
    expenseEntity.setId(1L);
    expenseEntity.setUser(userEntity);
    expenseEntity.setCategory(categoryEntity);
    expenseEntity.setAmount(new BigDecimal("25.50"));
    expenseEntity.setCurrencyCode("USD");
    expenseEntity.setDescription("Lunch at restaurant");
    expenseEntity.setExpenseDate(LocalDate.now());
    expenseEntity.setPaymentMethod(PaymentMethod.CREDIT_CARD);
    expenseEntity.setCreatedOn(LocalDateTime.now());
    expenseEntity.setUpdatedOn(LocalDateTime.now());

    createRequest = new ExpenseCreateRequest();
    createRequest.setCategoryId(1L);
    createRequest.setAmount(new BigDecimal("25.50"));
    createRequest.setDescription("Lunch at restaurant");
    createRequest.setExpenseDate(LocalDate.now());
    createRequest.setPaymentMethod(PaymentMethod.CREDIT_CARD);

    updateRequest = new ExpenseUpdateRequest();
    updateRequest.setCategoryId(1L);
    updateRequest.setAmount(new BigDecimal("30.00"));
    updateRequest.setDescription("Updated lunch expense");
    updateRequest.setExpenseDate(LocalDate.now());
    updateRequest.setPaymentMethod(PaymentMethod.CASH);

    categorySummary = new ExpenseResponse.CategorySummary();
    categorySummary.setId(1L);
    categorySummary.setName("Food & Dining");
    categorySummary.setColorCode("#FF5722");
    categorySummary.setIconName("restaurant");

    expenseResponse = new ExpenseResponse();
    expenseResponse.setId(1L);
    expenseResponse.setUserId(1L);
    expenseResponse.setCategory(categorySummary);
    expenseResponse.setAmount(new BigDecimal("25.50"));
    expenseResponse.setCurrencyCode("USD");
    expenseResponse.setDescription("Lunch at restaurant");
    expenseResponse.setExpenseDate(LocalDate.now());
    expenseResponse.setPaymentMethod(PaymentMethod.CREDIT_CARD);
    expenseResponse.setCreatedOn(LocalDateTime.now());
    expenseResponse.setUpdatedOn(LocalDateTime.now());
  }

  @Test
  @DisplayName("Should create expense successfully")
  void shouldCreateExpenseSuccessfully() {
    // Given
    Long userId = 1L;
    when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
    when(categoryRepository.findActiveCategoryById(1L)).thenReturn(Optional.of(categoryEntity));
    when(expenseMapper.createRequestToEntity(createRequest, userEntity, categoryEntity))
        .thenReturn(expenseEntity);
    when(expenseRepository.save(expenseEntity)).thenReturn(expenseEntity);
    when(expenseMapper.entityToResponse(expenseEntity)).thenReturn(expenseResponse);

    // When
    ExpenseResponse result = expenseService.createExpense(userId, createRequest);

    // Then
    assertNotNull(result);
    assertEquals(new BigDecimal("25.50"), result.getAmount());
    assertEquals("Lunch at restaurant", result.getDescription());

    verify(userRepository).findById(userId);
    verify(categoryRepository).findActiveCategoryById(1L);
    verify(expenseMapper).createRequestToEntity(createRequest, userEntity, categoryEntity);
    verify(expenseRepository).save(expenseEntity);
    verify(expenseMapper).entityToResponse(expenseEntity);
  }

  @Test
  @DisplayName("Should throw exception when user not found for expense creation")
  void shouldThrowExceptionWhenUserNotFoundForCreation() {
    // Given
    Long userId = 999L;
    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    // When & Then
    ResourceNotFoundException exception =
        assertThrows(
            ResourceNotFoundException.class,
            () -> expenseService.createExpense(userId, createRequest));

    assertEquals("User not found with id: " + userId, exception.getMessage());
    verify(userRepository).findById(userId);
    verify(expenseRepository, never()).save(any());
  }

  @Test
  @DisplayName("Should throw exception when category not found for expense creation")
  void shouldThrowExceptionWhenCategoryNotFoundForCreation() {
    // Given
    Long userId = 1L;
    when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
    when(categoryRepository.findActiveCategoryById(1L)).thenReturn(Optional.empty());

    // When & Then
    ResourceNotFoundException exception =
        assertThrows(
            ResourceNotFoundException.class,
            () -> expenseService.createExpense(userId, createRequest));

    assertEquals("Category not found with id: 1", exception.getMessage());
    verify(userRepository).findById(userId);
    verify(categoryRepository).findActiveCategoryById(1L);
    verify(expenseRepository, never()).save(any());
  }

  @Test
  @DisplayName("Should get expense by ID successfully")
  void shouldGetExpenseByIdSuccessfully() {
    // Given
    Long userId = 1L;
    Long expenseId = 1L;
    when(expenseRepository.findByIdAndUserId(expenseId, userId))
        .thenReturn(Optional.of(expenseEntity));
    when(expenseMapper.entityToResponse(expenseEntity)).thenReturn(expenseResponse);

    // When
    ExpenseResponse result = expenseService.getExpenseById(userId, expenseId);

    // Then
    assertNotNull(result);
    assertEquals(expenseId, result.getId());
    assertEquals(userId, result.getUserId());

    verify(expenseRepository).findByIdAndUserId(expenseId, userId);
    verify(expenseMapper).entityToResponse(expenseEntity);
  }

  @Test
  @DisplayName("Should throw exception when expense not found by ID")
  void shouldThrowExceptionWhenExpenseNotFoundById() {
    // Given
    Long userId = 1L;
    Long expenseId = 999L;
    when(expenseRepository.findByIdAndUserId(expenseId, userId)).thenReturn(Optional.empty());

    // When & Then
    ResourceNotFoundException exception =
        assertThrows(
            ResourceNotFoundException.class,
            () -> expenseService.getExpenseById(userId, expenseId));

    assertEquals("Expense not found with id: " + expenseId, exception.getMessage());
    verify(expenseRepository).findByIdAndUserId(expenseId, userId);
    verify(expenseMapper, never()).entityToResponse(any());
  }

  @Test
  @DisplayName("Should get user expenses with pagination successfully")
  void shouldGetUserExpensesWithPaginationSuccessfully() {
    // Given
    Long userId = 1L;
    Pageable pageable = PageRequest.of(0, 10);
    List<ExpenseEntity> entities = Arrays.asList(expenseEntity);
    Page<ExpenseEntity> entityPage = new PageImpl<>(entities, pageable, 1);

    when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
    when(expenseRepository.findByUserIdOrderByExpenseDateDesc(userId, pageable))
        .thenReturn(entityPage);
    when(expenseMapper.entityToResponse(expenseEntity)).thenReturn(expenseResponse);

    // When
    Page<ExpenseResponse> result = expenseService.getUserExpenses(userId, pageable);

    // Then
    assertNotNull(result);
    assertEquals(1, result.getTotalElements());
    assertEquals(userId, result.getContent().get(0).getUserId());

    verify(userRepository).findById(userId);
    verify(expenseRepository).findByUserIdOrderByExpenseDateDesc(userId, pageable);
  }

  @Test
  @DisplayName("Should search expenses successfully")
  void shouldSearchExpensesSuccessfully() {
    // Given
    Long userId = 1L;
    String searchText = "lunch";
    Pageable pageable = PageRequest.of(0, 10);
    List<ExpenseEntity> entities = Arrays.asList(expenseEntity);
    Page<ExpenseEntity> entityPage = new PageImpl<>(entities, pageable, 1);

    when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
    when(expenseRepository.searchExpensesByText(userId, searchText, pageable))
        .thenReturn(entityPage);
    when(expenseMapper.entityToResponse(expenseEntity)).thenReturn(expenseResponse);

    // When
    Page<ExpenseResponse> result = expenseService.searchExpenses(userId, searchText, pageable);

    // Then
    assertNotNull(result);
    assertEquals(1, result.getTotalElements());

    verify(userRepository).findById(userId);
    verify(expenseRepository).searchExpensesByText(userId, searchText, pageable);
  }

  @Test
  @DisplayName("Should get expenses by category successfully")
  void shouldGetExpensesByCategorySuccessfully() {
    // Given
    Long userId = 1L;
    Long categoryId = 1L;
    Pageable pageable = PageRequest.of(0, 10);
    List<ExpenseEntity> entities = Arrays.asList(expenseEntity);
    Page<ExpenseEntity> entityPage = new PageImpl<>(entities, pageable, 1);

    when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
    when(categoryRepository.findActiveCategoryById(categoryId))
        .thenReturn(Optional.of(categoryEntity));
    when(expenseRepository.findByUserIdAndCategoryIdOrderByExpenseDateDesc(
            userId, categoryId, pageable))
        .thenReturn(entityPage);
    when(expenseMapper.entityToResponse(expenseEntity)).thenReturn(expenseResponse);

    // When
    Page<ExpenseResponse> result =
        expenseService.getExpensesByCategory(userId, categoryId, pageable);

    // Then
    assertNotNull(result);
    assertEquals(1, result.getTotalElements());
    assertEquals(categoryId, result.getContent().get(0).getCategory().getId());

    verify(userRepository).findById(userId);
    verify(categoryRepository).findActiveCategoryById(categoryId);
    verify(expenseRepository)
        .findByUserIdAndCategoryIdOrderByExpenseDateDesc(userId, categoryId, pageable);
  }

  @Test
  @DisplayName("Should get expenses by date range successfully")
  void shouldGetExpensesByDateRangeSuccessfully() {
    // Given
    Long userId = 1L;
    LocalDate startDate = LocalDate.now().minusDays(7);
    LocalDate endDate = LocalDate.now();
    List<ExpenseEntity> entities = Arrays.asList(expenseEntity);
    List<ExpenseResponse> responses = Arrays.asList(expenseResponse);

    when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
    when(expenseRepository.findByUserIdAndDateRange(userId, startDate, endDate))
        .thenReturn(entities);
    when(expenseMapper.entitiesToResponses(entities)).thenReturn(responses);

    // When
    List<ExpenseResponse> result =
        expenseService.getExpensesByDateRange(userId, startDate, endDate);

    // Then
    assertNotNull(result);
    assertEquals(1, result.size());

    verify(userRepository).findById(userId);
    verify(expenseRepository).findByUserIdAndDateRange(userId, startDate, endDate);
    verify(expenseMapper).entitiesToResponses(entities);
  }

  @Test
  @DisplayName("Should get expense count successfully")
  void shouldGetExpenseCountSuccessfully() {
    // Given
    Long userId = 1L;
    long expectedCount = 5L;

    when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
    when(expenseRepository.countByUserId(userId)).thenReturn(expectedCount);

    // When
    long result = expenseService.getExpenseCount(userId);

    // Then
    assertEquals(expectedCount, result);

    verify(userRepository).findById(userId);
    verify(expenseRepository).countByUserId(userId);
  }

  @Test
  @DisplayName("Should update expense successfully")
  void shouldUpdateExpenseSuccessfully() {
    // Given
    Long userId = 1L;
    Long expenseId = 1L;
    when(expenseRepository.findByIdAndUserId(expenseId, userId))
        .thenReturn(Optional.of(expenseEntity));
    when(categoryRepository.findActiveCategoryById(1L)).thenReturn(Optional.of(categoryEntity));
    when(expenseRepository.save(expenseEntity)).thenReturn(expenseEntity);
    when(expenseMapper.entityToResponse(expenseEntity)).thenReturn(expenseResponse);

    // When
    ExpenseResponse result = expenseService.updateExpense(userId, expenseId, updateRequest);

    // Then
    assertNotNull(result);

    verify(expenseRepository).findByIdAndUserId(expenseId, userId);
    verify(categoryRepository).findActiveCategoryById(1L);
    verify(expenseMapper).updateEntityFromRequest(expenseEntity, updateRequest, categoryEntity);
    verify(expenseRepository).save(expenseEntity);
    verify(expenseMapper).entityToResponse(expenseEntity);
  }

  @Test
  @DisplayName("Should throw exception when updating non-existent expense")
  void shouldThrowExceptionWhenUpdatingNonExistentExpense() {
    // Given
    Long userId = 1L;
    Long expenseId = 999L;
    when(expenseRepository.findByIdAndUserId(expenseId, userId)).thenReturn(Optional.empty());

    // When & Then
    ResourceNotFoundException exception =
        assertThrows(
            ResourceNotFoundException.class,
            () -> expenseService.updateExpense(userId, expenseId, updateRequest));

    assertEquals("Expense not found with id: " + expenseId, exception.getMessage());
    verify(expenseRepository).findByIdAndUserId(expenseId, userId);
    verify(expenseRepository, never()).save(any());
  }

  @Test
  @DisplayName("Should delete expense successfully")
  void shouldDeleteExpenseSuccessfully() {
    // Given
    Long userId = 1L;
    Long expenseId = 1L;
    when(expenseRepository.findByIdAndUserId(expenseId, userId))
        .thenReturn(Optional.of(expenseEntity));

    // When
    assertDoesNotThrow(() -> expenseService.deleteExpense(userId, expenseId));

    // Then
    verify(expenseRepository).findByIdAndUserId(expenseId, userId);
    verify(expenseRepository).delete(expenseEntity);
  }

  @Test
  @DisplayName("Should throw exception when deleting non-existent expense")
  void shouldThrowExceptionWhenDeletingNonExistentExpense() {
    // Given
    Long userId = 1L;
    Long expenseId = 999L;
    when(expenseRepository.findByIdAndUserId(expenseId, userId)).thenReturn(Optional.empty());

    // When & Then
    ResourceNotFoundException exception =
        assertThrows(
            ResourceNotFoundException.class, () -> expenseService.deleteExpense(userId, expenseId));

    assertEquals("Expense not found with id: " + expenseId, exception.getMessage());
    verify(expenseRepository).findByIdAndUserId(expenseId, userId);
    verify(expenseRepository, never()).delete(any());
  }

  @Test
  @DisplayName("Should get recent expenses successfully")
  void shouldGetRecentExpensesSuccessfully() {
    // Given
    Long userId = 1L;
    int limit = 5;
    List<ExpenseEntity> entities = Arrays.asList(expenseEntity);
    List<ExpenseResponse> responses = Arrays.asList(expenseResponse);

    when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
    when(expenseRepository.findRecentExpensesByUserId(eq(userId), any(Pageable.class)))
        .thenReturn(entities);
    when(expenseMapper.entitiesToResponses(entities)).thenReturn(responses);

    // When
    List<ExpenseResponse> result = expenseService.getRecentExpenses(userId, limit);

    // Then
    assertNotNull(result);
    assertEquals(1, result.size());

    verify(userRepository).findById(userId);
    verify(expenseRepository).findRecentExpensesByUserId(eq(userId), any(Pageable.class));
    verify(expenseMapper).entitiesToResponses(entities);
  }

  @Test
  @DisplayName("Should throw exception when user not found for getting expenses")
  void shouldThrowExceptionWhenUserNotFoundForGettingExpenses() {
    // Given
    Long userId = 999L;
    Pageable pageable = PageRequest.of(0, 10);
    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    // When & Then
    ResourceNotFoundException exception =
        assertThrows(
            ResourceNotFoundException.class,
            () -> expenseService.getUserExpenses(userId, pageable));

    assertEquals("User not found with id: " + userId, exception.getMessage());
    verify(userRepository).findById(userId);
    verify(expenseRepository, never()).findByUserIdOrderByExpenseDateDesc(any(), any());
  }

  @Test
  @DisplayName("Should throw exception when category not found for filtering expenses")
  void shouldThrowExceptionWhenCategoryNotFoundForFiltering() {
    // Given
    Long userId = 1L;
    Long categoryId = 999L;
    Pageable pageable = PageRequest.of(0, 10);

    when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
    when(categoryRepository.findActiveCategoryById(categoryId)).thenReturn(Optional.empty());

    // When & Then
    ResourceNotFoundException exception =
        assertThrows(
            ResourceNotFoundException.class,
            () -> expenseService.getExpensesByCategory(userId, categoryId, pageable));

    assertEquals("Category not found with id: " + categoryId, exception.getMessage());
    verify(userRepository).findById(userId);
    verify(categoryRepository).findActiveCategoryById(categoryId);
    verify(expenseRepository, never())
        .findByUserIdAndCategoryIdOrderByExpenseDateDesc(any(), any(), any());
  }
}
