package com.mycodethesaurus.financeinspector.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for refresh token requests.
 *
 * <p>This DTO contains the refresh token required to obtain a new access token:
 *
 * <ul>
 *   <li><strong>refreshToken</strong> - The refresh token previously issued during login
 * </ul>
 *
 * <p>Security considerations:
 *
 * <ul>
 *   <li>Refresh tokens have longer expiration times than access tokens
 *   <li>Should be validated and checked against blacklist
 *   <li>Used to maintain user sessions without requiring re-authentication
 * </ul>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(
    name = "RefreshTokenRequest",
    description = "Refresh token request for obtaining new access token")
public class RefreshTokenRequest {

  /** The refresh token to exchange for a new access token. */
  @Schema(
      description = "Refresh token for obtaining new access token",
      example =
          "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJqb2huZG9lIiwidHlwZSI6InJlZnJlc2giLCJpYXQiOjE2NDA5OTUyMDAsImV4cCI6MTY0MTYwMDAwMH0.signature",
      requiredMode = Schema.RequiredMode.REQUIRED)
  @NotBlank(message = "Refresh token is required")
  private String refreshToken;
}
