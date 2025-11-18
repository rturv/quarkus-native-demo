package es.rtur.pruebas.recipes.infrastructure.persistence;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para ComentarioRepositoryImpl.
 */
@QuarkusTest
class ComentarioRepositoryImplTest {

    @Inject
    ComentarioRepositoryImpl comentarioRepository;

    @Inject
    RecetaRepositoryImpl recetaRepository;

    @Inject
    UsuarioRepositoryImpl usuarioRepository;

    private RecetaEntity receta;
    private UsuarioEntity autor;
    private UsuarioEntity comentarista;

    @BeforeEach
    @Transactional
    void setUp() {
        // Limpiar datos
        comentarioRepository.deleteAll();
        recetaRepository.deleteAll();
        usuarioRepository.deleteAll();

        // Crear datos de prueba
        autor = crearUsuario("Chef", "chef@test.com");
        usuarioRepository.persist(autor);

        comentarista = crearUsuario("Usuario", "usuario@test.com");
        usuarioRepository.persist(comentarista);

        receta = crearReceta("Paella Valenciana", autor);
        recetaRepository.persist(receta);
    }

    @Test
    @Transactional
    void testFindByReceta() {
        // Given
        comentarioRepository.persist(crearComentario(receta, comentarista, "Excelente receta"));
        comentarioRepository.persist(crearComentario(receta, autor, "Gracias"));

        // When
        List<ComentarioEntity> comentarios = comentarioRepository.findByReceta(receta.idReceta);

        // Then
        assertEquals(2, comentarios.size());
    }

    @Test
    @Transactional
    void testFindActivosByReceta() {
        // Given
        ComentarioEntity activo = crearComentario(receta, comentarista, "Buen comentario");
        activo.estado = "activo";
        comentarioRepository.persist(activo);

        ComentarioEntity eliminado = crearComentario(receta, comentarista, "Comentario eliminado");
        eliminado.estado = "eliminado";
        comentarioRepository.persist(eliminado);

        // When
        List<ComentarioEntity> activos = comentarioRepository.findActivosByReceta(receta.idReceta);

        // Then
        assertEquals(1, activos.size());
        assertEquals("Buen comentario", activos.get(0).contenido);
    }

    @Test
    @Transactional
    void testFindByAutor() {
        // Given
        comentarioRepository.persist(crearComentario(receta, comentarista, "Comentario 1"));
        comentarioRepository.persist(crearComentario(receta, comentarista, "Comentario 2"));
        comentarioRepository.persist(crearComentario(receta, autor, "Comentario del chef"));

        // When
        List<ComentarioEntity> comentarios = comentarioRepository.findByAutor(comentarista.idUsuario);

        // Then
        assertEquals(2, comentarios.size());
    }

    @Test
    @Transactional
    void testFindByEstado() {
        // Given
        ComentarioEntity activo1 = crearComentario(receta, comentarista, "Activo 1");
        activo1.estado = "activo";
        comentarioRepository.persist(activo1);

        ComentarioEntity activo2 = crearComentario(receta, comentarista, "Activo 2");
        activo2.estado = "activo";
        comentarioRepository.persist(activo2);

        ComentarioEntity pendiente = crearComentario(receta, comentarista, "Pendiente");
        pendiente.estado = "pendiente";
        comentarioRepository.persist(pendiente);

        // When
        List<ComentarioEntity> activos = comentarioRepository.findByEstado("activo");

        // Then
        assertEquals(2, activos.size());
    }

    @Test
    @Transactional
    void testCountActivosByReceta() {
        // Given
        ComentarioEntity activo1 = crearComentario(receta, comentarista, "Activo 1");
        comentarioRepository.persist(activo1);

        ComentarioEntity activo2 = crearComentario(receta, comentarista, "Activo 2");
        comentarioRepository.persist(activo2);

        ComentarioEntity eliminado = crearComentario(receta, comentarista, "Eliminado");
        eliminado.estado = "eliminado";
        comentarioRepository.persist(eliminado);

        // When
        long count = comentarioRepository.countActivosByReceta(receta.idReceta);

        // Then
        assertEquals(2, count);
    }

    @Test
    @Transactional
    void testCambiarEstado() {
        // Given
        ComentarioEntity comentario = crearComentario(receta, comentarista, "Test");
        comentarioRepository.persist(comentario);
        comentarioRepository.flush();

        // When
        boolean resultado = comentarioRepository.cambiarEstado((long) comentario.idComentario, "eliminado");

        // Then
        assertTrue(resultado);
        var actualizado = comentarioRepository.findByIdOptional((long) comentario.idComentario);
        assertTrue(actualizado.isPresent());
        assertEquals("eliminado", actualizado.get().estado);
    }

    @Test
    @Transactional
    void testFindRecientes() {
        // Given
        comentarioRepository.persist(crearComentario(receta, comentarista, "Comentario 1"));
        comentarioRepository.persist(crearComentario(receta, comentarista, "Comentario 2"));
        comentarioRepository.persist(crearComentario(receta, comentarista, "Comentario 3"));

        // When
        List<ComentarioEntity> recientes = comentarioRepository.findRecientes(2);

        // Then
        assertEquals(2, recientes.size());
    }

    @Test
    @Transactional
    void testPrePersistSetsFechas() {
        // Given
        ComentarioEntity comentario = crearComentario(receta, comentarista, "Test");
        comentario.fCreacion = null;
        comentario.fModificacion = null;

        // When
        comentarioRepository.persist(comentario);

        // Then
        assertNotNull(comentario.fCreacion);
        assertNotNull(comentario.fModificacion);
        assertEquals("activo", comentario.estado);
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

    private ComentarioEntity crearComentario(RecetaEntity receta, UsuarioEntity autor, String contenido) {
        ComentarioEntity comentario = new ComentarioEntity();
        comentario.receta = receta;
        comentario.autor = autor;
        comentario.contenido = contenido;
        comentario.estado = "activo";
        return comentario;
    }
}
