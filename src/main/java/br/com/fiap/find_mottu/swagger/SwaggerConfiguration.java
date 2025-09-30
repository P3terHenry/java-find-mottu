package br.com.fiap.find_mottu.swagger;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(
        name = "Bearer Authentication",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class SwaggerConfiguration {

    @Bean
    OpenAPI configurarSwagger() {

        return new OpenAPI().info(new Info()
                .title("Projeto de Gestão de Pátios da Mottu com Thymeleaf e API Rest")
                .description("Este projeto oferece uma implementação que possibilita "
                        + "a gestão de filias, localizações, usuários e motos de um pátio de motos da Mottu.")
                .summary("Sumário: Este projeto oferece uma implementação que possibilita"
                        + "a gestão de filias, localizações, usuários e motos de um pátio de motos da Mottu.")
                .version("v1.0.3"));

    }

}
