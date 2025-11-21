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