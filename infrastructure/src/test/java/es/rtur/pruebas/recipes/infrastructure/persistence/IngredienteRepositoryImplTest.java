package es.rtur.pruebas.recipes.infrastructure.persistence;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para IngredienteRepositoryImpl.
 */
@QuarkusTest
class IngredienteRepositoryImplTest {

    @Inject
    IngredienteRepositoryImpl repository;

    @BeforeEach
    @Transactional
    void setUp() {
        repository.deleteAll();
    }

    @Test
    @Transactional
    void testFindByNombre() {
        // Given
        IngredienteEntity ingrediente = crearIngrediente("Tomate", "verdura");
        repository.persist(ingrediente);

        // When
        Optional<IngredienteEntity> found = repository.findByNombre("Tomate");

        // Then
        assertTrue(found.isPresent());
        assertEquals("Tomate", found.get().nombre);
    }

    @Test
    @Transactional
    void testSearchByNombre() {
        // Given
        repository.persist(crearIngrediente("Tomate Cherry", "verdura"));
        repository.persist(crearIngrediente("Tomate Rama", "verdura"));
        repository.persist(crearIngrediente("Lechuga", "verdura"));

        // When
        List<IngredienteEntity> resultados = repository.searchByNombre("tomate");

        // Then
        assertEquals(2, resultados.size());
    }

    @Test
    @Transactional
    void testFindByTipo() {
        // Given
        repository.persist(crearIngrediente("Ternera", "carne"));
        repository.persist(crearIngrediente("Pollo", "carne"));
        repository.persist(crearIngrediente("Tomate", "verdura"));

        // When
        List<IngredienteEntity> carnes = repository.findByTipo("carne");

        // Then
        assertEquals(2, carnes.size());
    }

    @Test
    @Transactional
    void testFindAllTipos() {
        // Given
        repository.persist(crearIngrediente("Ternera", "carne"));
        repository.persist(crearIngrediente("Tomate", "verdura"));
        repository.persist(crearIngrediente("Leche", "lacteo"));

        // When
        List<String> tipos = repository.findAllTipos();

        // Then
        assertEquals(3, tipos.size());
        assertTrue(tipos.contains("carne"));
        assertTrue(tipos.contains("verdura"));
        assertTrue(tipos.contains("lacteo"));
    }

    @Test
    @Transactional
    void testExistsByNombre() {
        // Given
        repository.persist(crearIngrediente("Azúcar", "endulzante"));

        // When & Then
        assertTrue(repository.existsByNombre("Azúcar"));
        assertFalse(repository.existsByNombre("Sal"));
    }

    private IngredienteEntity crearIngrediente(String nombre, String tipo) {
        IngredienteEntity ingrediente = new IngredienteEntity();
        ingrediente.nombre = nombre;
        ingrediente.tipo = tipo;
        return ingrediente;
    }
}
