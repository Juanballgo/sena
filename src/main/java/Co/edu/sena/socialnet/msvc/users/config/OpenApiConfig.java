package Co.edu.sena.socialnet.msvc.users.config;

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
                        .title("MSVC Users API - Servicio de Usuarios")
                        .version("1.0.0")
                        .description("Documentación de la API REST del microservicio de usuarios del proyecto SENA.")
                        .contact(new Contact()
                                .name("SENA - SocialNet Team")
                                .email("soporte@sena.edu.co")
                                .url("https://www.sena.edu.co"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://springdoc.org")));
    }
}
