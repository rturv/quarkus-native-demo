package es.rtur.pruebas.recipes.domain.repository;

import es.rtur.pruebas.recipes.domain.entity.Usuario;
import es.rtur.pruebas.recipes.domain.valueobject.UsuarioId;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Usuario entity.
 * Defines the contract for data access operations.
 * Implementations will be in the infrastructure layer.
 */
public interface UsuarioRepository {

    /**
     * Saves a new usuario or updates an existing one.
     */
    Usuario save(Usuario usuario);

    /**
     * Finds a usuario by ID.
     */
    Optional<Usuario> findById(UsuarioId id);

    /**
     * Finds a usuario by email.
     * RF-01: Used for login functionality.
     */
    Optional<Usuario> findByEmail(String email);

    /**
     * Finds all usuarios.
     * RF-06: Admin can manage users.
     * Note: Named findAllUsuarios() to avoid conflict with Panache's findAll()
     */
    List<Usuario> findAllUsuarios();

    /**
     * Finds active usuarios.
     */
    List<Usuario> findByEstado(String estado);

    /**
     * Deletes a usuario by ID.
     */
    void deleteById(UsuarioId id);

    /**
     * Checks if a usuario exists by ID.
     */
    boolean existsById(UsuarioId id);

    /**
     * Checks if a usuario exists by email.
     * RF-01: Used for registration to avoid duplicates.
     */
    boolean existsByEmail(String email);
}
