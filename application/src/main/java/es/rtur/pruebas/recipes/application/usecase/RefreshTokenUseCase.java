package es.rtur.pruebas.recipes.application.usecase;

import es.rtur.pruebas.recipes.application.dto.UsuarioDTO;
import es.rtur.pruebas.recipes.domain.entity.Usuario;
import es.rtur.pruebas.recipes.domain.repository.UsuarioRepository;
import es.rtur.pruebas.recipes.domain.valueobject.UsuarioId;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * Use case para renovar el JWT de un usuario autenticado.
 */
@ApplicationScoped
public class RefreshTokenUseCase {

    private final UsuarioRepository usuarioRepository;

    @Inject
    public RefreshTokenUseCase(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public UsuarioDTO execute(Integer usuarioId) {
        if (usuarioId == null) {
            throw new IllegalArgumentException("Se requiere un usuario autenticado");
        }

        return usuarioRepository.findById(UsuarioId.of(usuarioId))
                .map(this::mapToDTO)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
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