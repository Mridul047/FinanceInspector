package com.mycodethesaurus.financeinspector.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
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
    localServer.setUrl("http://localhost:" + serverPort);
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
""");
    // .termsOfService("https://financeinspector.com/terms")
    //            .contact(contact)
    //            .license(license);

    return new OpenAPI().info(info).servers(List.of(localServer));
  }
}
