package com.mycodethesaurus.financeinspector.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.mycodethesaurus.financeinspector.component.IncomeMapper;
import com.mycodethesaurus.financeinspector.dto.SalaryIncomeCreateRequest;
import com.mycodethesaurus.financeinspector.dto.SalaryIncomeDto;
import com.mycodethesaurus.financeinspector.dto.SalaryIncomeUpdateRequest;
import com.mycodethesaurus.financeinspector.exception.ResourceNotFoundException;
import com.mycodethesaurus.financeinspector.persistence.entity.SalaryIncomeEntity;
import com.mycodethesaurus.financeinspector.persistence.entity.UserEntity;
import com.mycodethesaurus.financeinspector.persistence.repository.SalaryIncomeRepository;
import com.mycodethesaurus.financeinspector.persistence.repository.UserRepository;
import java.math.BigDecimal;
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

@ExtendWith(MockitoExtension.class)
@DisplayName("IncomeService Unit Tests")
class IncomeServiceTest {

  @Mock private SalaryIncomeRepository salaryIncomeRepository;

  @Mock private UserRepository userRepository;

  @Mock private IncomeMapper incomeMapper;

  @InjectMocks private IncomeService incomeService;

  private SalaryIncomeEntity salaryIncomeEntity;
  private UserEntity userEntity;
  private SalaryIncomeCreateRequest createRequest;
  private SalaryIncomeUpdateRequest updateRequest;
  private SalaryIncomeDto salaryIncomeDto;

  @BeforeEach
  void setUp() {
    userEntity = new UserEntity();
    userEntity.setId(1L);
    userEntity.setUserName("testuser");
    userEntity.setFirstName("John");
    userEntity.setLastName("Doe");
    userEntity.setEmail("john.doe@example.com");

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
    salaryIncomeEntity.setCreatedOn(LocalDateTime.now());
    salaryIncomeEntity.setUpdatedOn(LocalDateTime.now());

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
  void shouldGetAllSalaryIncomeSuccessfully() {
    // Given
    List<SalaryIncomeEntity> entities = Arrays.asList(salaryIncomeEntity);
    List<SalaryIncomeDto> dtos = Arrays.asList(salaryIncomeDto);

    when(salaryIncomeRepository.findAll()).thenReturn(entities);
    when(incomeMapper.convertEntityListToDtoList(entities)).thenReturn(dtos);

    // When
    List<SalaryIncomeDto> result = incomeService.getAllSalaryIncome();

    // Then
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals("INR", result.get(0).getCurrencyCode());
    assertEquals(new BigDecimal("70000.00"), result.get(0).getBasicAmount());

    verify(salaryIncomeRepository).findAll();
    verify(incomeMapper).convertEntityListToDtoList(entities);
  }

  @Test
  @DisplayName("Should get salary income for user successfully")
  void shouldGetSalaryIncomeForUserSuccessfully() {
    // Given
    Long userId = 1L;
    List<SalaryIncomeEntity> entities = Arrays.asList(salaryIncomeEntity);
    List<SalaryIncomeDto> dtos = Arrays.asList(salaryIncomeDto);

    when(salaryIncomeRepository.findByUserEntityId(userId)).thenReturn(entities);
    when(incomeMapper.convertEntityListToDtoList(entities)).thenReturn(dtos);

    // When
    List<SalaryIncomeDto> result = incomeService.getAllSalaryIncomeForUser(userId);

    // Then
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(userId, result.get(0).getUserId());

    verify(salaryIncomeRepository).findByUserEntityId(userId);
    verify(incomeMapper).convertEntityListToDtoList(entities);
  }

  @Test
  @DisplayName("Should get salary income by ID successfully")
  void shouldGetSalaryIncomeByIdSuccessfully() {
    // Given
    when(salaryIncomeRepository.findById(1L)).thenReturn(Optional.of(salaryIncomeEntity));
    when(incomeMapper.convertEntityToDto(salaryIncomeEntity)).thenReturn(salaryIncomeDto);

    // When
    SalaryIncomeDto result = incomeService.getSalaryIncomeById(1L);

    // Then
    assertNotNull(result);
    assertEquals(1L, result.getId());
    assertEquals("INR", result.getCurrencyCode());

    verify(salaryIncomeRepository).findById(1L);
    verify(incomeMapper).convertEntityToDto(salaryIncomeEntity);
  }

  @Test
  @DisplayName("Should throw exception when salary income not found by ID")
  void shouldThrowExceptionWhenSalaryIncomeNotFoundById() {
    // Given
    when(salaryIncomeRepository.findById(1L)).thenReturn(Optional.empty());

    // When & Then
    ResourceNotFoundException exception =
        assertThrows(ResourceNotFoundException.class, () -> incomeService.getSalaryIncomeById(1L));

    assertEquals("Salary income not found with id: 1", exception.getMessage());
    verify(salaryIncomeRepository).findById(1L);
    verify(incomeMapper, never()).convertEntityToDto(any());
  }

  @Test
  @DisplayName("Should create salary income successfully")
  void shouldCreateSalaryIncomeSuccessfully() {
    // Given
    when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
    when(incomeMapper.createRequestToEntity(createRequest, userEntity))
        .thenReturn(salaryIncomeEntity);
    when(salaryIncomeRepository.save(salaryIncomeEntity)).thenReturn(salaryIncomeEntity);
    when(incomeMapper.convertEntityToDto(salaryIncomeEntity)).thenReturn(salaryIncomeDto);

    // When
    SalaryIncomeDto result = incomeService.createSalaryIncome(createRequest);

    // Then
    assertNotNull(result);
    assertEquals("INR", result.getCurrencyCode());
    assertEquals(new BigDecimal("70000.00"), result.getBasicAmount());

    verify(userRepository).findById(1L);
    verify(incomeMapper).createRequestToEntity(createRequest, userEntity);
    verify(salaryIncomeRepository).save(salaryIncomeEntity);
    verify(incomeMapper).convertEntityToDto(salaryIncomeEntity);
  }

  @Test
  @DisplayName("Should throw exception when user not found for salary income creation")
  void shouldThrowExceptionWhenUserNotFoundForCreation() {
    // Given
    when(userRepository.findById(1L)).thenReturn(Optional.empty());

    // When & Then
    ResourceNotFoundException exception =
        assertThrows(
            ResourceNotFoundException.class, () -> incomeService.createSalaryIncome(createRequest));

    assertEquals("User not found with id: 1", exception.getMessage());
    verify(userRepository).findById(1L);
    verify(salaryIncomeRepository, never()).save(any());
  }

  @Test
  @DisplayName("Should update salary income successfully")
  void shouldUpdateSalaryIncomeSuccessfully() {
    // Given
    when(salaryIncomeRepository.findById(1L)).thenReturn(Optional.of(salaryIncomeEntity));
    when(salaryIncomeRepository.save(salaryIncomeEntity)).thenReturn(salaryIncomeEntity);
    when(incomeMapper.convertEntityToDto(salaryIncomeEntity)).thenReturn(salaryIncomeDto);

    // When
    SalaryIncomeDto result = incomeService.updateSalaryIncome(1L, updateRequest);

    // Then
    assertNotNull(result);

    verify(salaryIncomeRepository).findById(1L);
    verify(incomeMapper).updateEntityFromRequest(salaryIncomeEntity, updateRequest);
    verify(salaryIncomeRepository).save(salaryIncomeEntity);
    verify(incomeMapper).convertEntityToDto(salaryIncomeEntity);
  }

  @Test
  @DisplayName("Should throw exception when salary income not found for update")
  void shouldThrowExceptionWhenSalaryIncomeNotFoundForUpdate() {
    // Given
    when(salaryIncomeRepository.findById(1L)).thenReturn(Optional.empty());

    // When & Then
    ResourceNotFoundException exception =
        assertThrows(
            ResourceNotFoundException.class,
            () -> incomeService.updateSalaryIncome(1L, updateRequest));

    assertEquals("Salary income not found with id: 1", exception.getMessage());
    verify(salaryIncomeRepository).findById(1L);
    verify(salaryIncomeRepository, never()).save(any());
  }

  @Test
  @DisplayName("Should delete salary income successfully")
  void shouldDeleteSalaryIncomeSuccessfully() {
    // Given
    when(salaryIncomeRepository.existsById(1L)).thenReturn(true);

    // When
    assertDoesNotThrow(() -> incomeService.deleteSalaryIncome(1L));

    // Then
    verify(salaryIncomeRepository).existsById(1L);
    verify(salaryIncomeRepository).deleteById(1L);
  }

  @Test
  @DisplayName("Should throw exception when deleting non-existent salary income")
  void shouldThrowExceptionWhenDeletingNonExistentSalaryIncome() {
    // Given
    when(salaryIncomeRepository.existsById(1L)).thenReturn(false);

    // When & Then
    ResourceNotFoundException exception =
        assertThrows(ResourceNotFoundException.class, () -> incomeService.deleteSalaryIncome(1L));

    assertEquals("Salary income not found with id: 1", exception.getMessage());
    verify(salaryIncomeRepository).existsById(1L);
    verify(salaryIncomeRepository, never()).deleteById(any());
  }
}
