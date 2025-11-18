package es.rtur.pruebas.recipes.domain.repository;

import es.rtur.pruebas.recipes.domain.entity.Comentario;
import es.rtur.pruebas.recipes.domain.valueobject.ComentarioId;
import es.rtur.pruebas.recipes.domain.valueobject.RecetaId;
import es.rtur.pruebas.recipes.domain.valueobject.UsuarioId;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Comentario entity.
 * RF-03: Users can comment on recipes.
 */
public interface ComentarioRepository {

    /**
     * Saves a new comentario.
     */
    Comentario save(Comentario comentario);

    /**
     * Finds a comentario by ID.
     */
    Optional<Comentario> findById(ComentarioId id);

    /**
     * Finds all comentarios for a recipe.
     * RF-03: List comments on a recipe.
     */
    List<Comentario> findByReceta(RecetaId idReceta);

    /**
     * Finds active comentarios for a recipe.
     */
    List<Comentario> findByRecetaAndEstado(RecetaId idReceta, String estado);

    /**
     * Finds comentarios by author.
     */
    List<Comentario> findByAutor(UsuarioId idAutor);

    /**
     * Deletes a comentario by ID.
     */
    void deleteById(ComentarioId id);

    /**
     * Checks if a comentario exists by ID.
     */
    boolean existsById(ComentarioId id);
}
