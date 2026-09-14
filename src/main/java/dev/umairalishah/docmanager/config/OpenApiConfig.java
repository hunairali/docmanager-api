package dev.umairalishah.docmanager.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI docmanagerOpenApi() {
        return new OpenAPI().info(new Info()
                .title("Document Manager API")
                .description("REST API for tracking documents and correspondence through a review/approval workflow.")
                .version("0.1.0")
                .contact(new Contact().name("Umair Ali Shah").url("https://github.com/hunairali")));
    }
}

