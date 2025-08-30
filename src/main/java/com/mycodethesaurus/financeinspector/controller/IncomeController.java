package com.mycodethesaurus.financeinspector.controller;

import com.mycodethesaurus.financeinspector.dto.ErrorResponse;
import com.mycodethesaurus.financeinspector.dto.SalaryIncomeCreateRequest;
import com.mycodethesaurus.financeinspector.dto.SalaryIncomeDto;
import com.mycodethesaurus.financeinspector.dto.SalaryIncomeUpdateRequest;
import com.mycodethesaurus.financeinspector.dto.ValidationErrorResponse;
import com.mycodethesaurus.financeinspector.service.IncomeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1")
@Slf4j
@Tag(
    name = "Income Management",
    description =
        "APIs for managing salary income records, including comprehensive salary breakdown with allowances, deductions, and financial calculations")
public class IncomeController {

  private final IncomeService incomeService;

  public IncomeController(IncomeService incomeService) {
    this.incomeService = incomeService;
  }

  @GetMapping("/incomes")
  @Operation(
      summary = "Get all salary income records",
      description = "Retrieves a comprehensive list of all salary income records in the system")
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
                      ]""")))
      })
  public ResponseEntity<List<SalaryIncomeDto>> getAllIncome() {
    log.info("Received request to get all salary income records");
    return ResponseEntity.ok(incomeService.getAllSalaryIncome());
  }

  @GetMapping("/incomes/{userId}")
  @Operation(
      summary = "Get salary income records by user ID",
      description = "Retrieves all salary income records for a specific user")
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
                      ]""")))
      })
  public ResponseEntity<List<SalaryIncomeDto>> getAllIncomeByUserId(
      @Parameter(
              description = "User ID to retrieve salary records for",
              required = true,
              example = "1")
          @PathVariable
          long userId) {
    log.info("Received request to get salary income records for user id: {}", userId);
    return ResponseEntity.ok(incomeService.getAllSalaryIncomeForUser(userId));
  }

  @GetMapping("/incomes/salary/{incomeId}")
  @Operation(
      summary = "Get salary income record by ID",
      description = "Retrieves a specific salary income record by its unique identifier")
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
            responseCode = "404",
            description = "Salary income record not found with the specified ID",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
      })
  public ResponseEntity<SalaryIncomeDto> getSalaryIncomeById(
      @Parameter(description = "Salary income record ID", required = true, example = "1")
          @PathVariable
          Long incomeId) {
    log.info("Received request to get salary income record with id: {}", incomeId);
    SalaryIncomeDto response = incomeService.getSalaryIncomeById(incomeId);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/incomes/salary")
  @Operation(
      summary = "Create new salary income record",
      description =
          "Creates a new salary income record with comprehensive salary breakdown including basic amount, allowances, and deductions")
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
                    schema = @Schema(implementation = ValidationErrorResponse.class))),
        @ApiResponse(
            responseCode = "404",
            description = "User not found with the specified ID",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
      })
  public ResponseEntity<SalaryIncomeDto> createSalaryIncome(
      @Parameter(description = "Salary income creation details", required = true)
          @Valid
          @RequestBody
          SalaryIncomeCreateRequest request) {
    log.info(
        "Received request to create salary income record for user id: {}", request.getUserId());
    SalaryIncomeDto response = incomeService.createSalaryIncome(request);
    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }

  @PutMapping("/incomes/salary/{incomeId}")
  @Operation(
      summary = "Update salary income record",
      description =
          "Updates an existing salary income record with new salary breakdown information")
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
                    schema = @Schema(implementation = ValidationErrorResponse.class))),
        @ApiResponse(
            responseCode = "404",
            description = "Salary income record not found with the specified ID",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
      })
  public ResponseEntity<SalaryIncomeDto> updateSalaryIncome(
      @Parameter(description = "Salary income record ID", required = true, example = "1")
          @PathVariable
          Long incomeId,
      @Parameter(description = "Updated salary income information", required = true)
          @Valid
          @RequestBody
          SalaryIncomeUpdateRequest request) {
    log.info("Received request to update salary income record with id: {}", incomeId);
    SalaryIncomeDto response = incomeService.updateSalaryIncome(incomeId, request);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/incomes/salary/{incomeId}")
  @Operation(
      summary = "Delete salary income record",
      description = "Permanently deletes a salary income record from the system")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "204",
            description = "Salary income record deleted successfully"),
        @ApiResponse(
            responseCode = "404",
            description = "Salary income record not found with the specified ID",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
      })
  public ResponseEntity<Void> deleteSalaryIncome(
      @Parameter(description = "Salary income record ID", required = true, example = "1")
          @PathVariable
          Long incomeId) {
    log.info("Received request to delete salary income record with id: {}", incomeId);
    incomeService.deleteSalaryIncome(incomeId);
    return ResponseEntity.noContent().build();
  }
}
