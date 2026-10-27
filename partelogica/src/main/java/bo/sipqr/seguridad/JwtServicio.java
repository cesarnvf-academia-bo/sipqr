package bo.sipqr.seguridad;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtServicio {
    private final SecretKey clave;
    private final long expiracionMs;

    public JwtServicio(@Value("${jwt.secreto:}") String secreto,
                       @Value("${jwt.expiracion:3600000}") long expiracionMs) {
        if (secreto == null || secreto.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalStateException("JWT_SECRETO debe tener al menos 32 bytes");
        }
        this.clave = Keys.hmacShaKeyFor(secreto.getBytes(StandardCharsets.UTF_8));
        this.expiracionMs = expiracionMs;
    }

    public String generarToken(String usuario, String rol) {
        Date ahora = new Date();
        Date expiracion = new Date(ahora.getTime() + expiracionMs);
        return Jwts.builder()
            .subject(usuario)
            .claim("rol", rol)
            .issuedAt(ahora)
            .expiration(expiracion)
            .signWith(clave)
            .compact();
    }

    public Optional<Claims> validarToken(String token) {
        try {
            Claims claims = Jwts.parser().verifyWith(clave).build().parseSignedClaims(token).getPayload();
            return Optional.of(claims);
        } catch (Exception ex) {
            return Optional.empty();
        }
    }

    public long getExpiracionMs() {
        return expiracionMs;
    }
}
