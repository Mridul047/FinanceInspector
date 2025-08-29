package com.mycodethesaurus.financeinspector.controller;

import com.mycodethesaurus.financeinspector.dto.SalaryIncomeCreateRequest;
import com.mycodethesaurus.financeinspector.dto.SalaryIncomeDto;
import com.mycodethesaurus.financeinspector.dto.SalaryIncomeUpdateRequest;
import com.mycodethesaurus.financeinspector.service.IncomeService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1")
@Slf4j
public class IncomeController {

  private final IncomeService incomeService;

  public IncomeController(IncomeService incomeService) {
    this.incomeService = incomeService;
  }

  @GetMapping("/incomes")
  public ResponseEntity<List<SalaryIncomeDto>> getAllIncome() {
    log.info("Received request to get all salary income records");
    return ResponseEntity.ok(incomeService.getAllSalaryIncome());
  }

  @GetMapping("/incomes/{userId}")
  public ResponseEntity<List<SalaryIncomeDto>> getAllIncomeByUserId(@PathVariable long userId) {
    log.info("Received request to get salary income records for user id: {}", userId);
    return ResponseEntity.ok(incomeService.getAllSalaryIncomeForUser(userId));
  }

  @GetMapping("/incomes/salary/{incomeId}")
  public ResponseEntity<SalaryIncomeDto> getSalaryIncomeById(@PathVariable Long incomeId) {
    log.info("Received request to get salary income record with id: {}", incomeId);
    SalaryIncomeDto response = incomeService.getSalaryIncomeById(incomeId);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/incomes/salary")
  public ResponseEntity<SalaryIncomeDto> createSalaryIncome(
      @Valid @RequestBody SalaryIncomeCreateRequest request) {
    log.info(
        "Received request to create salary income record for user id: {}", request.getUserId());
    SalaryIncomeDto response = incomeService.createSalaryIncome(request);
    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }

  @PutMapping("/incomes/salary/{incomeId}")
  public ResponseEntity<SalaryIncomeDto> updateSalaryIncome(
      @PathVariable Long incomeId, @Valid @RequestBody SalaryIncomeUpdateRequest request) {
    log.info("Received request to update salary income record with id: {}", incomeId);
    SalaryIncomeDto response = incomeService.updateSalaryIncome(incomeId, request);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/incomes/salary/{incomeId}")
  public ResponseEntity<Void> deleteSalaryIncome(@PathVariable Long incomeId) {
    log.info("Received request to delete salary income record with id: {}", incomeId);
    incomeService.deleteSalaryIncome(incomeId);
    return ResponseEntity.noContent().build();
  }
}
