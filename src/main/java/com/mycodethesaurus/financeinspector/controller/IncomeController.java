package com.mycodethesaurus.financeinspector.controller;

import com.mycodethesaurus.financeinspector.dto.SalaryIncomeDto;
import com.mycodethesaurus.financeinspector.service.IncomeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1")
public class IncomeController {

  private final IncomeService incomeService;

  public IncomeController(IncomeService incomeService) {
    this.incomeService = incomeService;
  }

  @GetMapping("/incomes")
  public ResponseEntity<List<SalaryIncomeDto>> getIncomes() {
    return ResponseEntity.ok(incomeService.getAllSalaryIncome());
  }
}
