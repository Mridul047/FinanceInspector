package com.mycodethesaurus.financeinspector.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Standard error response DTO for API error handling.
 *
 * <p>This DTO provides a consistent error response format across all API endpoints. It includes
 * essential error information and optional details for debugging and user feedback.
 *
 * <p>Example usage:
 *
 * <pre>
 * {
 *   "message": "Authentication required",
 *   "error": "Unauthorized",
 *   "status": 401,
 *   "timestamp": "2024-01-15T10:30:00",
 *   "path": "/v1/categories",
 *   "details": "JWT token is missing or invalid"
 * }
 * </pre>
 *
 * <p>The response follows RFC 7807 Problem Details standard where applicable and provides
 * consistent error handling for the Finance Inspector API.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Standard error response for API operations")
public class ErrorResponse {

  /**
   * Human-readable error message describing what went wrong. This message is safe to display to end
   * users.
   */
  @Schema(
      description = "Human-readable error message",
      example = "Authentication required. Please provide a valid JWT token.",
      required = true)
  private String message;

  /**
   * Short error code or category describing the type of error. Typically matches HTTP status text
   * but can be more specific.
   */
  @Schema(description = "Error type or category", example = "Unauthorized", required = true)
  private String error;

  /** HTTP status code associated with this error. */
  @Schema(description = "HTTP status code", example = "401", required = true)
  private Integer status;

  /** Timestamp when the error occurred. Formatted as ISO-8601 datetime string. */
  @Schema(
      description = "Timestamp when the error occurred",
      example = "2024-01-15T10:30:00",
      required = true)
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime timestamp;

  /** API path that caused the error. Useful for debugging and error tracking. */
  @Schema(
      description = "API path where the error occurred",
      example = "/v1/categories",
      required = true)
  private String path;

  /**
   * Optional detailed error information for debugging. May contain technical details not suitable
   * for end users.
   */
  @Schema(
      description = "Additional error details for debugging",
      example = "JWT signature validation failed")
  private String details;

  /**
   * Optional trace ID for error tracking and correlation. Useful for distributed systems and
   * logging.
   */
  @Schema(description = "Trace ID for error correlation", example = "abc123-def456-ghi789")
  private String traceId;

  /**
   * Creates a basic error response with required fields.
   *
   * @param message the error message
   * @param error the error type
   * @param status the HTTP status code
   * @param path the API path
   * @return a new ErrorResponse instance
   */
  public static ErrorResponse of(String message, String error, Integer status, String path) {
    ErrorResponse response = new ErrorResponse();
    response.setMessage(message);
    response.setError(error);
    response.setStatus(status);
    response.setPath(path);
    response.setTimestamp(LocalDateTime.now());
    return response;
  }

  /**
   * Creates an error response with additional details.
   *
   * @param message the error message
   * @param error the error type
   * @param status the HTTP status code
   * @param path the API path
   * @param details additional error details
   * @return a new ErrorResponse instance
   */
  public static ErrorResponse of(
      String message, String error, Integer status, String path, String details) {
    ErrorResponse response = of(message, error, status, path);
    response.setDetails(details);
    return response;
  }

  /**
   * Creates an error response for authentication failures.
   *
   * @param path the API path where authentication failed
   * @return a new ErrorResponse for authentication errors
   */
  public static ErrorResponse unauthorized(String path) {
    return of(
        "Authentication required. Please provide a valid JWT token.", "Unauthorized", 401, path);
  }

  /**
   * Creates an error response for authorization failures.
   *
   * @param path the API path where authorization failed
   * @return a new ErrorResponse for authorization errors
   */
  public static ErrorResponse forbidden(String path) {
    return of(
        "Access denied. You don't have permission to access this resource.",
        "Forbidden",
        403,
        path);
  }

  /**
   * Creates an error response for resource not found.
   *
   * @param path the API path where resource was not found
   * @param resource the type of resource that was not found
   * @return a new ErrorResponse for not found errors
   */
  public static ErrorResponse notFound(String path, String resource) {
    return of(String.format("%s not found", resource), "Not Found", 404, path);
  }

  /**
   * Creates an error response for validation failures.
   *
   * @param path the API path where validation failed
   * @param details validation error details
   * @return a new ErrorResponse for validation errors
   */
  public static ErrorResponse validationError(String path, String details) {
    return of(
        "Validation failed. Please check your request data.", "Bad Request", 400, path, details);
  }

  /**
   * Creates an error response for internal server errors.
   *
   * @param path the API path where the error occurred
   * @return a new ErrorResponse for server errors
   */
  public static ErrorResponse internalServerError(String path) {
    return of(
        "An internal server error occurred. Please try again later.",
        "Internal Server Error",
        500,
        path);
  }

  /**
   * Creates an error response with trace ID for distributed tracing.
   *
   * @param message the error message
   * @param error the error type
   * @param status the HTTP status code
   * @param path the API path
   * @param traceId the trace ID
   * @return a new ErrorResponse with trace ID
   */
  public static ErrorResponse withTrace(
      String message, String error, Integer status, String path, String traceId) {
    ErrorResponse response = of(message, error, status, path);
    response.setTraceId(traceId);
    return response;
  }
}
