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
 * Tests unitarios para UtensilioRepositoryImpl.
 */
@QuarkusTest
class UtensilioRepositoryImplTest {

    @Inject
    UtensilioRepositoryImpl repository;

    @BeforeEach
    @Transactional
    void setUp() {
        repository.deleteAll();
    }

    @Test
    @Transactional
    void testFindByNombre() {
        // Given
        UtensilioEntity utensilio = crearUtensilio("Sartén", "cocción");
        repository.persist(utensilio);

        // When
        Optional<UtensilioEntity> found = repository.findByNombre("Sartén");

        // Then
        assertTrue(found.isPresent());
        assertEquals("Sartén", found.get().nombre);
    }

    @Test
    @Transactional
    void testSearchByNombre() {
        // Given
        repository.persist(crearUtensilio("Cuchillo Chef", "corte"));
        repository.persist(crearUtensilio("Cuchillo Pan", "corte"));
        repository.persist(crearUtensilio("Batidora", "mezcla"));

        // When
        List<UtensilioEntity> resultados = repository.searchByNombre("cuchillo");

        // Then
        assertEquals(2, resultados.size());
    }

    @Test
    @Transactional
    void testFindByTipo() {
        // Given
        repository.persist(crearUtensilio("Sartén", "cocción"));
        repository.persist(crearUtensilio("Olla", "cocción"));
        repository.persist(crearUtensilio("Cuchillo", "corte"));

        // When
        List<UtensilioEntity> cuchillos = repository.findEntitiesByTipo("corte");

        // Then
        assertEquals(1, cuchillos.size());
    }

    @Test
    @Transactional
    void testFindAllTipos() {
        // Given
        repository.persist(crearUtensilio("Sartén", "cocción"));
        repository.persist(crearUtensilio("Cuchillo", "corte"));
        repository.persist(crearUtensilio("Balanza", "medición"));

        // When
        List<String> tipos = repository.findAllTipos();

        // Then
        assertEquals(3, tipos.size());
        assertTrue(tipos.contains("cocción"));
        assertTrue(tipos.contains("corte"));
        assertTrue(tipos.contains("medición"));
    }

    @Test
    @Transactional
    void testExistsByNombre() {
        // Given
        repository.persist(crearUtensilio("Espátula", "mezcla"));

        // When & Then
        assertTrue(repository.existsByNombre("Espátula"));
        assertFalse(repository.existsByNombre("Tenedor"));
    }

    private UtensilioEntity crearUtensilio(String nombre, String tipo) {
        UtensilioEntity utensilio = new UtensilioEntity();
        utensilio.nombre = nombre;
        utensilio.tipo = tipo;
        return utensilio;
    }
}
