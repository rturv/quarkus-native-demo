package es.rtur.pruebas.recipes.infrastructure.persistence;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;

/**
 * Repositorio Panache para ComentarioEntity.
 * Proporciona operaciones CRUD y consultas personalizadas para comentarios.
 */
@ApplicationScoped
public class ComentarioRepositoryImpl implements PanacheRepository<ComentarioEntity> {

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
        ComentarioEntity comentario = findById(idComentario.longValue());
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
}
