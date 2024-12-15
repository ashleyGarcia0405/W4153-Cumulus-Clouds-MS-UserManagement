package com.cumulusclouds.w4153cumuluscloudsmsusermanagement.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Cumulus Clouds API",
        version = "1.0",
        description = "API documentation for the User Management application"
    )
)
public class SwaggerConfig {
}
