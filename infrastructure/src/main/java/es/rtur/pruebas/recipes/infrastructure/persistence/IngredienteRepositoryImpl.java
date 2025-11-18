package es.rtur.pruebas.recipes.infrastructure.persistence;

import es.rtur.pruebas.recipes.domain.entity.Ingrediente;
import es.rtur.pruebas.recipes.domain.repository.IngredienteRepository;
import es.rtur.pruebas.recipes.domain.valueobject.IngredienteId;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Repositorio Panache para IngredienteEntity.
 * Implementa IngredienteRepository del dominio.
 * Proporciona operaciones CRUD y consultas personalizadas para ingredientes.
 */
@ApplicationScoped
public class IngredienteRepositoryImpl implements PanacheRepository<IngredienteEntity>, IngredienteRepository {

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

    // Domain Repository Implementation

    @Override
    @Transactional
    public Ingrediente save(Ingrediente ingrediente) {
        IngredienteEntity entity;
        if (ingrediente.getId() == null) {
            entity = toEntity(ingrediente);
            persist(entity);
        } else {
            entity = findById(ingrediente.getId().getValue().longValue());
            if (entity == null) {
                entity = toEntity(ingrediente);
                persist(entity);
            } else {
                updateEntity(entity, ingrediente);
            }
        }
        return toDomain(entity);
    }

    @Override
    public Optional<Ingrediente> findById(IngredienteId id) {
        IngredienteEntity entity = findById(id.getValue().longValue());
        return entity != null ? Optional.of(toDomain(entity)) : Optional.empty();
    }

    @Override
    public List<Ingrediente> findAll() {
        return listAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Ingrediente> findByTipo(String tipo) {
        return list("tipo", tipo).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Ingrediente> findByNombreContaining(String nombre) {
        return searchByNombre(nombre).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteById(IngredienteId id) {
        deleteById(id.getValue().longValue());
    }

    @Override
    public boolean existsById(IngredienteId id) {
        return count("idIngrediente", id.getValue()) > 0;
    }

    // Mappers

    private Ingrediente toDomain(IngredienteEntity entity) {
        return new Ingrediente(
                IngredienteId.of(entity.idIngrediente),
                entity.nombre,
                entity.tipo,
                entity.fCreacion,
                entity.fModificacion
        );
    }

    private IngredienteEntity toEntity(Ingrediente domain) {
        IngredienteEntity entity = new IngredienteEntity();
        if (domain.getId() != null) {
            entity.idIngrediente = domain.getId().getValue();
        }
        entity.nombre = domain.getNombre();
        entity.tipo = domain.getTipo();
        return entity;
    }

    private void updateEntity(IngredienteEntity entity, Ingrediente domain) {
        entity.nombre = domain.getNombre();
        entity.tipo = domain.getTipo();
    }
}
