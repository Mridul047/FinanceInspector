package com.mycodethesaurus.financeinspector.service;

import com.mycodethesaurus.financeinspector.enums.UserRole;
import com.mycodethesaurus.financeinspector.persistence.entity.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

/**
 * Service for handling user access control and ownership validation.
 *
 * <p>This service provides methods to check if a user has permission to access or modify specific
 * resources based on role-based access control and resource ownership rules.
 *
 * <p>Access Control Rules:
 *
 * <ul>
 *   <li><strong>ADMIN_USER</strong>: Full access to all resources and operations
 *   <li><strong>REGULAR_USER</strong>: Limited access based on resource ownership
 *   <li><strong>Resource Ownership</strong>: Users can only access/modify their own data
 * </ul>
 */
@Service
@Slf4j
public class UserAccessService {

  /**
   * Checks if the authenticated user can access the specified user's data.
   *
   * <p>Access Rules:
   *
   * <ul>
   *   <li>Admin users can access any user's data
   *   <li>Regular users can only access their own data
   * </ul>
   *
   * @param authentication the current authentication context
   * @param targetUserId the ID of the user whose data is being accessed
   * @return true if access is allowed, false otherwise
   */
  public boolean canAccessUser(Authentication authentication, Long targetUserId) {
    if (authentication == null || !authentication.isAuthenticated()) {
      log.warn("Access denied: User not authenticated");
      return false;
    }

    // Admin users have access to all users
    if (hasRole(authentication, UserRole.ADMIN_USER)) {
      log.debug("Admin access granted for user ID: {}", targetUserId);
      return true;
    }

    // Regular users can only access their own data
    Long currentUserId = getCurrentUserId(authentication);
    boolean canAccess = currentUserId != null && currentUserId.equals(targetUserId);

    if (canAccess) {
      log.debug("User {} granted access to own data", currentUserId);
    } else {
      log.warn("User {} denied access to user {}", currentUserId, targetUserId);
    }

    return canAccess;
  }

  /**
   * Checks if the authenticated user can modify the specified user's data.
   *
   * <p>Modification Rules:
   *
   * <ul>
   *   <li>Admin users can modify any user's data
   *   <li>Regular users can only modify their own data
   * </ul>
   *
   * @param authentication the current authentication context
   * @param targetUserId the ID of the user whose data is being modified
   * @return true if modification is allowed, false otherwise
   */
  public boolean canModifyUser(Authentication authentication, Long targetUserId) {
    return canAccessUser(authentication, targetUserId);
  }

  /**
   * Checks if the authenticated user can access expenses for the specified user.
   *
   * <p>Expense Access Rules:
   *
   * <ul>
   *   <li>Admin users can access any user's expenses
   *   <li>Regular users can only access their own expenses
   * </ul>
   *
   * @param authentication the current authentication context
   * @param userId the ID of the user whose expenses are being accessed
   * @return true if access is allowed, false otherwise
   */
  public boolean canAccessExpenses(Authentication authentication, Long userId) {
    return canAccessUser(authentication, userId);
  }

  /**
   * Checks if the authenticated user can access income data for the specified user.
   *
   * <p>Income Access Rules:
   *
   * <ul>
   *   <li>Admin users can access any user's income data
   *   <li>Regular users can only access their own income data
   * </ul>
   *
   * @param authentication the current authentication context
   * @param userId the ID of the user whose income data is being accessed
   * @return true if access is allowed, false otherwise
   */
  public boolean canAccessIncome(Authentication authentication, Long userId) {
    return canAccessUser(authentication, userId);
  }

  /**
   * Checks if the authenticated user has administrative privileges.
   *
   * @param authentication the current authentication context
   * @return true if the user has admin role, false otherwise
   */
  public boolean isAdmin(Authentication authentication) {
    return hasRole(authentication, UserRole.ADMIN_USER);
  }

  /**
   * Checks if the authenticated user is a regular user.
   *
   * @param authentication the current authentication context
   * @return true if the user has regular user role, false otherwise
   */
  public boolean isRegularUser(Authentication authentication) {
    return hasRole(authentication, UserRole.REGULAR_USER);
  }

  /**
   * Checks if the authenticated user has the specified role.
   *
   * @param authentication the current authentication context
   * @param role the role to check for
   * @return true if the user has the specified role, false otherwise
   */
  public boolean hasRole(Authentication authentication, UserRole role) {
    if (authentication == null || role == null) {
      return false;
    }

    return authentication
        .getAuthorities()
        .contains(new SimpleGrantedAuthority(role.getAuthority()));
  }

  /**
   * Extracts the current user ID from the authentication context.
   *
   * @param authentication the current authentication context
   * @return the current user's ID, or null if not available
   */
  public Long getCurrentUserId(Authentication authentication) {
    if (authentication == null || !authentication.isAuthenticated()) {
      return null;
    }

    Object principal = authentication.getPrincipal();
    if (principal instanceof UserEntity userEntity) {
      return userEntity.getId();
    }

    log.warn("Could not extract user ID from authentication principal: {}", principal.getClass());
    return null;
  }

  /**
   * Gets the current authenticated user entity.
   *
   * @param authentication the current authentication context
   * @return the current UserEntity, or null if not available
   */
  public UserEntity getCurrentUser(Authentication authentication) {
    if (authentication == null || !authentication.isAuthenticated()) {
      return null;
    }

    Object principal = authentication.getPrincipal();
    if (principal instanceof UserEntity userEntity) {
      return userEntity;
    }

    return null;
  }

  /**
   * Returns a string representation of this access service.
   *
   * @return string description of the service
   */
  @Override
  public String toString() {
    return "UserAccessService{}";
  }
}
