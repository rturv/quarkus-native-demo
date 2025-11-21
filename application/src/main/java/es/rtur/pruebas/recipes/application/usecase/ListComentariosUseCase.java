package es.rtur.pruebas.recipes.application.usecase;

import es.rtur.pruebas.recipes.application.dto.ComentarioDTO;
import es.rtur.pruebas.recipes.domain.entity.Comentario;
import es.rtur.pruebas.recipes.domain.repository.ComentarioRepository;
import es.rtur.pruebas.recipes.domain.valueobject.RecetaId;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Use Case for listing comments on a recipe (RF-03).
 */
@ApplicationScoped
public class ListComentariosUseCase {

    private final ComentarioRepository comentarioRepository;

    @Inject
    public ListComentariosUseCase(ComentarioRepository comentarioRepository) {
        this.comentarioRepository = comentarioRepository;
    }

    /**
     * Lists all active comments for a recipe.
     * @param idReceta Recipe ID
     * @return List of ComentarioDTO
     */
    public List<ComentarioDTO> execute(Integer idReceta) {
        return comentarioRepository.findByRecetaAndEstado(RecetaId.of(idReceta), "activo").stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private ComentarioDTO mapToDTO(Comentario comentario) {
        return ComentarioDTO.builder()
                .id(comentario.getId() != null ? comentario.getId().getValue() : null)
                .idReceta(comentario.getIdReceta().getValue())
                .idAutor(comentario.getIdAutor().getValue())
                .contenido(comentario.getContenido())
                .estado(comentario.getEstado())
                .fCreacion(comentario.getFCreacion())
                .fModificacion(comentario.getFModificacion())
                .build();
    }
}
