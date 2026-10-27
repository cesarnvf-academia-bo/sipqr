package bo.sipqr.seguridad.baneco;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import bo.sipqr.cliente.baneco.BanecoClienteHttp;
import bo.sipqr.servicio.baneco.BanecoCifradoServicio;
import bo.sipqr.servicio.baneco.BanecoConfiguracionServicio;
import bo.sipqr.servicio.baneco.BanecoExcepcion;
import bo.sipqr.servicio.baneco.BanecoRespuestaServicio;
import bo.sipqr.servicio.baneco.ConfiguracionBanco;
import bo.sipqr.servicio.baneco.TipoErrorBaneco;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Base64;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

class TokenBanecoServicioPrueba {
    private BanecoClienteHttp cliente;
    private BanecoCifradoServicio cifrado;
    private BanecoConfiguracionServicio configuracion;
    private TokenBanecoServicio servicio;
    private ConfiguracionBanco config;

    @BeforeEach
    void preparar() {
        cliente = mock(BanecoClienteHttp.class);
        cifrado = mock(BanecoCifradoServicio.class);
        configuracion = mock(BanecoConfiguracionServicio.class);
        config = new ConfiguracionBanco("usuario", "clave", "llave", "cuenta",
            "https://apimktdesa.baneco.com.bo", OffsetDateTime.now());
        when(configuracion.obtener()).thenReturn(config);
        when(cifrado.encriptar("clave")).thenReturn("CLAVE-CIFRADA");
        servicio = new TokenBanecoServicio(cliente, cifrado, configuracion,
            new BanecoRespuestaServicio(JsonMapper.builder().build()), JsonMapper.builder().build());
    }

    @Test
    void autenticaYReutilizaTokenSinExponerCredenciales() {
        String token = tokenConExpiracion(Instant.now().plusSeconds(3600));
        when(cliente.postJson(eq("/api/authentication/authenticate"), any(), isNull()))
            .thenReturn("{\"responseCode\":0,\"token\":\"" + token + "\"}");

        assertEquals(token, servicio.obtenerToken());
        assertEquals(token, servicio.obtenerToken());

        verify(cifrado, times(1)).encriptar("clave");
        verify(cliente, times(1)).postJson(eq("/api/authentication/authenticate"), any(), isNull());
    }

    @Test
    void renuevaTokenCuandoCambiaLaConfiguracion() {
        String token = tokenConExpiracion(Instant.now().plusSeconds(3600));
        when(cliente.postJson(eq("/api/authentication/authenticate"), any(), isNull()))
            .thenReturn("{\"responseCode\":0,\"token\":\"" + token + "\"}");
        servicio.obtenerToken();

        when(configuracion.obtener()).thenReturn(new ConfiguracionBanco("usuario", "clave", "llave", "cuenta",
            "https://apimktdesa.baneco.com.bo", OffsetDateTime.now().plusMinutes(1)));
        servicio.obtenerToken();

        verify(cliente, times(2)).postJson(eq("/api/authentication/authenticate"), any(), isNull());
    }

    @Test
    void rechazaAutenticacionSinToken() {
        when(cliente.postJson(eq("/api/authentication/authenticate"), any(), isNull()))
            .thenReturn("{\"responseCode\":0}");

        BanecoExcepcion excepcion = assertThrows(BanecoExcepcion.class, () -> servicio.obtenerToken());

        assertEquals(TipoErrorBaneco.AUTENTICACION, excepcion.getTipo());
    }

    private String tokenConExpiracion(Instant expiracion) {
        String cabecera = codificar("{\"alg\":\"HS512\"}");
        String carga = codificar("{\"exp\":" + expiracion.getEpochSecond() + "}");
        return cabecera + "." + carga + ".firma";
    }

    private String codificar(String valor) {
        return Base64.getUrlEncoder().withoutPadding()
            .encodeToString(valor.getBytes(StandardCharsets.UTF_8));
    }
}
