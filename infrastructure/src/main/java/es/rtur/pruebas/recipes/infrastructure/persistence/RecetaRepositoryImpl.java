package es.rtur.pruebas.recipes.infrastructure.persistence;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

/**
 * Repositorio Panache para RecetaEntity.
 * Proporciona operaciones CRUD y consultas personalizadas para recetas.
 */
@ApplicationScoped
public class RecetaRepositoryImpl implements PanacheRepository<RecetaEntity> {

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
     * Busca recetas por dificultad.
     * @param dificultad Nivel de dificultad
     * @return Lista de recetas con esa dificultad
     */
    public List<RecetaEntity> findByDificultad(String dificultad) {
        return list("dificultad", dificultad);
    }

    /**
     * Busca recetas por categoría.
     * @param categoria Categoría de la receta
     * @return Lista de recetas de esa categoría
     */
    public List<RecetaEntity> findByCategoria(String categoria) {
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
}
