package com.mycodethesaurus.financeinspector.config;

import com.mycodethesaurus.financeinspector.security.JwtAuthenticationEntryPoint;
import com.mycodethesaurus.financeinspector.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security configuration for Finance Inspector application.
 *
 * <p>This configuration implements JWT-based authentication and authorization with:
 *
 * <ul>
 *   <li>JWT token authentication for API endpoints
 *   <li>Role-based access control (RBAC)
 *   <li>Stateless session management
 *   <li>Public access to authentication and documentation endpoints
 *   <li>Method-level security with @PreAuthorize annotations
 * </ul>
 *
 * <p>Security Features:
 *
 * <ul>
 *   <li>BCrypt password encoding with strength 12
 *   <li>JWT authentication filter for token validation
 *   <li>Custom authentication entry point for unauthorized access
 *   <li>CSRF protection disabled for stateless API
 *   <li>CORS configuration for cross-origin requests
 * </ul>
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

  private final UserDetailsService userDetailsService;
  private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
  private final JwtAuthenticationFilter jwtAuthenticationFilter;

  /**
   * Password encoder bean using BCrypt with strength 12 for secure password hashing.
   *
   * @return BCryptPasswordEncoder instance
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(12);
  }

  /**
   * Authentication provider that uses our custom UserDetailsService and password encoder.
   *
   * @return configured DaoAuthenticationProvider
   */
  @Bean
  public DaoAuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(userDetailsService);
    authProvider.setPasswordEncoder(passwordEncoder());
    return authProvider;
  }

  /**
   * Authentication manager bean for handling authentication requests.
   *
   * @param config the authentication configuration
   * @return AuthenticationManager instance
   * @throws Exception if configuration fails
   */
  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
      throws Exception {
    return config.getAuthenticationManager();
  }

  /**
   * Configures HTTP security for the application with JWT authentication.
   *
   * <p>Security Configuration:
   *
   * <ul>
   *   <li>Public endpoints: /v1/auth/**, API documentation
   *   <li>Protected endpoints: All other /v1/** endpoints require authentication
   *   <li>Role-based access: Enforced through @PreAuthorize annotations in controllers
   *   <li>Session management: Stateless (no server-side sessions)
   * </ul>
   *
   * @param http the HttpSecurity to configure
   * @return configured SecurityFilterChain
   * @throws Exception if configuration fails
   */
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        // Configure request authorization
        .authorizeHttpRequests(
            authz ->
                authz
                    // Public authentication endpoints
                    .requestMatchers("/v1/auth/**")
                    .permitAll()

                    // Public user registration endpoint
                    .requestMatchers(HttpMethod.POST, "/v1/users")
                    .permitAll()

                    // Public API documentation endpoints
                    .requestMatchers(
                        "/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/swagger-resources/**",
                        "/webjars/**")
                    .permitAll()

                    // Public health check endpoints
                    .requestMatchers("/actuator/health", "/actuator/info")
                    .permitAll()

                    // Allow OPTIONS requests for CORS preflight
                    .requestMatchers(HttpMethod.OPTIONS, "/**")
                    .permitAll()

                    // Public category read endpoints
                    .requestMatchers(HttpMethod.GET, "/v1/categories/**")
                    .permitAll()

                    // All other API endpoints require authentication
                    .requestMatchers("/v1/**")
                    .authenticated()

                    // All other requests require authentication
                    .anyRequest()
                    .authenticated())

        // Configure authentication provider
        .authenticationProvider(authenticationProvider())

        // Configure JWT authentication entry point
        .exceptionHandling(
            exceptions -> exceptions.authenticationEntryPoint(jwtAuthenticationEntryPoint))

        // Configure session management (stateless for JWT)
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

        // Disable CSRF for stateless API
        .csrf(csrf -> csrf.disable())

        // Configure headers
        .headers(
            headers ->
                headers
                    .contentTypeOptions(contentTypeOptions -> {})
                    .httpStrictTransportSecurity(
                        hsts -> hsts.maxAgeInSeconds(31536000).includeSubDomains(true)))

        // Add JWT authentication filter before username/password authentication
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

        // Configure CORS
        .cors(
            cors ->
                cors.configurationSource(
                    request -> {
                      var corsConfiguration = new org.springframework.web.cors.CorsConfiguration();
                      corsConfiguration.setAllowedOriginPatterns(java.util.List.of("*"));
                      corsConfiguration.setAllowedMethods(
                          java.util.List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                      corsConfiguration.setAllowedHeaders(java.util.List.of("*"));
                      corsConfiguration.setAllowCredentials(true);
                      corsConfiguration.setMaxAge(3600L);
                      return corsConfiguration;
                    }));

    return http.build();
  }

  /**
   * Returns a string representation of the security configuration.
   *
   * @return string description of the configuration
   */
  @Override
  public String toString() {
    return "SecurityConfig{"
        + "userDetailsService="
        + userDetailsService
        + ", jwtAuthenticationEntryPoint="
        + jwtAuthenticationEntryPoint
        + ", jwtAuthenticationFilter="
        + jwtAuthenticationFilter
        + '}';
  }
}
