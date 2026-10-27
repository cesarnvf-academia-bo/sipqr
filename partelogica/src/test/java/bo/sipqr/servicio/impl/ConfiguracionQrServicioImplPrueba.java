package bo.sipqr.servicio.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import bo.sipqr.dto.ConfiguracionQrRespuesta;
import bo.sipqr.dto.ConfiguracionQrSolicitud;
import bo.sipqr.modelo.ConfiguracionQr;
import bo.sipqr.repositorio.ConfiguracionQrRepositorio;
import bo.sipqr.seguridad.ValidadorUrlServicio;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class ConfiguracionQrServicioImplPrueba {
    @Mock
    private ConfiguracionQrRepositorio repositorio;

    private ConfiguracionQrServicioImpl servicio;

    @BeforeEach
    void inicializar() {
        servicio = new ConfiguracionQrServicioImpl(repositorio, new ValidadorUrlServicio());
    }

    @Test
    void obtenerDevuelveLosValoresTalCual() {
        given(repositorio.obtener()).willReturn(Optional.of(configuracionExistente()));

        ConfiguracionQrRespuesta respuesta = servicio.obtener();

        assertEquals("demo_password", respuesta.getPassword());
        assertEquals("demo_clave", respuesta.getKeyencript());
        assertEquals("demo_cuenta", respuesta.getAccount());
    }

    @Test
    void actualizarGuardaSecretosSinCifrarNiModificar() {
        given(repositorio.obtener()).willReturn(Optional.of(configuracionExistente()));
        ConfiguracionQrSolicitud solicitud = solicitud("https://baneco.com.bo/qr");
        solicitud.setPassword(" nueva-clave ");
        solicitud.setKeyencript("llave-aes");
        solicitud.setAccount("1234567890");

        servicio.actualizar(solicitud);

        ArgumentCaptor<ConfiguracionQr> captor = ArgumentCaptor.forClass(ConfiguracionQr.class);
        verify(repositorio).actualizar(captor.capture());
        assertEquals(" nueva-clave ", captor.getValue().getPassword());
        assertEquals("llave-aes", captor.getValue().getKeyencript());
        assertEquals("1234567890", captor.getValue().getAccount());
    }

    @Test
    void rechazaSecretosVacios() {
        given(repositorio.obtener()).willReturn(Optional.of(configuracionExistente()));
        ConfiguracionQrSolicitud solicitud = solicitud("https://baneco.com.bo/qr");
        solicitud.setPassword(" ");

        ResponseStatusException excepcion = assertThrows(ResponseStatusException.class,
            () -> servicio.actualizar(solicitud));

        assertEquals(HttpStatus.BAD_REQUEST, excepcion.getStatusCode());
    }

    @Test
    void rechazaUrlInterna() {
        given(repositorio.obtener()).willReturn(Optional.of(configuracionExistente()));

        ResponseStatusException excepcion = assertThrows(ResponseStatusException.class,
            () -> servicio.actualizar(solicitud("https://localhost/qr")));

        assertEquals(HttpStatus.BAD_REQUEST, excepcion.getStatusCode());
    }

    private ConfiguracionQr configuracionExistente() {
        ConfiguracionQr configuracion = new ConfiguracionQr();
        configuracion.setId((short) 1);
        configuracion.setUserName("demo_usuario");
        configuracion.setPassword("demo_password");
        configuracion.setKeyencript("demo_clave");
        configuracion.setAccount("demo_cuenta");
        configuracion.setUrl("https://demo.baneco.com.bo");
        return configuracion;
    }

    private ConfiguracionQrSolicitud solicitud(String url) {
        ConfiguracionQrSolicitud solicitud = new ConfiguracionQrSolicitud();
        solicitud.setUserName("demo_usuario");
        solicitud.setPassword("demo_password");
        solicitud.setKeyencript("demo_clave");
        solicitud.setAccount("demo_cuenta");
        solicitud.setUrl(url);
        return solicitud;
    }
}
