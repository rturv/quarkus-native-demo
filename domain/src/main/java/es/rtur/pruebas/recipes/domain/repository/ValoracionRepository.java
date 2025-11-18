package es.rtur.pruebas.recipes.domain.repository;

import es.rtur.pruebas.recipes.domain.entity.Valoracion;
import es.rtur.pruebas.recipes.domain.valueobject.ValoracionId;
import es.rtur.pruebas.recipes.domain.valueobject.RecetaId;
import es.rtur.pruebas.recipes.domain.valueobject.UsuarioId;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Valoracion entity.
 * RF-04: Users can rate recipes with likes/dislikes.
 * RN-04: A user can only give one like/dislike per recipe.
 */
public interface ValoracionRepository {

    /**
     * Saves a new valoracion or updates an existing one.
     */
    Valoracion save(Valoracion valoracion);

    /**
     * Finds a valoracion by ID.
     */
    Optional<Valoracion> findById(ValoracionId id);

    /**
     * Finds a valoracion by recipe and user.
     * RN-04: Used to check if user already rated a recipe.
     */
    Optional<Valoracion> findByRecetaAndUsuario(RecetaId idReceta, UsuarioId idUsuario);

    /**
     * Finds all valoraciones for a recipe.
     * RF-04: Get all ratings for a recipe.
     */
    List<Valoracion> findByReceta(RecetaId idReceta);

    /**
     * Finds active valoraciones for a recipe.
     */
    List<Valoracion> findActiveByReceta(RecetaId idReceta);

    /**
     * Counts likes for a recipe.
     */
    long countLikesByReceta(RecetaId idReceta);

    /**
     * Counts dislikes for a recipe.
     */
    long countDislikesByReceta(RecetaId idReceta);

    /**
     * Deletes a valoracion by ID.
     */
    void deleteById(ValoracionId id);

    /**
     * Checks if a valoracion exists by ID.
     */
    boolean existsById(ValoracionId id);
}
