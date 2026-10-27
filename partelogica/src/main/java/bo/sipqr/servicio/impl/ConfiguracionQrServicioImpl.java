package bo.sipqr.servicio.impl;

import bo.sipqr.dto.ConfiguracionQrRespuesta;
import bo.sipqr.dto.ConfiguracionQrSolicitud;
import bo.sipqr.modelo.ConfiguracionQr;
import bo.sipqr.repositorio.ConfiguracionQrRepositorio;
import bo.sipqr.seguridad.ValidadorUrlServicio;
import bo.sipqr.servicio.ConfiguracionQrServicio;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ConfiguracionQrServicioImpl implements ConfiguracionQrServicio {
    private final ConfiguracionQrRepositorio configuracionQrRepositorio;
    private final ValidadorUrlServicio validadorUrlServicio;

    public ConfiguracionQrServicioImpl(ConfiguracionQrRepositorio configuracionQrRepositorio,
                                       ValidadorUrlServicio validadorUrlServicio) {
        this.configuracionQrRepositorio = configuracionQrRepositorio;
        this.validadorUrlServicio = validadorUrlServicio;
    }

    @Override
    @Transactional(readOnly = true)
    public ConfiguracionQrRespuesta obtener() {
        return construirRespuesta(obtenerConfiguracion());
    }

    @Override
    @Transactional
    public ConfiguracionQrRespuesta actualizar(ConfiguracionQrSolicitud solicitud) {
        ConfiguracionQr configuracion = obtenerConfiguracion();
        String userName = solicitud.getUserName() == null ? "" : solicitud.getUserName().trim();
        if (userName.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El usuario bancario es obligatorio");
        }
        String url = solicitud.getUrl() == null ? "" : solicitud.getUrl().trim();
        if (url.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La URL es obligatoria");
        }
        try {
            validadorUrlServicio.validar(url);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }

        configuracion.setUserName(userName);
        configuracion.setUrl(url);
        configuracion.setPassword(requerido(solicitud.getPassword(), "La contraseña"));
        configuracion.setKeyencript(requerido(solicitud.getKeyencript(), "La clave de encriptación"));
        configuracion.setAccount(requerido(solicitud.getAccount(), "La cuenta"));

        configuracionQrRepositorio.actualizar(configuracion);
        return construirRespuesta(configuracion);
    }

    private ConfiguracionQr obtenerConfiguracion() {
        return configuracionQrRepositorio.obtener()
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Configuración QR no encontrada"));
    }

    private String requerido(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, campo + " es obligatorio");
        }
        return valor;
    }

    private ConfiguracionQrRespuesta construirRespuesta(ConfiguracionQr configuracion) {
        ConfiguracionQrRespuesta respuesta = new ConfiguracionQrRespuesta();
        respuesta.setUserName(configuracion.getUserName());
        respuesta.setUrl(configuracion.getUrl());
        respuesta.setPassword(configuracion.getPassword());
        respuesta.setKeyencript(configuracion.getKeyencript());
        respuesta.setAccount(configuracion.getAccount());
        respuesta.setFechaActualizacion(configuracion.getFechaActualizacion());
        return respuesta;
    }
}
