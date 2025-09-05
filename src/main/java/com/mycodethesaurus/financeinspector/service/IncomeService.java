package com.mycodethesaurus.financeinspector.service;

import com.mycodethesaurus.financeinspector.component.IncomeMapper;
import com.mycodethesaurus.financeinspector.dto.SalaryIncomeCreateRequest;
import com.mycodethesaurus.financeinspector.dto.SalaryIncomeDto;
import com.mycodethesaurus.financeinspector.dto.SalaryIncomeUpdateRequest;
import com.mycodethesaurus.financeinspector.exception.ResourceNotFoundException;
import com.mycodethesaurus.financeinspector.persistence.entity.SalaryIncomeEntity;
import com.mycodethesaurus.financeinspector.persistence.entity.UserEntity;
import com.mycodethesaurus.financeinspector.persistence.repository.SalaryIncomeRepository;
import com.mycodethesaurus.financeinspector.persistence.repository.UserRepository;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
public class IncomeService {

  private final SalaryIncomeRepository salaryIncomeRepository;
  private final UserRepository userRepository;
  private final IncomeMapper incomeMapper;

  public IncomeService(
      SalaryIncomeRepository salaryIncomeRepository,
      UserRepository userRepository,
      IncomeMapper incomeMapper) {
    this.salaryIncomeRepository = salaryIncomeRepository;
    this.userRepository = userRepository;
    this.incomeMapper = incomeMapper;
  }

  @Transactional(readOnly = true)
  public List<SalaryIncomeDto> getAllSalaryIncome() {
    log.info("Fetching all salary income records");
    return incomeMapper.convertEntityListToDtoList(salaryIncomeRepository.findAll());
  }

  @Transactional(readOnly = true)
  public List<SalaryIncomeDto> getAllSalaryIncomeForUser(long userId) {
    log.info("Fetching salary income records for user id: {}", userId);
    return incomeMapper.convertEntityListToDtoList(
        salaryIncomeRepository.findByUserEntityId(userId));
  }

  @Transactional(readOnly = true)
  public SalaryIncomeDto getSalaryIncomeById(Long incomeId) {
    log.info("Fetching salary income record with id: {}", incomeId);

    SalaryIncomeEntity entity =
        salaryIncomeRepository
            .findById(incomeId)
            .orElseThrow(
                () ->
                    new ResourceNotFoundException("Salary income not found with id: " + incomeId));

    return incomeMapper.convertEntityToDto(entity);
  }

  public SalaryIncomeDto createSalaryIncome(SalaryIncomeCreateRequest request) {
    log.info("Creating salary income record for user id: {}", request.getUserId());

    // Validate user exists
    UserEntity userEntity =
        userRepository
            .findById(request.getUserId())
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "User not found with id: " + request.getUserId()));

    // Map request to entity
    SalaryIncomeEntity entity = incomeMapper.createRequestToEntity(request, userEntity);

    // Save entity
    SalaryIncomeEntity savedEntity = salaryIncomeRepository.save(entity);

    log.info("Salary income record created successfully with id: {}", savedEntity.getId());
    return incomeMapper.convertEntityToDto(savedEntity);
  }

  public SalaryIncomeDto updateSalaryIncome(Long incomeId, SalaryIncomeUpdateRequest request) {
    log.info("Updating salary income record with id: {}", incomeId);

    SalaryIncomeEntity entity =
        salaryIncomeRepository
            .findById(incomeId)
            .orElseThrow(
                () ->
                    new ResourceNotFoundException("Salary income not found with id: " + incomeId));

    // Update entity
    incomeMapper.updateEntityFromRequest(entity, request);

    // Save updated entity
    SalaryIncomeEntity savedEntity = salaryIncomeRepository.save(entity);

    log.info("Salary income record updated successfully with id: {}", savedEntity.getId());
    return incomeMapper.convertEntityToDto(savedEntity);
  }

  public void deleteSalaryIncome(Long incomeId) {
    log.info("Deleting salary income record with id: {}", incomeId);

    if (!salaryIncomeRepository.existsById(incomeId)) {
      throw new ResourceNotFoundException("Salary income not found with id: " + incomeId);
    }

    salaryIncomeRepository.deleteById(incomeId);
    log.info("Salary income record deleted successfully with id: {}", incomeId);
  }

  /**
   * Checks if a salary income record is owned by the specified user.
   *
   * @param incomeId the ID of the income record to check
   * @param userId the ID of the user to check ownership for
   * @return true if the income record is owned by the user, false otherwise
   */
  @Transactional(readOnly = true)
  public boolean isIncomeOwnedByUser(Long incomeId, Long userId) {
    if (incomeId == null || userId == null) {
      return false;
    }

    return salaryIncomeRepository
        .findById(incomeId)
        .map(income -> income.getUserEntity().getId().equals(userId))
        .orElse(false);
  }
}
