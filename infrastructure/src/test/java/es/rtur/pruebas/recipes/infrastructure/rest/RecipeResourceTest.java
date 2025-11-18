package es.rtur.pruebas.recipes.infrastructure.rest;

import es.rtur.pruebas.recipes.application.dto.RecetaDTO;
import es.rtur.pruebas.recipes.application.usecase.CreateRecetaUseCase;
import es.rtur.pruebas.recipes.application.usecase.GetRecetaUseCase;
import es.rtur.pruebas.recipes.application.usecase.ListRecetasUseCase;
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
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@QuarkusTest
class RecipeResourceTest {

    private ListRecetasUseCase listRecetasUseCase;
    private GetRecetaUseCase getRecetaUseCase;
    private CreateRecetaUseCase createRecetaUseCase;

    @BeforeEach
    void setupMocks() {
        listRecetasUseCase = Mockito.mock(ListRecetasUseCase.class);
        getRecetaUseCase = Mockito.mock(GetRecetaUseCase.class);
        createRecetaUseCase = Mockito.mock(CreateRecetaUseCase.class);
        QuarkusMock.installMockForType(listRecetasUseCase, ListRecetasUseCase.class);
        QuarkusMock.installMockForType(getRecetaUseCase, GetRecetaUseCase.class);
        QuarkusMock.installMockForType(createRecetaUseCase, CreateRecetaUseCase.class);
    }

    @Test
    void listShouldExposeRecetas() {
        Mockito.when(listRecetasUseCase.execute()).thenReturn(List.of(sampleReceta(42, "Tortilla Clean")));

        given()
                .when()
                .get("/api/recipes")
                .then()
                .statusCode(200)
                .body("size()", equalTo(1))
                .body("[0].nombre", equalTo("Tortilla Clean"))
                .body("[0].tiempo", equalTo(30));
    }

    @Test
    void getShouldReturn404WhenMissing() {
        Mockito.when(getRecetaUseCase.execute(999)).thenThrow(new IllegalArgumentException("Receta 999 no encontrada"));

        given()
                .pathParam("id", 999)
                .when()
                .get("/api/recipes/{id}")
                .then()
                .statusCode(404)
                .body("error", equalTo("not_found"))
                .body("message", containsString("999"));
    }

    @Test
    @TestSecurity(user = "7", roles = {"user"})
    void createShouldUseAuthenticatedUser() {
        RecetaDTO persisted = sampleReceta(101, "Sopa Verde");
        Mockito.when(createRecetaUseCase.execute(Mockito.any(RecetaDTO.class), Mockito.eq(7)))
                .thenReturn(persisted);

        given()
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "nombre", "Sopa Verde",
                        "tiempo", 25,
                        "comensales", 3,
                        "dificultad", "MEDIA",
                        "preparacion", "Pasos sencillos"
                ))
                .when()
                .post("/api/recipes")
                .then()
                .statusCode(201)
                .body("id", equalTo(101))
                .body("nombre", equalTo("Sopa Verde"));

        ArgumentCaptor<RecetaDTO> dtoCaptor = ArgumentCaptor.forClass(RecetaDTO.class);
        Mockito.verify(createRecetaUseCase).execute(dtoCaptor.capture(), Mockito.eq(7));
        RecetaDTO captured = dtoCaptor.getValue();
        assertEquals("Sopa Verde", captured.getNombre());
        assertNull(captured.getIdAutor());
    }

    private RecetaDTO sampleReceta(Integer id, String nombre) {
        RecetaDTO dto = new RecetaDTO();
        dto.setId(id);
        dto.setNombre(nombre);
        dto.setTiempo(30);
        dto.setComensales(4);
        dto.setDificultad("MEDIA");
        dto.setPreparacion("Batir huevos y freír patatas");
        dto.setCategoria("Tradicional");
        dto.setIdAutor(5);
        dto.setNombreAutor("Laura");
        dto.setFCreacion(LocalDateTime.now());
        dto.setFModificacion(LocalDateTime.now());
        return dto;
    }
}
