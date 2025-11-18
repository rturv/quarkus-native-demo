package es.rtur.pruebas.recipes.domain.repository;

import es.rtur.pruebas.recipes.domain.entity.Utensilio;
import es.rtur.pruebas.recipes.domain.valueobject.UtensilioId;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Utensilio entity.
 * RF-06: Admin can manage utensils.
 */
public interface UtensilioRepository {

    /**
     * Saves a new utensilio or updates an existing one.
     */
    Utensilio save(Utensilio utensilio);

    /**
     * Finds a utensilio by ID.
     */
    Optional<Utensilio> findById(UtensilioId id);

    /**
     * Finds all utensilios.
     * Note: Named findAllUtensilios() to avoid conflict with Panache's findAll()
     */
    List<Utensilio> findAllUtensilios();

    /**
     * Finds utensilios by type.
     */
    List<Utensilio> findByTipo(String tipo);

    /**
     * Finds utensilios by name containing a search term.
     */
    List<Utensilio> findByNombreContaining(String nombre);

    /**
     * Deletes a utensilio by ID.
     */
    void deleteById(UtensilioId id);

    /**
     * Checks if a utensilio exists by ID.
     */
    boolean existsById(UtensilioId id);
}
