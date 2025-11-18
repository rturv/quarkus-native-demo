package es.rtur.pruebas.recipes.application.usecase;

import es.rtur.pruebas.recipes.domain.entity.Receta;
import es.rtur.pruebas.recipes.domain.repository.RecetaRepository;
import es.rtur.pruebas.recipes.domain.valueobject.RecetaId;
import es.rtur.pruebas.recipes.domain.valueobject.UsuarioId;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * Use Case for deleting a recipe (RF-02).
 * RN-02: A user can only delete their own recipes (except admin).
 */
@ApplicationScoped
public class DeleteRecetaUseCase {

    private final RecetaRepository recetaRepository;

    @Inject
    public DeleteRecetaUseCase(RecetaRepository recetaRepository) {
        this.recetaRepository = recetaRepository;
    }

    /**
     * Deletes a recipe.
     * @param id Recipe ID to delete
     * @param idUsuario ID of the user requesting the deletion
     * @param isAdmin Whether the user is an admin
     */
    public void execute(Integer id, Integer idUsuario, boolean isAdmin) {
        Receta receta = recetaRepository.findById(RecetaId.of(id))
                .orElseThrow(() -> new IllegalArgumentException("Recipe not found with id: " + id));

        // Check if user can delete this recipe (RN-02)
        if (!receta.canDelete(UsuarioId.of(idUsuario), isAdmin)) {
            throw new SecurityException("User is not authorized to delete this recipe");
        }

        recetaRepository.deleteById(RecetaId.of(id));
    }
}
