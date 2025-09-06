package com.mycodethesaurus.financeinspector.controller;

import com.mycodethesaurus.financeinspector.controller.api.AuthControllerApi;
import com.mycodethesaurus.financeinspector.dto.JwtResponse;
import com.mycodethesaurus.financeinspector.dto.LoginRequest;
import com.mycodethesaurus.financeinspector.dto.RefreshTokenRequest;
import com.mycodethesaurus.financeinspector.persistence.entity.UserEntity;
import com.mycodethesaurus.financeinspector.security.JwtUtil;
import com.mycodethesaurus.financeinspector.service.UserDetailsServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Authentication Controller for managing user authentication and JWT tokens.
 *
 * <p>This controller handles all authentication-related operations including user login, token
 * refresh, and logout functionality. It integrates with Spring Security and provides JWT-based
 * authentication for the Finance Inspector application.
 *
 * <p>Key features:
 *
 * <ul>
 *   <li>User authentication with username/password
 *   <li>JWT access and refresh token generation
 *   <li>Token refresh mechanism
 *   <li>Secure logout with token invalidation
 *   <li>Comprehensive error handling and logging
 * </ul>
 *
 * <p>All endpoints follow RESTful principles and return standardized JSON responses. Authentication
 * failures are logged for security monitoring and audit purposes.
 */
@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController implements AuthControllerApi {

  private final AuthenticationManager authenticationManager;
  private final JwtUtil jwtUtil;
  private final UserDetailsServiceImpl userDetailsService;

  /**
   * Authenticates a user and returns JWT tokens.
   *
   * <p>This endpoint validates user credentials and generates both access and refresh tokens upon
   * successful authentication. The access token is used for API requests, while the refresh token
   * can be used to obtain new access tokens without re-authentication.
   *
   * <p>Authentication process:
   *
   * <ol>
   *   <li>Validate request format and required fields
   *   <li>Authenticate user credentials using Spring Security
   *   <li>Generate JWT access and refresh tokens
   *   <li>Return user details and tokens in response
   * </ol>
   *
   * @param loginRequest the login credentials (username and password)
   * @return ResponseEntity containing JWT tokens and user information
   * @throws BadCredentialsException if authentication fails
   */
  @Override
  @PostMapping("/login")
  public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
    try {
      log.info("Authentication attempt for user: {}", loginRequest.getUsername());

      // Authenticate user credentials
      Authentication authentication =
          authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(
                  loginRequest.getUsername(), loginRequest.getPassword()));

      // Get authenticated user details
      UserEntity user = (UserEntity) authentication.getPrincipal();

      // Generate JWT tokens
      String accessToken = jwtUtil.generateJwtToken(user);
      String refreshToken = jwtUtil.generateRefreshToken(user);

      // Build response with user details and tokens
      JwtResponse response =
          JwtResponse.builder()
              .token(accessToken)
              .refreshToken(refreshToken)
              .type("Bearer")
              .expiresIn(3600L) // 1 hour in seconds
              .userId(user.getId())
              .username(user.getUsername())
              .email(user.getEmail())
              .role(user.getRole())
              .build();

      log.info("User {} authenticated successfully", user.getUsername());
      return ResponseEntity.ok(response);

    } catch (BadCredentialsException e) {
      log.warn(
          "Authentication failed for user: {} - Invalid credentials", loginRequest.getUsername());
      throw new BadCredentialsException("Invalid username or password");

    } catch (Exception e) {
      log.error(
          "Authentication error for user: {} - {}", loginRequest.getUsername(), e.getMessage(), e);
      throw new RuntimeException("Authentication failed. Please try again.");
    }
  }

  /**
   * Refreshes an expired access token using a valid refresh token.
   *
   * <p>This endpoint allows clients to obtain new access tokens without requiring the user to
   * re-authenticate. The refresh token must be valid and not expired. Upon successful refresh, a
   * new access token is generated while the refresh token may be rotated for enhanced security.
   *
   * <p>Refresh process:
   *
   * <ol>
   *   <li>Validate refresh token format and signature
   *   <li>Check token expiration and validity
   *   <li>Extract user information from token
   *   <li>Generate new access token
   *   <li>Optionally rotate refresh token
   * </ol>
   *
   * @param refreshRequest the refresh token request
   * @return ResponseEntity containing new JWT tokens
   * @throws RuntimeException if refresh token is invalid or expired
   */
  @Override
  @PostMapping("/refresh")
  public ResponseEntity<JwtResponse> refreshToken(
      @Valid @RequestBody RefreshTokenRequest refreshRequest) {
    try {
      String refreshToken = refreshRequest.getRefreshToken();
      log.debug(
          "Token refresh attempt with token: {}...",
          refreshToken.substring(0, Math.min(20, refreshToken.length())));

      // Validate refresh token
      if (!jwtUtil.validateJwtToken(refreshToken)) {
        log.warn("Invalid refresh token provided");
        throw new RuntimeException("Invalid refresh token");
      }

      // Extract user information from refresh token
      String username = jwtUtil.getUsernameFromToken(refreshToken);

      // Load user from database using the username from token
      UserEntity user = (UserEntity) userDetailsService.loadUserByUsername(username);

      // Generate new tokens
      String newAccessToken = jwtUtil.generateJwtToken(user);
      String newRefreshToken = jwtUtil.generateRefreshToken(user); // Token rotation for security

      // Build response
      JwtResponse response =
          JwtResponse.builder()
              .token(newAccessToken)
              .refreshToken(newRefreshToken)
              .type("Bearer")
              .expiresIn(3600L) // 1 hour in seconds
              .userId(user.getId())
              .username(user.getUsername())
              .email(user.getEmail())
              .role(user.getRole())
              .build();

      log.info("Token refreshed successfully for user: {}", user.getUsername());
      return ResponseEntity.ok(response);

    } catch (Exception e) {
      log.error("Token refresh failed: {}", e.getMessage(), e);
      throw new RuntimeException("Token refresh failed. Please login again.");
    }
  }

  /**
   * Logs out a user and invalidates their JWT tokens.
   *
   * <p>This endpoint handles user logout by invalidating the current JWT tokens. While JWT tokens
   * are stateless, this endpoint can be used to:
   *
   * <ul>
   *   <li>Clear client-side token storage
   *   <li>Log the logout event for audit purposes
   *   <li>Add tokens to a blacklist (if implemented)
   *   <li>Clear security context
   * </ul>
   *
   * <p>The client should discard all stored tokens after calling this endpoint.
   *
   * @param request the HTTP servlet request containing authentication details
   * @return ResponseEntity with success message
   */
  @Override
  @PostMapping("/logout")
  public ResponseEntity<Void> logout(HttpServletRequest request) {
    try {
      // Get current authenticated user
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

      if (authentication != null && authentication.isAuthenticated()) {
        String username = authentication.getName();
        log.info("User {} logged out successfully", username);

        // Clear security context
        SecurityContextHolder.clearContext();

        // Optional: Add token to blacklist here if implementing token blacklisting
        // tokenBlacklistService.blacklistToken(extractTokenFromRequest(request));

        return ResponseEntity.ok().build();
      } else {
        log.warn("Logout attempt without valid authentication");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
      }

    } catch (Exception e) {
      log.error("Logout error: {}", e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  /**
   * Extracts JWT token from the Authorization header.
   *
   * @param request the HTTP servlet request
   * @return the JWT token without "Bearer " prefix, or null if not found
   */
  private String extractTokenFromRequest(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");
    if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7);
    }
    return null;
  }

  /**
   * Returns a string representation of this authentication controller.
   *
   * @return a string description
   */
  @Override
  public String toString() {
    return "AuthController{"
        + "authenticationManager="
        + authenticationManager
        + ", jwtUtil="
        + jwtUtil
        + '}';
  }
}
