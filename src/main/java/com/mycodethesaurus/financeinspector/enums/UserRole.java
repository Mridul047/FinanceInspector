package com.mycodethesaurus.financeinspector.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enumeration defining user roles in the Finance Inspector application.
 *
 * <p>This enum defines the two primary user roles:
 *
 * <ul>
 *   <li><strong>REGULAR_USER</strong> - Standard users with basic access to personal financial data
 *   <li><strong>ADMIN_USER</strong> - Administrators with full system access and management
 *       capabilities
 * </ul>
 *
 * <p>Each role includes:
 *
 * <ul>
 *   <li><strong>authority</strong> - Spring Security authority string (e.g., "ROLE_USER",
 *       "ROLE_ADMIN")
 *   <li><strong>description</strong> - Human-readable description of the role's purpose
 * </ul>
 */
@Getter
@AllArgsConstructor
public enum UserRole {

  /**
   * Regular user role with basic access permissions. Users with this role can: - Manage their own
   * expenses and income - View and update their own profile - Access global categories (read-only)
   */
  REGULAR_USER("ROLE_USER", "Regular user with basic access to personal financial data"),

  /**
   * Administrator role with full system access. Users with this role can: - All REGULAR_USER
   * capabilities - Manage global categories (create, update, delete) - View and manage all users -
   * Access system administration features
   */
  ADMIN_USER("ROLE_ADMIN", "Administrator with full system access and management capabilities");

  /**
   * Spring Security authority string used for role-based access control. This follows the Spring
   * Security convention of prefixing roles with "ROLE_".
   */
  private final String authority;

  /**
   * Human-readable description of the role's purpose and capabilities. Used for documentation and
   * UI display purposes.
   */
  private final String description;

  /**
   * Checks if this role has administrative privileges.
   *
   * @return true if this role is ADMIN_USER, false otherwise
   */
  public boolean isAdmin() {
    return this == ADMIN_USER;
  }

  /**
   * Checks if this role is a regular user role.
   *
   * @return true if this role is REGULAR_USER, false otherwise
   */
  public boolean isRegularUser() {
    return this == REGULAR_USER;
  }

  /**
   * Gets the role name without the "ROLE_" prefix.
   *
   * @return the simple role name (e.g., "USER", "ADMIN")
   */
  public String getSimpleName() {
    return authority.substring(5); // Remove "ROLE_" prefix
  }

  /**
   * Finds a UserRole by its authority string.
   *
   * @param authority the authority string to search for
   * @return the matching UserRole, or null if not found
   */
  public static UserRole fromAuthority(String authority) {
    for (UserRole role : values()) {
      if (role.authority.equals(authority)) {
        return role;
      }
    }
    return null;
  }

  /**
   * Finds a UserRole by its simple name (without "ROLE_" prefix).
   *
   * @param simpleName the simple name to search for (e.g., "USER", "ADMIN")
   * @return the matching UserRole, or null if not found
   */
  public static UserRole fromSimpleName(String simpleName) {
    return fromAuthority("ROLE_" + simpleName.toUpperCase());
  }
}
