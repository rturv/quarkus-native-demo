package es.rtur.pruebas.recipes.domain.repository;

import es.rtur.pruebas.recipes.domain.entity.Ingrediente;
import es.rtur.pruebas.recipes.domain.valueobject.IngredienteId;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Ingrediente entity.
 * RF-06: Admin can manage ingredients.
 */
public interface IngredienteRepository {

    /**
     * Saves a new ingrediente or updates an existing one.
     */
    Ingrediente save(Ingrediente ingrediente);

    /**
     * Finds an ingrediente by ID.
     */
    Optional<Ingrediente> findById(IngredienteId id);

    /**
     * Finds all ingredientes.
     */
    List<Ingrediente> findAll();

    /**
     * Finds ingredientes by type.
     */
    List<Ingrediente> findByTipo(String tipo);

    /**
     * Finds ingredientes by name containing a search term.
     */
    List<Ingrediente> findByNombreContaining(String nombre);

    /**
     * Deletes an ingrediente by ID.
     */
    void deleteById(IngredienteId id);

    /**
     * Checks if an ingrediente exists by ID.
     */
    boolean existsById(IngredienteId id);
}
