package es.rtur.pruebas.recipes.infrastructure.security;

import es.rtur.pruebas.recipes.application.dto.UsuarioDTO;
import io.smallrye.jwt.build.Jwt;
import io.smallrye.jwt.build.JwtClaimsBuilder;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@ApplicationScoped
public class JwtTokenService {

    private final int expirationMinutes;

    @Inject
    public JwtTokenService(@ConfigProperty(name = "app.security.jwt.expiration-minutes", defaultValue = "30") int expirationMinutes) {
        if (expirationMinutes <= 0) {
            throw new IllegalArgumentException("La ventana de expiración debe ser mayor que cero");
        }
        this.expirationMinutes = expirationMinutes;
    }

    public JwtToken generateToken(UsuarioDTO usuario) {
        if (usuario.getId() == null) {
            throw new IllegalStateException("No se puede generar un token para un usuario sin id");
        }

        Instant now = Instant.now();
        Instant expiresAt = now.plus(Duration.ofMinutes(expirationMinutes));

        Set<String> roles = new HashSet<>();
        roles.add("user");
        if (Boolean.TRUE.equals(usuario.getEsAdmin())) {
            roles.add("admin");
        }

        JwtClaimsBuilder claims = Jwt.claims();
        claims.issuer("quarkus-recipes");
        claims.subject(String.valueOf(usuario.getId()));
        claims.issuedAt(now.getEpochSecond());
        claims.expiresAt(expiresAt.getEpochSecond());
        claims.groups(roles);
        claims.claim("email", usuario.getEmail());
        claims.claim("name", usuario.getNombre());

        return new JwtToken(claims.sign(), now, expiresAt);
    }

    public static record JwtToken(String token, Instant issuedAt, Instant expiresAt) {
    }
}