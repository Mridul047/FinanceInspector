package com.mycodethesaurus.financeinspector.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycodethesaurus.financeinspector.dto.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/**
 * JWT Authentication Entry Point that handles unauthorized access attempts.
 *
 * <p>This component is triggered when an unauthenticated user tries to access a protected resource.
 * It provides a standardized JSON error response instead of the default Spring Security HTML error
 * page.
 *
 * <p>Key features:
 *
 * <ul>
 *   <li>Returns JSON error responses for API consistency
 *   <li>Provides detailed error information for debugging
 *   <li>Logs security violations for monitoring
 *   <li>Follows standard HTTP 401 Unauthorized response format
 * </ul>
 *
 * <p>Response format matches the application's standard ErrorResponse DTO:
 *
 * <pre>
 * {
 *   "message": "Authentication required",
 *   "error": "Unauthorized",
 *   "status": 401,
 *   "timestamp": "2024-01-15T10:30:00",
 *   "path": "/v1/protected-endpoint"
 * }
 * </pre>
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

  private final ObjectMapper objectMapper;

  /**
   * Handles authentication failures by returning a standardized JSON error response.
   *
   * <p>This method is called whenever an exception is thrown due to an unauthenticated user trying
   * to access a resource that requires authentication.
   *
   * @param request the HTTP request that resulted in authentication failure
   * @param response the HTTP response to send back to the client
   * @param authException the exception that was thrown during authentication
   * @throws IOException if an I/O error occurs while writing the response
   * @throws ServletException if a servlet error occurs
   */
  @Override
  public void commence(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException authException)
      throws IOException, ServletException {

    // Log the unauthorized access attempt
    String clientIp = getClientIpAddress(request);
    String userAgent = request.getHeader("User-Agent");
    String requestedUrl = request.getRequestURL().toString();

    log.warn(
        "Unauthorized access attempt - IP: {}, User-Agent: {}, URL: {}, Exception: {}",
        clientIp,
        userAgent,
        requestedUrl,
        authException.getMessage());

    // Create error response
    ErrorResponse errorResponse = createErrorResponse(request, authException);

    // Set response properties
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setCharacterEncoding("UTF-8");

    // Add security headers
    addSecurityHeaders(response);

    // Write JSON response
    try {
      String jsonResponse = objectMapper.writeValueAsString(errorResponse);
      response.getWriter().write(jsonResponse);
      response.getWriter().flush();

      log.debug("Sent 401 Unauthorized response for request: {}", requestedUrl);

    } catch (Exception e) {
      log.error("Error writing authentication failure response: {}", e.getMessage(), e);

      // Fallback to simple text response
      response.getWriter().write("{\"message\":\"Authentication required\",\"status\":401}");
      response.getWriter().flush();
    }
  }

  /**
   * Creates a standardized error response for authentication failures.
   *
   * @param request the HTTP request that failed authentication
   * @param authException the authentication exception that occurred
   * @return an ErrorResponse DTO with appropriate error details
   */
  private ErrorResponse createErrorResponse(
      HttpServletRequest request, AuthenticationException authException) {
    ErrorResponse errorResponse = new ErrorResponse();

    // Set standard error information
    errorResponse.setMessage("Authentication required. Please provide a valid JWT token.");
    errorResponse.setError("Unauthorized");
    errorResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    errorResponse.setTimestamp(LocalDateTime.now());
    errorResponse.setPath(request.getRequestURI());

    // Add additional context based on the authentication exception
    if (authException != null && authException.getMessage() != null) {
      String exceptionMessage = authException.getMessage();

      // Customize message based on exception type
      if (exceptionMessage.contains("JWT")) {
        errorResponse.setMessage("Invalid or expired JWT token. Please login again.");
      } else if (exceptionMessage.contains("expired")) {
        errorResponse.setMessage("Authentication token has expired. Please login again.");
      } else if (exceptionMessage.contains("malformed")) {
        errorResponse.setMessage("Malformed authentication token. Please provide a valid token.");
      } else if (exceptionMessage.contains("signature")) {
        errorResponse.setMessage("Invalid token signature. Please login again.");
      }
    }

    return errorResponse;
  }

  /**
   * Adds security headers to the response to prevent security vulnerabilities.
   *
   * @param response the HTTP response to add headers to
   */
  private void addSecurityHeaders(HttpServletResponse response) {
    // Prevent caching of authentication errors
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
    response.setHeader("Pragma", "no-cache");
    response.setHeader("Expires", "0");

    // Security headers
    response.setHeader("X-Content-Type-Options", "nosniff");
    response.setHeader("X-Frame-Options", "DENY");
    response.setHeader("X-XSS-Protection", "1; mode=block");

    // CORS headers for API responses (if needed)
    response.setHeader("Access-Control-Allow-Origin", "*");
    response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
    response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type");
  }

  /**
   * Extracts the client IP address from the request, considering proxy headers.
   *
   * @param request the HTTP request
   * @return the client IP address
   */
  private String getClientIpAddress(HttpServletRequest request) {
    // Check for IP address from various proxy headers
    String[] headerNames = {
      "X-Forwarded-For",
      "X-Real-IP",
      "Proxy-Client-IP",
      "WL-Proxy-Client-IP",
      "HTTP_X_FORWARDED_FOR",
      "HTTP_X_FORWARDED",
      "HTTP_X_CLUSTER_CLIENT_IP",
      "HTTP_CLIENT_IP",
      "HTTP_FORWARDED_FOR",
      "HTTP_FORWARDED",
      "HTTP_VIA",
      "REMOTE_ADDR"
    };

    for (String headerName : headerNames) {
      String ip = request.getHeader(headerName);
      if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
        // Handle comma-separated IPs (X-Forwarded-For can contain multiple IPs)
        if (ip.contains(",")) {
          ip = ip.split(",")[0].trim();
        }
        return ip;
      }
    }

    // Fallback to remote address
    return request.getRemoteAddr();
  }

  /**
   * Returns a string representation of this authentication entry point.
   *
   * @return a string description
   */
  @Override
  public String toString() {
    return "JwtAuthenticationEntryPoint{configured with Spring ObjectMapper}";
  }
}
