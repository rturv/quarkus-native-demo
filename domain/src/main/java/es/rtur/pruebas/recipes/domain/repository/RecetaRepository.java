package es.rtur.pruebas.recipes.domain.repository;

import es.rtur.pruebas.recipes.domain.entity.Receta;
import es.rtur.pruebas.recipes.domain.valueobject.RecetaId;
import es.rtur.pruebas.recipes.domain.valueobject.UsuarioId;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Receta entity.
 * Defines the contract for data access operations.
 */
public interface RecetaRepository {

    /**
     * Saves a new receta or updates an existing one.
     * RF-02: Create and update recipes.
     */
    Receta save(Receta receta);

    /**
     * Finds a receta by ID.
     * RF-02: Get recipe details.
     */
    Optional<Receta> findById(RecetaId id);

    /**
     * Finds all recetas.
     * RF-02: List all recipes.
     * Note: Named findAllRecetas() to avoid conflict with Panache's findAll()
     */
    List<Receta> findAllRecetas();

    /**
     * Finds recetas by author.
     * RF-02: List user's own recipes.
     */
    List<Receta> findByAutor(UsuarioId idAutor);

    /**
     * Finds recetas by name containing a search term.
     * RF-07: Search recipes.
     */
    List<Receta> findByNombreContaining(String nombre);

    /**
     * Finds recetas by difficulty.
     * RF-07: Filter by difficulty.
     */
    List<Receta> findByDificultad(String dificultad);

    /**
     * Finds recetas by category.
     * RF-07: Filter by category.
     */
    List<Receta> findByCategoria(String categoria);

    /**
     * Deletes a receta by ID.
     * RF-02: Delete recipe.
     */
    void deleteById(RecetaId id);

    /**
     * Checks if a receta exists by ID.
     */
    boolean existsById(RecetaId id);
}
