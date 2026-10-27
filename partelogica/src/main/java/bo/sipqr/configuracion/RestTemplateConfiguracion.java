package bo.sipqr.configuracion;

import java.net.http.HttpClient;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfiguracion {
    @Bean
    public RestTemplate restTemplateBaneco(
            @Value("${baneco.http.conexion-ms:5000}") long tiempoConexionMs,
            @Value("${baneco.http.lectura-ms:15000}") long tiempoLecturaMs) {
        HttpClient httpCliente = HttpClient.newBuilder()
            .connectTimeout(Duration.ofMillis(tiempoConexionMs))
            .followRedirects(HttpClient.Redirect.NEVER)
            .build();
        JdkClientHttpRequestFactory fabrica = new JdkClientHttpRequestFactory(httpCliente);
        fabrica.setReadTimeout(Duration.ofMillis(tiempoLecturaMs));
        return new RestTemplate(fabrica);
    }
}
