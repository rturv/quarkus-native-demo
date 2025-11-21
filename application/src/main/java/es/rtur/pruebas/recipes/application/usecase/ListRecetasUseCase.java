package es.rtur.pruebas.recipes.application.usecase;

import es.rtur.pruebas.recipes.application.dto.RecetaDTO;
import es.rtur.pruebas.recipes.domain.entity.Receta;
import es.rtur.pruebas.recipes.domain.repository.RecetaRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Use Case for listing all recipes (RF-02).
 */
@ApplicationScoped
public class ListRecetasUseCase {

    private final RecetaRepository recetaRepository;

    @Inject
    public ListRecetasUseCase(RecetaRepository recetaRepository) {
        this.recetaRepository = recetaRepository;
    }

    /**
     * Lists all recipes.
     * @return List of RecetaDTO
     */
    public List<RecetaDTO> execute() {
        return recetaRepository.findAllRecetas().stream()
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
