package com.mycodethesaurus.financeinspector.config;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Abstract base class for integration tests that provides PostgreSQL test container configuration.
 * Uses a global singleton container to prevent lifecycle conflicts between test classes.
 */
@SpringBootTest
@ActiveProfiles("integration-test")
@Testcontainers
public abstract class AbstractIntegrationTest {

  /** Configure Spring Boot properties dynamically based on the test container */
  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    PostgreSQLContainer<?> container = GlobalPostgreSQLContainer.getInstance();

    registry.add("spring.datasource.url", container::getJdbcUrl);
    registry.add("spring.datasource.username", container::getUsername);
    registry.add("spring.datasource.password", container::getPassword);
    registry.add("spring.datasource.driver-class-name", container::getDriverClassName);

    // Ensure proper connection pool configuration
    registry.add("spring.datasource.hikari.connection-timeout", () -> "60000");
    registry.add("spring.datasource.hikari.maximum-pool-size", () -> "10");
    registry.add("spring.datasource.hikari.minimum-idle", () -> "5");
    registry.add("spring.datasource.hikari.idle-timeout", () -> "300000");
    registry.add("spring.datasource.hikari.max-lifetime", () -> "600000");
    registry.add("spring.datasource.hikari.leak-detection-threshold", () -> "60000");
  }

  /**
   * Provides access to the PostgreSQL container for test classes that need direct access
   *
   * @return The singleton PostgreSQL container instance
   */
  protected static PostgreSQLContainer<?> getPostgresContainer() {
    return GlobalPostgreSQLContainer.getInstance();
  }
}
