package es.rtur.pruebas.recipes.infrastructure.persistence;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio Panache para IngredienteEntity.
 * Proporciona operaciones CRUD y consultas personalizadas para ingredientes.
 */
@ApplicationScoped
public class IngredienteRepositoryImpl implements PanacheRepository<IngredienteEntity> {

    /**
     * Busca un ingrediente por su nombre exacto.
     * @param nombre Nombre del ingrediente
     * @return Optional con el ingrediente si existe
     */
    public Optional<IngredienteEntity> findByNombre(String nombre) {
        return find("nombre", nombre).firstResultOptional();
    }

    /**
     * Busca ingredientes cuyo nombre contenga el texto dado (case-insensitive).
     * @param nombre Texto a buscar
     * @return Lista de ingredientes que coinciden
     */
    public List<IngredienteEntity> searchByNombre(String nombre) {
        return list("LOWER(nombre) LIKE LOWER(?1)", "%" + nombre + "%");
    }

    /**
     * Busca ingredientes por tipo.
     * @param tipo Tipo del ingrediente (ej: verdura, carne, lácteo, etc.)
     * @return Lista de ingredientes de ese tipo
     */
    public List<IngredienteEntity> findByTipo(String tipo) {
        return list("tipo", tipo);
    }

    /**
     * Obtiene todos los tipos de ingredientes únicos.
     * @return Lista de tipos de ingredientes
     */
    public List<String> findAllTipos() {
        return find("SELECT DISTINCT tipo FROM IngredienteEntity ORDER BY tipo").project(String.class).list();
    }

    /**
     * Verifica si existe un ingrediente con el nombre dado.
     * @param nombre Nombre del ingrediente
     * @return true si existe, false en caso contrario
     */
    public boolean existsByNombre(String nombre) {
        return count("nombre", nombre) > 0;
    }
}
