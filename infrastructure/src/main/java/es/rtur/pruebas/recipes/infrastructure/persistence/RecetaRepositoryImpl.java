package es.rtur.pruebas.recipes.infrastructure.persistence;

import es.rtur.pruebas.recipes.domain.entity.Receta;
import es.rtur.pruebas.recipes.domain.repository.RecetaRepository;
import es.rtur.pruebas.recipes.domain.valueobject.RecetaId;
import es.rtur.pruebas.recipes.domain.valueobject.UsuarioId;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Repositorio Panache para RecetaEntity.
 * Implementa RecetaRepository del dominio.
 * Proporciona mapeo entre entidades JPA y entidades de dominio.
 */
@ApplicationScoped
public class RecetaRepositoryImpl implements PanacheRepository<RecetaEntity>, RecetaRepository {

    @PersistenceContext
    EntityManager em;

    /**
     * Busca recetas por autor.
     * @param idAutor ID del autor
     * @return Lista de recetas del autor
     */
    public List<RecetaEntity> findByAutor(Integer idAutor) {
        return list("autor.idUsuario", idAutor);
    }

    /**
     * Busca recetas cuyo nombre contenga el texto dado (case-insensitive).
     * @param nombre Texto a buscar
     * @return Lista de recetas que coinciden
     */
    public List<RecetaEntity> searchByNombre(String nombre) {
        return list("LOWER(nombre) LIKE LOWER(?1)", "%" + nombre + "%");
    }

    /**
     * Busca recetas por dificultad (internal method for JPA entities).
     * @param dificultad Nivel de dificultad
     * @return Lista de recetas con esa dificultad
     */
    private List<RecetaEntity> findEntitiesByDificultad(String dificultad) {
        return list("dificultad", dificultad);
    }

    /**
     * Busca recetas por categoría (internal method for JPA entities).
     * @param categoria Categoría de la receta
     * @return Lista de recetas de esa categoría
     */
    private List<RecetaEntity> findEntitiesByCategoria(String categoria) {
        return list("categoria", categoria);
    }

    /**
     * Busca recetas que contengan un ingrediente específico.
     * @param nombreIngrediente Nombre del ingrediente
     * @return Lista de recetas que contienen ese ingrediente
     */
    public List<RecetaEntity> findByIngrediente(String nombreIngrediente) {
        return em.createQuery(
            "SELECT DISTINCT r FROM RecetaEntity r " +
            "JOIN r.ingredientesReceta ir " +
            "JOIN ir.ingrediente i " +
            "WHERE LOWER(i.nombre) LIKE LOWER(:nombre)", 
            RecetaEntity.class)
            .setParameter("nombre", "%" + nombreIngrediente + "%")
            .getResultList();
    }

    /**
     * Busca recetas con tiempo de preparación menor o igual al especificado.
     * @param tiempoMaximo Tiempo máximo en minutos
     * @return Lista de recetas rápidas
     */
    public List<RecetaEntity> findByTiempoMenorIgualQue(Integer tiempoMaximo) {
        return list("tiempo <= ?1", tiempoMaximo);
    }

    /**
     * Busca recetas para un número específico de comensales (con rango ±2).
     * @param comensales Número de comensales
     * @return Lista de recetas adecuadas
     */
    public List<RecetaEntity> findByComensales(Integer comensales) {
        return list("comensales BETWEEN ?1 AND ?2", comensales - 2, comensales + 2);
    }

    /**
     * Obtiene las recetas más recientes.
     * @param limit Número máximo de recetas a retornar
     * @return Lista de recetas ordenadas por fecha de creación descendente
     */
    public List<RecetaEntity> findRecientes(int limit) {
        return find("ORDER BY fCreacion DESC").page(0, limit).list();
    }

    /**
     * Busca recetas con filtros combinados.
     * @param dificultad Dificultad (opcional)
     * @param categoria Categoría (opcional)
     * @param tiempoMaximo Tiempo máximo (opcional)
     * @return Lista de recetas que cumplen los criterios
     */
    public List<RecetaEntity> buscarConFiltros(String dificultad, String categoria, Integer tiempoMaximo) {
        StringBuilder query = new StringBuilder("1=1");
        
        if (dificultad != null && !dificultad.isEmpty()) {
            query.append(" AND dificultad = ?1");
        }
        if (categoria != null && !categoria.isEmpty()) {
            query.append(" AND categoria = ?").append(dificultad != null && !dificultad.isEmpty() ? "2" : "1");
        }
        if (tiempoMaximo != null) {
            int paramIndex = 1;
            if (dificultad != null && !dificultad.isEmpty()) paramIndex++;
            if (categoria != null && !categoria.isEmpty()) paramIndex++;
            query.append(" AND tiempo <= ?").append(paramIndex);
        }
        
        // Construir lista de parámetros
        if (dificultad != null && !dificultad.isEmpty() && categoria != null && !categoria.isEmpty() && tiempoMaximo != null) {
            return list(query.toString(), dificultad, categoria, tiempoMaximo);
        } else if (dificultad != null && !dificultad.isEmpty() && categoria != null && !categoria.isEmpty()) {
            return list(query.toString(), dificultad, categoria);
        } else if (dificultad != null && !dificultad.isEmpty() && tiempoMaximo != null) {
            return list(query.toString(), dificultad, tiempoMaximo);
        } else if (categoria != null && !categoria.isEmpty() && tiempoMaximo != null) {
            return list(query.toString(), categoria, tiempoMaximo);
        } else if (dificultad != null && !dificultad.isEmpty()) {
            return list(query.toString(), dificultad);
        } else if (categoria != null && !categoria.isEmpty()) {
            return list(query.toString(), categoria);
        } else if (tiempoMaximo != null) {
            return list(query.toString(), tiempoMaximo);
        } else {
            return listAll();
        }
    }

    // Domain Repository Implementation

    @Override
    @Transactional
    public Receta save(Receta receta) {
        RecetaEntity entity;
        if (receta.getId() == null) {
            // New entity
            entity = toEntity(receta);
            persist(entity);
        } else {
            // Update existing
            entity = PanacheRepository.super.findById(receta.getId().getValue().longValue());
            if (entity == null) {
                entity = toEntity(receta);
                persist(entity);
            } else {
                updateEntity(entity, receta);
            }
        }
        return toDomain(entity);
    }

    @Override
    public Optional<Receta> findById(RecetaId id) {
        RecetaEntity entity = PanacheRepository.super.findById(id.getValue().longValue());
        return entity != null ? Optional.of(toDomain(entity)) : Optional.empty();
    }

    @Override
    public List<Receta> findAllRecetas() {
        return listAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Receta> findByAutor(UsuarioId idAutor) {
        return findByAutor(idAutor.getValue()).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Receta> findByNombreContaining(String nombre) {
        return searchByNombre(nombre).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Receta> findByDificultad(String dificultad) {
        return findEntitiesByDificultad(dificultad).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Receta> findByCategoria(String categoria) {
        return findEntitiesByCategoria(categoria).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteById(RecetaId id) {
        PanacheRepository.super.deleteById(id.getValue().longValue());
    }

    @Override
    public boolean existsById(RecetaId id) {
        return count("idReceta", id.getValue()) > 0;
    }

    // Mappers

    private Receta toDomain(RecetaEntity entity) {
        return new Receta(
                RecetaId.of(entity.idReceta),
                entity.nombre,
                entity.tiempo,
                entity.comensales,
                entity.dificultad,
                entity.preparacion,
                entity.categoria,
                UsuarioId.of(entity.autor.idUsuario),
                entity.fCreacion,
                entity.fModificacion
        );
    }

    private RecetaEntity toEntity(Receta domain) {
        RecetaEntity entity = new RecetaEntity();
        if (domain.getId() != null) {
            entity.idReceta = domain.getId().getValue();
        }
        entity.nombre = domain.getNombre();
        entity.tiempo = domain.getTiempo();
        entity.comensales = domain.getComensales();
        entity.dificultad = domain.getDificultad();
        entity.preparacion = domain.getPreparacion();
        entity.categoria = domain.getCategoria();
        
        // Load author from database
        UsuarioEntity autor = em.find(UsuarioEntity.class, domain.getIdAutor().getValue());
        entity.autor = autor;
        
        return entity;
    }

    private void updateEntity(RecetaEntity entity, Receta domain) {
        entity.nombre = domain.getNombre();
        entity.tiempo = domain.getTiempo();
        entity.comensales = domain.getComensales();
        entity.dificultad = domain.getDificultad();
        entity.preparacion = domain.getPreparacion();
        entity.categoria = domain.getCategoria();
    }
}
