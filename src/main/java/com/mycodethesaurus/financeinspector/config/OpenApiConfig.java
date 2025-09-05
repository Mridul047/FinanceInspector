package com.mycodethesaurus.financeinspector.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Value("${server.port:8080}")
  private String serverPort;

  @Bean
  public OpenAPI financeInspectorOpenAPI() {
    Server localServer = new Server();
    localServer.setUrl("http://localhost:" + serverPort + "/api");
    localServer.setDescription("Local Development Server");

    //    Contact contact = new Contact();
    //    contact.setName("FinanceInspector Development Team");
    //    contact.setEmail("support@financeinspector.com");
    //    contact.setUrl("https://github.com/mycodethesaurus/FinanceInspector");
    //
    //    License license = new License();
    //    license.setName("MIT License");
    //    license.setUrl("https://opensource.org/licenses/MIT");

    Info info =
        new Info()
            .title("Finance Inspector API")
            .version("1.0.0")
            .description(
                """
A comprehensive personal finance tracking REST API built with Spring Boot 3.4.5 and Java 21.
This API provides complete user management and salary income tracking capabilities with
robust validation, error handling, and financial precision using BigDecimal for all monetary calculations.

**Authentication:**
This API uses JWT Bearer token authentication. To access protected endpoints:
1. First authenticate using the `/v1/auth/login` endpoint
2. Copy the `token` value from the response
3. Click the "Authorize" button below and enter: `Bearer <your-token>`
4. Use the authenticated endpoints

**Authorization Levels:**
- **Public**: Category read operations
- **User**: Expense and income management for own account
- **Admin**: User management and global category management
""");
    // .termsOfService("https://financeinspector.com/terms")
    //            .contact(contact)
    //            .license(license);

    // Configure JWT Bearer Token Security Scheme
    Components components =
        new Components()
            .addSecuritySchemes(
                "bearerAuth",
                new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                    .description("JWT Bearer token authentication. Format: Bearer <token>"));

    return new OpenAPI().info(info).servers(List.of(localServer)).components(components);
  }
}
