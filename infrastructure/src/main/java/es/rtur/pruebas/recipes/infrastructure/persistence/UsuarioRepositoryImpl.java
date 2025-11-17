package es.rtur.pruebas.recipes.infrastructure.persistence;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio Panache para UsuarioEntity.
 * Proporciona operaciones CRUD y consultas personalizadas para usuarios.
 */
@ApplicationScoped
public class UsuarioRepositoryImpl implements PanacheRepository<UsuarioEntity> {

    /**
     * Busca un usuario por su email.
     * @param email El email del usuario
     * @return Optional con el usuario si existe
     */
    public Optional<UsuarioEntity> findByEmail(String email) {
        return find("email", email).firstResultOptional();
    }

    /**
     * Busca usuarios por su estado.
     * @param estado El estado del usuario (activo, inactivo, etc.)
     * @return Lista de usuarios con ese estado
     */
    public List<UsuarioEntity> findByEstado(String estado) {
        return list("estado", estado);
    }

    /**
     * Busca todos los usuarios administradores.
     * @return Lista de usuarios administradores
     */
    public List<UsuarioEntity> findAdministradores() {
        return list("esAdmin", true);
    }

    /**
     * Verifica si existe un usuario con el email dado.
     * @param email El email a verificar
     * @return true si existe, false en caso contrario
     */
    public boolean existsByEmail(String email) {
        return count("email", email) > 0;
    }

    /**
     * Busca usuarios cuyo nombre contenga el texto dado (case-insensitive).
     * @param nombre Texto a buscar en el nombre
     * @return Lista de usuarios que coinciden
     */
    public List<UsuarioEntity> searchByNombre(String nombre) {
        return list("LOWER(nombre) LIKE LOWER(?1)", "%" + nombre + "%");
    }

    /**
     * Cambia el estado de un usuario.
     * @param idUsuario ID del usuario
     * @param nuevoEstado Nuevo estado
     * @return true si se actualizó, false si no existe
     */
    @Transactional
    public boolean cambiarEstado(Integer idUsuario, String nuevoEstado) {
        UsuarioEntity usuario = findById(idUsuario.longValue());
        if (usuario != null) {
            usuario.estado = nuevoEstado;
            persist(usuario);
            return true;
        }
        return false;
    }
}
