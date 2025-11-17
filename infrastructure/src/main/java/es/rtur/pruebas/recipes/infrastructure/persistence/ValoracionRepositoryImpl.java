package es.rtur.pruebas.recipes.infrastructure.persistence;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Repositorio Panache para ValoracionEntity.
 * Proporciona operaciones CRUD y consultas personalizadas para valoraciones (likes/dislikes).
 * Implementa RN-04: Un usuario solo puede dar un like/dislike por receta.
 */
@ApplicationScoped
public class ValoracionRepositoryImpl implements PanacheRepository<ValoracionEntity> {

    @PersistenceContext
    EntityManager em;

    /**
     * Busca la valoración activa de un usuario en una receta específica.
     * @param idReceta ID de la receta
     * @param idUsuario ID del usuario
     * @return Optional con la valoración si existe y está activa
     */
    public Optional<ValoracionEntity> findActivaByRecetaAndUsuario(Integer idReceta, Integer idUsuario) {
        return find("receta.idReceta = ?1 AND usuario.idUsuario = ?2 AND fEliminacion IS NULL", 
                    idReceta, idUsuario)
                .firstResultOptional();
    }

    /**
     * Busca todas las valoraciones activas de una receta.
     * @param idReceta ID de la receta
     * @return Lista de valoraciones activas
     */
    public List<ValoracionEntity> findActivasByReceta(Integer idReceta) {
        return list("receta.idReceta = ?1 AND fEliminacion IS NULL", idReceta);
    }

    /**
     * Busca todas las valoraciones de un usuario.
     * @param idUsuario ID del usuario
     * @return Lista de valoraciones del usuario
     */
    public List<ValoracionEntity> findByUsuario(Integer idUsuario) {
        return list("usuario.idUsuario = ?1 AND fEliminacion IS NULL ORDER BY fCreacion DESC", idUsuario);
    }

    /**
     * Cuenta likes activos de una receta.
     * @param idReceta ID de la receta
     * @return Número de likes
     */
    public long countLikesByReceta(Integer idReceta) {
        return count("receta.idReceta = ?1 AND tipo = 'like' AND fEliminacion IS NULL", idReceta);
    }

    /**
     * Cuenta dislikes activos de una receta.
     * @param idReceta ID de la receta
     * @return Número de dislikes
     */
    public long countDislikesByReceta(Integer idReceta) {
        return count("receta.idReceta = ?1 AND tipo = 'dislike' AND fEliminacion IS NULL", idReceta);
    }

    /**
     * Obtiene estadísticas de valoraciones de una receta.
     * @param idReceta ID de la receta
     * @return Map con "likes" y "dislikes"
     */
    public Map<String, Long> getEstadisticasByReceta(Integer idReceta) {
        long likes = countLikesByReceta(idReceta);
        long dislikes = countDislikesByReceta(idReceta);
        return Map.of("likes", likes, "dislikes", dislikes);
    }

    /**
     * Elimina una valoración (soft delete).
     * @param idValoracion ID de la valoración
     * @return true si se eliminó, false si no existe
     */
    @Transactional
    public boolean eliminarValoracion(Long idValoracion) {
        Optional<ValoracionEntity> valoracion = findByIdOptional(idValoracion);
        if (valoracion.isPresent() && valoracion.get().estaActiva()) {
            valoracion.get().eliminar();
            persist(valoracion.get());
            return true;
        }
        return false;
    }

    /**
     * Cambia el tipo de valoración (de like a dislike o viceversa).
     * @param idValoracion ID de la valoración
     * @param nuevoTipo Nuevo tipo ('like' o 'dislike')
     * @return true si se actualizó, false si no existe
     */
    @Transactional
    public boolean cambiarTipo(Long idValoracion, String nuevoTipo) {
        ValoracionEntity valoracion = findById(idValoracion);
        if (valoracion != null && valoracion.estaActiva()) {
            valoracion.tipo = nuevoTipo;
            persist(valoracion);
            return true;
        }
        return false;
    }

    /**
     * Verifica si un usuario ha valorado una receta.
     * @param idReceta ID de la receta
     * @param idUsuario ID del usuario
     * @return true si existe una valoración activa
     */
    public boolean hasValorado(Integer idReceta, Integer idUsuario) {
        return findActivaByRecetaAndUsuario(idReceta, idUsuario).isPresent();
    }

    /**
     * Obtiene el tipo de valoración de un usuario en una receta.
     * @param idReceta ID de la receta
     * @param idUsuario ID del usuario
     * @return "like", "dislike" o null si no ha valorado
     */
    public String getTipoValoracion(Integer idReceta, Integer idUsuario) {
        return findActivaByRecetaAndUsuario(idReceta, idUsuario)
                .map(v -> v.tipo)
                .orElse(null);
    }

    /**
     * Obtiene las recetas más valoradas (likes - dislikes).
     * @param limit Número máximo de recetas
     * @return Lista de IDs de recetas ordenadas por popularidad
     */
    public List<Integer> getRecetasMasValoradas(int limit) {
        return em.createQuery(
            "SELECT v.receta.idReceta " +
            "FROM ValoracionEntity v " +
            "WHERE v.fEliminacion IS NULL " +
            "GROUP BY v.receta.idReceta " +
            "ORDER BY SUM(CASE WHEN v.tipo = 'like' THEN 1 ELSE -1 END) DESC",
            Integer.class)
            .setMaxResults(limit)
            .getResultList();
    }
}
