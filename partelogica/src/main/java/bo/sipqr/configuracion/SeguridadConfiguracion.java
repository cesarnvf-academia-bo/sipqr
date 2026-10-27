package bo.sipqr.configuracion;

import bo.sipqr.seguridad.FiltroAutenticacionJwt;
import bo.sipqr.seguridad.ManejadorAccesoDenegado;
import bo.sipqr.seguridad.ManejadorNoAutorizado;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SeguridadConfiguracion {
    private final FiltroAutenticacionJwt filtroJwt;
    private final ManejadorNoAutorizado manejadorNoAutorizado;
    private final ManejadorAccesoDenegado manejadorAccesoDenegado;

    public SeguridadConfiguracion(FiltroAutenticacionJwt filtroJwt,
                                  ManejadorNoAutorizado manejadorNoAutorizado,
                                  ManejadorAccesoDenegado manejadorAccesoDenegado) {
        this.filtroJwt = filtroJwt;
        this.manejadorNoAutorizado = manejadorNoAutorizado;
        this.manejadorAccesoDenegado = manejadorAccesoDenegado;
    }

    @Bean
    public SecurityFilterChain filtroSeguridad(HttpSecurity httpSeguridad) throws Exception {
        httpSeguridad
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(configuracionCors()))
            .sessionManagement(sesion -> sesion.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(errores -> errores
                .authenticationEntryPoint(manejadorNoAutorizado)
                .accessDeniedHandler(manejadorAccesoDenegado))
            .authorizeHttpRequests(autorizacion -> autorizacion
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/api/acceso", "/api/verificacion/**", "/swagger-ui/**", "/swagger-ui.html",
                    "/api-docs/**", "/v3/api-docs/**", "/error").permitAll()
                .requestMatchers("/api/qrsimple/notifyPaymentQR", "/api/notifyStatus").permitAll()
                .requestMatchers("/api/usuarios/**", "/api/configuracionqr/**", "/api/baneco/**",
                    "/api/pagosqr/**").hasRole("ADMIN")
                .anyRequest().authenticated())
            .addFilterBefore(filtroJwt, UsernamePasswordAuthenticationFilter.class);
        return httpSeguridad.build();
    }

    @Bean
    public CorsConfigurationSource configuracionCors() {
        CorsConfiguration configuracion = new CorsConfiguration();
        configuracion.setAllowedOrigins(List.of("http://localhost:4200", "http://127.0.0.1:4200"));
        configuracion.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuracion.setAllowedHeaders(List.of("*"));
        configuracion.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource fuente = new UrlBasedCorsConfigurationSource();
        fuente.registerCorsConfiguration("/**", configuracion);
        return fuente;
    }

    @Bean
    public PasswordEncoder codificadorContrasena() {
        return new BCryptPasswordEncoder();
    }
}
