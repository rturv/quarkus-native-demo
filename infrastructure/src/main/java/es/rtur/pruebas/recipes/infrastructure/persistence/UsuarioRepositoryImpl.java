package es.rtur.pruebas.recipes.infrastructure.persistence;

import es.rtur.pruebas.recipes.domain.entity.Usuario;
import es.rtur.pruebas.recipes.domain.repository.UsuarioRepository;
import es.rtur.pruebas.recipes.domain.valueobject.UsuarioId;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Repositorio Panache para UsuarioEntity.
 * Implementa UsuarioRepository del dominio.
 * Proporciona operaciones CRUD y consultas personalizadas para usuarios.
 */
@ApplicationScoped
public class UsuarioRepositoryImpl extends io.quarkus.hibernate.orm.panache.PanacheRepositoryBase<UsuarioEntity, Long> implements UsuarioRepository {

    /**
     * Busca un usuario por su email (internal method for JPA entities).
     * @param email El email del usuario
     * @return Optional con el usuario si existe
     */
    private Optional<UsuarioEntity> findEntityByEmail(String email) {
        return find("email", email).firstResultOptional();
    }

    /**
     * Busca usuarios por su estado (internal method for JPA entities).
     * @param estado El estado del usuario (activo, inactivo, etc.)
     * @return Lista de usuarios con ese estado
     */
    private List<UsuarioEntity> findEntitiesByEstado(String estado) {
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

    // Domain Repository Implementation

    @Override
    @Transactional
    public Usuario save(Usuario usuario) {
        UsuarioEntity entity;
        if (usuario.getId() == null) {
            // New entity
            entity = toEntity(usuario);
            persist(entity);
        } else {
            // Update existing
            entity = findById(usuario.getId().getValue().longValue());
            if (entity == null) {
                entity = toEntity(usuario);
                persist(entity);
            } else {
                updateEntity(entity, usuario);
            }
        }
        return toDomain(entity);
    }

    @Override
    public Optional<Usuario> findById(UsuarioId id) {
        UsuarioEntity entity = findById(id.getValue().longValue());
        return entity != null ? Optional.of(toDomain(entity)) : Optional.empty();
    }

    @Override
    public Optional<Usuario> findByEmail(String email) {
        return findEntityByEmail(email)
                .map(this::toDomain);
    }

    // Domain repository method - different from Panache's findAll()
    public List<Usuario> findAllDomain() {
        return listAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Usuario> findAll() {
        return findAllDomain();
    }

    @Override
    public List<Usuario> findByEstado(String estado) {
        return findEntitiesByEstado(estado).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteById(UsuarioId id) {
        deleteById(id.getValue().longValue());
    }

    @Override
    public boolean existsById(UsuarioId id) {
        return count("idUsuario", id.getValue()) > 0;
    }

    // Note: existsByEmail(String) is already defined above as a public method

    // Mappers

    private Usuario toDomain(UsuarioEntity entity) {
        return new Usuario(
                UsuarioId.of(entity.idUsuario),
                entity.nombre,
                entity.claveAcceso,
                entity.email,
                entity.esAdmin,
                entity.estado,
                entity.fCreacion,
                entity.fModificacion
        );
    }

    private UsuarioEntity toEntity(Usuario domain) {
        UsuarioEntity entity = new UsuarioEntity();
        if (domain.getId() != null) {
            entity.idUsuario = domain.getId().getValue();
        }
        entity.nombre = domain.getNombre();
        entity.claveAcceso = domain.getClaveAcceso();
        entity.email = domain.getEmail();
        entity.esAdmin = domain.getEsAdmin();
        entity.estado = domain.getEstado();
        return entity;
    }

    private void updateEntity(UsuarioEntity entity, Usuario domain) {
        entity.nombre = domain.getNombre();
        entity.claveAcceso = domain.getClaveAcceso();
        entity.email = domain.getEmail();
        entity.esAdmin = domain.getEsAdmin();
        entity.estado = domain.getEstado();
    }
}
