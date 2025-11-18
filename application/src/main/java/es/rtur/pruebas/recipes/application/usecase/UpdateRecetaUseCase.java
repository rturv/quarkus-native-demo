package es.rtur.pruebas.recipes.application.usecase;

import es.rtur.pruebas.recipes.application.dto.RecetaDTO;
import es.rtur.pruebas.recipes.domain.entity.Receta;
import es.rtur.pruebas.recipes.domain.repository.RecetaRepository;
import es.rtur.pruebas.recipes.domain.valueobject.RecetaId;
import es.rtur.pruebas.recipes.domain.valueobject.UsuarioId;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;

/**
 * Use Case for updating a recipe (RF-02).
 * RN-02: A user can only edit their own recipes (except admin).
 */
@ApplicationScoped
public class UpdateRecetaUseCase {

    private final RecetaRepository recetaRepository;

    @Inject
    public UpdateRecetaUseCase(RecetaRepository recetaRepository) {
        this.recetaRepository = recetaRepository;
    }

    /**
     * Updates an existing recipe.
     * @param id Recipe ID to update
     * @param dto Updated recipe data
     * @param idUsuario ID of the user requesting the update
     * @param isAdmin Whether the user is an admin
     * @return RecetaDTO with updated recipe data
     */
    public RecetaDTO execute(Integer id, @Valid RecetaDTO dto, Integer idUsuario, boolean isAdmin) {
        Receta receta = recetaRepository.findById(RecetaId.of(id))
                .orElseThrow(() -> new IllegalArgumentException("Recipe not found with id: " + id));

        // Check if user can edit this recipe (RN-02)
        if (!receta.canEdit(UsuarioId.of(idUsuario), isAdmin)) {
            throw new SecurityException("User is not authorized to edit this recipe");
        }

        // Update recipe details
        receta.updateDetails(
                dto.getNombre(),
                dto.getTiempo(),
                dto.getComensales(),
                dto.getDificultad(),
                dto.getPreparacion(),
                dto.getCategoria()
        );

        Receta updatedReceta = recetaRepository.save(receta);

        return mapToDTO(updatedReceta);
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
