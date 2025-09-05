package com.mycodethesaurus.financeinspector.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mycodethesaurus.financeinspector.dto.ExpenseCreateRequest;
import com.mycodethesaurus.financeinspector.dto.ExpenseResponse;
import com.mycodethesaurus.financeinspector.dto.ExpenseUpdateRequest;
import com.mycodethesaurus.financeinspector.enums.PaymentMethod;
import com.mycodethesaurus.financeinspector.exception.ResourceNotFoundException;
import com.mycodethesaurus.financeinspector.exception.handler.CustomGlobalExceptionHandler;
import com.mycodethesaurus.financeinspector.service.ExpenseService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
@DisplayName("Expense Controller Tests")
class ExpenseControllerTest {

  private MockMvc mockMvc;

  @Mock private ExpenseService expenseService;

  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    mockMvc =
        MockMvcBuilders.standaloneSetup(new ExpenseController(expenseService))
            .setControllerAdvice(new CustomGlobalExceptionHandler())
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .build();
  }

  @Test
  @DisplayName("Should create expense successfully")
  void shouldCreateExpenseSuccessfully() throws Exception {
    // Given
    ExpenseCreateRequest request = new ExpenseCreateRequest();
    request.setCategoryId(1L);
    request.setDescription("Lunch at restaurant");
    request.setAmount(new BigDecimal("25.50"));
    request.setExpenseDate(LocalDate.now());
    request.setPaymentMethod(PaymentMethod.CREDIT_CARD);
    request.setCurrencyCode("USD");

    ExpenseResponse response = new ExpenseResponse();
    response.setId(1L);
    response.setUserId(1L);
    response.setDescription("Lunch at restaurant");
    response.setAmount(new BigDecimal("25.50"));
    response.setExpenseDate(LocalDate.now());
    response.setPaymentMethod(PaymentMethod.CREDIT_CARD);
    response.setCurrencyCode("USD");
    response.setCreatedOn(LocalDateTime.now());

    when(expenseService.createExpense(eq(1L), any(ExpenseCreateRequest.class)))
        .thenReturn(response);

    // When & Then
    mockMvc
        .perform(
            post("/v1/expenses")
                .param("userId", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.description").value("Lunch at restaurant"))
        .andExpect(jsonPath("$.amount").value(25.50))
        .andExpect(jsonPath("$.paymentMethod").value("CREDIT_CARD"))
        .andExpect(jsonPath("$.currencyCode").value("USD"))
        .andExpect(jsonPath("$.userId").value(1));
  }

  @Test
  @DisplayName("Should return 400 when creating expense with invalid data")
  void shouldReturn400WhenCreatingExpenseWithInvalidData() throws Exception {
    // Given
    ExpenseCreateRequest request = new ExpenseCreateRequest();
    request.setCategoryId(null); // Invalid - required field
    request.setDescription(""); // Invalid - blank
    request.setAmount(new BigDecimal("-10.00")); // Invalid - negative amount
    request.setExpenseDate(null); // Invalid - required field
    request.setPaymentMethod(null); // Invalid - required field
    request.setCurrencyCode(""); // Invalid - blank

    // When & Then
    mockMvc
        .perform(
            post("/v1/expenses")
                .param("userId", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.error").value("Validation Failed"));
  }

  @Test
  @DisplayName("Should return 404 when creating expense for non-existent user")
  void shouldReturn404WhenCreatingExpenseForNonExistentUser() throws Exception {
    // Given
    ExpenseCreateRequest request = new ExpenseCreateRequest();
    request.setCategoryId(1L);
    request.setDescription("Test expense");
    request.setAmount(new BigDecimal("25.50"));
    request.setExpenseDate(LocalDate.now());
    request.setPaymentMethod(PaymentMethod.CASH);
    request.setCurrencyCode("USD");

    when(expenseService.createExpense(eq(999L), any(ExpenseCreateRequest.class)))
        .thenThrow(new ResourceNotFoundException("User not found with id: 999"));

    // When & Then
    mockMvc
        .perform(
            post("/v1/expenses")
                .param("userId", "999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.error").value("Resource Not Found"))
        .andExpect(jsonPath("$.message").value("User not found with id: 999"));
  }

  @Test
  @DisplayName("Should get expense by ID successfully")
  void shouldGetExpenseByIdSuccessfully() throws Exception {
    // Given
    ExpenseResponse response = new ExpenseResponse();
    response.setId(1L);
    response.setUserId(1L);
    response.setDescription("Lunch at restaurant");
    response.setAmount(new BigDecimal("25.50"));
    response.setExpenseDate(LocalDate.now());
    response.setPaymentMethod(PaymentMethod.CREDIT_CARD);
    response.setCurrencyCode("USD");
    response.setCreatedOn(LocalDateTime.now());

    when(expenseService.getExpenseById(eq(1L), eq(1L))).thenReturn(response);

    // When & Then
    mockMvc
        .perform(get("/v1/expenses/1").param("userId", "1"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.description").value("Lunch at restaurant"))
        .andExpect(jsonPath("$.amount").value(25.50));
  }

  @Test
  @DisplayName("Should return 404 when expense not found by ID")
  void shouldReturn404WhenExpenseNotFoundById() throws Exception {
    // Given
    when(expenseService.getExpenseById(eq(1L), eq(1L)))
        .thenThrow(new ResourceNotFoundException("Expense not found with id: 1"));

    // When & Then
    mockMvc
        .perform(get("/v1/expenses/1").param("userId", "1"))
        .andExpect(status().isNotFound())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.error").value("Resource Not Found"))
        .andExpect(jsonPath("$.message").value("Expense not found with id: 1"));
  }

  @Test
  @DisplayName("Should get expenses for user successfully")
  void shouldGetExpensesForUserSuccessfully() throws Exception {
    // Given
    ExpenseResponse expense1 = new ExpenseResponse();
    expense1.setId(1L);
    expense1.setUserId(1L);
    expense1.setDescription("Lunch");
    expense1.setAmount(new BigDecimal("25.50"));
    expense1.setExpenseDate(LocalDate.now());
    expense1.setPaymentMethod(PaymentMethod.CREDIT_CARD);
    expense1.setCurrencyCode("USD");
    expense1.setCreatedOn(LocalDateTime.now());

    ExpenseResponse expense2 = new ExpenseResponse();
    expense2.setId(2L);
    expense2.setUserId(1L);
    expense2.setDescription("Gas");
    expense2.setAmount(new BigDecimal("45.00"));
    expense2.setExpenseDate(LocalDate.now().minusDays(1));
    expense2.setPaymentMethod(PaymentMethod.DEBIT_CARD);
    expense2.setCurrencyCode("USD");
    expense2.setCreatedOn(LocalDateTime.now());

    List<ExpenseResponse> expenses = Arrays.asList(expense1, expense2);
    Page<ExpenseResponse> page = new PageImpl<>(expenses, PageRequest.of(0, 10), expenses.size());

    when(expenseService.getUserExpenses(eq(1L), any())).thenReturn(page);

    // When & Then
    mockMvc
        .perform(get("/v1/expenses").param("userId", "1"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content.length()").value(2))
        .andExpect(jsonPath("$.content[0].id").value(1))
        .andExpect(jsonPath("$.content[1].id").value(2));
  }

  @Test
  @DisplayName("Should update expense successfully")
  void shouldUpdateExpenseSuccessfully() throws Exception {
    // Given
    ExpenseUpdateRequest request = new ExpenseUpdateRequest();
    request.setCategoryId(1L);
    request.setDescription("Updated lunch expense");
    request.setAmount(new BigDecimal("30.00"));
    request.setExpenseDate(LocalDate.now());
    request.setCurrencyCode("USD");
    request.setPaymentMethod(PaymentMethod.CASH);

    ExpenseResponse response = new ExpenseResponse();
    response.setId(1L);
    response.setUserId(1L);
    response.setDescription("Updated lunch expense");
    response.setAmount(new BigDecimal("30.00"));
    response.setExpenseDate(LocalDate.now());
    response.setPaymentMethod(PaymentMethod.CASH);
    response.setCurrencyCode("USD");
    response.setCreatedOn(LocalDateTime.now());
    response.setUpdatedOn(LocalDateTime.now());

    when(expenseService.updateExpense(eq(1L), eq(1L), any(ExpenseUpdateRequest.class)))
        .thenReturn(response);

    // When & Then
    mockMvc
        .perform(
            put("/v1/expenses/1")
                .param("userId", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.description").value("Updated lunch expense"))
        .andExpect(jsonPath("$.amount").value(30.00))
        .andExpect(jsonPath("$.paymentMethod").value("CASH"));
  }

  @Test
  @DisplayName("Should return 404 when updating non-existent expense")
  void shouldReturn404WhenUpdatingNonExistentExpense() throws Exception {
    // Given
    ExpenseUpdateRequest request = new ExpenseUpdateRequest();
    request.setCategoryId(1L);
    request.setDescription("Updated expense");
    request.setAmount(new BigDecimal("30.00"));
    request.setExpenseDate(LocalDate.now());
    request.setCurrencyCode("USD");

    when(expenseService.updateExpense(eq(1L), eq(1L), any(ExpenseUpdateRequest.class)))
        .thenThrow(new ResourceNotFoundException("Expense not found with id: 1"));

    // When & Then
    mockMvc
        .perform(
            put("/v1/expenses/1")
                .param("userId", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.error").value("Resource Not Found"));
  }

  @Test
  @DisplayName("Should delete expense successfully")
  void shouldDeleteExpenseSuccessfully() throws Exception {
    // When & Then
    mockMvc
        .perform(delete("/v1/expenses/1").param("userId", "1"))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("Should return 404 when deleting non-existent expense")
  void shouldReturn404WhenDeletingNonExistentExpense() throws Exception {
    // Given
    doThrow(new ResourceNotFoundException("Expense not found with id: 1"))
        .when(expenseService)
        .deleteExpense(1L, 1L);

    // When & Then
    mockMvc
        .perform(delete("/v1/expenses/1").param("userId", "1"))
        .andExpect(status().isNotFound())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.error").value("Resource Not Found"));
  }
}
