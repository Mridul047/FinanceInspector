package com.mycodethesaurus.financeinspector.service;

import com.mycodethesaurus.financeinspector.persistence.entity.UserEntity;
import com.mycodethesaurus.financeinspector.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of Spring Security's UserDetailsService for loading user-specific data.
 *
 * <p>This service is responsible for retrieving user authentication and authorization information
 * from the database during the authentication process. It integrates with Spring Security's
 * authentication framework and provides the bridge between the application's user entity and Spring
 * Security's UserDetails interface.
 *
 * <p>Key features:
 *
 * <ul>
 *   <li>Loads user details by username for authentication
 *   <li>Supports both username and email-based authentication
 *   <li>Provides comprehensive error handling and logging
 *   <li>Transactional support for database operations
 *   <li>Integration with custom UserEntity that implements UserDetails
 * </ul>
 *
 * <p>Security considerations:
 *
 * <ul>
 *   <li>Logs authentication attempts for security monitoring
 *   <li>Provides detailed error messages for debugging while maintaining security
 *   <li>Handles account status checks (enabled, locked, expired, etc.)
 * </ul>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

  private final UserRepository userRepository;

  /**
   * Loads user details by username for Spring Security authentication.
   *
   * <p>This method is called by Spring Security during the authentication process. It retrieves the
   * user from the database and returns the UserEntity which implements UserDetails interface.
   *
   * <p>Authentication Process:
   *
   * <ol>
   *   <li>Search for user by username in the database
   *   <li>If not found by username, attempt search by email
   *   <li>Validate user account status (enabled, non-locked, etc.)
   *   <li>Return UserDetails for Spring Security authentication
   * </ol>
   *
   * <p>The method supports authentication with either username or email address, providing
   * flexibility for users while maintaining security.
   *
   * @param username the username (or email) identifying the user whose data is required
   * @return a fully populated UserDetails record (never null)
   * @throws UsernameNotFoundException if the user could not be found or has no GrantedAuthority
   */
  @Override
  @Transactional(readOnly = true)
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    log.debug("Attempting to load user by username: {}", username);

    // Validate input
    if (username == null || username.trim().isEmpty()) {
      log.warn("Authentication attempt with null or empty username");
      throw new UsernameNotFoundException("Username cannot be null or empty");
    }

    String trimmedUsername = username.trim();
    UserEntity user = null;

    try {
      // First attempt: search by username
      user = userRepository.findByUserName(trimmedUsername).orElse(null);

      // Second attempt: if not found by username, try email
      if (user == null) {
        log.debug("User not found by username, attempting to find by email: {}", trimmedUsername);
        user = userRepository.findByEmail(trimmedUsername).orElse(null);
      }

      // If still not found, throw exception
      if (user == null) {
        log.warn("User not found: {}", trimmedUsername);
        throw new UsernameNotFoundException("User not found: " + trimmedUsername);
      }

      // Additional account status validations
      validateUserAccount(user);

      log.info(
          "Successfully loaded user: {} (ID: {}, Role: {})",
          user.getUsername(),
          user.getId(),
          user.getRole());

      return user;

    } catch (UsernameNotFoundException e) {
      // Re-throw UsernameNotFoundException as-is
      throw e;

    } catch (Exception e) {
      log.error("Error loading user by username '{}': {}", trimmedUsername, e.getMessage(), e);
      throw new UsernameNotFoundException("Error loading user: " + trimmedUsername, e);
    }
  }

  /**
   * Validates the user account status and throws appropriate exceptions if the account is not in a
   * valid state for authentication.
   *
   * <p>This method performs additional validation beyond what Spring Security automatically checks
   * through the UserDetails interface methods. It provides more detailed logging and specific error
   * messages for different account states.
   *
   * @param user the user entity to validate
   * @throws UsernameNotFoundException if the account is in an invalid state
   */
  private void validateUserAccount(UserEntity user) throws UsernameNotFoundException {
    String username = user.getUsername();

    // Check if account is enabled
    if (!user.isEnabled()) {
      log.warn("Authentication attempt for disabled account: {}", username);
      throw new UsernameNotFoundException("Account is disabled: " + username);
    }

    // Check if account is not expired
    if (!user.isAccountNonExpired()) {
      log.warn("Authentication attempt for expired account: {}", username);
      throw new UsernameNotFoundException("Account has expired: " + username);
    }

    // Check if account is not locked
    if (!user.isAccountNonLocked()) {
      log.warn("Authentication attempt for locked account: {}", username);
      throw new UsernameNotFoundException("Account is locked: " + username);
    }

    // Check if credentials are not expired
    if (!user.isCredentialsNonExpired()) {
      log.warn("Authentication attempt for account with expired credentials: {}", username);
      throw new UsernameNotFoundException("Credentials have expired for account: " + username);
    }

    // Check if user has authorities
    if (user.getAuthorities() == null || user.getAuthorities().isEmpty()) {
      log.warn("Authentication attempt for account with no authorities: {}", username);
      throw new UsernameNotFoundException("Account has no assigned authorities: " + username);
    }

    log.debug("Account validation successful for user: {}", username);
  }

  /**
   * Loads user details by user ID.
   *
   * <p>This is a convenience method for loading users by their unique ID, which can be useful for
   * token refresh operations or when the user ID is available from JWT tokens.
   *
   * @param userId the unique user ID
   * @return the UserDetails for the specified user
   * @throws UsernameNotFoundException if the user is not found
   */
  @Transactional(readOnly = true)
  public UserDetails loadUserById(Long userId) throws UsernameNotFoundException {
    log.debug("Attempting to load user by ID: {}", userId);

    if (userId == null) {
      log.warn("Authentication attempt with null user ID");
      throw new UsernameNotFoundException("User ID cannot be null");
    }

    try {
      UserEntity user =
          userRepository
              .findById(userId)
              .orElseThrow(
                  () -> new UsernameNotFoundException("User not found with ID: " + userId));

      validateUserAccount(user);

      log.debug("Successfully loaded user by ID: {} (Username: {})", userId, user.getUsername());
      return user;

    } catch (UsernameNotFoundException e) {
      throw e;
    } catch (Exception e) {
      log.error("Error loading user by ID '{}': {}", userId, e.getMessage(), e);
      throw new UsernameNotFoundException("Error loading user with ID: " + userId, e);
    }
  }

  /**
   * Checks if a user exists by username or email.
   *
   * @param usernameOrEmail the username or email to check
   * @return true if the user exists, false otherwise
   */
  @Transactional(readOnly = true)
  public boolean userExists(String usernameOrEmail) {
    if (usernameOrEmail == null || usernameOrEmail.trim().isEmpty()) {
      return false;
    }

    String trimmed = usernameOrEmail.trim();
    return userRepository.findByUserName(trimmed).isPresent()
        || userRepository.findByEmail(trimmed).isPresent();
  }

  /**
   * Returns a string representation of this service.
   *
   * @return string description of the service
   */
  @Override
  public String toString() {
    return "UserDetailsServiceImpl{" + "userRepository=" + userRepository + '}';
  }
}
