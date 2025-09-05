package com.mycodethesaurus.financeinspector.controller;

import com.mycodethesaurus.financeinspector.controller.api.ExpenseControllerApi;
import com.mycodethesaurus.financeinspector.dto.ExpenseCreateRequest;
import com.mycodethesaurus.financeinspector.dto.ExpenseResponse;
import com.mycodethesaurus.financeinspector.dto.ExpenseUpdateRequest;
import com.mycodethesaurus.financeinspector.service.ExpenseService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/expenses")
@Slf4j
public class ExpenseController implements ExpenseControllerApi {

  private final ExpenseService expenseService;

  public ExpenseController(ExpenseService expenseService) {
    this.expenseService = expenseService;
  }

  @PostMapping
  @PreAuthorize("@userAccessService.canAccessExpenses(authentication, #userId)")
  @Override
  public ResponseEntity<ExpenseResponse> createExpense(
      @RequestParam Long userId, @Valid @RequestBody ExpenseCreateRequest request) {
    log.info("Received request to create expense for user: {}", userId);
    ExpenseResponse response = expenseService.createExpense(userId, request);
    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }

  @GetMapping("/{expenseId}")
  @PreAuthorize("@userAccessService.canAccessExpenses(authentication, #userId)")
  @Override
  public ResponseEntity<ExpenseResponse> getExpenseById(
      @RequestParam Long userId, @PathVariable Long expenseId) {
    log.info("Received request to get expense {} for user: {}", expenseId, userId);
    ExpenseResponse response = expenseService.getExpenseById(userId, expenseId);
    return ResponseEntity.ok(response);
  }

  @GetMapping
  @PreAuthorize("@userAccessService.canAccessExpenses(authentication, #userId)")
  @Override
  public ResponseEntity<Page<ExpenseResponse>> getUserExpenses(
      @RequestParam Long userId, @PageableDefault(size = 20) Pageable pageable) {
    log.info("Received request to get expenses for user: {} with pagination: {}", userId, pageable);
    Page<ExpenseResponse> response = expenseService.getUserExpenses(userId, pageable);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/search")
  @PreAuthorize("@userAccessService.canAccessExpenses(authentication, #userId)")
  @Override
  public ResponseEntity<Page<ExpenseResponse>> searchExpenses(
      @RequestParam Long userId,
      @RequestParam String searchText,
      @PageableDefault(size = 20) Pageable pageable) {
    log.info("Received request to search expenses for user: {} with text: {}", userId, searchText);
    Page<ExpenseResponse> response = expenseService.searchExpenses(userId, searchText, pageable);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/category/{categoryId}")
  @PreAuthorize("@userAccessService.canAccessExpenses(authentication, #userId)")
  @Override
  public ResponseEntity<Page<ExpenseResponse>> getExpensesByCategory(
      @RequestParam Long userId,
      @PathVariable Long categoryId,
      @PageableDefault(size = 20) Pageable pageable) {
    log.info("Received request to get expenses by category: {} for user: {}", categoryId, userId);
    Page<ExpenseResponse> response =
        expenseService.getExpensesByCategory(userId, categoryId, pageable);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/date-range")
  @PreAuthorize("@userAccessService.canAccessExpenses(authentication, #userId)")
  @Override
  public ResponseEntity<List<ExpenseResponse>> getExpensesByDateRange(
      @RequestParam Long userId,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
    log.info(
        "Received request to get expenses for user: {} between {} and {}",
        userId,
        startDate,
        endDate);
    List<ExpenseResponse> response =
        expenseService.getExpensesByDateRange(userId, startDate, endDate);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/recent")
  @PreAuthorize("@userAccessService.canAccessExpenses(authentication, #userId)")
  @Override
  public ResponseEntity<List<ExpenseResponse>> getRecentExpenses(
      @RequestParam Long userId, @RequestParam(defaultValue = "10") int limit) {
    log.info("Received request to get {} recent expenses for user: {}", limit, userId);
    List<ExpenseResponse> response = expenseService.getRecentExpenses(userId, limit);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/count")
  @PreAuthorize("@userAccessService.canAccessExpenses(authentication, #userId)")
  @Override
  public ResponseEntity<Long> getExpenseCount(@RequestParam Long userId) {
    log.info("Received request to get expense count for user: {}", userId);
    long count = expenseService.getExpenseCount(userId);
    return ResponseEntity.ok(count);
  }

  @PutMapping("/{expenseId}")
  @PreAuthorize("@userAccessService.canAccessExpenses(authentication, #userId)")
  @Override
  public ResponseEntity<ExpenseResponse> updateExpense(
      @RequestParam Long userId,
      @PathVariable Long expenseId,
      @Valid @RequestBody ExpenseUpdateRequest request) {
    log.info("Received request to update expense {} for user: {}", expenseId, userId);
    ExpenseResponse response = expenseService.updateExpense(userId, expenseId, request);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{expenseId}")
  @PreAuthorize("@userAccessService.canAccessExpenses(authentication, #userId)")
  @Override
  public ResponseEntity<Void> deleteExpense(
      @RequestParam Long userId, @PathVariable Long expenseId) {
    log.info("Received request to delete expense {} for user: {}", expenseId, userId);
    expenseService.deleteExpense(userId, expenseId);
    return ResponseEntity.noContent().build();
  }
}
