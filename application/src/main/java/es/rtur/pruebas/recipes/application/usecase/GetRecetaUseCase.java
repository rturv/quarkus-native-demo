package es.rtur.pruebas.recipes.application.usecase;

import es.rtur.pruebas.recipes.application.dto.RecetaDTO;
import es.rtur.pruebas.recipes.domain.entity.Receta;
import es.rtur.pruebas.recipes.domain.repository.RecetaRepository;
import es.rtur.pruebas.recipes.domain.valueobject.RecetaId;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * Use Case for getting a recipe by ID (RF-02).
 */
@ApplicationScoped
public class GetRecetaUseCase {

    private final RecetaRepository recetaRepository;

    @Inject
    public GetRecetaUseCase(RecetaRepository recetaRepository) {
        this.recetaRepository = recetaRepository;
    }

    /**
     * Gets a recipe by ID.
     * @param id Recipe ID
     * @return RecetaDTO with recipe data
     */
    public RecetaDTO execute(Integer id) {
        Receta receta = recetaRepository.findById(RecetaId.of(id))
                .orElseThrow(() -> new IllegalArgumentException("Recipe not found with id: " + id));

        return mapToDTO(receta);
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
