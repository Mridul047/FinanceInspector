package com.mycodethesaurus.financeinspector.security;

import com.mycodethesaurus.financeinspector.security.JwtUtil.JwtAuthenticationException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * JWT Authentication Filter that validates JWT tokens on incoming requests.
 *
 * <p>This filter is executed once per request and performs the following operations:
 *
 * <ul>
 *   <li>Extracts JWT token from the Authorization header
 *   <li>Validates the token signature and expiration
 *   <li>Loads user details from the database
 *   <li>Sets up Spring Security authentication context
 * </ul>
 *
 * <p>Security features:
 *
 * <ul>
 *   <li>Validates Bearer token format
 *   <li>Comprehensive token validation including signature and expiration
 *   <li>Automatic security context setup for authenticated users
 *   <li>Graceful error handling for invalid tokens
 * </ul>
 *
 * <p>The filter processes requests in the following order:
 *
 * <ol>
 *   <li>Skip if authentication already exists in security context
 *   <li>Extract JWT token from Authorization header
 *   <li>Validate token format and signature
 *   <li>Extract username from token
 *   <li>Load user details from UserDetailsService
 *   <li>Validate token against user and set authentication context
 * </ol>
 */
@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  @Autowired private JwtUtil jwtUtil;

  @Autowired private UserDetailsService userDetailsService;

  /**
   * Performs JWT authentication for each incoming request.
   *
   * @param request the HTTP request
   * @param response the HTTP response
   * @param filterChain the filter chain to continue processing
   * @throws ServletException if servlet processing fails
   * @throws IOException if I/O processing fails
   */
  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    try {
      // Skip if already authenticated
      if (SecurityContextHolder.getContext().getAuthentication() != null) {
        log.debug("Authentication already exists in security context, skipping JWT validation");
        filterChain.doFilter(request, response);
        return;
      }

      // Extract JWT token from request
      String jwt = getJwtFromRequest(request);

      if (jwt != null && StringUtils.hasText(jwt)) {
        log.debug(
            "Processing JWT token for request: {} {}",
            request.getMethod(),
            request.getRequestURI());

        // Validate token format and signature
        if (jwtUtil.validateJwtToken(jwt)) {

          // Extract username from token
          String username = jwtUtil.getUsernameFromToken(jwt);
          log.debug("Extracted username from JWT: {}", username);

          // Load user details
          UserDetails userDetails = userDetailsService.loadUserByUsername(username);

          // Double-check token validity against user
          if (jwtUtil.validateTokenForUser(jwt, username)) {

            // Create authentication token
            UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());

            // Set authentication details
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // Set authentication in security context
            SecurityContextHolder.getContext().setAuthentication(authentication);

            log.debug(
                "Successfully authenticated user: {} with roles: {}",
                username,
                userDetails.getAuthorities());

          } else {
            log.warn("Token validation failed for user: {}", username);
          }
        } else {
          log.warn("Invalid JWT token format or signature");
        }
      } else {
        log.debug("No JWT token found in request headers");
      }

    } catch (JwtAuthenticationException e) {
      log.error("JWT authentication error: {}", e.getMessage());
      // Continue filter chain - let Spring Security handle the unauthorized request
    } catch (Exception e) {
      log.error("Cannot set user authentication in security context: {}", e.getMessage(), e);
      // Continue filter chain - let Spring Security handle the error
    }

    // Continue with the filter chain
    filterChain.doFilter(request, response);
  }

  /**
   * Extracts JWT token from the Authorization header.
   *
   * <p>Expected header format: "Authorization: Bearer {token}"
   *
   * @param request the HTTP request
   * @return the JWT token without the "Bearer " prefix, or null if not found
   */
  private String getJwtFromRequest(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");

    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
      String token = bearerToken.substring(7); // Remove "Bearer " prefix
      log.debug("Extracted JWT token from Authorization header");
      return token;
    }

    return null;
  }

  /**
   * Determines if this filter should be applied to the given request.
   *
   * <p>This filter is applied to all requests except:
   *
   * <ul>
   *   <li>Authentication endpoints (/v1/auth/**)
   *   <li>Public endpoints that don't require authentication
   *   <li>Static resources
   * </ul>
   *
   * @param request the HTTP request
   * @return false if the filter should be skipped, true otherwise
   */
  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    String path = request.getRequestURI();

    // Skip authentication endpoints
    if (path.startsWith("/v1/auth/")) {
      log.debug("Skipping JWT filter for authentication endpoint: {}", path);
      return true;
    }

    // Skip API documentation endpoints
    if (path.startsWith("/api-docs/")
        || path.startsWith("/swagger-ui/")
        || path.equals("/swagger-ui.html")
        || path.startsWith("/swagger-resources/")
        || path.startsWith("/webjars/")) {
      log.debug("Skipping JWT filter for documentation endpoint: {}", path);
      return true;
    }

    // Skip actuator endpoints (if enabled)
    if (path.startsWith("/actuator/")) {
      log.debug("Skipping JWT filter for actuator endpoint: {}", path);
      return true;
    }

    // Apply filter to all other requests
    return false;
  }

  /**
   * Returns a description of this filter for logging purposes.
   *
   * @return a string description of this filter
   */
  @Override
  public String toString() {
    return "JwtAuthenticationFilter{"
        + "jwtUtil="
        + jwtUtil
        + ", userDetailsService="
        + userDetailsService
        + '}';
  }
}
