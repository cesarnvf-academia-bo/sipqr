package bo.sipqr.configuracion;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DocumentacionConfiguracion {
    @Bean
    public OpenAPI configuracionOpenApi() {
        final String esquemaSeguridad = "bearerAuth";
        return new OpenAPI()
            .info(new Info()
                .title("SIPQR API")
                .version("1.0.0")
                .description("API REST del Sistema de Informacion de Pagos QR"))
            .addSecurityItem(new SecurityRequirement().addList(esquemaSeguridad))
            .components(new Components()
                .addSecuritySchemes(esquemaSeguridad, new SecurityScheme()
                    .name(esquemaSeguridad)
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")));
    }
}
