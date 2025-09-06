package com.mycodethesaurus.financeinspector.config;

import org.testcontainers.containers.PostgreSQLContainer;

/**
 * Global singleton PostgreSQL container for all integration tests. This ensures a single container
 * instance is shared across all test classes to prevent lifecycle conflicts and connection issues.
 */
public class GlobalPostgreSQLContainer {

  private static final String IMAGE_VERSION = "postgres:15-alpine";
  private static volatile PostgreSQLContainer<?> container;

  /**
   * Gets the singleton PostgreSQL container instance. Thread-safe lazy initialization ensures the
   * container is created only once.
   */
  public static PostgreSQLContainer<?> getInstance() {
    PostgreSQLContainer<?> result = container;
    if (result == null) {
      synchronized (GlobalPostgreSQLContainer.class) {
        result = container;
        if (result == null) {
          container = result = createContainer();
        }
      }
    }
    return result;
  }

  /** Creates and configures the PostgreSQL container. */
  private static PostgreSQLContainer<?> createContainer() {
    PostgreSQLContainer<?> container =
        new PostgreSQLContainer<>(IMAGE_VERSION)
            .withDatabaseName("financeinspector_test")
            .withUsername("test")
            .withPassword("test")
            .withStartupTimeoutSeconds(120)
            .withConnectTimeoutSeconds(60);

    container.start();

    // Add shutdown hook to clean up container when JVM exits
    Runtime.getRuntime()
        .addShutdownHook(
            new Thread(
                () -> {
                  if (container.isRunning()) {
                    container.stop();
                  }
                }));

    return container;
  }

  private GlobalPostgreSQLContainer() {
    // Prevent instantiation
  }
}
