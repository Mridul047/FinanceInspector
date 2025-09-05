package com.mycodethesaurus.financeinspector.persistence.entity;

import com.mycodethesaurus.financeinspector.enums.UserRole;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * User entity representing a user in the Finance Inspector application.
 *
 * <p>This entity implements Spring Security's {@link UserDetails} interface to provide
 * authentication and authorization capabilities. It includes both basic user information and
 * security-related fields for account management.
 *
 * <p>Security features include:
 *
 * <ul>
 *   <li>Role-based access control via {@link UserRole}
 *   <li>Account status tracking (enabled, expired, locked)
 *   <li>Failed login attempt tracking
 *   <li>Last login timestamp
 * </ul>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sys_user", schema = "fip")
public class UserEntity implements UserDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "username", unique = true, nullable = false)
  private String userName;

  @Column(name = "password", nullable = false)
  private String password;

  @Column(name = "first_name", nullable = false)
  private String firstName;

  @Column(name = "last_name", nullable = false)
  private String lastName;

  @Column(name = "email", unique = true, nullable = false)
  private String email;

  // ========== SECURITY FIELDS ==========

  /**
   * User's role in the system (REGULAR_USER or ADMIN_USER). Default is REGULAR_USER for new
   * accounts.
   */
  @Enumerated(EnumType.STRING)
  @Column(name = "role", nullable = false)
  private UserRole role = UserRole.REGULAR_USER;

  /** Whether the user account is enabled. Disabled accounts cannot authenticate. */
  @Column(name = "enabled", nullable = false)
  private Boolean enabled = true;

  /** Whether the user account has not expired. Expired accounts cannot authenticate. */
  @Column(name = "account_non_expired", nullable = false)
  private Boolean accountNonExpired = true;

  /**
   * Whether the user account is not locked. Locked accounts cannot authenticate (usually due to
   * failed login attempts).
   */
  @Column(name = "account_non_locked", nullable = false)
  private Boolean accountNonLocked = true;

  /**
   * Whether the user's credentials (password) have not expired. Users with expired credentials need
   * to change their password.
   */
  @Column(name = "credentials_non_expired", nullable = false)
  private Boolean credentialsNonExpired = true;

  /**
   * Timestamp of the user's last successful login. Used for security monitoring and user activity
   * tracking.
   */
  @Column(name = "last_login")
  private LocalDateTime lastLogin;

  /** Number of consecutive failed login attempts. Used for account lockout protection. */
  @Column(name = "failed_login_attempts", nullable = false)
  private Integer failedLoginAttempts = 0;

  // ========== RELATIONSHIPS ==========

  @OneToMany(mappedBy = "userEntity", cascade = CascadeType.ALL)
  private List<SalaryIncomeEntity> salaryIncomeEntityList;

  // ========== AUDIT FIELDS ==========

  @Column(name = "created_on", nullable = false)
  private LocalDateTime createdOn;

  @Column(name = "updated_on", nullable = false)
  private LocalDateTime updatedOn;

  // ========== LIFECYCLE METHODS ==========

  @PrePersist
  protected void onCreate() {
    createdOn = LocalDateTime.now();
    updatedOn = LocalDateTime.now();
  }

  @PreUpdate
  protected void onUpdate() {
    updatedOn = LocalDateTime.now();
  }

  // ========== USERDETAILS IMPLEMENTATION ==========

  /**
   * Returns the authorities granted to the user.
   *
   * @return a collection containing the user's role as a GrantedAuthority
   */
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority(role.getAuthority()));
  }

  /**
   * Returns the password used to authenticate the user.
   *
   * @return the user's password (should be encoded)
   */
  @Override
  public String getPassword() {
    return password;
  }

  /**
   * Returns the username used to authenticate the user.
   *
   * @return the user's username
   */
  @Override
  public String getUsername() {
    return userName;
  }

  /**
   * Indicates whether the user's account has expired.
   *
   * @return true if the user's account is valid (non-expired), false otherwise
   */
  @Override
  public boolean isAccountNonExpired() {
    return accountNonExpired != null ? accountNonExpired : true;
  }

  /**
   * Indicates whether the user is locked or unlocked.
   *
   * @return true if the user is not locked, false otherwise
   */
  @Override
  public boolean isAccountNonLocked() {
    return accountNonLocked != null ? accountNonLocked : true;
  }

  /**
   * Indicates whether the user's credentials (password) has expired.
   *
   * @return true if the user's credentials are valid (non-expired), false otherwise
   */
  @Override
  public boolean isCredentialsNonExpired() {
    return credentialsNonExpired != null ? credentialsNonExpired : true;
  }

  /**
   * Indicates whether the user is enabled or disabled.
   *
   * @return true if the user is enabled, false otherwise
   */
  @Override
  public boolean isEnabled() {
    return enabled != null ? enabled : true;
  }

  // ========== SECURITY HELPER METHODS ==========

  /**
   * Checks if this user has administrative privileges.
   *
   * @return true if the user has ADMIN_USER role, false otherwise
   */
  public boolean isAdmin() {
    return role != null && role.isAdmin();
  }

  /**
   * Checks if this user is a regular user.
   *
   * @return true if the user has REGULAR_USER role, false otherwise
   */
  public boolean isRegularUser() {
    return role != null && role.isRegularUser();
  }

  /**
   * Records a successful login by updating the last login timestamp and resetting failed login
   * attempts.
   */
  public void recordSuccessfulLogin() {
    this.lastLogin = LocalDateTime.now();
    this.failedLoginAttempts = 0;
  }

  /** Records a failed login attempt by incrementing the failed attempts counter. */
  public void recordFailedLogin() {
    this.failedLoginAttempts =
        (this.failedLoginAttempts != null ? this.failedLoginAttempts : 0) + 1;
  }

  /** Locks the user account due to excessive failed login attempts. */
  public void lockAccount() {
    this.accountNonLocked = false;
  }

  /** Unlocks the user account and resets failed login attempts. */
  public void unlockAccount() {
    this.accountNonLocked = true;
    this.failedLoginAttempts = 0;
  }

  /** Disables the user account. */
  public void disable() {
    this.enabled = false;
  }

  /** Enables the user account. */
  public void enable() {
    this.enabled = true;
  }
}
