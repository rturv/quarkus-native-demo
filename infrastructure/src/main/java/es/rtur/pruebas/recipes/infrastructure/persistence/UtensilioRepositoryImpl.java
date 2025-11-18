package es.rtur.pruebas.recipes.infrastructure.persistence;

import es.rtur.pruebas.recipes.domain.entity.Utensilio;
import es.rtur.pruebas.recipes.domain.repository.UtensilioRepository;
import es.rtur.pruebas.recipes.domain.valueobject.UtensilioId;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Repositorio Panache para UtensilioEntity.
 * Implementa UtensilioRepository del dominio.
 * Proporciona operaciones CRUD y consultas personalizadas para utensilios.
 */
@ApplicationScoped
public class UtensilioRepositoryImpl implements PanacheRepository<UtensilioEntity>, UtensilioRepository {

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
     * Busca utensilios por tipo (internal method for JPA entities).
     * @param tipo Tipo del utensilio (ej: corte, cocción, medición, etc.)
     * @return Lista de utensilios de ese tipo
     */
    private List<UtensilioEntity> findEntitiesByTipo(String tipo) {
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

    // Domain Repository Implementation

    @Override
    @Transactional
    public Utensilio save(Utensilio utensilio) {
        UtensilioEntity entity;
        if (utensilio.getId() == null) {
            entity = toEntity(utensilio);
            persist(entity);
        } else {
            entity = PanacheRepository.super.findById(utensilio.getId().getValue().longValue());
            if (entity == null) {
                entity = toEntity(utensilio);
                persist(entity);
            } else {
                updateEntity(entity, utensilio);
            }
        }
        return toDomain(entity);
    }

    @Override
    public Optional<Utensilio> findById(UtensilioId id) {
        UtensilioEntity entity = PanacheRepository.super.findById(id.getValue().longValue());
        return entity != null ? Optional.of(toDomain(entity)) : Optional.empty();
    }

    @Override
    public List<Utensilio> findAllUtensilios() {
        return listAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Utensilio> findByTipo(String tipo) {
        return findEntitiesByTipo(tipo).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Utensilio> findByNombreContaining(String nombre) {
        return searchByNombre(nombre).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteById(UtensilioId id) {
        PanacheRepository.super.deleteById(id.getValue().longValue());
    }

    @Override
    public boolean existsById(UtensilioId id) {
        return count("idUtensilio", id.getValue()) > 0;
    }

    // Mappers

    private Utensilio toDomain(UtensilioEntity entity) {
        return new Utensilio(
                UtensilioId.of(entity.idUtensilio),
                entity.nombre,
                entity.tipo,
                entity.fCreacion,
                entity.fModificacion
        );
    }

    private UtensilioEntity toEntity(Utensilio domain) {
        UtensilioEntity entity = new UtensilioEntity();
        if (domain.getId() != null) {
            entity.idUtensilio = domain.getId().getValue();
        }
        entity.nombre = domain.getNombre();
        entity.tipo = domain.getTipo();
        return entity;
    }

    private void updateEntity(UtensilioEntity entity, Utensilio domain) {
        entity.nombre = domain.getNombre();
        entity.tipo = domain.getTipo();
    }
}
