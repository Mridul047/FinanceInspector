package com.mycodethesaurus.financeinspector.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Jackson configuration for proper JSON serialization and deserialization.
 *
 * <p>This configuration ensures proper handling of Java 8 time types (LocalDateTime, LocalDate,
 * etc.) and provides consistent JSON formatting across the application.
 *
 * <p>Key features:
 *
 * <ul>
 *   <li>JSR310 (Java Time) module registration for LocalDateTime support
 *   <li>ISO-8601 datetime formatting
 *   <li>Consistent timestamp handling
 * </ul>
 */
@Configuration
public class JacksonConfig {

  /**
   * Configures ObjectMapper with JSR310 support for LocalDateTime serialization.
   *
   * <p>This bean ensures that LocalDateTime fields in DTOs (like ErrorResponse.timestamp) are
   * properly serialized to JSON without requiring additional modules.
   *
   * @return configured ObjectMapper with JSR310 support
   */
  @Bean
  @Primary
  public ObjectMapper objectMapper() {
    ObjectMapper mapper = new ObjectMapper();

    // Register JSR310 module for Java 8 time support
    mapper.registerModule(new JavaTimeModule());

    // Configure date/time serialization
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    return mapper;
  }
}
