package com.mycodethesaurus.financeinspector.component;

import static org.junit.jupiter.api.Assertions.*;

import com.mycodethesaurus.financeinspector.dto.SalaryIncomeCreateRequest;
import com.mycodethesaurus.financeinspector.dto.SalaryIncomeDto;
import com.mycodethesaurus.financeinspector.dto.SalaryIncomeUpdateRequest;
import com.mycodethesaurus.financeinspector.persistence.entity.SalaryIncomeEntity;
import com.mycodethesaurus.financeinspector.persistence.entity.UserEntity;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("IncomeMapper Unit Tests")
class IncomeMapperTest {

  @InjectMocks private IncomeMapper incomeMapper;

  private SalaryIncomeCreateRequest createRequest;
  private SalaryIncomeUpdateRequest updateRequest;
  private SalaryIncomeEntity salaryIncomeEntity;
  private UserEntity userEntity;

  @BeforeEach
  void setUp() {
    LocalDateTime now = LocalDateTime.now();

    userEntity = new UserEntity();
    userEntity.setId(1L);
    userEntity.setUserName("testuser");
    userEntity.setFirstName("John");
    userEntity.setLastName("Doe");
    userEntity.setEmail("john.doe@example.com");

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

    salaryIncomeEntity = new SalaryIncomeEntity();
    salaryIncomeEntity.setId(1L);
    salaryIncomeEntity.setUserEntity(userEntity);
    salaryIncomeEntity.setCurrencyCode("INR");
    salaryIncomeEntity.setBasicAmount(new BigDecimal("70000.00"));
    salaryIncomeEntity.setHraAmount(new BigDecimal("35000.00"));
    salaryIncomeEntity.setOtherAllowanceAmount(new BigDecimal("5000.00"));
    salaryIncomeEntity.setBonusAmount(new BigDecimal("12000.00"));
    salaryIncomeEntity.setEmpPfAmount(new BigDecimal("8400.00"));
    salaryIncomeEntity.setProfessionTaxAmount(new BigDecimal("200.00"));
    salaryIncomeEntity.setIncomeTaxAmount(new BigDecimal("9000.00"));
    salaryIncomeEntity.setCreatedOn(now);
    salaryIncomeEntity.setUpdatedOn(now);
  }

  @Test
  @DisplayName("Should convert entity to DTO correctly")
  void shouldConvertEntityToDtoCorrectly() {
    // When
    SalaryIncomeDto result = incomeMapper.convertEntityToDto(salaryIncomeEntity);

    // Then
    assertNotNull(result);
    assertEquals(1L, result.getId());
    assertEquals(1L, result.getUserId());
    assertEquals("INR", result.getCurrencyCode());
    assertEquals(new BigDecimal("70000.00"), result.getBasicAmount());
    assertEquals(new BigDecimal("35000.00"), result.getHraAmount());
    assertEquals(new BigDecimal("5000.00"), result.getOtherAllowanceAmount());
    assertEquals(new BigDecimal("12000.00"), result.getBonusAmount());
    assertEquals(new BigDecimal("8400.00"), result.getEmpPfAmount());
    assertEquals(new BigDecimal("200.00"), result.getProfessionTaxAmount());
    assertEquals(new BigDecimal("9000.00"), result.getIncomeTaxAmount());
    assertEquals(salaryIncomeEntity.getCreatedOn(), result.getCreatedOn());
    assertEquals(salaryIncomeEntity.getUpdatedOn(), result.getUpdatedOn());
  }

  @Test
  @DisplayName("Should handle null entity in convertEntityToDto")
  void shouldHandleNullEntityInConvertEntityToDto() {
    // Given
    SalaryIncomeEntity nullEntity = null;

    // When & Then
    assertThrows(
        NullPointerException.class,
        () -> {
          incomeMapper.convertEntityToDto(nullEntity);
        });
  }

  @Test
  @DisplayName("Should convert entity list to DTO list correctly")
  void shouldConvertEntityListToDtoListCorrectly() {
    // Given
    SalaryIncomeEntity entity2 = new SalaryIncomeEntity();
    entity2.setId(2L);
    entity2.setUserEntity(userEntity);
    entity2.setCurrencyCode("USD");
    entity2.setBasicAmount(new BigDecimal("80000.00"));
    entity2.setHraAmount(new BigDecimal("40000.00"));
    entity2.setOtherAllowanceAmount(new BigDecimal("6000.00"));
    entity2.setBonusAmount(new BigDecimal("15000.00"));
    entity2.setEmpPfAmount(new BigDecimal("9600.00"));
    entity2.setProfessionTaxAmount(new BigDecimal("250.00"));
    entity2.setIncomeTaxAmount(new BigDecimal("12000.00"));
    entity2.setCreatedOn(LocalDateTime.now());
    entity2.setUpdatedOn(LocalDateTime.now());

    List<SalaryIncomeEntity> entities = Arrays.asList(salaryIncomeEntity, entity2);

    // When
    List<SalaryIncomeDto> result = incomeMapper.convertEntityListToDtoList(entities);

    // Then
    assertNotNull(result);
    assertEquals(2, result.size());

    SalaryIncomeDto dto1 = result.get(0);
    assertEquals(1L, dto1.getId());
    assertEquals("INR", dto1.getCurrencyCode());
    assertEquals(new BigDecimal("70000.00"), dto1.getBasicAmount());

    SalaryIncomeDto dto2 = result.get(1);
    assertEquals(2L, dto2.getId());
    assertEquals("USD", dto2.getCurrencyCode());
    assertEquals(new BigDecimal("80000.00"), dto2.getBasicAmount());
  }

  @Test
  @DisplayName("Should handle empty list in convertEntityListToDtoList")
  void shouldHandleEmptyListInConvertEntityListToDtoList() {
    // Given
    List<SalaryIncomeEntity> emptyList = Arrays.asList();

    // When
    List<SalaryIncomeDto> result = incomeMapper.convertEntityListToDtoList(emptyList);

    // Then
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName("Should handle null list in convertEntityListToDtoList")
  void shouldHandleNullListInConvertEntityListToDtoList() {
    // Given
    List<SalaryIncomeEntity> nullList = null;

    // When & Then
    assertThrows(
        NullPointerException.class,
        () -> {
          incomeMapper.convertEntityListToDtoList(nullList);
        });
  }

  @Test
  @DisplayName("Should create entity from request correctly")
  void shouldCreateEntityFromRequestCorrectly() {
    // When
    SalaryIncomeEntity result = incomeMapper.createRequestToEntity(createRequest, userEntity);

    // Then
    assertNotNull(result);
    assertEquals(userEntity, result.getUserEntity());
    assertEquals("INR", result.getCurrencyCode());
    assertEquals(new BigDecimal("70000.00"), result.getBasicAmount());
    assertEquals(new BigDecimal("35000.00"), result.getHraAmount());
    assertEquals(new BigDecimal("5000.00"), result.getOtherAllowanceAmount());
    assertEquals(new BigDecimal("12000.00"), result.getBonusAmount());
    assertEquals(new BigDecimal("8400.00"), result.getEmpPfAmount());
    assertEquals(new BigDecimal("200.00"), result.getProfessionTaxAmount());
    assertEquals(new BigDecimal("9000.00"), result.getIncomeTaxAmount());
    assertNotNull(result.getCreatedOn());
    assertNotNull(result.getUpdatedOn());
  }

  @Test
  @DisplayName("Should handle null request in createRequestToEntity")
  void shouldHandleNullRequestInCreateRequestToEntity() {
    // Given
    SalaryIncomeCreateRequest nullRequest = null;

    // When & Then
    assertThrows(
        NullPointerException.class,
        () -> {
          incomeMapper.createRequestToEntity(nullRequest, userEntity);
        });
  }

  @Test
  @DisplayName("Should handle null user entity in createRequestToEntity")
  void shouldHandleNullUserEntityInCreateRequestToEntity() {
    // Given
    UserEntity nullUser = null;

    // When & Then
    assertDoesNotThrow(
        () -> {
          SalaryIncomeEntity result = incomeMapper.createRequestToEntity(createRequest, nullUser);
          assertNull(result.getUserEntity());
        });
  }

  @Test
  @DisplayName("Should update entity from request correctly")
  void shouldUpdateEntityFromRequestCorrectly() {
    // Given
    LocalDateTime originalCreatedOn = salaryIncomeEntity.getCreatedOn();
    LocalDateTime originalUpdatedOn = salaryIncomeEntity.getUpdatedOn();

    // When
    incomeMapper.updateEntityFromRequest(salaryIncomeEntity, updateRequest);

    // Then
    assertEquals("INR", salaryIncomeEntity.getCurrencyCode());
    assertEquals(new BigDecimal("75000.00"), salaryIncomeEntity.getBasicAmount());
    assertEquals(new BigDecimal("37500.00"), salaryIncomeEntity.getHraAmount());
    assertEquals(new BigDecimal("6000.00"), salaryIncomeEntity.getOtherAllowanceAmount());
    assertEquals(new BigDecimal("15000.00"), salaryIncomeEntity.getBonusAmount());
    assertEquals(new BigDecimal("9000.00"), salaryIncomeEntity.getEmpPfAmount());
    assertEquals(new BigDecimal("200.00"), salaryIncomeEntity.getProfessionTaxAmount());
    assertEquals(new BigDecimal("10000.00"), salaryIncomeEntity.getIncomeTaxAmount());
    assertEquals(originalCreatedOn, salaryIncomeEntity.getCreatedOn()); // Should remain unchanged
    // UpdatedOn should be updated or at least not before the original
    assertTrue(
        salaryIncomeEntity.getUpdatedOn().isAfter(originalUpdatedOn)
            || salaryIncomeEntity.getUpdatedOn().equals(originalUpdatedOn));
  }

  @Test
  @DisplayName("Should handle null entity in updateEntityFromRequest")
  void shouldHandleNullEntityInUpdateEntityFromRequest() {
    // Given
    SalaryIncomeEntity nullEntity = null;

    // When & Then
    assertThrows(
        NullPointerException.class,
        () -> {
          incomeMapper.updateEntityFromRequest(nullEntity, updateRequest);
        });
  }

  @Test
  @DisplayName("Should handle null request in updateEntityFromRequest")
  void shouldHandleNullRequestInUpdateEntityFromRequest() {
    // Given
    SalaryIncomeUpdateRequest nullRequest = null;

    // When & Then
    assertThrows(
        NullPointerException.class,
        () -> {
          incomeMapper.updateEntityFromRequest(salaryIncomeEntity, nullRequest);
        });
  }

  @Test
  @DisplayName("Should preserve user entity reference during update")
  void shouldPreserveUserEntityReferenceDuringUpdate() {
    // Given
    UserEntity originalUserEntity = salaryIncomeEntity.getUserEntity();

    // When
    incomeMapper.updateEntityFromRequest(salaryIncomeEntity, updateRequest);

    // Then
    assertEquals(originalUserEntity, salaryIncomeEntity.getUserEntity()); // Should remain unchanged
    assertEquals(originalUserEntity.getId(), salaryIncomeEntity.getUserEntity().getId());
  }

  @Test
  @DisplayName("Should handle decimal precision correctly")
  void shouldHandleDecimalPrecisionCorrectly() {
    // Given
    SalaryIncomeCreateRequest preciseRequest = new SalaryIncomeCreateRequest();
    preciseRequest.setUserId(1L);
    preciseRequest.setCurrencyCode("INR");
    preciseRequest.setBasicAmount(new BigDecimal("70000.1234"));
    preciseRequest.setHraAmount(new BigDecimal("35000.5678"));
    preciseRequest.setOtherAllowanceAmount(new BigDecimal("5000.9999"));
    preciseRequest.setBonusAmount(new BigDecimal("12000.0001"));
    preciseRequest.setEmpPfAmount(new BigDecimal("8400.4567"));
    preciseRequest.setProfessionTaxAmount(new BigDecimal("200.12"));
    preciseRequest.setIncomeTaxAmount(new BigDecimal("9000.89"));

    // When
    SalaryIncomeEntity result = incomeMapper.createRequestToEntity(preciseRequest, userEntity);

    // Then
    assertNotNull(result);
    assertEquals(new BigDecimal("70000.1234"), result.getBasicAmount());
    assertEquals(new BigDecimal("35000.5678"), result.getHraAmount());
    assertEquals(new BigDecimal("5000.9999"), result.getOtherAllowanceAmount());
    assertEquals(new BigDecimal("12000.0001"), result.getBonusAmount());
    assertEquals(new BigDecimal("8400.4567"), result.getEmpPfAmount());
    assertEquals(new BigDecimal("200.12"), result.getProfessionTaxAmount());
    assertEquals(new BigDecimal("9000.89"), result.getIncomeTaxAmount());
  }

  @Test
  @DisplayName("Should handle zero and negative amounts correctly")
  void shouldHandleZeroAndNegativeAmountsCorrectly() {
    // Given
    SalaryIncomeCreateRequest zeroRequest = new SalaryIncomeCreateRequest();
    zeroRequest.setUserId(1L);
    zeroRequest.setCurrencyCode("INR");
    zeroRequest.setBasicAmount(BigDecimal.ZERO);
    zeroRequest.setHraAmount(new BigDecimal("-100.00")); // Negative amount
    zeroRequest.setOtherAllowanceAmount(BigDecimal.ZERO);
    zeroRequest.setBonusAmount(BigDecimal.ZERO);
    zeroRequest.setEmpPfAmount(BigDecimal.ZERO);
    zeroRequest.setProfessionTaxAmount(BigDecimal.ZERO);
    zeroRequest.setIncomeTaxAmount(BigDecimal.ZERO);

    // When
    SalaryIncomeEntity result = incomeMapper.createRequestToEntity(zeroRequest, userEntity);

    // Then
    assertNotNull(result);
    assertEquals(BigDecimal.ZERO, result.getBasicAmount());
    assertEquals(new BigDecimal("-100.00"), result.getHraAmount());
    assertEquals(BigDecimal.ZERO, result.getOtherAllowanceAmount());
  }
}
