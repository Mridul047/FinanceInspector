package com.mycodethesaurus.financeinspector.security;

import com.mycodethesaurus.financeinspector.persistence.entity.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * Utility class for JWT token operations including generation, validation, and parsing.
 *
 * <p>This utility provides comprehensive JWT token management for the Finance Inspector
 * application:
 *
 * <ul>
 *   <li>Token generation with user-specific claims
 *   <li>Token validation and signature verification
 *   <li>Claims extraction and parsing
 *   <li>Token expiration management
 * </ul>
 *
 * <p>Security features:
 *
 * <ul>
 *   <li>HMAC-SHA512 signature algorithm for strong security
 *   <li>Configurable token expiration times
 *   <li>Unique JWT IDs for token tracking/blacklisting
 *   <li>Comprehensive error handling for different token failures
 * </ul>
 */
@Component
@Slf4j
public class JwtUtil {

  /**
   * Secret key for JWT signing. Should be at least 256 bits for HS256. In production, this should
   * be loaded from a secure configuration.
   */
  @Value("${app.jwt.secret}")
  private String jwtSecret;

  /** JWT token expiration time in milliseconds. Default is 24 hours (86400000 ms). */
  @Value("${app.jwt.expirationMs}")
  private int jwtExpirationMs;

  /** Refresh token expiration time in milliseconds. Default is 7 days (604800000 ms). */
  @Value("${app.jwt.refreshExpirationMs:604800000}")
  private int refreshExpirationMs;

  /**
   * Generates a JWT token for the given user.
   *
   * <p>The token includes the following claims:
   *
   * <ul>
   *   <li><strong>sub</strong> - Subject (username)
   *   <li><strong>userId</strong> - User's unique ID
   *   <li><strong>role</strong> - User's role authority
   *   <li><strong>email</strong> - User's email address
   *   <li><strong>iat</strong> - Issued at timestamp
   *   <li><strong>exp</strong> - Expiration timestamp
   *   <li><strong>jti</strong> - JWT ID (unique token identifier)
   * </ul>
   *
   * @param userEntity the user for whom to generate the token
   * @return the generated JWT token string
   * @throws IllegalArgumentException if userEntity is null
   */
  public String generateJwtToken(UserEntity userEntity) {
    if (userEntity == null) {
      throw new IllegalArgumentException("UserEntity cannot be null");
    }

    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + jwtExpirationMs);
    String tokenId = UUID.randomUUID().toString();

    SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    String token =
        Jwts.builder()
            .setSubject(userEntity.getUsername())
            .claim("userId", userEntity.getId())
            .claim("role", userEntity.getRole().getAuthority())
            .claim("email", userEntity.getEmail())
            .setId(tokenId)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(key, SignatureAlgorithm.HS512)
            .compact();

    log.debug("Generated JWT token for user: {} with ID: {}", userEntity.getUsername(), tokenId);
    return token;
  }

  /**
   * Generates a JWT token for the given UserDetails. This is used when the user is already
   * authenticated.
   *
   * @param userDetails the UserDetails for whom to generate the token
   * @return the generated JWT token string
   * @throws IllegalArgumentException if userDetails is null or not a UserEntity
   */
  public String generateJwtToken(UserDetails userDetails) {
    if (userDetails == null) {
      throw new IllegalArgumentException("UserDetails cannot be null");
    }

    if (!(userDetails instanceof UserEntity)) {
      throw new IllegalArgumentException("UserDetails must be an instance of UserEntity");
    }

    return generateJwtToken((UserEntity) userDetails);
  }

  /**
   * Generates a refresh token with extended expiration time.
   *
   * @param userEntity the user for whom to generate the refresh token
   * @return the generated refresh token string
   */
  public String generateRefreshToken(UserEntity userEntity) {
    if (userEntity == null) {
      throw new IllegalArgumentException("UserEntity cannot be null");
    }

    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + refreshExpirationMs);
    String tokenId = UUID.randomUUID().toString();

    SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    String token =
        Jwts.builder()
            .setSubject(userEntity.getUsername())
            .claim("userId", userEntity.getId())
            .claim("type", "refresh")
            .setId(tokenId)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(key, SignatureAlgorithm.HS512)
            .compact();

    log.debug(
        "Generated refresh token for user: {} with ID: {}", userEntity.getUsername(), tokenId);
    return token;
  }

  /**
   * Extracts the username from a JWT token.
   *
   * @param token the JWT token
   * @return the username (subject) from the token
   * @throws JwtAuthenticationException if the token is invalid
   */
  public String getUsernameFromToken(String token) {
    try {
      Claims claims = getClaimsFromToken(token);
      return claims.getSubject();
    } catch (Exception e) {
      log.error("Error extracting username from token: {}", e.getMessage());
      throw new JwtAuthenticationException("Invalid token", e);
    }
  }

  /**
   * Extracts the user ID from a JWT token.
   *
   * @param token the JWT token
   * @return the user ID from the token
   * @throws JwtAuthenticationException if the token is invalid
   */
  public Long getUserIdFromToken(String token) {
    try {
      Claims claims = getClaimsFromToken(token);
      return claims.get("userId", Long.class);
    } catch (Exception e) {
      log.error("Error extracting user ID from token: {}", e.getMessage());
      throw new JwtAuthenticationException("Invalid token", e);
    }
  }

  /**
   * Extracts the user role from a JWT token.
   *
   * @param token the JWT token
   * @return the user role authority from the token
   * @throws JwtAuthenticationException if the token is invalid
   */
  public String getRoleFromToken(String token) {
    try {
      Claims claims = getClaimsFromToken(token);
      return claims.get("role", String.class);
    } catch (Exception e) {
      log.error("Error extracting role from token: {}", e.getMessage());
      throw new JwtAuthenticationException("Invalid token", e);
    }
  }

  /**
   * Extracts the JWT ID from a JWT token. This can be used for token blacklisting.
   *
   * @param token the JWT token
   * @return the JWT ID from the token
   * @throws JwtAuthenticationException if the token is invalid
   */
  public String getTokenIdFromToken(String token) {
    try {
      Claims claims = getClaimsFromToken(token);
      return claims.getId();
    } catch (Exception e) {
      log.error("Error extracting token ID from token: {}", e.getMessage());
      throw new JwtAuthenticationException("Invalid token", e);
    }
  }

  /**
   * Extracts the expiration date from a JWT token.
   *
   * @param token the JWT token
   * @return the expiration date from the token
   * @throws JwtAuthenticationException if the token is invalid
   */
  public Date getExpirationDateFromToken(String token) {
    try {
      Claims claims = getClaimsFromToken(token);
      return claims.getExpiration();
    } catch (Exception e) {
      log.error("Error extracting expiration date from token: {}", e.getMessage());
      throw new JwtAuthenticationException("Invalid token", e);
    }
  }

  /**
   * Extracts all claims from a JWT token.
   *
   * @param token the JWT token
   * @return the claims from the token
   * @throws JwtAuthenticationException if the token is invalid
   */
  public Claims getClaimsFromToken(String token) {
    try {
      SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
      return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    } catch (Exception e) {
      log.error("Error parsing token claims: {}", e.getMessage());
      throw new JwtAuthenticationException("Invalid token", e);
    }
  }

  /**
   * Validates a JWT token for authenticity and expiration.
   *
   * @param token the JWT token to validate
   * @return true if the token is valid, false otherwise
   */
  public boolean validateJwtToken(String token) {
    try {
      SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
      Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
      log.debug("JWT token validation successful");
      return true;
    } catch (SignatureException e) {
      log.error("Invalid JWT signature: {}", e.getMessage());
    } catch (MalformedJwtException e) {
      log.error("Invalid JWT token: {}", e.getMessage());
    } catch (ExpiredJwtException e) {
      log.error("JWT token is expired: {}", e.getMessage());
    } catch (UnsupportedJwtException e) {
      log.error("JWT token is unsupported: {}", e.getMessage());
    } catch (IllegalArgumentException e) {
      log.error("JWT claims string is empty: {}", e.getMessage());
    } catch (Exception e) {
      log.error("JWT token validation failed: {}", e.getMessage());
    }
    return false;
  }

  /**
   * Checks if a JWT token is expired.
   *
   * @param token the JWT token to check
   * @return true if the token is expired, false otherwise
   */
  public boolean isTokenExpired(String token) {
    try {
      Date expiration = getExpirationDateFromToken(token);
      return expiration.before(new Date());
    } catch (Exception e) {
      log.error("Error checking token expiration: {}", e.getMessage());
      return true; // Assume expired if we can't parse
    }
  }

  /**
   * Validates a JWT token against a specific username.
   *
   * @param token the JWT token to validate
   * @param username the username to validate against
   * @return true if the token is valid for the given username, false otherwise
   */
  public boolean validateTokenForUser(String token, String username) {
    try {
      String tokenUsername = getUsernameFromToken(token);
      return username.equals(tokenUsername) && validateJwtToken(token);
    } catch (Exception e) {
      log.error("Error validating token for user {}: {}", username, e.getMessage());
      return false;
    }
  }

  /**
   * Gets the remaining time until token expiration in milliseconds.
   *
   * @param token the JWT token
   * @return the remaining time in milliseconds, or 0 if expired/invalid
   */
  public long getRemainingExpirationTime(String token) {
    try {
      Date expiration = getExpirationDateFromToken(token);
      long remainingTime = expiration.getTime() - System.currentTimeMillis();
      return Math.max(0, remainingTime);
    } catch (Exception e) {
      log.error("Error calculating remaining expiration time: {}", e.getMessage());
      return 0;
    }
  }

  /** Custom exception for JWT authentication errors. */
  public static class JwtAuthenticationException extends RuntimeException {
    public JwtAuthenticationException(String message) {
      super(message);
    }

    public JwtAuthenticationException(String message, Throwable cause) {
      super(message, cause);
    }
  }
}
