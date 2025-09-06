package com.mycodethesaurus.financeinspector.controller.api;

import com.mycodethesaurus.financeinspector.dto.ErrorResponse;
import com.mycodethesaurus.financeinspector.dto.SalaryIncomeCreateRequest;
import com.mycodethesaurus.financeinspector.dto.SalaryIncomeDto;
import com.mycodethesaurus.financeinspector.dto.SalaryIncomeUpdateRequest;
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
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * API interface for Income Management operations.
 *
 * <p>This interface defines the contract for salary income management operations including
 * comprehensive salary breakdown with allowances, deductions, and financial calculations.
 */
@Tag(
    name = "Income Management",
    description =
        "APIs for managing salary income records, including comprehensive salary breakdown with allowances, deductions, and financial calculations")
public interface IncomeControllerApi {

  @Operation(
      summary = "Get all salary income records",
      description =
          "Retrieves a comprehensive list of all salary income records in the system. Requires admin authentication.")
  @SecurityRequirement(name = "bearerAuth")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Salary income records retrieved successfully",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = SalaryIncomeDto.class),
                    examples =
                        @ExampleObject(
                            name = "Salary Income List Response",
                            value =
                                """
                      [
                        {
                          "id": 1,
                          "userId": 1,
                          "currencyCode": "USD",
                          "basicAmount": 5000.0000,
                          "hraAmount": 1000.0000,
                          "otherAllowanceAmount": 500.0000,
                          "bonusAmount": 2000.0000,
                          "empPfAmount": 600.0000,
                          "professionTaxAmount": 200.0000,
                          "incomeTaxAmount": 1500.0000,
                          "createdOn": "2024-01-15T10:30:00",
                          "updatedOn": "2024-01-15T10:30:00"
                        }
                      ]"""))),
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
                             "timestamp": "2024-01-15T10:30:00",
                             "path": "/v1/income"
                           }"""))),
        @ApiResponse(
            responseCode = "403",
            description = "Admin access required",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples =
                        @ExampleObject(
                            name = "Admin Access Required",
                            value =
                                """
                           {
                             "message": "Admin access required to view all income records",
                             "error": "Forbidden",
                             "status": 403,
                             "timestamp": "2024-01-15T10:30:00",
                             "path": "/v1/income"
                           }"""))),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
      })
  ResponseEntity<List<SalaryIncomeDto>> getAllIncome();

  @Operation(
      summary = "Get salary income records by user ID",
      description =
          "Retrieves all salary income records for a specific user. Requires user authentication and access to own records.")
  @SecurityRequirement(name = "bearerAuth")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "User's salary income records retrieved successfully",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = SalaryIncomeDto.class),
                    examples =
                        @ExampleObject(
                            name = "User Salary Income Records",
                            value =
                                """
                      [
                        {
                          "id": 1,
                          "userId": 1,
                          "currencyCode": "USD",
                          "basicAmount": 5000.0000,
                          "hraAmount": 1000.0000,
                          "otherAllowanceAmount": 500.0000,
                          "bonusAmount": 2000.0000,
                          "empPfAmount": 600.0000,
                          "professionTaxAmount": 200.0000,
                          "incomeTaxAmount": 1500.0000,
                          "createdOn": "2024-01-15T10:30:00",
                          "updatedOn": "2024-01-15T10:30:00"
                        }
                      ]"""))),
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
                             "timestamp": "2024-01-15T10:30:00",
                             "path": "/v1/income/user/1"
                           }"""))),
        @ApiResponse(
            responseCode = "403",
            description = "Access forbidden - user can only access own income records",
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
                             "message": "Access forbidden. You can only access your own income records.",
                             "error": "Forbidden",
                             "status": 403,
                             "timestamp": "2024-01-15T10:30:00",
                             "path": "/v1/income/user/1"
                           }"""))),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
      })
  ResponseEntity<List<SalaryIncomeDto>> getAllIncomeByUserId(
      @Parameter(
              description = "Unique identifier of the user to retrieve salary records for",
              required = true,
              example = "1",
              schema = @Schema(type = "integer", format = "int64", minimum = "1"))
          @PathVariable
          long userId);

  @Operation(
      summary = "Get salary income record by ID",
      description =
          "Retrieves a specific salary income record by its unique identifier. Requires user authentication and access to own records.")
  @SecurityRequirement(name = "bearerAuth")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Salary income record found and returned successfully",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = SalaryIncomeDto.class),
                    examples =
                        @ExampleObject(
                            name = "Salary Income Details Response",
                            value =
                                """
                      {
                        "id": 1,
                        "userId": 1,
                        "currencyCode": "USD",
                        "basicAmount": 5000.0000,
                        "hraAmount": 1000.0000,
                        "otherAllowanceAmount": 500.0000,
                        "bonusAmount": 2000.0000,
                        "empPfAmount": 600.0000,
                        "professionTaxAmount": 200.0000,
                        "incomeTaxAmount": 1500.0000,
                        "createdOn": "2024-01-15T10:30:00",
                        "updatedOn": "2024-01-15T10:30:00"
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
                             "timestamp": "2024-01-15T10:30:00",
                             "path": "/v1/income/1"
                           }"""))),
        @ApiResponse(
            responseCode = "403",
            description = "Access forbidden - user can only access own income records",
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
                             "message": "Access forbidden. You can only access your own income records.",
                             "error": "Forbidden",
                             "status": 403,
                             "timestamp": "2024-01-15T10:30:00",
                             "path": "/v1/income/1"
                           }"""))),
        @ApiResponse(
            responseCode = "404",
            description = "Salary income record not found with the specified ID",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples =
                        @ExampleObject(
                            name = "Income Record Not Found",
                            value =
                                """
                           {
                             "message": "Salary income record not found with ID: 1",
                             "error": "Not Found",
                             "status": 404,
                             "timestamp": "2024-01-15T10:30:00",
                             "path": "/v1/income/1"
                           }"""))),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
      })
  ResponseEntity<SalaryIncomeDto> getSalaryIncomeById(
      @Parameter(
              description = "Unique identifier of the salary income record to retrieve",
              required = true,
              example = "1",
              schema = @Schema(type = "integer", format = "int64", minimum = "1"))
          @PathVariable
          Long incomeId);

  @Operation(
      summary = "Create new salary income record",
      description =
          "Creates a new salary income record with comprehensive salary breakdown including basic amount, allowances, and deductions. Requires user authentication.")
  @SecurityRequirement(name = "bearerAuth")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "Salary income record created successfully",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = SalaryIncomeDto.class),
                    examples =
                        @ExampleObject(
                            name = "Created Salary Income Response",
                            value =
                                """
                      {
                        "id": 1,
                        "userId": 1,
                        "currencyCode": "USD",
                        "basicAmount": 5000.0000,
                        "hraAmount": 1000.0000,
                        "otherAllowanceAmount": 500.0000,
                        "bonusAmount": 2000.0000,
                        "empPfAmount": 600.0000,
                        "professionTaxAmount": 200.0000,
                        "incomeTaxAmount": 1500.0000,
                        "createdOn": "2024-01-15T10:30:00",
                        "updatedOn": "2024-01-15T10:30:00"
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
                             "timestamp": "2024-01-15T10:30:00",
                             "path": "/v1/income",
                             "details": "Basic amount must be greater than 0"
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
                             "timestamp": "2024-01-15T10:30:00",
                             "path": "/v1/income"
                           }"""))),
        @ApiResponse(
            responseCode = "403",
            description = "Access forbidden - user can only create income records for own account",
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
                             "message": "Access forbidden. You can only create income records for your own account.",
                             "error": "Forbidden",
                             "status": 403,
                             "timestamp": "2024-01-15T10:30:00",
                             "path": "/v1/income"
                           }"""))),
        @ApiResponse(
            responseCode = "404",
            description = "User not found with the specified ID",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples =
                        @ExampleObject(
                            name = "User Not Found",
                            value =
                                """
                           {
                             "message": "User not found with ID: 1",
                             "error": "Not Found",
                             "status": 404,
                             "timestamp": "2024-01-15T10:30:00",
                             "path": "/v1/income"
                           }"""))),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
      })
  ResponseEntity<SalaryIncomeDto> createSalaryIncome(
      @Parameter(
              description =
                  "Salary income creation details including user ID, currency, basic amount, allowances, and deductions",
              required = true,
              schema = @Schema(implementation = SalaryIncomeCreateRequest.class),
              examples =
                  @ExampleObject(
                      name = "Salary Income Creation Request",
                      value =
                          """
                      {
                        "userId": 1,
                        "currencyCode": "USD",
                        "basicAmount": 5000.0000,
                        "hraAmount": 1000.0000,
                        "otherAllowanceAmount": 500.0000,
                        "bonusAmount": 2000.0000,
                        "empPfAmount": 600.0000,
                        "professionTaxAmount": 200.0000,
                        "incomeTaxAmount": 1500.0000
                      }"""))
          @Valid
          @RequestBody
          SalaryIncomeCreateRequest request);

  @Operation(
      summary = "Update salary income record",
      description =
          "Updates an existing salary income record with new salary breakdown information. Requires user authentication and access to own records.")
  @SecurityRequirement(name = "bearerAuth")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Salary income record updated successfully",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = SalaryIncomeDto.class),
                    examples =
                        @ExampleObject(
                            name = "Updated Salary Income Response",
                            value =
                                """
                      {
                        "id": 1,
                        "userId": 1,
                        "currencyCode": "USD",
                        "basicAmount": 5500.0000,
                        "hraAmount": 1100.0000,
                        "otherAllowanceAmount": 600.0000,
                        "bonusAmount": 2500.0000,
                        "empPfAmount": 660.0000,
                        "professionTaxAmount": 200.0000,
                        "incomeTaxAmount": 1650.0000,
                        "createdOn": "2024-01-15T10:30:00",
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
                             "path": "/v1/income/1",
                             "details": "HRA amount cannot be negative"
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
                             "path": "/v1/income/1"
                           }"""))),
        @ApiResponse(
            responseCode = "403",
            description = "Access forbidden - user can only update own income records",
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
                             "message": "Access forbidden. You can only update your own income records.",
                             "error": "Forbidden",
                             "status": 403,
                             "timestamp": "2024-01-15T14:45:00",
                             "path": "/v1/income/1"
                           }"""))),
        @ApiResponse(
            responseCode = "404",
            description = "Salary income record not found with the specified ID",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples =
                        @ExampleObject(
                            name = "Income Record Not Found",
                            value =
                                """
                           {
                             "message": "Salary income record not found with ID: 1",
                             "error": "Not Found",
                             "status": 404,
                             "timestamp": "2024-01-15T14:45:00",
                             "path": "/v1/income/1"
                           }"""))),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
      })
  ResponseEntity<SalaryIncomeDto> updateSalaryIncome(
      @Parameter(
              description = "Unique identifier of the salary income record to update",
              required = true,
              example = "1",
              schema = @Schema(type = "integer", format = "int64", minimum = "1"))
          @PathVariable
          Long incomeId,
      @Parameter(
              description =
                  "Updated salary income information including currency, amounts, allowances, and deductions",
              required = true,
              schema = @Schema(implementation = SalaryIncomeUpdateRequest.class),
              examples =
                  @ExampleObject(
                      name = "Salary Income Update Request",
                      value =
                          """
                      {
                        "currencyCode": "USD",
                        "basicAmount": 5500.0000,
                        "hraAmount": 1100.0000,
                        "otherAllowanceAmount": 600.0000,
                        "bonusAmount": 2500.0000,
                        "empPfAmount": 660.0000,
                        "professionTaxAmount": 200.0000,
                        "incomeTaxAmount": 1650.0000
                      }"""))
          @Valid
          @RequestBody
          SalaryIncomeUpdateRequest request);

  @Operation(
      summary = "Delete salary income record",
      description =
          "Permanently deletes a salary income record from the system. Requires user authentication and access to own records.")
  @SecurityRequirement(name = "bearerAuth")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "204",
            description = "Salary income record deleted successfully"),
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
                              "path": "/v1/income/1"
                            }"""))),
        @ApiResponse(
            responseCode = "403",
            description = "Access forbidden - user can only delete own income records",
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
                              "message": "Access forbidden. You can only delete your own income records.",
                              "error": "Forbidden",
                              "status": 403,
                              "timestamp": "2024-01-15T14:45:00",
                              "path": "/v1/income/1"
                            }"""))),
        @ApiResponse(
            responseCode = "404",
            description = "Salary income record not found with the specified ID",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples =
                        @ExampleObject(
                            name = "Income Record Not Found",
                            value =
                                """
                            {
                              "message": "Salary income record not found with ID: 1",
                              "error": "Not Found",
                              "status": 404,
                              "timestamp": "2024-01-15T14:45:00",
                              "path": "/v1/income/1"
                            }"""))),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
      })
  ResponseEntity<Void> deleteSalaryIncome(
      @Parameter(
              description = "Unique identifier of the salary income record to permanently delete",
              required = true,
              example = "1",
              schema = @Schema(type = "integer", format = "int64", minimum = "1"))
          @PathVariable
          Long incomeId);
}
