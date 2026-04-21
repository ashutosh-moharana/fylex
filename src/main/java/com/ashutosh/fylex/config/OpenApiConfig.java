package com.ashutosh.fylex.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Fylex API",
                version = "1.0.0",
                description = "Fylex is a room-based file sharing API. It allows users to create temporary rooms, upload files, list shared files, download files, and delete files before expiry."
        )
)
public class OpenApiConfig {
}
