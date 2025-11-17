package es.rtur.pruebas.recipes.infrastructure.persistence;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio Panache para UtensilioEntity.
 * Proporciona operaciones CRUD y consultas personalizadas para utensilios.
 */
@ApplicationScoped
public class UtensilioRepositoryImpl implements PanacheRepository<UtensilioEntity> {

    /**
     * Busca un utensilio por su nombre exacto.
     * @param nombre Nombre del utensilio
     * @return Optional con el utensilio si existe
     */
    public Optional<UtensilioEntity> findByNombre(String nombre) {
        return find("nombre", nombre).firstResultOptional();
    }

    /**
     * Busca utensilios cuyo nombre contenga el texto dado (case-insensitive).
     * @param nombre Texto a buscar
     * @return Lista de utensilios que coinciden
     */
    public List<UtensilioEntity> searchByNombre(String nombre) {
        return list("LOWER(nombre) LIKE LOWER(?1)", "%" + nombre + "%");
    }

    /**
     * Busca utensilios por tipo.
     * @param tipo Tipo del utensilio (ej: corte, cocción, medición, etc.)
     * @return Lista de utensilios de ese tipo
     */
    public List<UtensilioEntity> findByTipo(String tipo) {
        return list("tipo", tipo);
    }

    /**
     * Obtiene todos los tipos de utensilios únicos.
     * @return Lista de tipos de utensilios
     */
    public List<String> findAllTipos() {
        return find("SELECT DISTINCT tipo FROM UtensilioEntity ORDER BY tipo").project(String.class).list();
    }

    /**
     * Verifica si existe un utensilio con el nombre dado.
     * @param nombre Nombre del utensilio
     * @return true si existe, false en caso contrario
     */
    public boolean existsByNombre(String nombre) {
        return count("nombre", nombre) > 0;
    }
}
