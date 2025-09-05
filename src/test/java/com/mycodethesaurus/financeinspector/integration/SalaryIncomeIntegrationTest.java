package com.mycodethesaurus.financeinspector.integration;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycodethesaurus.financeinspector.config.AbstractIntegrationTest;
import com.mycodethesaurus.financeinspector.dto.SalaryIncomeCreateRequest;
import com.mycodethesaurus.financeinspector.dto.SalaryIncomeDto;
import com.mycodethesaurus.financeinspector.dto.SalaryIncomeUpdateRequest;
import com.mycodethesaurus.financeinspector.dto.UserCreateRequest;
import com.mycodethesaurus.financeinspector.dto.UserResponse;
import com.mycodethesaurus.financeinspector.persistence.repository.SalaryIncomeRepository;
import com.mycodethesaurus.financeinspector.persistence.repository.UserRepository;
import java.math.BigDecimal;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@AutoConfigureMockMvc
@Transactional
@WithMockUser(roles = "ADMIN")
@DisplayName("Salary Income Integration Tests")
class SalaryIncomeIntegrationTest extends AbstractIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private UserRepository userRepository;

  @Autowired private SalaryIncomeRepository salaryIncomeRepository;

  private UserResponse testUser;
  private SalaryIncomeCreateRequest validCreateRequest;

  @BeforeEach
  void setUp() throws Exception {
    // Clean database before each test
    salaryIncomeRepository.deleteAll();
    userRepository.deleteAll();

    // Create a test user first
    UserCreateRequest userRequest = new UserCreateRequest();
    userRequest.setUserName("testuser");
    userRequest.setPassword("password123");
    userRequest.setFirstName("Test");
    userRequest.setLastName("User");
    userRequest.setEmail("test.user@example.com");

    String userResponse =
        mockMvc
            .perform(
                post("/v1/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(userRequest))
                    .with(csrf()))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

    testUser = objectMapper.readValue(userResponse, UserResponse.class);

    // Setup valid salary income request
    validCreateRequest = new SalaryIncomeCreateRequest();
    validCreateRequest.setUserId(testUser.getId()); // Add userId to request
    validCreateRequest.setCurrencyCode("INR");
    validCreateRequest.setBasicAmount(new BigDecimal("50000.00"));
    validCreateRequest.setHraAmount(new BigDecimal("20000.00"));
    validCreateRequest.setOtherAllowanceAmount(new BigDecimal("5000.00"));
    validCreateRequest.setBonusAmount(new BigDecimal("10000.00"));
    validCreateRequest.setEmpPfAmount(new BigDecimal("6000.00"));
    validCreateRequest.setProfessionTaxAmount(new BigDecimal("2400.00"));
    validCreateRequest.setIncomeTaxAmount(new BigDecimal("12000.00"));
  }

  @AfterEach
  void tearDown() {
    // Clean up after each test
    salaryIncomeRepository.deleteAll();
    userRepository.deleteAll();
  }

  @Test
  @DisplayName("Should create salary income successfully - Integration")
  void shouldCreateSalaryIncomeSuccessfully() throws Exception {
    // When & Then
    String responseContent =
        mockMvc
            .perform(
                post("/v1/incomes/salary")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(validCreateRequest))
                    .with(csrf()))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.currencyCode").value("INR"))
            .andExpect(jsonPath("$.basicAmount").value(50000.00))
            .andExpect(jsonPath("$.hraAmount").value(20000.00))
            .andExpect(jsonPath("$.otherAllowanceAmount").value(5000.00))
            .andExpect(jsonPath("$.bonusAmount").value(10000.00))
            .andExpect(jsonPath("$.empPfAmount").value(6000.00))
            .andExpect(jsonPath("$.professionTaxAmount").value(2400.00))
            .andExpect(jsonPath("$.incomeTaxAmount").value(12000.00))
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.createdOn").exists())
            .andExpect(jsonPath("$.updatedOn").exists())
            .andReturn()
            .getResponse()
            .getContentAsString();

    // Verify in database
    assertEquals(1, salaryIncomeRepository.count());

    // Extract and verify response
    SalaryIncomeDto response = objectMapper.readValue(responseContent, SalaryIncomeDto.class);
    assertEquals("INR", response.getCurrencyCode());
    assertEquals(new BigDecimal("50000.00"), response.getBasicAmount());
  }

  @Test
  @DisplayName("Should get salary income by ID successfully - Integration")
  void shouldGetSalaryIncomeByIdSuccessfully() throws Exception {
    // Given - Create a salary income record first
    String createResponse =
        mockMvc
            .perform(
                post("/v1/incomes/salary")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(validCreateRequest))
                    .with(csrf()))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

    SalaryIncomeDto createdIncome = objectMapper.readValue(createResponse, SalaryIncomeDto.class);

    // When & Then
    mockMvc
        .perform(get("/v1/incomes/salary/" + createdIncome.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(createdIncome.getId()))
        .andExpect(jsonPath("$.currencyCode").value("INR"))
        .andExpect(jsonPath("$.basicAmount").value(50000.00));
  }

  @Test
  @DisplayName("Should get all salary income records - Integration")
  void shouldGetAllSalaryIncomeRecords() throws Exception {
    // Given - Create two salary income records
    mockMvc
        .perform(
            post("/v1/incomes/salary")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validCreateRequest))
                .with(csrf()))
        .andExpect(status().isCreated());

    SalaryIncomeCreateRequest secondIncome = new SalaryIncomeCreateRequest();
    secondIncome.setUserId(testUser.getId()); // Add userId to request
    secondIncome.setCurrencyCode("USD");
    secondIncome.setBasicAmount(new BigDecimal("3000.00"));
    secondIncome.setHraAmount(new BigDecimal("1200.00"));
    secondIncome.setOtherAllowanceAmount(new BigDecimal("300.00"));
    secondIncome.setBonusAmount(new BigDecimal("600.00"));
    secondIncome.setEmpPfAmount(new BigDecimal("360.00"));
    secondIncome.setProfessionTaxAmount(new BigDecimal("144.00"));
    secondIncome.setIncomeTaxAmount(new BigDecimal("720.00"));

    mockMvc
        .perform(
            post("/v1/incomes/salary")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(secondIncome))
                .with(csrf()))
        .andExpect(status().isCreated());

    // When & Then
    mockMvc
        .perform(get("/v1/incomes"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[?(@.currencyCode == 'INR')]").exists())
        .andExpect(jsonPath("$[?(@.currencyCode == 'USD')]").exists());
  }

  @Test
  @DisplayName("Should get salary income by user ID - Integration")
  void shouldGetSalaryIncomeByUserId() throws Exception {
    // Given - Create salary income record
    mockMvc
        .perform(
            post("/v1/incomes/salary")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validCreateRequest))
                .with(csrf()))
        .andExpect(status().isCreated());

    // When & Then
    mockMvc
        .perform(get("/v1/incomes/" + testUser.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].currencyCode").value("INR"))
        .andExpect(jsonPath("$[0].basicAmount").value(50000.00));
  }

  @Test
  @DisplayName("Should update salary income successfully - Integration")
  void shouldUpdateSalaryIncomeSuccessfully() throws Exception {
    // Given - Create a salary income record first
    String createResponse =
        mockMvc
            .perform(
                post("/v1/incomes/salary")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(validCreateRequest))
                    .with(csrf()))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

    SalaryIncomeDto createdIncome = objectMapper.readValue(createResponse, SalaryIncomeDto.class);

    SalaryIncomeUpdateRequest updateRequest = new SalaryIncomeUpdateRequest();
    updateRequest.setCurrencyCode("USD");
    updateRequest.setBasicAmount(new BigDecimal("60000.00"));
    updateRequest.setHraAmount(new BigDecimal("25000.00"));
    updateRequest.setOtherAllowanceAmount(new BigDecimal("7000.00"));
    updateRequest.setBonusAmount(new BigDecimal("15000.00"));
    updateRequest.setEmpPfAmount(new BigDecimal("7200.00"));
    updateRequest.setProfessionTaxAmount(new BigDecimal("2880.00"));
    updateRequest.setIncomeTaxAmount(new BigDecimal("15000.00"));

    // When & Then
    mockMvc
        .perform(
            put("/v1/incomes/salary/" + createdIncome.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest))
                .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(createdIncome.getId()))
        .andExpect(jsonPath("$.currencyCode").value("USD"))
        .andExpect(jsonPath("$.basicAmount").value(60000.00))
        .andExpect(jsonPath("$.hraAmount").value(25000.00));
  }

  @Test
  @DisplayName("Should handle validation errors correctly - Integration")
  void shouldHandleValidationErrors() throws Exception {
    // Given - Invalid request with multiple validation errors
    SalaryIncomeCreateRequest invalidRequest = new SalaryIncomeCreateRequest();
    invalidRequest.setCurrencyCode(""); // Empty currency code
    invalidRequest.setBasicAmount(new BigDecimal("-1000")); // Negative amount
    invalidRequest.setHraAmount(null); // Null required field
    invalidRequest.setOtherAllowanceAmount(null); // Null required field
    invalidRequest.setBonusAmount(null); // Null required field
    invalidRequest.setEmpPfAmount(null); // Null required field
    invalidRequest.setProfessionTaxAmount(null); // Null required field
    invalidRequest.setIncomeTaxAmount(null); // Null required field

    // When & Then
    mockMvc
        .perform(
            post("/v1/incomes/salary")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("Validation Failed"))
        .andExpect(jsonPath("$.validationErrors").exists())
        .andExpect(
            jsonPath("$.validationErrors", hasSize(greaterThan(5)))); // Multiple validation errors
  }

  @Test
  @DisplayName("Should handle non-existent salary income - Integration")
  void shouldHandleNonExistentSalaryIncome() throws Exception {
    // When & Then
    mockMvc
        .perform(get("/v1/incomes/salary/99999"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error").value("Resource Not Found"))
        .andExpect(jsonPath("$.message").value("Salary income not found with id: 99999"));
  }

  @Test
  @DisplayName("Should handle non-existent user for salary income creation - Integration")
  void shouldHandleNonExistentUserForSalaryIncomeCreation() throws Exception {
    // Given
    SalaryIncomeCreateRequest invalidUserRequest = new SalaryIncomeCreateRequest();
    invalidUserRequest.setUserId(99999L); // Non-existent user
    invalidUserRequest.setCurrencyCode("INR");
    invalidUserRequest.setBasicAmount(new BigDecimal("50000.00"));
    invalidUserRequest.setHraAmount(new BigDecimal("20000.00"));
    invalidUserRequest.setOtherAllowanceAmount(new BigDecimal("5000.00"));
    invalidUserRequest.setBonusAmount(new BigDecimal("10000.00"));
    invalidUserRequest.setEmpPfAmount(new BigDecimal("6000.00"));
    invalidUserRequest.setProfessionTaxAmount(new BigDecimal("2400.00"));
    invalidUserRequest.setIncomeTaxAmount(new BigDecimal("12000.00"));

    // When & Then
    mockMvc
        .perform(
            post("/v1/incomes/salary")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidUserRequest))
                .with(csrf()))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error").value("Resource Not Found"))
        .andExpect(jsonPath("$.message").value("User not found with id: 99999"));
  }

  @Test
  @DisplayName("Should delete salary income successfully - Integration")
  void shouldDeleteSalaryIncomeSuccessfully() throws Exception {
    // Given - Create a salary income record first
    String createResponse =
        mockMvc
            .perform(
                post("/v1/incomes/salary")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(validCreateRequest)))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

    SalaryIncomeDto createdIncome = objectMapper.readValue(createResponse, SalaryIncomeDto.class);
    assertEquals(1, salaryIncomeRepository.count());

    // When & Then
    mockMvc
        .perform(delete("/v1/incomes/salary/" + createdIncome.getId()).with(csrf()))
        .andExpect(status().isNoContent());

    // Verify salary income is deleted
    mockMvc
        .perform(get("/v1/incomes/salary/" + createdIncome.getId()))
        .andExpect(status().isNotFound());

    // Verify count decreased
    assertEquals(0, salaryIncomeRepository.count());
  }

  @Test
  @DisplayName("Should handle delete non-existent salary income - Integration")
  void shouldHandleDeleteNonExistentSalaryIncome() throws Exception {
    // When & Then
    mockMvc
        .perform(delete("/v1/incomes/salary/99999"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error").value("Resource Not Found"))
        .andExpect(jsonPath("$.message").value("Salary income not found with id: 99999"));
  }

  @Test
  @DisplayName("Should handle update non-existent salary income - Integration")
  void shouldHandleUpdateNonExistentSalaryIncome() throws Exception {
    // Given
    SalaryIncomeUpdateRequest updateRequest = new SalaryIncomeUpdateRequest();
    updateRequest.setCurrencyCode("USD");
    updateRequest.setBasicAmount(new BigDecimal("60000.00"));
    updateRequest.setHraAmount(new BigDecimal("25000.00"));
    updateRequest.setOtherAllowanceAmount(new BigDecimal("7000.00"));
    updateRequest.setBonusAmount(new BigDecimal("15000.00"));
    updateRequest.setEmpPfAmount(new BigDecimal("7200.00"));
    updateRequest.setProfessionTaxAmount(new BigDecimal("2880.00"));
    updateRequest.setIncomeTaxAmount(new BigDecimal("15000.00"));

    // When & Then
    mockMvc
        .perform(
            put("/v1/incomes/salary/99999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest))
                .with(csrf()))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error").value("Resource Not Found"))
        .andExpect(jsonPath("$.message").value("Salary income not found with id: 99999"));
  }

  @Test
  @DisplayName("Should handle partial update correctly - Integration")
  void shouldHandlePartialUpdateCorrectly() throws Exception {
    // Given - Create a salary income record first
    String createResponse =
        mockMvc
            .perform(
                post("/v1/incomes/salary")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(validCreateRequest))
                    .with(csrf()))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

    SalaryIncomeDto createdIncome = objectMapper.readValue(createResponse, SalaryIncomeDto.class);

    // Partial update - only update basic amount and currency
    SalaryIncomeUpdateRequest partialUpdateRequest = new SalaryIncomeUpdateRequest();
    partialUpdateRequest.setCurrencyCode("USD");
    partialUpdateRequest.setBasicAmount(new BigDecimal("75000.00"));
    partialUpdateRequest.setHraAmount(new BigDecimal("20000.00")); // Keep original HRA
    partialUpdateRequest.setOtherAllowanceAmount(new BigDecimal("5000.00")); // Keep original
    partialUpdateRequest.setBonusAmount(new BigDecimal("10000.00")); // Keep original
    partialUpdateRequest.setEmpPfAmount(new BigDecimal("6000.00")); // Keep original
    partialUpdateRequest.setProfessionTaxAmount(new BigDecimal("2400.00")); // Keep original
    partialUpdateRequest.setIncomeTaxAmount(new BigDecimal("12000.00")); // Keep original

    // When & Then
    mockMvc
        .perform(
            put("/v1/incomes/salary/" + createdIncome.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(partialUpdateRequest))
                .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.currencyCode").value("USD")) // Should be updated
        .andExpect(jsonPath("$.basicAmount").value(75000.00)) // Should be updated
        .andExpect(jsonPath("$.hraAmount").value(20000.00)) // Should remain same
        .andExpect(jsonPath("$.otherAllowanceAmount").value(5000.00)); // Should remain same
  }

  @Test
  @DisplayName("Should return empty list for user with no salary income records - Integration")
  void shouldReturnEmptyListForUserWithNoSalaryIncomeRecords() throws Exception {
    // When & Then - No salary income records created
    mockMvc
        .perform(get("/v1/incomes/" + testUser.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(0))); // Empty array
  }
}
