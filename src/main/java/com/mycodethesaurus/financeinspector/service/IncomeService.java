package com.mycodethesaurus.financeinspector.service;

import com.mycodethesaurus.financeinspector.component.IncomeMapper;
import com.mycodethesaurus.financeinspector.dto.SalaryIncomeDto;
import com.mycodethesaurus.financeinspector.persistence.repository.SalaryIncomeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class IncomeService {

  private final SalaryIncomeRepository salaryIncomeRepository;
  private final IncomeMapper incomeMapper;

  public IncomeService(
      SalaryIncomeRepository salaryIncomeRepository, IncomeMapper incomeMapper) {
    this.salaryIncomeRepository = salaryIncomeRepository;
    this.incomeMapper = incomeMapper;
  }

  public List<SalaryIncomeDto> getAllSalaryIncome() {
    return incomeMapper.convertEntityListToDtoList(salaryIncomeRepository.findAll());
  }
}
