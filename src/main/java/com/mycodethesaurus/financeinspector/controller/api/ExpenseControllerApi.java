package com.mycodethesaurus.financeinspector.controller.api;

import com.mycodethesaurus.financeinspector.controller.api.common.ApiParameters;
import com.mycodethesaurus.financeinspector.dto.ErrorResponse;
import com.mycodethesaurus.financeinspector.dto.ExpenseCreateRequest;
import com.mycodethesaurus.financeinspector.dto.ExpenseResponse;
import com.mycodethesaurus.financeinspector.dto.ExpenseUpdateRequest;
import com.mycodethesaurus.financeinspector.dto.ValidationErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;

/**
 * API interface for expense management operations. Contains all OpenAPI documentation and method
 * signatures.
 */
@Tag(
    name = "Expense Management",
    description =
        "APIs for managing personal expenses, including creation, search, categorization, and reporting")
public interface ExpenseControllerApi {

  @Operation(
      summary = "Create a new expense",
      description =
          "Creates a new expense record for the specified user with amount, category, and other details. Requires user authentication.")
  @SecurityRequirement(name = "bearerAuth")
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
                    schema = @Schema(implementation = ValidationErrorResponse.class),
                    examples =
                        @ExampleObject(
                            name = "Validation Error",
                            value =
                                """
                            {
                              "message": "Validation failed. Please check your request data.",
                              "error": "Bad Request",
                              "status": 400,
                              "timestamp": "2024-01-15T12:30:00",
                              "path": "/v1/users/1/expenses",
                              "details": "Amount must be greater than 0"
                            }"""))),
        @ApiResponse(
            responseCode = "401",
            description = "Authentication required",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples =
                        @ExampleObject(
                            name = "Authentication Required",
                            value =
                                """
                            {
                              "message": "Authentication required. Please provide a valid JWT token.",
                              "error": "Unauthorized",
                              "status": 401,
                              "timestamp": "2024-01-15T12:30:00",
                              "path": "/v1/users/1/expenses"
                            }"""))),
        @ApiResponse(
            responseCode = "403",
            description = "Access forbidden - user can only create expenses for own account",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples =
                        @ExampleObject(
                            name = "Access Forbidden",
                            value =
                                """
                            {
                              "message": "Access forbidden. You can only create expenses for your own account.",
                              "error": "Forbidden",
                              "status": 403,
                              "timestamp": "2024-01-15T12:30:00",
                              "path": "/v1/users/1/expenses"
                            }"""))),
        @ApiResponse(
            responseCode = "404",
            description = "User or category not found",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples =
                        @ExampleObject(
                            name = "Resource Not Found",
                            value =
                                """
                            {
                              "message": "Category not found with ID: 1",
                              "error": "Not Found",
                              "status": 404,
                              "timestamp": "2024-01-15T12:30:00",
                              "path": "/v1/users/1/expenses"
                            }"""))),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
      })
  ResponseEntity<ExpenseResponse> createExpense(
      @Parameter(
              description = ApiParameters.USER_ID_DESC,
              required = true,
              example = ApiParameters.USER_ID_EXAMPLE)
          Long userId,
      @Parameter(
              description =
                  "Expense creation details including amount, category, description, date, and optional metadata",
              required = true,
              schema = @Schema(implementation = ExpenseCreateRequest.class),
              examples =
                  @ExampleObject(
                      name = "Expense Creation Request",
                      value =
                          """
                      {
                        "categoryId": 1,
                        "amount": 25.50,
                        "currencyCode": "USD",
                        "description": "Lunch at downtown restaurant",
                        "expenseDate": "2024-01-15",
                        "location": "Downtown",
                        "merchant": "Cafe Express",
                        "paymentMethod": "CREDIT_CARD",
                        "receiptUrl": "https://example.com/receipts/receipt-123.pdf",
                        "notes": "Business lunch with client",
                        "tags": ["business", "lunch", "client-meeting"]
                      }"""))
          @Valid
          ExpenseCreateRequest request);

  @Operation(
      summary = "Get expense by ID",
      description =
          "Retrieves a specific expense by its unique identifier. Requires user authentication and access to own expenses.")
  @SecurityRequirement(name = "bearerAuth")
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
            responseCode = "401",
            description = "Authentication required",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples =
                        @ExampleObject(
                            name = "Authentication Required",
                            value =
                                """
                            {
                              "message": "Authentication required. Please provide a valid JWT token.",
                              "error": "Unauthorized",
                              "status": 401,
                              "timestamp": "2024-01-15T12:30:00",
                              "path": "/v1/users/1/expenses/1"
                            }"""))),
        @ApiResponse(
            responseCode = "403",
            description = "Access forbidden - user can only access own expenses",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples =
                        @ExampleObject(
                            name = "Access Forbidden",
                            value =
                                """
                            {
                              "message": "Access forbidden. You can only access your own expenses.",
                              "error": "Forbidden",
                              "status": 403,
                              "timestamp": "2024-01-15T12:30:00",
                              "path": "/v1/users/1/expenses/1"
                            }"""))),
        @ApiResponse(
            responseCode = "404",
            description = "Expense not found with the specified ID",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples =
                        @ExampleObject(
                            name = "Expense Not Found",
                            value =
                                """
                            {
                              "message": "Expense not found with ID: 1",
                              "error": "Not Found",
                              "status": 404,
                              "timestamp": "2024-01-15T12:30:00",
                              "path": "/v1/users/1/expenses/1"
                            }"""))),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
      })
  ResponseEntity<ExpenseResponse> getExpenseById(
      @Parameter(
              description = ApiParameters.USER_ID_DESC,
              required = true,
              example = ApiParameters.USER_ID_EXAMPLE)
          Long userId,
      @Parameter(
              description = ApiParameters.EXPENSE_ID_DESC,
              required = true,
              example = ApiParameters.EXPENSE_ID_EXAMPLE)
          Long expenseId);

  @Operation(
      summary = "Get user expenses",
      description =
          "Retrieves paginated list of expenses for the specified user, ordered by expense date (most recent first). Requires user authentication and access to own expenses.")
  @SecurityRequirement(name = "bearerAuth")
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
                            }"""))),
        @ApiResponse(
            responseCode = "401",
            description = "Authentication required",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples =
                        @ExampleObject(
                            name = "Authentication Required",
                            value =
                                """
                            {
                              "message": "Authentication required. Please provide a valid JWT token.",
                              "error": "Unauthorized",
                              "status": 401,
                              "timestamp": "2024-01-15T12:30:00",
                              "path": "/v1/users/1/expenses"
                            }"""))),
        @ApiResponse(
            responseCode = "403",
            description = "Access forbidden - user can only access own expenses",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples =
                        @ExampleObject(
                            name = "Access Forbidden",
                            value =
                                """
                            {
                              "message": "Access forbidden. You can only access your own expenses.",
                              "error": "Forbidden",
                              "status": 403,
                              "timestamp": "2024-01-15T12:30:00",
                              "path": "/v1/users/1/expenses"
                            }"""))),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
      })
  ResponseEntity<Page<ExpenseResponse>> getUserExpenses(
      @Parameter(
              description = ApiParameters.USER_ID_DESC,
              required = true,
              example = ApiParameters.USER_ID_EXAMPLE)
          Long userId,
      @Parameter(
              description = "Pagination parameters for expense list",
              schema = @Schema(implementation = Pageable.class),
              examples =
                  @ExampleObject(
                      name = "Pagination Example",
                      value = "page=0&size=20&sort=expenseDate,desc"))
          Pageable pageable);

  @Operation(
      summary = "Search expenses",
      description =
          "Search expenses by description or merchant name with pagination. Requires user authentication and searches only user's own expenses.")
  @SecurityRequirement(name = "bearerAuth")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Search results retrieved successfully",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Page.class))),
        @ApiResponse(
            responseCode = "401",
            description = "Authentication required",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Access forbidden - user can only search own expenses",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
      })
  ResponseEntity<Page<ExpenseResponse>> searchExpenses(
      @Parameter(
              description = ApiParameters.USER_ID_DESC,
              required = true,
              example = ApiParameters.USER_ID_EXAMPLE)
          Long userId,
      @Parameter(
              description = ApiParameters.SEARCH_TEXT_DESC,
              required = true,
              example = ApiParameters.SEARCH_TEXT_EXAMPLE)
          String searchText,
      @Parameter(
              description = "Pagination parameters for search results",
              schema = @Schema(implementation = Pageable.class),
              examples =
                  @ExampleObject(
                      name = "Search Pagination",
                      value = "page=0&size=10&sort=expenseDate,desc"))
          Pageable pageable);

  @Operation(
      summary = "Get expenses by category",
      description =
          "Retrieves paginated expenses for a specific category. Requires user authentication and returns only user's own expenses.")
  @SecurityRequirement(name = "bearerAuth")
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
            responseCode = "401",
            description = "Authentication required",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Access forbidden - user can only access own expenses",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(
            responseCode = "404",
            description = "Category not found",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
      })
  ResponseEntity<Page<ExpenseResponse>> getExpensesByCategory(
      @Parameter(
              description = ApiParameters.USER_ID_DESC,
              required = true,
              example = ApiParameters.USER_ID_EXAMPLE)
          Long userId,
      @Parameter(
              description = ApiParameters.CATEGORY_ID_DESC,
              required = true,
              example = ApiParameters.CATEGORY_ID_EXAMPLE)
          Long categoryId,
      @Parameter(
              description = "Pagination parameters for category expenses",
              schema = @Schema(implementation = Pageable.class),
              examples =
                  @ExampleObject(
                      name = "Category Pagination",
                      value = "page=0&size=15&sort=expenseDate,desc"))
          Pageable pageable);

  @Operation(
      summary = "Get expenses by date range",
      description =
          "Retrieves expenses within the specified date range. Requires user authentication and returns only user's own expenses.")
  @SecurityRequirement(name = "bearerAuth")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Date range expenses retrieved successfully",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = List.class))),
        @ApiResponse(
            responseCode = "401",
            description = "Authentication required",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Access forbidden - user can only access own expenses",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
      })
  ResponseEntity<List<ExpenseResponse>> getExpensesByDateRange(
      @Parameter(
              description = ApiParameters.USER_ID_DESC,
              required = true,
              example = ApiParameters.USER_ID_EXAMPLE)
          Long userId,
      @Parameter(
              description = "Start date for expense range query (inclusive)",
              required = true,
              example = "2024-01-01",
              schema = @Schema(type = "string", format = "date", pattern = "yyyy-MM-dd"))
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate startDate,
      @Parameter(
              description = "End date for expense range query (inclusive)",
              required = true,
              example = "2024-01-31",
              schema = @Schema(type = "string", format = "date", pattern = "yyyy-MM-dd"))
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate endDate);

  @Operation(
      summary = "Get recent expenses",
      description =
          "Retrieves the most recent expenses for the user. Requires user authentication and returns only user's own expenses.")
  @SecurityRequirement(name = "bearerAuth")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Recent expenses retrieved successfully",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = List.class))),
        @ApiResponse(
            responseCode = "401",
            description = "Authentication required",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Access forbidden - user can only access own expenses",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
      })
  ResponseEntity<List<ExpenseResponse>> getRecentExpenses(
      @Parameter(
              description = ApiParameters.USER_ID_DESC,
              required = true,
              example = ApiParameters.USER_ID_EXAMPLE)
          Long userId,
      @Parameter(
              description = "Maximum number of recent expenses to return",
              example = "10",
              schema =
                  @Schema(type = "integer", minimum = "1", maximum = "100", defaultValue = "10"))
          int limit);

  @Operation(
      summary = "Get expense count",
      description =
          "Returns the total number of expenses for the user. Requires user authentication and counts only user's own expenses.")
  @SecurityRequirement(name = "bearerAuth")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Expense count retrieved successfully",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Long.class),
                    examples = @ExampleObject(name = "Expense Count Response", value = "42"))),
        @ApiResponse(
            responseCode = "401",
            description = "Authentication required",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Access forbidden - user can only access own expense count",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
      })
  ResponseEntity<Long> getExpenseCount(
      @Parameter(
              description = ApiParameters.USER_ID_DESC,
              required = true,
              example = ApiParameters.USER_ID_EXAMPLE)
          Long userId);

  @Operation(
      summary = "Update expense",
      description =
          "Updates an existing expense record. Requires user authentication and user can only update own expenses.")
  @SecurityRequirement(name = "bearerAuth")
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
                    schema = @Schema(implementation = ValidationErrorResponse.class),
                    examples =
                        @ExampleObject(
                            name = "Validation Error",
                            value =
                                """
                            {
                              "message": "Validation failed. Please check your request data.",
                              "error": "Bad Request",
                              "status": 400,
                              "timestamp": "2024-01-15T14:45:00",
                              "path": "/v1/users/1/expenses/1",
                              "details": "Amount must be greater than 0"
                            }"""))),
        @ApiResponse(
            responseCode = "401",
            description = "Authentication required",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples =
                        @ExampleObject(
                            name = "Authentication Required",
                            value =
                                """
                            {
                              "message": "Authentication required. Please provide a valid JWT token.",
                              "error": "Unauthorized",
                              "status": 401,
                              "timestamp": "2024-01-15T14:45:00",
                              "path": "/v1/users/1/expenses/1"
                            }"""))),
        @ApiResponse(
            responseCode = "403",
            description = "Access forbidden - user can only update own expenses",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples =
                        @ExampleObject(
                            name = "Access Forbidden",
                            value =
                                """
                            {
                              "message": "Access forbidden. You can only update your own expenses.",
                              "error": "Forbidden",
                              "status": 403,
                              "timestamp": "2024-01-15T14:45:00",
                              "path": "/v1/users/1/expenses/1"
                            }"""))),
        @ApiResponse(
            responseCode = "404",
            description = "Expense or category not found",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples =
                        @ExampleObject(
                            name = "Resource Not Found",
                            value =
                                """
                            {
                              "message": "Expense not found with ID: 1",
                              "error": "Not Found",
                              "status": 404,
                              "timestamp": "2024-01-15T14:45:00",
                              "path": "/v1/users/1/expenses/1"
                            }"""))),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
      })
  ResponseEntity<ExpenseResponse> updateExpense(
      @Parameter(
              description = ApiParameters.USER_ID_DESC,
              required = true,
              example = ApiParameters.USER_ID_EXAMPLE)
          Long userId,
      @Parameter(
              description = ApiParameters.EXPENSE_ID_DESC,
              required = true,
              example = ApiParameters.EXPENSE_ID_EXAMPLE)
          Long expenseId,
      @Parameter(
              description =
                  "Updated expense information including amount, category, description, date, and optional metadata",
              required = true,
              schema = @Schema(implementation = ExpenseUpdateRequest.class),
              examples =
                  @ExampleObject(
                      name = "Expense Update Request",
                      value =
                          """
                      {
                        "categoryId": 1,
                        "amount": 30.00,
                        "currencyCode": "USD",
                        "description": "Updated lunch expense - premium restaurant",
                        "expenseDate": "2024-01-15",
                        "location": "Downtown Premium District",
                        "merchant": "Premium Cafe Express",
                        "paymentMethod": "CREDIT_CARD",
                        "receiptUrl": "https://example.com/receipts/updated-receipt-123.pdf",
                        "notes": "Updated: Business lunch with important client - upgraded venue",
                        "tags": ["business", "lunch", "client-meeting", "premium"]
                      }"""))
          @Valid
          ExpenseUpdateRequest request);

  @Operation(
      summary = "Delete expense",
      description =
          "Permanently deletes an expense record. Requires user authentication and user can only delete own expenses.")
  @SecurityRequirement(name = "bearerAuth")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "204", description = "Expense deleted successfully"),
        @ApiResponse(
            responseCode = "401",
            description = "Authentication required",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples =
                        @ExampleObject(
                            name = "Authentication Required",
                            value =
                                """
                            {
                              "message": "Authentication required. Please provide a valid JWT token.",
                              "error": "Unauthorized",
                              "status": 401,
                              "timestamp": "2024-01-15T14:45:00",
                              "path": "/v1/users/1/expenses/1"
                            }"""))),
        @ApiResponse(
            responseCode = "403",
            description = "Access forbidden - user can only delete own expenses",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples =
                        @ExampleObject(
                            name = "Access Forbidden",
                            value =
                                """
                            {
                              "message": "Access forbidden. You can only delete your own expenses.",
                              "error": "Forbidden",
                              "status": 403,
                              "timestamp": "2024-01-15T14:45:00",
                              "path": "/v1/users/1/expenses/1"
                            }"""))),
        @ApiResponse(
            responseCode = "404",
            description = "Expense not found with the specified ID",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples =
                        @ExampleObject(
                            name = "Expense Not Found",
                            value =
                                """
                            {
                              "message": "Expense not found with ID: 1",
                              "error": "Not Found",
                              "status": 404,
                              "timestamp": "2024-01-15T14:45:00",
                              "path": "/v1/users/1/expenses/1"
                            }"""))),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
      })
  ResponseEntity<Void> deleteExpense(
      @Parameter(
              description = ApiParameters.USER_ID_DESC,
              required = true,
              example = ApiParameters.USER_ID_EXAMPLE)
          Long userId,
      @Parameter(
              description = ApiParameters.EXPENSE_ID_DESC,
              required = true,
              example = ApiParameters.EXPENSE_ID_EXAMPLE)
          Long expenseId);
}
