package com.mycodethesaurus.financeinspector.component;

import static org.junit.jupiter.api.Assertions.*;

import com.mycodethesaurus.financeinspector.dto.ExpenseCreateRequest;
import com.mycodethesaurus.financeinspector.dto.ExpenseResponse;
import com.mycodethesaurus.financeinspector.dto.ExpenseUpdateRequest;
import com.mycodethesaurus.financeinspector.enums.PaymentMethod;
import com.mycodethesaurus.financeinspector.persistence.entity.ExpenseCategoryEntity;
import com.mycodethesaurus.financeinspector.persistence.entity.ExpenseEntity;
import com.mycodethesaurus.financeinspector.persistence.entity.UserEntity;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("ExpenseMapper Tests")
class ExpenseMapperTest {

  private ExpenseMapper expenseMapper;

  private UserEntity userEntity;
  private ExpenseCategoryEntity categoryEntity;
  private ExpenseCategoryEntity parentCategoryEntity;
  private ExpenseEntity expenseEntity;
  private ExpenseCreateRequest createRequest;
  private ExpenseUpdateRequest updateRequest;

  @BeforeEach
  void setUp() {
    expenseMapper = new ExpenseMapper();

    userEntity = new UserEntity();
    userEntity.setId(1L);
    userEntity.setUserName("testuser");
    userEntity.setFirstName("John");
    userEntity.setLastName("Doe");
    userEntity.setEmail("john.doe@example.com");

    parentCategoryEntity = new ExpenseCategoryEntity();
    parentCategoryEntity.setId(2L);
    parentCategoryEntity.setName("General");
    parentCategoryEntity.setDescription("General expenses");
    parentCategoryEntity.setColorCode("#FF0000");
    parentCategoryEntity.setIconName("general");
    parentCategoryEntity.setIsActive(true);

    categoryEntity = new ExpenseCategoryEntity();
    categoryEntity.setId(1L);
    categoryEntity.setName("Food & Dining");
    categoryEntity.setDescription("Restaurant meals");
    categoryEntity.setColorCode("#FF5722");
    categoryEntity.setIconName("restaurant");
    categoryEntity.setParent(parentCategoryEntity);
    categoryEntity.setIsActive(true);

    Set<String> tags = new HashSet<>();
    tags.add("business");
    tags.add("meal");

    expenseEntity = new ExpenseEntity();
    expenseEntity.setId(1L);
    expenseEntity.setUser(userEntity);
    expenseEntity.setCategory(categoryEntity);
    expenseEntity.setAmount(new BigDecimal("25.50"));
    expenseEntity.setCurrencyCode("USD");
    expenseEntity.setDescription("Lunch at restaurant");
    expenseEntity.setExpenseDate(LocalDate.of(2024, 3, 15));
    expenseEntity.setLocation("Downtown");
    expenseEntity.setMerchant("McDonald's");
    expenseEntity.setPaymentMethod(PaymentMethod.CREDIT_CARD);
    expenseEntity.setReceiptUrl("https://receipts.example.com/receipt123.jpg");
    expenseEntity.setNotes("Business lunch with client");
    expenseEntity.setTags(tags);
    expenseEntity.setCreatedOn(LocalDateTime.of(2024, 3, 15, 12, 30, 0));
    expenseEntity.setUpdatedOn(LocalDateTime.of(2024, 3, 15, 14, 45, 0));

    createRequest = new ExpenseCreateRequest();
    createRequest.setCategoryId(1L);
    createRequest.setAmount(new BigDecimal("25.50"));
    createRequest.setCurrencyCode("USD");
    createRequest.setDescription("Lunch at restaurant");
    createRequest.setExpenseDate(LocalDate.of(2024, 3, 15));
    createRequest.setLocation("Downtown");
    createRequest.setMerchant("McDonald's");
    createRequest.setPaymentMethod(PaymentMethod.CREDIT_CARD);
    createRequest.setReceiptUrl("https://receipts.example.com/receipt123.jpg");
    createRequest.setNotes("Business lunch with client");
    createRequest.setTags(tags);

    updateRequest = new ExpenseUpdateRequest();
    updateRequest.setCategoryId(1L);
    updateRequest.setAmount(new BigDecimal("30.00"));
    updateRequest.setCurrencyCode("USD");
    updateRequest.setDescription("Updated lunch expense");
    updateRequest.setExpenseDate(LocalDate.of(2024, 3, 16));
    updateRequest.setLocation("Uptown");
    updateRequest.setMerchant("Burger King");
    updateRequest.setPaymentMethod(PaymentMethod.CASH);
    updateRequest.setReceiptUrl("https://receipts.example.com/receipt456.jpg");
    updateRequest.setNotes("Updated business lunch");
    updateRequest.setTags(Set.of("business", "updated"));
  }

  @Test
  @DisplayName("Should map create request to entity successfully")
  void shouldMapCreateRequestToEntitySuccessfully() {
    // When
    ExpenseEntity result =
        expenseMapper.createRequestToEntity(createRequest, userEntity, categoryEntity);

    // Then
    assertNotNull(result);
    assertEquals(userEntity, result.getUser());
    assertEquals(categoryEntity, result.getCategory());
    assertEquals(createRequest.getAmount(), result.getAmount());
    assertEquals(createRequest.getCurrencyCode(), result.getCurrencyCode());
    assertEquals(createRequest.getDescription(), result.getDescription());
    assertEquals(createRequest.getExpenseDate(), result.getExpenseDate());
    assertEquals(createRequest.getLocation(), result.getLocation());
    assertEquals(createRequest.getMerchant(), result.getMerchant());
    assertEquals(createRequest.getPaymentMethod(), result.getPaymentMethod());
    assertEquals(createRequest.getReceiptUrl(), result.getReceiptUrl());
    assertEquals(createRequest.getNotes(), result.getNotes());
    assertEquals(createRequest.getTags(), result.getTags());
  }

  @Test
  @DisplayName("Should map create request to entity with null values")
  void shouldMapCreateRequestToEntityWithNullValues() {
    // Given
    ExpenseCreateRequest requestWithNulls = new ExpenseCreateRequest();
    requestWithNulls.setCategoryId(1L);
    requestWithNulls.setAmount(new BigDecimal("10.00"));
    requestWithNulls.setDescription("Simple expense");
    requestWithNulls.setExpenseDate(LocalDate.now());

    // When
    ExpenseEntity result =
        expenseMapper.createRequestToEntity(requestWithNulls, userEntity, categoryEntity);

    // Then
    assertNotNull(result);
    assertEquals(userEntity, result.getUser());
    assertEquals(categoryEntity, result.getCategory());
    assertEquals(requestWithNulls.getAmount(), result.getAmount());
    assertEquals(requestWithNulls.getDescription(), result.getDescription());
    assertEquals(requestWithNulls.getExpenseDate(), result.getExpenseDate());
    assertNull(result.getCurrencyCode());
    assertNull(result.getLocation());
    assertNull(result.getMerchant());
    assertNull(result.getPaymentMethod());
    assertNull(result.getReceiptUrl());
    assertNull(result.getNotes());
    assertTrue(result.getTags() == null || result.getTags().isEmpty());
  }

  @Test
  @DisplayName("Should map entity to response successfully")
  void shouldMapEntityToResponseSuccessfully() {
    // When
    ExpenseResponse result = expenseMapper.entityToResponse(expenseEntity);

    // Then
    assertNotNull(result);
    assertEquals(expenseEntity.getId(), result.getId());
    assertEquals(expenseEntity.getUser().getId(), result.getUserId());
    assertEquals(expenseEntity.getAmount(), result.getAmount());
    assertEquals(expenseEntity.getCurrencyCode(), result.getCurrencyCode());
    assertEquals(expenseEntity.getDescription(), result.getDescription());
    assertEquals(expenseEntity.getExpenseDate(), result.getExpenseDate());
    assertEquals(expenseEntity.getLocation(), result.getLocation());
    assertEquals(expenseEntity.getMerchant(), result.getMerchant());
    assertEquals(expenseEntity.getPaymentMethod(), result.getPaymentMethod());
    assertEquals(expenseEntity.getReceiptUrl(), result.getReceiptUrl());
    assertEquals(expenseEntity.getNotes(), result.getNotes());
    assertEquals(expenseEntity.getTags(), result.getTags());
    assertEquals(expenseEntity.getCreatedOn(), result.getCreatedOn());
    assertEquals(expenseEntity.getUpdatedOn(), result.getUpdatedOn());

    // Verify category mapping
    assertNotNull(result.getCategory());
    assertEquals(categoryEntity.getId(), result.getCategory().getId());
    assertEquals(categoryEntity.getName(), result.getCategory().getName());
    assertEquals(categoryEntity.getColorCode(), result.getCategory().getColorCode());
    assertEquals(categoryEntity.getIconName(), result.getCategory().getIconName());

    // Verify parent category mapping
    assertNotNull(result.getCategory().getParent());
    assertEquals(parentCategoryEntity.getId(), result.getCategory().getParent().getId());
    assertEquals(parentCategoryEntity.getName(), result.getCategory().getParent().getName());
    assertEquals(
        parentCategoryEntity.getColorCode(), result.getCategory().getParent().getColorCode());
    assertEquals(
        parentCategoryEntity.getIconName(), result.getCategory().getParent().getIconName());
  }

  @Test
  @DisplayName("Should map entity to response with null category parent")
  void shouldMapEntityToResponseWithNullCategoryParent() {
    // Given
    categoryEntity.setParent(null);

    // When
    ExpenseResponse result = expenseMapper.entityToResponse(expenseEntity);

    // Then
    assertNotNull(result);
    assertNotNull(result.getCategory());
    assertNull(result.getCategory().getParent());
  }

  @Test
  @DisplayName("Should map entity to response with null values")
  void shouldMapEntityToResponseWithNullValues() {
    // Given
    ExpenseEntity entityWithNulls = new ExpenseEntity();
    entityWithNulls.setId(1L);
    entityWithNulls.setUser(userEntity);
    entityWithNulls.setCategory(categoryEntity);
    entityWithNulls.setAmount(new BigDecimal("10.00"));
    entityWithNulls.setDescription("Simple expense");
    entityWithNulls.setExpenseDate(LocalDate.now());

    // When
    ExpenseResponse result = expenseMapper.entityToResponse(entityWithNulls);

    // Then
    assertNotNull(result);
    assertEquals(entityWithNulls.getId(), result.getId());
    assertEquals(entityWithNulls.getAmount(), result.getAmount());
    assertEquals(entityWithNulls.getDescription(), result.getDescription());
    assertNull(result.getCurrencyCode());
    assertNull(result.getLocation());
    assertNull(result.getMerchant());
    assertNull(result.getPaymentMethod());
    assertNull(result.getReceiptUrl());
    assertNull(result.getNotes());
    assertTrue(result.getTags() == null || result.getTags().isEmpty());
    assertNull(result.getCreatedOn());
    assertNull(result.getUpdatedOn());
  }

  @Test
  @DisplayName("Should map entities to responses successfully")
  void shouldMapEntitiesToResponsesSuccessfully() {
    // Given
    ExpenseEntity secondEntity = new ExpenseEntity();
    secondEntity.setId(2L);
    secondEntity.setUser(userEntity);
    secondEntity.setCategory(categoryEntity);
    secondEntity.setAmount(new BigDecimal("15.75"));
    secondEntity.setDescription("Coffee");
    secondEntity.setExpenseDate(LocalDate.now());

    List<ExpenseEntity> entities = Arrays.asList(expenseEntity, secondEntity);

    // When
    List<ExpenseResponse> results = expenseMapper.entitiesToResponses(entities);

    // Then
    assertNotNull(results);
    assertEquals(2, results.size());
    assertEquals(expenseEntity.getId(), results.get(0).getId());
    assertEquals(secondEntity.getId(), results.get(1).getId());
  }

  @Test
  @DisplayName("Should map empty entity list to empty response list")
  void shouldMapEmptyEntityListToEmptyResponseList() {
    // When
    List<ExpenseResponse> results = expenseMapper.entitiesToResponses(Arrays.asList());

    // Then
    assertNotNull(results);
    assertTrue(results.isEmpty());
  }

  @Test
  @DisplayName("Should update entity from request successfully")
  void shouldUpdateEntityFromRequestSuccessfully() {
    // Given
    ExpenseEntity entityToUpdate = new ExpenseEntity();
    entityToUpdate.setId(1L);
    entityToUpdate.setUser(userEntity);
    entityToUpdate.setCategory(categoryEntity);
    entityToUpdate.setAmount(new BigDecimal("20.00"));
    entityToUpdate.setDescription("Original expense");

    // When
    expenseMapper.updateEntityFromRequest(entityToUpdate, updateRequest, categoryEntity);

    // Then
    assertEquals(categoryEntity, entityToUpdate.getCategory());
    assertEquals(updateRequest.getAmount(), entityToUpdate.getAmount());
    assertEquals(updateRequest.getCurrencyCode(), entityToUpdate.getCurrencyCode());
    assertEquals(updateRequest.getDescription(), entityToUpdate.getDescription());
    assertEquals(updateRequest.getExpenseDate(), entityToUpdate.getExpenseDate());
    assertEquals(updateRequest.getLocation(), entityToUpdate.getLocation());
    assertEquals(updateRequest.getMerchant(), entityToUpdate.getMerchant());
    assertEquals(updateRequest.getPaymentMethod(), entityToUpdate.getPaymentMethod());
    assertEquals(updateRequest.getReceiptUrl(), entityToUpdate.getReceiptUrl());
    assertEquals(updateRequest.getNotes(), entityToUpdate.getNotes());
    assertEquals(updateRequest.getTags(), entityToUpdate.getTags());

    // Original properties should remain unchanged
    assertEquals(1L, entityToUpdate.getId());
    assertEquals(userEntity, entityToUpdate.getUser());
  }

  @Test
  @DisplayName("Should update entity from request with null values")
  void shouldUpdateEntityFromRequestWithNullValues() {
    // Given
    ExpenseEntity entityToUpdate = new ExpenseEntity();
    entityToUpdate.setId(1L);
    entityToUpdate.setUser(userEntity);
    entityToUpdate.setCategory(categoryEntity);
    entityToUpdate.setAmount(new BigDecimal("20.00"));
    entityToUpdate.setDescription("Original expense");
    entityToUpdate.setLocation("Original location");
    entityToUpdate.setMerchant("Original merchant");

    ExpenseUpdateRequest requestWithNulls = new ExpenseUpdateRequest();
    requestWithNulls.setCategoryId(1L);
    requestWithNulls.setAmount(new BigDecimal("25.00"));
    requestWithNulls.setDescription("Updated expense");
    requestWithNulls.setExpenseDate(LocalDate.now());

    // When
    expenseMapper.updateEntityFromRequest(entityToUpdate, requestWithNulls, categoryEntity);

    // Then
    assertEquals(requestWithNulls.getAmount(), entityToUpdate.getAmount());
    assertEquals(requestWithNulls.getDescription(), entityToUpdate.getDescription());
    assertEquals(requestWithNulls.getExpenseDate(), entityToUpdate.getExpenseDate());
    assertNull(entityToUpdate.getCurrencyCode());
    assertNull(entityToUpdate.getLocation());
    assertNull(entityToUpdate.getMerchant());
    assertNull(entityToUpdate.getPaymentMethod());
    assertNull(entityToUpdate.getReceiptUrl());
    assertNull(entityToUpdate.getNotes());
    assertTrue(entityToUpdate.getTags() == null || entityToUpdate.getTags().isEmpty());
  }

  @Test
  @DisplayName("Should handle null user in entityToResponse")
  void shouldHandleNullUserInEntityToResponse() {
    // Given
    expenseEntity.setUser(null);

    // When & Then
    assertThrows(NullPointerException.class, () -> expenseMapper.entityToResponse(expenseEntity));
  }

  @Test
  @DisplayName("Should handle null category in entityToResponse")
  void shouldHandleNullCategoryInEntityToResponse() {
    // Given
    expenseEntity.setCategory(null);

    // When
    ExpenseResponse result = expenseMapper.entityToResponse(expenseEntity);

    // Then
    assertNotNull(result);
    assertNull(result.getCategory());
  }

  @Test
  @DisplayName("Should map category summary correctly with all fields")
  void shouldMapCategorySummaryCorrectlyWithAllFields() {
    // Given - using the existing categoryEntity with parent

    // When
    ExpenseResponse result = expenseMapper.entityToResponse(expenseEntity);

    // Then
    assertNotNull(result.getCategory());
    ExpenseResponse.CategorySummary category = result.getCategory();

    assertEquals(categoryEntity.getId(), category.getId());
    assertEquals(categoryEntity.getName(), category.getName());
    assertEquals(categoryEntity.getColorCode(), category.getColorCode());
    assertEquals(categoryEntity.getIconName(), category.getIconName());

    assertNotNull(category.getParent());
    assertEquals(parentCategoryEntity.getId(), category.getParent().getId());
    assertEquals(parentCategoryEntity.getName(), category.getParent().getName());
    assertEquals(parentCategoryEntity.getColorCode(), category.getParent().getColorCode());
    assertEquals(parentCategoryEntity.getIconName(), category.getParent().getIconName());
    assertNull(category.getParent().getParent()); // Parent category has no parent
  }

  @Test
  @DisplayName("Should handle different payment methods")
  void shouldHandleDifferentPaymentMethods() {
    // Test all PaymentMethod enum values
    for (PaymentMethod paymentMethod : PaymentMethod.values()) {
      // Given
      createRequest.setPaymentMethod(paymentMethod);

      // When
      ExpenseEntity result =
          expenseMapper.createRequestToEntity(createRequest, userEntity, categoryEntity);

      // Then
      assertEquals(paymentMethod, result.getPaymentMethod());
    }
  }

  @Test
  @DisplayName("Should preserve empty tags set")
  void shouldPreserveEmptyTagsSet() {
    // Given
    createRequest.setTags(new HashSet<>());

    // When
    ExpenseEntity result =
        expenseMapper.createRequestToEntity(createRequest, userEntity, categoryEntity);

    // Then
    assertNotNull(result.getTags());
    assertTrue(result.getTags().isEmpty());
  }

  @Test
  @DisplayName("Should handle large amounts correctly")
  void shouldHandleLargeAmountsCorrectly() {
    // Given
    BigDecimal largeAmount = new BigDecimal("999999.99");
    createRequest.setAmount(largeAmount);

    // When
    ExpenseEntity result =
        expenseMapper.createRequestToEntity(createRequest, userEntity, categoryEntity);

    // Then
    assertEquals(largeAmount, result.getAmount());
  }

  @Test
  @DisplayName("Should handle null entities list")
  void shouldHandleNullEntitiesList() {
    // When & Then
    assertThrows(NullPointerException.class, () -> expenseMapper.entitiesToResponses(null));
  }

  @Test
  @DisplayName("Should test buildCategorySummary with null category")
  void shouldBuildCategorySummaryWithNullCategory() {
    // Given
    ExpenseEntity entityWithNullCategory = new ExpenseEntity();
    entityWithNullCategory.setId(1L);
    entityWithNullCategory.setUser(userEntity);
    entityWithNullCategory.setCategory(null);
    entityWithNullCategory.setAmount(new BigDecimal("10.00"));
    entityWithNullCategory.setDescription("Test");
    entityWithNullCategory.setExpenseDate(LocalDate.now());

    // When
    ExpenseResponse result = expenseMapper.entityToResponse(entityWithNullCategory);

    // Then
    assertNotNull(result);
    assertNull(result.getCategory());
  }

  @Test
  @DisplayName("Should handle category without parent correctly")
  void shouldHandleCategoryWithoutParentCorrectly() {
    // Given
    ExpenseCategoryEntity categoryWithoutParent = new ExpenseCategoryEntity();
    categoryWithoutParent.setId(3L);
    categoryWithoutParent.setName("Root Category");
    categoryWithoutParent.setColorCode("#000000");
    categoryWithoutParent.setIconName("root");
    categoryWithoutParent.setParent(null);

    expenseEntity.setCategory(categoryWithoutParent);

    // When
    ExpenseResponse result = expenseMapper.entityToResponse(expenseEntity);

    // Then
    assertNotNull(result.getCategory());
    assertEquals(categoryWithoutParent.getId(), result.getCategory().getId());
    assertEquals(categoryWithoutParent.getName(), result.getCategory().getName());
    assertNull(result.getCategory().getParent());
  }
}
