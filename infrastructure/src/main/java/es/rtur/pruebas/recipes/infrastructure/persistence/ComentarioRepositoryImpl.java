package es.rtur.pruebas.recipes.infrastructure.persistence;

import es.rtur.pruebas.recipes.domain.entity.Comentario;
import es.rtur.pruebas.recipes.domain.repository.ComentarioRepository;
import es.rtur.pruebas.recipes.domain.valueobject.ComentarioId;
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
 * Repositorio Panache para ComentarioEntity.
 * Implementa ComentarioRepository del dominio.
 * Proporciona operaciones CRUD y consultas personalizadas para comentarios.
 */
@ApplicationScoped
public class ComentarioRepositoryImpl implements PanacheRepository<ComentarioEntity>, ComentarioRepository {

    @PersistenceContext
    EntityManager em;

    /**
     * Busca comentarios de una receta específica.
     * @param idReceta ID de la receta
     * @return Lista de comentarios ordenados por fecha de creación descendente
     */
    public List<ComentarioEntity> findByReceta(Integer idReceta) {
        return list("receta.idReceta = ?1 ORDER BY fCreacion DESC", idReceta);
    }

    /**
     * Busca comentarios activos de una receta específica.
     * @param idReceta ID de la receta
     * @return Lista de comentarios activos ordenados por fecha
     */
    public List<ComentarioEntity> findActivosByReceta(Integer idReceta) {
        return list("receta.idReceta = ?1 AND estado = 'activo' ORDER BY fCreacion DESC", idReceta);
    }

    /**
     * Busca comentarios de un autor específico.
     * @param idAutor ID del autor
     * @return Lista de comentarios del autor ordenados por fecha
     */
    public List<ComentarioEntity> findByAutor(Integer idAutor) {
        return list("autor.idUsuario = ?1 ORDER BY fCreacion DESC", idAutor);
    }

    /**
     * Busca comentarios por estado.
     * @param estado Estado del comentario (activo, eliminado, etc.)
     * @return Lista de comentarios con ese estado
     */
    public List<ComentarioEntity> findByEstado(String estado) {
        return list("estado = ?1 ORDER BY fCreacion DESC", estado);
    }

    /**
     * Cuenta comentarios activos de una receta.
     * @param idReceta ID de la receta
     * @return Número de comentarios activos
     */
    public long countActivosByReceta(Integer idReceta) {
        return count("receta.idReceta = ?1 AND estado = 'activo'", idReceta);
    }

    /**
     * Cambia el estado de un comentario.
     * @param idComentario ID del comentario
     * @param nuevoEstado Nuevo estado
     * @return true si se actualizó, false si no existe
     */
    @Transactional
    public boolean cambiarEstado(Integer idComentario, String nuevoEstado) {
        ComentarioEntity comentario = PanacheRepository.super.findById(idComentario.longValue());
        if (comentario != null) {
            comentario.estado = nuevoEstado;
            persist(comentario);
            return true;
        }
        return false;
    }

    /**
     * Obtiene los comentarios más recientes del sistema.
     * @param limit Número máximo de comentarios
     * @return Lista de comentarios recientes
     */
    public List<ComentarioEntity> findRecientes(int limit) {
        return find("estado = 'activo' ORDER BY fCreacion DESC").page(0, limit).list();
    }

    // Domain Repository Implementation

    @Override
    @Transactional
    public Comentario save(Comentario comentario) {
        ComentarioEntity entity;
        if (comentario.getId() == null) {
            entity = toEntity(comentario);
            persist(entity);
        } else {
            entity = PanacheRepository.super.findById(comentario.getId().getValue().longValue());
            if (entity == null) {
                entity = toEntity(comentario);
                persist(entity);
            } else {
                updateEntity(entity, comentario);
            }
        }
        return toDomain(entity);
    }

    @Override
    public Optional<Comentario> findById(ComentarioId id) {
        ComentarioEntity entity = PanacheRepository.super.findById(id.getValue().longValue());
        return entity != null ? Optional.of(toDomain(entity)) : Optional.empty();
    }

    @Override
    public List<Comentario> findByReceta(RecetaId idReceta) {
        return findByReceta(idReceta.getValue()).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Comentario> findByRecetaAndEstado(RecetaId idReceta, String estado) {
        return list("receta.idReceta = ?1 AND estado = ?2 ORDER BY fCreacion DESC", idReceta.getValue(), estado).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Comentario> findByAutor(UsuarioId idAutor) {
        return findByAutor(idAutor.getValue()).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteById(ComentarioId id) {
        PanacheRepository.super.deleteById(id.getValue().longValue());
    }

    @Override
    public boolean existsById(ComentarioId id) {
        return count("idComentario", id.getValue()) > 0;
    }

    // Mappers

    private Comentario toDomain(ComentarioEntity entity) {
        return new Comentario(
                ComentarioId.of(entity.idComentario),
                RecetaId.of(entity.receta.idReceta),
                UsuarioId.of(entity.autor.idUsuario),
                entity.contenido,
                entity.estado,
                entity.fCreacion,
                entity.fModificacion
        );
    }

    private ComentarioEntity toEntity(Comentario domain) {
        ComentarioEntity entity = new ComentarioEntity();
        if (domain.getId() != null) {
            entity.idComentario = domain.getId().getValue();
        }
        entity.receta = em.find(RecetaEntity.class, domain.getIdReceta().getValue());
        entity.autor = em.find(UsuarioEntity.class, domain.getIdAutor().getValue());
        entity.contenido = domain.getContenido();
        entity.estado = domain.getEstado();
        return entity;
    }

    private void updateEntity(ComentarioEntity entity, Comentario domain) {
        entity.contenido = domain.getContenido();
        entity.estado = domain.getEstado();
    }
}
