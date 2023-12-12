package com.merantory.dostavim.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {
    @Value("${openapi.dev-url}")
    private String devUrl;

    @Value("${openapi.dev-url}")
    private String prodUrl;

    @Bean
    public OpenAPI myOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl(devUrl);
        devServer.setDescription("Server URL в разработке");

        Server prodServer = new Server();
        prodServer.setUrl(prodUrl);
        prodServer.setDescription("Server URL в проде");

        Contact contact = new Contact();
        contact.setEmail("tynin22@gmail.com");
        contact.setName("Stanislav");

        Info info = new Info()
                .title("Dostavim API")
                .version("0.1")
                .contact(contact)
                .description("Не является публичным API");

        return new OpenAPI().info(info).servers(List.of(devServer, prodServer));
    }
}
