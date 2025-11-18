package es.rtur.pruebas.recipes.infrastructure.persistence;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.hibernate.orm.panache.Panache;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para RecetaRepositoryImpl.
 * Valida operaciones CRUD y consultas personalizadas sobre recetas.
 */
@QuarkusTest
class RecetaRepositoryImplTest {

    @Inject
    RecetaRepositoryImpl recetaRepository;

    @Inject
    UsuarioRepositoryImpl usuarioRepository;

    @Inject
    IngredienteRepositoryImpl ingredienteRepository;

    @Inject
    ComentarioRepositoryImpl comentarioRepository;

    @Inject
    ValoracionRepositoryImpl valoracionRepository;

    private UsuarioEntity autor;

    @BeforeEach
    @Transactional
    void setUp() {
        // Limpiar datos - orden importante por foreign keys
        // Borrar hijos primero: comentarios y valoraciones dependen de recetas
        comentarioRepository.deleteAll();
        valoracionRepository.deleteAll();
        // Borrar tablas de unión antes de borrar recetas
        Panache.getEntityManager().createQuery("DELETE FROM IngredienteRecetaEntity").executeUpdate();
        Panache.getEntityManager().createQuery("DELETE FROM UtensilioRecetaEntity").executeUpdate();
        recetaRepository.deleteAll();
        usuarioRepository.deleteAll();
        ingredienteRepository.deleteAll();

        // Crear autor para las recetas
        autor = new UsuarioEntity();
        autor.nombre = "Chef Test";
        autor.email = "chef@test.com";
        autor.claveAcceso = "password";
        autor.esAdmin = false;
        usuarioRepository.persist(autor);
    }

    @Test
    @Transactional
    void testPersistAndFindById() {
        // Given
        RecetaEntity receta = crearReceta("Paella Valenciana", "facil", 60);
        recetaRepository.persist(receta);

        // When
        var found = recetaRepository.findByIdOptional((long) receta.idReceta);

        // Then
        assertTrue(found.isPresent());
        assertEquals("Paella Valenciana", found.get().nombre);
    }

    @Test
    @Transactional
    void testFindByAutor() {
        // Given
        recetaRepository.persist(crearReceta("Receta 1", "facil", 30));
        recetaRepository.persist(crearReceta("Receta 2", "media", 45));

        // When
        List<RecetaEntity> recetas = recetaRepository.findByAutor(autor.idUsuario);

        // Then
        assertEquals(2, recetas.size());
    }

    @Test
    @Transactional
    void testSearchByNombre() {
        // Given
        recetaRepository.persist(crearReceta("Paella Valenciana", "facil", 60));
        recetaRepository.persist(crearReceta("Paella de Mariscos", "media", 50));
        recetaRepository.persist(crearReceta("Tortilla Española", "facil", 20));

        // When
        List<RecetaEntity> resultados = recetaRepository.searchByNombre("paella");

        // Then
        assertEquals(2, resultados.size());
        assertTrue(resultados.stream().allMatch(r -> r.nombre.toLowerCase().contains("paella")));
    }

    @Test
    @Transactional
    void testFindByDificultad() {
        // Given
        recetaRepository.persist(crearReceta("Receta Fácil 1", "facil", 15));
        recetaRepository.persist(crearReceta("Receta Fácil 2", "facil", 20));
        recetaRepository.persist(crearReceta("Receta Difícil", "dificil", 120));

        // When
        List<RecetaEntity> faciles = recetaRepository.findEntitiesByDificultad("facil");

        // Then
        assertEquals(2, faciles.size());
        assertTrue(faciles.stream().allMatch(r -> "facil".equals(r.dificultad)));
    }

    @Test
    @Transactional
    void testFindByCategoria() {
        // Given
        RecetaEntity postre1 = crearReceta("Tarta de Chocolate", "media", 90);
        postre1.categoria = "postre";
        recetaRepository.persist(postre1);

        RecetaEntity principal = crearReceta("Lentejas", "facil", 45);
        principal.categoria = "plato principal";
        recetaRepository.persist(principal);

        // When
        List<RecetaEntity> postres = recetaRepository.findEntitiesByCategoria("postre");

        // Then
        assertEquals(1, postres.size());
        assertEquals("Tarta de Chocolate", postres.get(0).nombre);
    }

    @Test
    @Transactional
    void testFindByIngrediente() {
        // Given
        IngredienteEntity arroz = crearIngrediente("Arroz", "cereal");
        ingredienteRepository.persist(arroz);

        RecetaEntity paella = crearReceta("Paella", "media", 60);
        recetaRepository.persist(paella);

        IngredienteRecetaEntity ingredienteReceta = new IngredienteRecetaEntity();
        ingredienteReceta.receta = paella;
        ingredienteReceta.ingrediente = arroz;
        ingredienteReceta.cantidad = new BigDecimal("400");
        ingredienteReceta.unidadMedida = "gramos";
        paella.ingredientesReceta.add(ingredienteReceta);
        recetaRepository.persist(paella);

        // When
        List<RecetaEntity> recetas = recetaRepository.findByIngrediente("arroz");

        // Then
        assertEquals(1, recetas.size());
        assertEquals("Paella", recetas.get(0).nombre);
    }

    @Test
    @Transactional
    void testFindByTiempoMenorIgualQue() {
        // Given
        recetaRepository.persist(crearReceta("Receta Rápida", "facil", 15));
        recetaRepository.persist(crearReceta("Receta Media", "media", 45));
        recetaRepository.persist(crearReceta("Receta Lenta", "dificil", 120));

        // When
        List<RecetaEntity> rapidas = recetaRepository.findByTiempoMenorIgualQue(30);

        // Then
        assertEquals(1, rapidas.size());
        assertEquals("Receta Rápida", rapidas.get(0).nombre);
    }

    @Test
    @Transactional
    void testFindByComensales() {
        // Given
        RecetaEntity para2 = crearReceta("Para 2 personas", "facil", 20);
        para2.comensales = 2;
        recetaRepository.persist(para2);

        RecetaEntity para4 = crearReceta("Para 4 personas", "facil", 30);
        para4.comensales = 4;
        recetaRepository.persist(para4);

        RecetaEntity para6 = crearReceta("Para 6 personas", "media", 45);
        para6.comensales = 6;
        recetaRepository.persist(para6);

        // When (buscar para 4 comensales, rango ±2)
        List<RecetaEntity> resultados = recetaRepository.findByComensales(4);

        // Then (debe incluir 2, 4 y 6 comensales)
        assertEquals(3, resultados.size());
    }

    @Test
    @Transactional
    void testFindRecientes() {
        // Given
        recetaRepository.persist(crearReceta("Receta 1", "facil", 20));
        recetaRepository.persist(crearReceta("Receta 2", "media", 30));
        recetaRepository.persist(crearReceta("Receta 3", "dificil", 60));

        // When
        List<RecetaEntity> recientes = recetaRepository.findRecientes(2);

        // Then
        assertEquals(2, recientes.size());
    }

    @Test
    @Transactional
    void testBuscarConFiltros_TodosLosFiltros() {
        // Given
        RecetaEntity receta1 = crearReceta("Pasta Carbonara", "facil", 20);
        receta1.categoria = "pasta";
        recetaRepository.persist(receta1);

        RecetaEntity receta2 = crearReceta("Lasaña", "media", 60);
        receta2.categoria = "pasta";
        recetaRepository.persist(receta2);

        RecetaEntity receta3 = crearReceta("Ensalada César", "facil", 10);
        receta3.categoria = "ensalada";
        recetaRepository.persist(receta3);

        // When
        List<RecetaEntity> resultados = recetaRepository.buscarConFiltros("facil", "pasta", 30);

        // Then
        assertEquals(1, resultados.size());
        assertEquals("Pasta Carbonara", resultados.get(0).nombre);
    }

    @Test
    @Transactional
    void testBuscarConFiltros_SoloDificultad() {
        // Given
        recetaRepository.persist(crearReceta("Receta 1", "facil", 20));
        recetaRepository.persist(crearReceta("Receta 2", "facil", 30));
        recetaRepository.persist(crearReceta("Receta 3", "dificil", 60));

        // When
        List<RecetaEntity> resultados = recetaRepository.buscarConFiltros("facil", null, null);

        // Then
        assertEquals(2, resultados.size());
    }

    @Test
    @Transactional
    void testBuscarConFiltros_SinFiltros() {
        // Given
        recetaRepository.persist(crearReceta("Receta 1", "facil", 20));
        recetaRepository.persist(crearReceta("Receta 2", "media", 30));

        // When
        List<RecetaEntity> resultados = recetaRepository.buscarConFiltros(null, null, null);

        // Then
        assertEquals(2, resultados.size());
    }

    @Test
    @Transactional
    void testPrePersistSetsFechas() {
        // Given
        RecetaEntity receta = crearReceta("Test", "facil", 20);
        receta.fCreacion = null;
        receta.fModificacion = null;

        // When
        recetaRepository.persist(receta);

        // Then
        assertNotNull(receta.fCreacion);
        assertNotNull(receta.fModificacion);
    }

    // Métodos helper
    private RecetaEntity crearReceta(String nombre, String dificultad, Integer tiempo) {
        RecetaEntity receta = new RecetaEntity();
        receta.nombre = nombre;
        receta.dificultad = dificultad;
        receta.tiempo = tiempo;
        receta.comensales = 4;
        receta.preparacion = "Preparación de " + nombre;
        receta.categoria = "general";
        receta.autor = autor;
        return receta;
    }

    private IngredienteEntity crearIngrediente(String nombre, String tipo) {
        IngredienteEntity ingrediente = new IngredienteEntity();
        ingrediente.nombre = nombre;
        ingrediente.tipo = tipo;
        return ingrediente;
    }
}
