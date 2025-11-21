package es.rtur.pruebas.recipes.application.usecase;

import es.rtur.pruebas.recipes.application.dto.RecetaDTO;
import es.rtur.pruebas.recipes.domain.entity.Receta;
import es.rtur.pruebas.recipes.domain.repository.RecetaRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Use Case for searching recipes by name (RF-07).
 */
@ApplicationScoped
public class SearchRecetasUseCase {

    private final RecetaRepository recetaRepository;

    @Inject
    public SearchRecetasUseCase(RecetaRepository recetaRepository) {
        this.recetaRepository = recetaRepository;
    }

    /**
     * Searches recipes by name.
     * @param searchTerm Search term to look for in recipe names
     * @return List of RecetaDTO matching the search
     */
    public List<RecetaDTO> execute(String searchTerm) {
        if (searchTerm == null || searchTerm.isBlank()) {
            return List.of();
        }

        return recetaRepository.findByNombreContaining(searchTerm).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private RecetaDTO mapToDTO(Receta receta) {
        return RecetaDTO.builder()
                .id(receta.getId() != null ? receta.getId().getValue() : null)
                .nombre(receta.getNombre())
                .tiempo(receta.getTiempo())
                .comensales(receta.getComensales())
                .dificultad(receta.getDificultad())
                .preparacion(receta.getPreparacion())
                .categoria(receta.getCategoria())
                .idAutor(receta.getIdAutor().getValue())
                .fCreacion(receta.getFCreacion())
                .fModificacion(receta.getFModificacion())
                .build();
    }
}
