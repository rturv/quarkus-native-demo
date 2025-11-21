package es.rtur.pruebas.recipes.application.usecase;

import es.rtur.pruebas.recipes.application.dto.UsuarioDTO;
import es.rtur.pruebas.recipes.domain.entity.Usuario;
import es.rtur.pruebas.recipes.domain.repository.UsuarioRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Optional;

/**
 * Use Case for user login (RF-01).
 * Validates credentials and returns user data if successful.
 */
@ApplicationScoped
public class LoginUserUseCase {

    private final UsuarioRepository usuarioRepository;

    @Inject
    public LoginUserUseCase(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Authenticates a user with email and password.
     * @param email User email
     * @param password User password (plain text - should be hashed in production)
     * @return UsuarioDTO if authentication successful
     * @throws IllegalArgumentException if authentication fails
     */
    public UsuarioDTO execute(String email, String password) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);

        if (usuarioOpt.isEmpty()) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        Usuario usuario = usuarioOpt.get();

        // Check if user is active
        if (!usuario.isActive()) {
            throw new IllegalArgumentException("User account is inactive");
        }

        // TODO: Implement proper password hashing and comparison
        // For now, comparing plain text (NOT PRODUCTION READY)
        if (!usuario.getClaveAcceso().equals(password)) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        return mapToDTO(usuario);
    }

    private UsuarioDTO mapToDTO(Usuario usuario) {
        return UsuarioDTO.builder()
                .id(usuario.getId() != null ? usuario.getId().getValue() : null)
                .nombre(usuario.getNombre())
                .email(usuario.getEmail())
                .esAdmin(usuario.getEsAdmin())
                .estado(usuario.getEstado())
                .fCreacion(usuario.getFCreacion())
                .fModificacion(usuario.getFModificacion())
                .build();
    }
}
