package com.mycodethesaurus.financeinspector.controller;

import com.mycodethesaurus.financeinspector.dto.ErrorResponse;
import com.mycodethesaurus.financeinspector.dto.ExpenseCreateRequest;
import com.mycodethesaurus.financeinspector.dto.ExpenseResponse;
import com.mycodethesaurus.financeinspector.dto.ExpenseUpdateRequest;
import com.mycodethesaurus.financeinspector.dto.ValidationErrorResponse;
import com.mycodethesaurus.financeinspector.service.ExpenseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/expenses")
@Slf4j
@Tag(
    name = "Expense Management",
    description =
        "APIs for managing personal expenses, including creation, search, categorization, and reporting")
public class ExpenseController {

  private final ExpenseService expenseService;

  public ExpenseController(ExpenseService expenseService) {
    this.expenseService = expenseService;
  }

  @PostMapping
  @Operation(
      summary = "Create a new expense",
      description =
          "Creates a new expense record for the specified user with amount, category, and other details.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "Expense created successfully",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ExpenseResponse.class),
                    examples =
                        @ExampleObject(
                            name = "Created Expense Response",
                            value =
                                """
                    {
                      "id": 1,
                      "userId": 1,
                      "category": {
                        "id": 1,
                        "name": "Food & Dining",
                        "colorCode": "#FF5722",
                        "iconName": "restaurant",
                        "parent": null
                      },
                      "amount": 25.50,
                      "currencyCode": "USD",
                      "description": "Lunch at downtown restaurant",
                      "expenseDate": "2024-01-15",
                      "location": "Downtown",
                      "merchant": "Cafe Express",
                      "paymentMethod": "CREDIT_CARD",
                      "receiptUrl": null,
                      "notes": "Business lunch with client",
                      "tags": ["business", "lunch"],
                      "createdOn": "2024-01-15T12:30:00",
                      "updatedOn": "2024-01-15T12:30:00"
                    }"""))),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input data or validation errors",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ValidationErrorResponse.class))),
        @ApiResponse(
            responseCode = "404",
            description = "User or category not found",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
      })
  public ResponseEntity<ExpenseResponse> createExpense(
      @Parameter(description = "User ID", required = true, example = "1") @RequestParam Long userId,
      @Parameter(description = "Expense creation details", required = true) @Valid @RequestBody
          ExpenseCreateRequest request) {
    log.info("Received request to create expense for user: {}", userId);
    ExpenseResponse response = expenseService.createExpense(userId, request);
    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }

  @GetMapping("/{expenseId}")
  @Operation(
      summary = "Get expense by ID",
      description = "Retrieves a specific expense by its unique identifier")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Expense found and returned successfully",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ExpenseResponse.class),
                    examples =
                        @ExampleObject(
                            name = "Expense Details Response",
                            value =
                                """
                    {
                      "id": 1,
                      "userId": 1,
                      "category": {
                        "id": 1,
                        "name": "Food & Dining",
                        "colorCode": "#FF5722",
                        "iconName": "restaurant",
                        "parent": null
                      },
                      "amount": 25.50,
                      "currencyCode": "USD",
                      "description": "Lunch at downtown restaurant",
                      "expenseDate": "2024-01-15",
                      "location": "Downtown",
                      "merchant": "Cafe Express",
                      "paymentMethod": "CREDIT_CARD",
                      "receiptUrl": null,
                      "notes": "Business lunch with client",
                      "tags": ["business", "lunch"],
                      "createdOn": "2024-01-15T12:30:00",
                      "updatedOn": "2024-01-15T12:30:00"
                    }"""))),
        @ApiResponse(
            responseCode = "404",
            description = "Expense not found with the specified ID",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
      })
  public ResponseEntity<ExpenseResponse> getExpenseById(
      @Parameter(description = "User ID", required = true, example = "1") @RequestParam Long userId,
      @Parameter(description = "Expense ID", required = true, example = "1") @PathVariable
          Long expenseId) {
    log.info("Received request to get expense {} for user: {}", expenseId, userId);
    ExpenseResponse response = expenseService.getExpenseById(userId, expenseId);
    return ResponseEntity.ok(response);
  }

  @GetMapping
  @Operation(
      summary = "Get user expenses",
      description =
          "Retrieves paginated list of expenses for the specified user, ordered by expense date (most recent first)")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Expenses retrieved successfully",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Page.class),
                    examples =
                        @ExampleObject(
                            name = "Paginated Expenses Response",
                            value =
                                """
                    {
                      "content": [
                        {
                          "id": 1,
                          "userId": 1,
                          "category": {
                            "id": 1,
                            "name": "Food & Dining",
                            "colorCode": "#FF5722",
                            "iconName": "restaurant"
                          },
                          "amount": 25.50,
                          "currencyCode": "USD",
                          "description": "Lunch at downtown restaurant",
                          "expenseDate": "2024-01-15",
                          "merchant": "Cafe Express",
                          "paymentMethod": "CREDIT_CARD"
                        }
                      ],
                      "pageable": {
                        "pageNumber": 0,
                        "pageSize": 20
                      },
                      "totalElements": 1,
                      "totalPages": 1,
                      "first": true,
                      "last": true
                    }""")))
      })
  public ResponseEntity<Page<ExpenseResponse>> getUserExpenses(
      @Parameter(description = "User ID", required = true, example = "1") @RequestParam Long userId,
      @Parameter(description = "Pagination parameters") @PageableDefault(size = 20)
          Pageable pageable) {
    log.info("Received request to get expenses for user: {} with pagination: {}", userId, pageable);
    Page<ExpenseResponse> response = expenseService.getUserExpenses(userId, pageable);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/search")
  @Operation(
      summary = "Search expenses",
      description = "Search expenses by description or merchant name with pagination")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Search results retrieved successfully",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Page.class)))
      })
  public ResponseEntity<Page<ExpenseResponse>> searchExpenses(
      @Parameter(description = "User ID", required = true, example = "1") @RequestParam Long userId,
      @Parameter(
              description = "Search text for description or merchant",
              required = true,
              example = "restaurant")
          @RequestParam
          String searchText,
      @Parameter(description = "Pagination parameters") @PageableDefault(size = 20)
          Pageable pageable) {
    log.info("Received request to search expenses for user: {} with text: {}", userId, searchText);
    Page<ExpenseResponse> response = expenseService.searchExpenses(userId, searchText, pageable);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/category/{categoryId}")
  @Operation(
      summary = "Get expenses by category",
      description = "Retrieves paginated expenses for a specific category")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Category expenses retrieved successfully",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Page.class))),
        @ApiResponse(
            responseCode = "404",
            description = "Category not found",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
      })
  public ResponseEntity<Page<ExpenseResponse>> getExpensesByCategory(
      @Parameter(description = "User ID", required = true, example = "1") @RequestParam Long userId,
      @Parameter(description = "Category ID", required = true, example = "1") @PathVariable
          Long categoryId,
      @Parameter(description = "Pagination parameters") @PageableDefault(size = 20)
          Pageable pageable) {
    log.info("Received request to get expenses by category: {} for user: {}", categoryId, userId);
    Page<ExpenseResponse> response =
        expenseService.getExpensesByCategory(userId, categoryId, pageable);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/date-range")
  @Operation(
      summary = "Get expenses by date range",
      description = "Retrieves expenses within the specified date range")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Date range expenses retrieved successfully",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = List.class)))
      })
  public ResponseEntity<List<ExpenseResponse>> getExpensesByDateRange(
      @Parameter(description = "User ID", required = true, example = "1") @RequestParam Long userId,
      @Parameter(description = "Start date (inclusive)", required = true, example = "2024-01-01")
          @RequestParam
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate startDate,
      @Parameter(description = "End date (inclusive)", required = true, example = "2024-01-31")
          @RequestParam
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate endDate) {
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
  @Operation(
      summary = "Get recent expenses",
      description = "Retrieves the most recent expenses for the user")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Recent expenses retrieved successfully",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = List.class)))
      })
  public ResponseEntity<List<ExpenseResponse>> getRecentExpenses(
      @Parameter(description = "User ID", required = true, example = "1") @RequestParam Long userId,
      @Parameter(description = "Number of recent expenses to retrieve", example = "10")
          @RequestParam(defaultValue = "10")
          int limit) {
    log.info("Received request to get {} recent expenses for user: {}", limit, userId);
    List<ExpenseResponse> response = expenseService.getRecentExpenses(userId, limit);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/count")
  @Operation(
      summary = "Get expense count",
      description = "Returns the total number of expenses for the user")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Expense count retrieved successfully",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Long.class),
                    examples = @ExampleObject(name = "Expense Count Response", value = "42")))
      })
  public ResponseEntity<Long> getExpenseCount(
      @Parameter(description = "User ID", required = true, example = "1") @RequestParam
          Long userId) {
    log.info("Received request to get expense count for user: {}", userId);
    long count = expenseService.getExpenseCount(userId);
    return ResponseEntity.ok(count);
  }

  @PutMapping("/{expenseId}")
  @Operation(summary = "Update expense", description = "Updates an existing expense record")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Expense updated successfully",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ExpenseResponse.class),
                    examples =
                        @ExampleObject(
                            name = "Updated Expense Response",
                            value =
                                """
                    {
                      "id": 1,
                      "userId": 1,
                      "category": {
                        "id": 1,
                        "name": "Food & Dining",
                        "colorCode": "#FF5722",
                        "iconName": "restaurant"
                      },
                      "amount": 30.00,
                      "currencyCode": "USD",
                      "description": "Updated lunch expense",
                      "expenseDate": "2024-01-15",
                      "location": "Downtown",
                      "merchant": "Cafe Express",
                      "paymentMethod": "CREDIT_CARD",
                      "receiptUrl": null,
                      "notes": "Updated business lunch with client",
                      "tags": ["business", "lunch"],
                      "createdOn": "2024-01-15T12:30:00",
                      "updatedOn": "2024-01-15T14:45:00"
                    }"""))),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input data or validation errors",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ValidationErrorResponse.class))),
        @ApiResponse(
            responseCode = "404",
            description = "Expense or category not found",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
      })
  public ResponseEntity<ExpenseResponse> updateExpense(
      @Parameter(description = "User ID", required = true, example = "1") @RequestParam Long userId,
      @Parameter(description = "Expense ID", required = true, example = "1") @PathVariable
          Long expenseId,
      @Parameter(description = "Updated expense information", required = true) @Valid @RequestBody
          ExpenseUpdateRequest request) {
    log.info("Received request to update expense {} for user: {}", expenseId, userId);
    ExpenseResponse response = expenseService.updateExpense(userId, expenseId, request);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{expenseId}")
  @Operation(summary = "Delete expense", description = "Permanently deletes an expense record")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "204", description = "Expense deleted successfully"),
        @ApiResponse(
            responseCode = "404",
            description = "Expense not found with the specified ID",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
      })
  public ResponseEntity<Void> deleteExpense(
      @Parameter(description = "User ID", required = true, example = "1") @RequestParam Long userId,
      @Parameter(description = "Expense ID", required = true, example = "1") @PathVariable
          Long expenseId) {
    log.info("Received request to delete expense {} for user: {}", expenseId, userId);
    expenseService.deleteExpense(userId, expenseId);
    return ResponseEntity.noContent().build();
  }
}
