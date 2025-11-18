package es.rtur.pruebas.recipes.infrastructure.rest;

import es.rtur.pruebas.recipes.application.dto.IngredienteDTO;
import es.rtur.pruebas.recipes.application.usecase.CreateIngredienteUseCase;
import es.rtur.pruebas.recipes.application.usecase.ListIngredientesUseCase;
import io.quarkus.test.junit.QuarkusMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@QuarkusTest
class IngredienteResourceTest {

    private ListIngredientesUseCase listIngredientesUseCase;
    private CreateIngredienteUseCase createIngredienteUseCase;

    @BeforeEach
    void setupMocks() {
        listIngredientesUseCase = Mockito.mock(ListIngredientesUseCase.class);
        createIngredienteUseCase = Mockito.mock(CreateIngredienteUseCase.class);
        QuarkusMock.installMockForType(listIngredientesUseCase, ListIngredientesUseCase.class);
        QuarkusMock.installMockForType(createIngredienteUseCase, CreateIngredienteUseCase.class);
    }

    @Test
    void listShouldReturnIngredientes() {
        Mockito.when(listIngredientesUseCase.execute()).thenReturn(List.of(ingrediente(5, "Tomate", "Verdura")));

        given()
                .when()
                .get("/api/ingredients")
                .then()
                .statusCode(200)
                .body("size()", equalTo(1))
                .body("[0].nombre", equalTo("Tomate"));
    }

    @Test
    @TestSecurity(user = "18", roles = {"admin"})
    void createShouldAllowAdmin() {
        IngredienteDTO created = ingrediente(9, "Cebolla", "Bulbo");
        Mockito.when(createIngredienteUseCase.execute(Mockito.any(IngredienteDTO.class), Mockito.eq(true)))
                .thenReturn(created);

        given()
                .contentType(ContentType.JSON)
                .body(Map.of("nombre", "Cebolla", "tipo", "Bulbo"))
                .when()
                .post("/api/ingredients")
                .then()
                .statusCode(201)
                .body("id", equalTo(9))
                .body("tipo", equalTo("Bulbo"));

        ArgumentCaptor<IngredienteDTO> captor = ArgumentCaptor.forClass(IngredienteDTO.class);
        Mockito.verify(createIngredienteUseCase).execute(captor.capture(), Mockito.eq(true));
        org.junit.jupiter.api.Assertions.assertEquals("Cebolla", captor.getValue().getNombre());
    }

    @Test
    @TestSecurity(user = "19", roles = {"user"})
    void createShouldBeForbiddenForNonAdmin() {
        given()
                .contentType(ContentType.JSON)
                .body(Map.of("nombre", "Ajo", "tipo", "Bulbo"))
                .when()
                .post("/api/ingredients")
                .then()
                .statusCode(403);
    }

    private IngredienteDTO ingrediente(Integer id, String nombre, String tipo) {
        IngredienteDTO dto = new IngredienteDTO();
        dto.setId(id);
        dto.setNombre(nombre);
        dto.setTipo(tipo);
        dto.setFCreacion(LocalDateTime.now());
        dto.setFModificacion(LocalDateTime.now());
        return dto;
    }
}