package bo.sipqr.cliente.baneco;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import bo.sipqr.configuracion.RestTemplateConfiguracion;
import bo.sipqr.servicio.baneco.BanecoConfiguracionServicio;
import bo.sipqr.servicio.baneco.BanecoExcepcion;
import bo.sipqr.servicio.baneco.BanecoRespuestaServicio;
import bo.sipqr.servicio.baneco.ConfiguracionBanco;
import bo.sipqr.servicio.baneco.TipoErrorBaneco;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import static org.hamcrest.Matchers.containsString;
import tools.jackson.databind.json.JsonMapper;

class BanecoClienteHttpPrueba {
    private MockRestServiceServer servidor;
    private BanecoClienteHttp cliente;

    @BeforeEach
    void preparar() {
        RestTemplate restTemplate = new RestTemplateConfiguracion().restTemplateBaneco(5000, 15000);
        servidor = MockRestServiceServer.bindTo(restTemplate).build();
        BanecoConfiguracionServicio configuracion = mock(BanecoConfiguracionServicio.class);
        when(configuracion.obtener()).thenReturn(new ConfiguracionBanco(
            "usuario", "clave", "llave", "cuenta", "https://apimktdesa.baneco.com.bo", OffsetDateTime.now()));
        cliente = new BanecoClienteHttp(restTemplate, configuracion,
            new BanecoRespuestaServicio(JsonMapper.builder().build()));
    }

    @Test
    void enviaGetConParametrosSinToken() {
        Map<String, String> parametros = new LinkedHashMap<String, String>();
        parametros.put("text", "hola mundo");
        parametros.put("aesKey", "llave");
        servidor.expect(requestTo("https://apimktdesa.baneco.com.bo/api/authentication/encrypt?text=hola%20mundo&aesKey=llave"))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess("TEXTO-CIFRADO", MediaType.TEXT_PLAIN));

        String respuesta = cliente.obtener("/api/authentication/encrypt", parametros, null);

        assertEquals("TEXTO-CIFRADO", respuesta);
        servidor.verify();
    }

    @Test
    void enviaPostJsonConTokenBearer() {
        servidor.expect(requestTo("https://apimktdesa.baneco.com.bo/api/authentication/authenticate"))
            .andExpect(method(HttpMethod.POST))
            .andExpect(header("Authorization", "Bearer token-123"))
            .andExpect(content().string(containsString("\"userName\":\"usuario\"")))
            .andRespond(withSuccess("{\"responseCode\":0,\"token\":\"token-123\"}", MediaType.APPLICATION_JSON));

        String respuesta = cliente.postJson("/api/authentication/authenticate",
            Map.of("userName", "usuario"), "token-123");

        assertTrue(respuesta.contains("token-123"));
        servidor.verify();
    }

    @Test
    void enviaDeleteConCuerpo() {
        servidor.expect(requestTo("https://apimktdesa.baneco.com.bo/api/qrsimple/cancelQR"))
            .andExpect(method(HttpMethod.DELETE))
            .andExpect(content().string(containsString("\"qrId\":\"QR-1\"")))
            .andRespond(withSuccess("{\"responseCode\":0,\"message\":\"\"}", MediaType.APPLICATION_JSON));

        String respuesta = cliente.deleteJson("/api/qrsimple/cancelQR", Map.of("qrId", "QR-1"), "token-123");

        assertTrue(respuesta.contains("\"responseCode\":0"));
        servidor.verify();
    }

    @Test
    void convierteErrorHttpEnExcepcionConEstado() {
        servidor.expect(requestTo("https://apimktdesa.baneco.com.bo/api/qrsimple/v2/statusQR/QR-1"))
            .andRespond(withStatus(HttpStatus.UNAUTHORIZED));

        BanecoExcepcion excepcion = assertThrows(BanecoExcepcion.class,
            () -> cliente.obtener("/api/qrsimple/v2/statusQR/QR-1", Map.of(), "token-vencido"));

        assertEquals(TipoErrorBaneco.COMUNICACION, excepcion.getTipo());
        assertEquals(401, excepcion.getEstadoHttp());
        servidor.verify();
    }

    @Test
    void convierteRespuestaVaciaEnErrorDeRespuesta() {
        servidor.expect(requestTo("https://apimktdesa.baneco.com.bo/api/qrsimple/v2/paidQR/20261026"))
            .andRespond(withSuccess("", MediaType.APPLICATION_JSON));

        BanecoExcepcion excepcion = assertThrows(BanecoExcepcion.class,
            () -> cliente.obtener("/api/qrsimple/v2/paidQR/20261026", Map.of(), "token-123"));

        assertEquals(TipoErrorBaneco.RESPUESTA_INVALIDA, excepcion.getTipo());
        servidor.verify();
    }
}
