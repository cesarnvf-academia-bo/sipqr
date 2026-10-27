package bo.sipqr.cliente.baneco;

import bo.sipqr.servicio.baneco.BanecoConfiguracionServicio;
import bo.sipqr.servicio.baneco.BanecoExcepcion;
import bo.sipqr.servicio.baneco.BanecoRespuestaServicio;
import bo.sipqr.servicio.baneco.TipoErrorBaneco;
import java.net.URI;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class BanecoClienteHttp {
    private static final Logger REGISTRO = LoggerFactory.getLogger(BanecoClienteHttp.class);

    private final RestTemplate restTemplate;
    private final BanecoConfiguracionServicio configuracionServicio;
    private final BanecoRespuestaServicio respuestaServicio;

    public BanecoClienteHttp(@Qualifier("restTemplateBaneco") RestTemplate restTemplate,
                             BanecoConfiguracionServicio configuracionServicio,
                             BanecoRespuestaServicio respuestaServicio) {
        this.restTemplate = restTemplate;
        this.configuracionServicio = configuracionServicio;
        this.respuestaServicio = respuestaServicio;
    }

    public String obtener(String ruta, Map<String, String> parametros, String token) {
        URI uri = construirUri(ruta, parametros);
        return ejecutar(uri, HttpMethod.GET, null, token, ruta);
    }

    public String postJson(String ruta, Object cuerpo, String token) {
        URI uri = construirUri(ruta, Map.of());
        return ejecutar(uri, HttpMethod.POST, respuestaServicio.serializar(cuerpo), token, ruta);
    }

    public String deleteJson(String ruta, Object cuerpo, String token) {
        URI uri = construirUri(ruta, Map.of());
        return ejecutar(uri, HttpMethod.DELETE, respuestaServicio.serializar(cuerpo), token, ruta);
    }

    private String ejecutar(URI uri, HttpMethod metodo, String cuerpo, String token, String ruta) {
        HttpHeaders cabeceras = new HttpHeaders();
        cabeceras.setAccept(List.of(MediaType.ALL));
        if (cuerpo != null) {
            cabeceras.setContentType(MediaType.APPLICATION_JSON);
        }
        if (token != null && !token.isBlank()) {
            cabeceras.setBearerAuth(token);
        }
        HttpEntity<String> entidad = new HttpEntity<String>(cuerpo, cabeceras);
        REGISTRO.info("BANECO {} {}", metodo, ruta);
        try {
            ResponseEntity<String> respuesta = restTemplate.exchange(uri, metodo, entidad, String.class);
            if (respuesta.getBody() == null || respuesta.getBody().isBlank()) {
                throw new BanecoExcepcion(TipoErrorBaneco.RESPUESTA_INVALIDA,
                    "El banco devolvió una respuesta vacía");
            }
            return respuesta.getBody();
        } catch (HttpStatusCodeException ex) {
            int estado = ex.getStatusCode().value();
            REGISTRO.warn("BANECO {} {} respondió HTTP {}", metodo, ruta, estado);
            throw new BanecoExcepcion(TipoErrorBaneco.COMUNICACION,
                "El banco respondió con estado HTTP " + estado, null, estado, ex);
        } catch (RestClientException ex) {
            REGISTRO.warn("BANECO {} {} no pudo completarse: {}", metodo, ruta, ex.getClass().getSimpleName());
            throw new BanecoExcepcion(TipoErrorBaneco.COMUNICACION,
                "No fue posible comunicarse con el banco", ex);
        }
    }

    private URI construirUri(String ruta, Map<String, String> parametros) {
        String base = configuracionServicio.obtener().url();
        String baseNormalizada = base.endsWith("/") ? base.substring(0, base.length() - 1) : base;
        String rutaNormalizada = ruta.startsWith("/") ? ruta : "/" + ruta;
        UriComponentsBuilder constructor = UriComponentsBuilder.fromUriString(baseNormalizada + rutaNormalizada);
        if (parametros != null) {
            parametros.forEach(constructor::queryParam);
        }
        return constructor.build().encode().toUri();
    }
}
