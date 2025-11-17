package es.rtur.pruebas.recipes.infrastructure.persistence;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.hibernate.orm.panache.Panache;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para UsuarioRepositoryImpl.
 * Valida operaciones CRUD y consultas personalizadas sobre usuarios.
 */
@QuarkusTest
class UsuarioRepositoryImplTest {

    @Inject
    UsuarioRepositoryImpl repository;

    @Inject
    ComentarioRepositoryImpl comentarioRepository;

    @Inject
    ValoracionRepositoryImpl valoracionRepository;

    @Inject
    RecetaRepositoryImpl recetaRepository;

    @BeforeEach
    @Transactional
    void setUp() {
        // Limpiar datos - orden importante por foreign keys
        // Borrar hijos primero: comentarios y valoraciones dependen de usuarios
        comentarioRepository.deleteAll();
        valoracionRepository.deleteAll();
        // Borrar tablas de unión antes de borrar recetas
        Panache.getEntityManager().createQuery("DELETE FROM IngredienteRecetaEntity").executeUpdate();
        Panache.getEntityManager().createQuery("DELETE FROM UtensilioRecetaEntity").executeUpdate();
        recetaRepository.deleteAll();
        repository.deleteAll();
    }

    @Test
    @Transactional
    void testPersistAndFindById() {
        // Given
        UsuarioEntity usuario = crearUsuario("Juan Pérez", "juan@test.com", false);
        repository.persist(usuario);

        // When
        Optional<UsuarioEntity> found = repository.findByIdOptional((long) usuario.idUsuario);

        // Then
        assertTrue(found.isPresent());
        assertEquals("Juan Pérez", found.get().nombre);
        assertEquals("juan@test.com", found.get().email);
    }

    @Test
    @Transactional
    void testFindByEmail() {
        // Given
        UsuarioEntity usuario = crearUsuario("María García", "maria@test.com", false);
        repository.persist(usuario);

        // When
        Optional<UsuarioEntity> found = repository.findByEmail("maria@test.com");

        // Then
        assertTrue(found.isPresent());
        assertEquals("María García", found.get().nombre);
    }

    @Test
    @Transactional
    void testFindByEmailNoExiste() {
        // When
        Optional<UsuarioEntity> found = repository.findByEmail("noexiste@test.com");

        // Then
        assertFalse(found.isPresent());
    }

    @Test
    @Transactional
    void testFindByEstado() {
        // Given
        UsuarioEntity activo1 = crearUsuario("Usuario 1", "u1@test.com", false);
        activo1.estado = "activo";
        repository.persist(activo1);

        UsuarioEntity activo2 = crearUsuario("Usuario 2", "u2@test.com", false);
        activo2.estado = "activo";
        repository.persist(activo2);

        UsuarioEntity inactivo = crearUsuario("Usuario 3", "u3@test.com", false);
        inactivo.estado = "inactivo";
        repository.persist(inactivo);

        // When
        List<UsuarioEntity> activos = repository.findByEstado("activo");
        List<UsuarioEntity> inactivos = repository.findByEstado("inactivo");

        // Then
        assertEquals(2, activos.size());
        assertEquals(1, inactivos.size());
    }

    @Test
    @Transactional
    void testFindAdministradores() {
        // Given
        UsuarioEntity admin = crearUsuario("Admin", "admin@test.com", true);
        repository.persist(admin);

        UsuarioEntity usuario = crearUsuario("Usuario", "user@test.com", false);
        repository.persist(usuario);

        // When
        List<UsuarioEntity> admins = repository.findAdministradores();

        // Then
        assertEquals(1, admins.size());
        assertTrue(admins.get(0).esAdmin);
        assertEquals("admin@test.com", admins.get(0).email);
    }

    @Test
    @Transactional
    void testExistsByEmail() {
        // Given
        UsuarioEntity usuario = crearUsuario("Test User", "test@test.com", false);
        repository.persist(usuario);

        // When & Then
        assertTrue(repository.existsByEmail("test@test.com"));
        assertFalse(repository.existsByEmail("noexiste@test.com"));
    }

    @Test
    @Transactional
    void testSearchByNombre() {
        // Given
        repository.persist(crearUsuario("Juan Pérez", "juan@test.com", false));
        repository.persist(crearUsuario("María Juan", "maria@test.com", false));
        repository.persist(crearUsuario("Pedro García", "pedro@test.com", false));

        // When
        List<UsuarioEntity> resultados = repository.searchByNombre("juan");

        // Then
        assertEquals(2, resultados.size());
        assertTrue(resultados.stream().anyMatch(u -> u.nombre.contains("Juan")));
    }

    @Test
    @Transactional
    void testCambiarEstado() {
        // Given
        UsuarioEntity usuario = crearUsuario("Test User", "test@test.com", false);
        usuario.estado = "activo";
        repository.persist(usuario);
        repository.flush();

        // When
        boolean resultado = repository.cambiarEstado(usuario.idUsuario, "inactivo");

        // Then
        assertTrue(resultado);
        
        Optional<UsuarioEntity> actualizado = repository.findByIdOptional((long) usuario.idUsuario);
        assertTrue(actualizado.isPresent());
        assertEquals("inactivo", actualizado.get().estado);
    }

    @Test
    @Transactional
    void testCambiarEstadoUsuarioNoExiste() {
        // When
        boolean resultado = repository.cambiarEstado(99999, "inactivo");

        // Then
        assertFalse(resultado);
    }

    @Test
    @Transactional
    void testPrePersistSetsFechas() {
        // Given
        UsuarioEntity usuario = crearUsuario("Test User", "test@test.com", false);
        usuario.fCreacion = null;
        usuario.fModificacion = null;

        // When
        repository.persist(usuario);

        // Then
        assertNotNull(usuario.fCreacion);
        assertNotNull(usuario.fModificacion);
        assertFalse(usuario.esAdmin); // Default value
        assertEquals("activo", usuario.estado); // Default value
    }

    @Test
    @Transactional
    void testPreUpdateActualizaFechaModificacion() throws InterruptedException {
        // Given
        UsuarioEntity usuario = crearUsuario("Test User", "test@test.com", false);
        repository.persist(usuario);
        repository.flush();
        
        var fechaCreacion = usuario.fCreacion;
        var fechaModificacionOriginal = usuario.fModificacion;

        // Pequeña espera para asegurar que las fechas sean diferentes
        Thread.sleep(10);

        // When
        usuario.nombre = "Nombre Actualizado";
        repository.persist(usuario);
        repository.flush();

        // Then
        assertEquals(fechaCreacion, usuario.fCreacion); // No debe cambiar
        assertTrue(usuario.fModificacion.isAfter(fechaModificacionOriginal));
    }

    // Método helper
    private UsuarioEntity crearUsuario(String nombre, String email, boolean esAdmin) {
        UsuarioEntity usuario = new UsuarioEntity();
        usuario.nombre = nombre;
        usuario.email = email;
        usuario.claveAcceso = "password123";
        usuario.esAdmin = esAdmin;
        usuario.estado = "activo";
        return usuario;
    }
}
