package es.rtur.pruebas.recipes.application.usecase;

import es.rtur.pruebas.recipes.application.dto.RecetaDTO;
import es.rtur.pruebas.recipes.domain.entity.Receta;
import es.rtur.pruebas.recipes.domain.repository.RecetaRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Use Case for filtering recipes by difficulty or category (RF-07).
 */
@ApplicationScoped
public class FilterRecetasUseCase {

    private final RecetaRepository recetaRepository;

    @Inject
    public FilterRecetasUseCase(RecetaRepository recetaRepository) {
        this.recetaRepository = recetaRepository;
    }

    /**
     * Filters recipes by difficulty.
     * @param dificultad Difficulty level to filter by
     * @return List of RecetaDTO matching the difficulty
     */
    public List<RecetaDTO> executeByDificultad(String dificultad) {
        if (dificultad == null || dificultad.isBlank()) {
            return List.of();
        }

        return recetaRepository.findByDificultad(dificultad).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Filters recipes by category.
     * @param categoria Category to filter by
     * @return List of RecetaDTO matching the category
     */
    public List<RecetaDTO> executeByCategoria(String categoria) {
        if (categoria == null || categoria.isBlank()) {
            return List.of();
        }

        return recetaRepository.findByCategoria(categoria).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private RecetaDTO mapToDTO(Receta receta) {
        return new RecetaDTO(
                receta.getId() != null ? receta.getId().getValue() : null,
                receta.getNombre(),
                receta.getTiempo(),
                receta.getComensales(),
                receta.getDificultad(),
                receta.getPreparacion(),
                receta.getCategoria(),
                receta.getIdAutor().getValue(),
                receta.getFCreacion(),
                receta.getFModificacion()
        );
    }
}
