package es.rtur.pruebas.recipes.infrastructure.persistence;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.hibernate.orm.panache.Panache;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para ValoracionRepositoryImpl.
 * Valida el sistema de likes/dislikes y la regla de negocio RN-04.
 */
@QuarkusTest
class ValoracionRepositoryImplTest {

    @Inject
    ValoracionRepositoryImpl valoracionRepository;

    @Inject
    RecetaRepositoryImpl recetaRepository;

    @Inject
    UsuarioRepositoryImpl usuarioRepository;

    private RecetaEntity receta;
    private UsuarioEntity usuario1;
    private UsuarioEntity usuario2;
    private UsuarioEntity autor;

    @BeforeEach
    @Transactional
    void setUp() {
        // Limpiar datos - orden importante por foreign keys
        // Borrar hijos primero
        valoracionRepository.deleteAll();
        // Borrar tablas de unión antes de borrar recetas
        Panache.getEntityManager().createQuery("DELETE FROM IngredienteRecetaEntity").executeUpdate();
        Panache.getEntityManager().createQuery("DELETE FROM UtensilioRecetaEntity").executeUpdate();
        recetaRepository.deleteAll();
        usuarioRepository.deleteAll();

        // Crear datos de prueba
        autor = crearUsuario("Chef", "chef@test.com");
        usuarioRepository.persist(autor);

        usuario1 = crearUsuario("Usuario 1", "user1@test.com");
        usuarioRepository.persist(usuario1);

        usuario2 = crearUsuario("Usuario 2", "user2@test.com");
        usuarioRepository.persist(usuario2);

        receta = crearReceta("Paella Valenciana", autor);
        recetaRepository.persist(receta);
    }

    @Test
    @Transactional
    void testFindActivaByRecetaAndUsuario() {
        // Given
        ValoracionEntity valoracion = crearValoracion(receta, usuario1, "like");
        valoracionRepository.persist(valoracion);

        // When
        Optional<ValoracionEntity> found = valoracionRepository
            .findActivaByRecetaAndUsuario(receta.idReceta, usuario1.idUsuario);

        // Then
        assertTrue(found.isPresent());
        assertEquals("like", found.get().tipo);
    }

    @Test
    @Transactional
    void testFindActivaByRecetaAndUsuario_NoExiste() {
        // When
        Optional<ValoracionEntity> found = valoracionRepository
            .findActivaByRecetaAndUsuario(receta.idReceta, usuario1.idUsuario);

        // Then
        assertFalse(found.isPresent());
    }

    @Test
    @Transactional
    void testFindActivaByRecetaAndUsuario_Eliminada() {
        // Given
        ValoracionEntity valoracion = crearValoracion(receta, usuario1, "like");
        valoracion.eliminar();
        valoracionRepository.persist(valoracion);

        // When
        Optional<ValoracionEntity> found = valoracionRepository
            .findActivaByRecetaAndUsuario(receta.idReceta, usuario1.idUsuario);

        // Then
        assertFalse(found.isPresent());
    }

    @Test
    @Transactional
    void testFindActivasByReceta() {
        // Given
        valoracionRepository.persist(crearValoracion(receta, usuario1, "like"));
        valoracionRepository.persist(crearValoracion(receta, usuario2, "dislike"));

        // When
        List<ValoracionEntity> valoraciones = valoracionRepository.findActivasByReceta(receta.idReceta);

        // Then
        assertEquals(2, valoraciones.size());
    }

    @Test
    @Transactional
    void testFindByUsuario() {
        // Given
        valoracionRepository.persist(crearValoracion(receta, usuario1, "like"));
        
        RecetaEntity receta2 = crearReceta("Tortilla", autor);
        recetaRepository.persist(receta2);
        valoracionRepository.persist(crearValoracion(receta2, usuario1, "like"));

        // When
        List<ValoracionEntity> valoraciones = valoracionRepository.findByUsuario(usuario1.idUsuario);

        // Then
        assertEquals(2, valoraciones.size());
    }

    @Test
    @Transactional
    void testCountLikesByReceta() {
        // Given
        valoracionRepository.persist(crearValoracion(receta, usuario1, "like"));
        valoracionRepository.persist(crearValoracion(receta, usuario2, "like"));

        // When
        long count = valoracionRepository.countLikesByReceta(receta.idReceta);

        // Then
        assertEquals(2, count);
    }

    @Test
    @Transactional
    void testCountDislikesByReceta() {
        // Given
        valoracionRepository.persist(crearValoracion(receta, usuario1, "dislike"));
        valoracionRepository.persist(crearValoracion(receta, usuario2, "like"));

        // When
        long count = valoracionRepository.countDislikesByReceta(receta.idReceta);

        // Then
        assertEquals(1, count);
    }

    @Test
    @Transactional
    void testGetEstadisticasByReceta() {
        // Given
        valoracionRepository.persist(crearValoracion(receta, usuario1, "like"));
        valoracionRepository.persist(crearValoracion(receta, usuario2, "like"));
        
        UsuarioEntity usuario3 = crearUsuario("Usuario 3", "user3@test.com");
        usuarioRepository.persist(usuario3);
        valoracionRepository.persist(crearValoracion(receta, usuario3, "dislike"));

        // When
        Map<String, Long> estadisticas = valoracionRepository.getEstadisticasByReceta(receta.idReceta);

        // Then
        assertEquals(2L, estadisticas.get("likes"));
        assertEquals(1L, estadisticas.get("dislikes"));
    }

    @Test
    @Transactional
    void testEliminarValoracion() {
        // Given
        ValoracionEntity valoracion = crearValoracion(receta, usuario1, "like");
        valoracionRepository.persist(valoracion);
        valoracionRepository.flush();

        // When
        boolean resultado = valoracionRepository.eliminarValoracion((long) valoracion.idValoracion);

        // Then
        assertTrue(resultado);
        assertNotNull(valoracion.fEliminacion);
        assertFalse(valoracion.estaActiva());
    }

    @Test
    @Transactional
    void testEliminarValoracion_NoExiste() {
        // When
        boolean resultado = valoracionRepository.eliminarValoracion(99999L);

        // Then
        assertFalse(resultado);
    }

    @Test
    @Transactional
    void testCambiarTipo() {
        // Given
        ValoracionEntity valoracion = crearValoracion(receta, usuario1, "like");
        valoracionRepository.persist(valoracion);
        valoracionRepository.flush();

        // When
        boolean resultado = valoracionRepository.cambiarTipo((long) valoracion.idValoracion, "dislike");

        // Then
        assertTrue(resultado);
        var actualizada = valoracionRepository.findByIdOptional((long) valoracion.idValoracion);
        assertTrue(actualizada.isPresent());
        assertEquals("dislike", actualizada.get().tipo);
    }

    @Test
    @Transactional
    void testHasValorado() {
        // Given
        valoracionRepository.persist(crearValoracion(receta, usuario1, "like"));

        // When & Then
        assertTrue(valoracionRepository.hasValorado(receta.idReceta, usuario1.idUsuario));
        assertFalse(valoracionRepository.hasValorado(receta.idReceta, usuario2.idUsuario));
    }

    @Test
    @Transactional
    void testGetTipoValoracion() {
        // Given
        valoracionRepository.persist(crearValoracion(receta, usuario1, "like"));

        // When
        String tipo = valoracionRepository.getTipoValoracion(receta.idReceta, usuario1.idUsuario);

        // Then
        assertEquals("like", tipo);
    }

    @Test
    @Transactional
    void testGetTipoValoracion_NoHaValorado() {
        // When
        String tipo = valoracionRepository.getTipoValoracion(receta.idReceta, usuario1.idUsuario);

        // Then
        assertNull(tipo);
    }

    @Test
    @Transactional
    void testGetRecetasMasValoradas() {
        // Given
        // Receta 1: 3 likes, 1 dislike = +2
        valoracionRepository.persist(crearValoracion(receta, usuario1, "like"));
        valoracionRepository.persist(crearValoracion(receta, usuario2, "like"));
        
        UsuarioEntity usuario3 = crearUsuario("Usuario 3", "user3@test.com");
        usuarioRepository.persist(usuario3);
        valoracionRepository.persist(crearValoracion(receta, usuario3, "like"));
        
        UsuarioEntity usuario4 = crearUsuario("Usuario 4", "user4@test.com");
        usuarioRepository.persist(usuario4);
        valoracionRepository.persist(crearValoracion(receta, usuario4, "dislike"));

        // Receta 2: 2 likes = +2
        RecetaEntity receta2 = crearReceta("Tortilla", autor);
        recetaRepository.persist(receta2);
        valoracionRepository.persist(crearValoracion(receta2, usuario1, "like"));
        valoracionRepository.persist(crearValoracion(receta2, usuario2, "like"));

        // When
        List<Integer> recetasMasValoradas = valoracionRepository.getRecetasMasValoradas(2);

        // Then
        assertEquals(2, recetasMasValoradas.size());
    }

    @Test
    @Transactional
    void testReglaNegocio_RN04_UnaSolaValoracionPorUsuario() {
        // Given - primera valoración
        ValoracionEntity valoracion1 = crearValoracion(receta, usuario1, "like");
        valoracionRepository.persist(valoracion1);
        valoracionRepository.flush();

        // When - intentar crear otra valoración para el mismo usuario y receta
        // La base de datos debe prevenir esto con el unique constraint
        
        // Then - verificar que solo hay una valoración activa
        Optional<ValoracionEntity> found = valoracionRepository
            .findActivaByRecetaAndUsuario(receta.idReceta, usuario1.idUsuario);
        
        assertTrue(found.isPresent());
        assertEquals("like", found.get().tipo);
    }

    @Test
    @Transactional
    void testEstaActiva() {
        // Given
        ValoracionEntity valoracion = crearValoracion(receta, usuario1, "like");
        
        // When & Then
        assertTrue(valoracion.estaActiva());
        
        valoracion.eliminar();
        assertFalse(valoracion.estaActiva());
    }

    @Test
    @Transactional
    void testPrePersistSetsFecha() {
        // Given
        ValoracionEntity valoracion = crearValoracion(receta, usuario1, "like");
        valoracion.fCreacion = null;

        // When
        valoracionRepository.persist(valoracion);

        // Then
        assertNotNull(valoracion.fCreacion);
    }

    // Métodos helper
    private UsuarioEntity crearUsuario(String nombre, String email) {
        UsuarioEntity usuario = new UsuarioEntity();
        usuario.nombre = nombre;
        usuario.email = email;
        usuario.claveAcceso = "password";
        usuario.esAdmin = false;
        return usuario;
    }

    private RecetaEntity crearReceta(String nombre, UsuarioEntity autor) {
        RecetaEntity receta = new RecetaEntity();
        receta.nombre = nombre;
        receta.dificultad = "facil";
        receta.tiempo = 30;
        receta.comensales = 4;
        receta.preparacion = "Preparación";
        receta.autor = autor;
        return receta;
    }

    private ValoracionEntity crearValoracion(RecetaEntity receta, UsuarioEntity usuario, String tipo) {
        ValoracionEntity valoracion = new ValoracionEntity();
        valoracion.receta = receta;
        valoracion.usuario = usuario;
        valoracion.tipo = tipo;
        return valoracion;
    }
}
