package com.mycodethesaurus.financeinspector.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycodethesaurus.financeinspector.dto.SalaryIncomeCreateRequest;
import com.mycodethesaurus.financeinspector.dto.SalaryIncomeDto;
import com.mycodethesaurus.financeinspector.dto.SalaryIncomeUpdateRequest;
import com.mycodethesaurus.financeinspector.exception.ResourceNotFoundException;
import com.mycodethesaurus.financeinspector.exception.handler.CustomGlobalExceptionHandler;
import com.mycodethesaurus.financeinspector.service.IncomeService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(IncomeController.class)
@DisplayName("IncomeController Unit Tests")
class IncomeControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private IncomeService incomeService;

  @Autowired private ObjectMapper objectMapper;

  private SalaryIncomeCreateRequest createRequest;
  private SalaryIncomeUpdateRequest updateRequest;
  private SalaryIncomeDto salaryIncomeDto;

  @BeforeEach
  void setUp() {
    mockMvc =
        MockMvcBuilders.standaloneSetup(new IncomeController(incomeService))
            .setControllerAdvice(new CustomGlobalExceptionHandler())
            .build();

    createRequest = new SalaryIncomeCreateRequest();
    createRequest.setUserId(1L);
    createRequest.setCurrencyCode("INR");
    createRequest.setBasicAmount(new BigDecimal("70000.00"));
    createRequest.setHraAmount(new BigDecimal("35000.00"));
    createRequest.setOtherAllowanceAmount(new BigDecimal("5000.00"));
    createRequest.setBonusAmount(new BigDecimal("12000.00"));
    createRequest.setEmpPfAmount(new BigDecimal("8400.00"));
    createRequest.setProfessionTaxAmount(new BigDecimal("200.00"));
    createRequest.setIncomeTaxAmount(new BigDecimal("9000.00"));

    updateRequest = new SalaryIncomeUpdateRequest();
    updateRequest.setCurrencyCode("INR");
    updateRequest.setBasicAmount(new BigDecimal("75000.00"));
    updateRequest.setHraAmount(new BigDecimal("37500.00"));
    updateRequest.setOtherAllowanceAmount(new BigDecimal("6000.00"));
    updateRequest.setBonusAmount(new BigDecimal("15000.00"));
    updateRequest.setEmpPfAmount(new BigDecimal("9000.00"));
    updateRequest.setProfessionTaxAmount(new BigDecimal("200.00"));
    updateRequest.setIncomeTaxAmount(new BigDecimal("10000.00"));

    salaryIncomeDto = new SalaryIncomeDto();
    salaryIncomeDto.setId(1L);
    salaryIncomeDto.setUserId(1L);
    salaryIncomeDto.setCurrencyCode("INR");
    salaryIncomeDto.setBasicAmount(new BigDecimal("70000.00"));
    salaryIncomeDto.setHraAmount(new BigDecimal("35000.00"));
    salaryIncomeDto.setOtherAllowanceAmount(new BigDecimal("5000.00"));
    salaryIncomeDto.setBonusAmount(new BigDecimal("12000.00"));
    salaryIncomeDto.setEmpPfAmount(new BigDecimal("8400.00"));
    salaryIncomeDto.setProfessionTaxAmount(new BigDecimal("200.00"));
    salaryIncomeDto.setIncomeTaxAmount(new BigDecimal("9000.00"));
    salaryIncomeDto.setCreatedOn(LocalDateTime.now());
    salaryIncomeDto.setUpdatedOn(LocalDateTime.now());
  }

  @Test
  @DisplayName("Should get all salary income successfully")
  void shouldGetAllSalaryIncomeSuccessfully() throws Exception {
    // Given
    List<SalaryIncomeDto> incomes = Arrays.asList(salaryIncomeDto);
    when(incomeService.getAllSalaryIncome()).thenReturn(incomes);

    // When & Then
    mockMvc
        .perform(get("/v1/incomes"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0].id").value(1L))
        .andExpect(jsonPath("$[0].currencyCode").value("INR"))
        .andExpect(jsonPath("$[0].basicAmount").value(70000.00));
  }

  @Test
  @DisplayName("Should get salary income by user ID successfully")
  void shouldGetSalaryIncomeByUserIdSuccessfully() throws Exception {
    // Given
    List<SalaryIncomeDto> incomes = Arrays.asList(salaryIncomeDto);
    when(incomeService.getAllSalaryIncomeForUser(1L)).thenReturn(incomes);

    // When & Then
    mockMvc
        .perform(get("/v1/incomes/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0].id").value(1L))
        .andExpect(jsonPath("$[0].userId").value(1L));
  }

  @Test
  @DisplayName("Should get salary income by ID successfully")
  void shouldGetSalaryIncomeByIdSuccessfully() throws Exception {
    // Given
    when(incomeService.getSalaryIncomeById(1L)).thenReturn(salaryIncomeDto);

    // When & Then
    mockMvc
        .perform(get("/v1/incomes/salary/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1L))
        .andExpect(jsonPath("$.currencyCode").value("INR"))
        .andExpect(jsonPath("$.basicAmount").value(70000.00));
  }

  @Test
  @DisplayName("Should return 404 when salary income not found by ID")
  void shouldReturn404WhenSalaryIncomeNotFoundById() throws Exception {
    // Given
    when(incomeService.getSalaryIncomeById(1L))
        .thenThrow(new ResourceNotFoundException("Salary income not found with id: 1"));

    // When & Then
    mockMvc
        .perform(get("/v1/incomes/salary/1"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error").value("Resource Not Found"))
        .andExpect(jsonPath("$.message").value("Salary income not found with id: 1"));
  }

  @Test
  @DisplayName("Should create salary income successfully")
  void shouldCreateSalaryIncomeSuccessfully() throws Exception {
    // Given
    when(incomeService.createSalaryIncome(any(SalaryIncomeCreateRequest.class)))
        .thenReturn(salaryIncomeDto);

    // When & Then
    mockMvc
        .perform(
            post("/v1/incomes/salary")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(1L))
        .andExpect(jsonPath("$.userId").value(1L))
        .andExpect(jsonPath("$.currencyCode").value("INR"))
        .andExpect(jsonPath("$.basicAmount").value(70000.00))
        .andExpect(jsonPath("$.hraAmount").value(35000.00));
  }

  @Test
  @DisplayName("Should return 400 when creating salary income with invalid data")
  void shouldReturn400WhenCreatingWithInvalidData() throws Exception {
    // Given
    SalaryIncomeCreateRequest invalidRequest = new SalaryIncomeCreateRequest();
    invalidRequest.setCurrencyCode(""); // Invalid empty currency code
    invalidRequest.setBasicAmount(new BigDecimal("-1000")); // Negative amount

    // When & Then
    mockMvc
        .perform(
            post("/v1/incomes/salary")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("Validation Failed"));
  }

  @Test
  @DisplayName("Should return 404 when creating salary income for non-existent user")
  void shouldReturn404WhenCreatingForNonExistentUser() throws Exception {
    // Given
    when(incomeService.createSalaryIncome(any(SalaryIncomeCreateRequest.class)))
        .thenThrow(new ResourceNotFoundException("User not found with id: 999"));

    // When & Then
    mockMvc
        .perform(
            post("/v1/incomes/salary")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error").value("Resource Not Found"))
        .andExpect(jsonPath("$.message").value("User not found with id: 999"));
  }

  @Test
  @DisplayName("Should update salary income successfully")
  void shouldUpdateSalaryIncomeSuccessfully() throws Exception {
    // Given
    SalaryIncomeDto updatedDto = new SalaryIncomeDto();
    updatedDto.setId(1L);
    updatedDto.setUserId(1L);
    updatedDto.setCurrencyCode("INR");
    updatedDto.setBasicAmount(new BigDecimal("75000.00"));
    updatedDto.setHraAmount(new BigDecimal("37500.00"));
    updatedDto.setCreatedOn(LocalDateTime.now());
    updatedDto.setUpdatedOn(LocalDateTime.now());

    when(incomeService.updateSalaryIncome(eq(1L), any(SalaryIncomeUpdateRequest.class)))
        .thenReturn(updatedDto);

    // When & Then
    mockMvc
        .perform(
            put("/v1/incomes/salary/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1L))
        .andExpect(jsonPath("$.basicAmount").value(75000.00))
        .andExpect(jsonPath("$.hraAmount").value(37500.00));
  }

  @Test
  @DisplayName("Should return 400 when updating with invalid data")
  void shouldReturn400WhenUpdatingWithInvalidData() throws Exception {
    // Given
    SalaryIncomeUpdateRequest invalidRequest = new SalaryIncomeUpdateRequest();
    invalidRequest.setCurrencyCode("INVALID"); // Invalid currency code length
    invalidRequest.setBasicAmount(new BigDecimal("-5000")); // Negative amount

    // When & Then
    mockMvc
        .perform(
            put("/v1/incomes/salary/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("Validation Failed"));
  }

  @Test
  @DisplayName("Should return 404 when updating non-existent salary income")
  void shouldReturn404WhenUpdatingNonExistentSalaryIncome() throws Exception {
    // Given
    when(incomeService.updateSalaryIncome(eq(1L), any(SalaryIncomeUpdateRequest.class)))
        .thenThrow(new ResourceNotFoundException("Salary income not found with id: 1"));

    // When & Then
    mockMvc
        .perform(
            put("/v1/incomes/salary/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error").value("Resource Not Found"))
        .andExpect(jsonPath("$.message").value("Salary income not found with id: 1"));
  }

  @Test
  @DisplayName("Should delete salary income successfully")
  void shouldDeleteSalaryIncomeSuccessfully() throws Exception {
    // When & Then
    mockMvc.perform(delete("/v1/incomes/salary/1")).andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("Should return 404 when deleting non-existent salary income")
  void shouldReturn404WhenDeletingNonExistentSalaryIncome() throws Exception {
    // Given
    doThrow(new ResourceNotFoundException("Salary income not found with id: 1"))
        .when(incomeService)
        .deleteSalaryIncome(1L);

    // When & Then
    mockMvc
        .perform(delete("/v1/incomes/salary/1"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error").value("Resource Not Found"))
        .andExpect(jsonPath("$.message").value("Salary income not found with id: 1"));
  }
}
