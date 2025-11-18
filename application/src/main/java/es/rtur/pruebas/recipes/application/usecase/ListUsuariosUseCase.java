package es.rtur.pruebas.recipes.application.usecase;

import es.rtur.pruebas.recipes.application.dto.UsuarioDTO;
import es.rtur.pruebas.recipes.domain.entity.Usuario;
import es.rtur.pruebas.recipes.domain.repository.UsuarioRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Use Case para listar usuarios (RF-06).
 */
@ApplicationScoped
public class ListUsuariosUseCase {

    private final UsuarioRepository usuarioRepository;

    @Inject
    public ListUsuariosUseCase(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public List<UsuarioDTO> execute() {
        return usuarioRepository.findAllUsuarios().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private UsuarioDTO mapToDTO(Usuario usuario) {
        return new UsuarioDTO(
                usuario.getId() != null ? usuario.getId().getValue() : null,
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getEsAdmin(),
                usuario.getEstado(),
                usuario.getFCreacion(),
                usuario.getFModificacion()
        );
    }
}