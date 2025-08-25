package sn.afrizar.afrizar.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Afrizar API")
                        .version("1.0.0")
                        .description("API de la plateforme Afrizar.sn - Couture sénégalaise et accessoires artisanaux")
                        .contact(new Contact()
                                .name("Équipe Afrizar")
                                .email("contact@afrizar.sn")
                                .url("https://afrizar.sn"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")));
    }
}

