package es.rtur.pruebas.recipes.infrastructure.rest;

import es.rtur.pruebas.recipes.application.dto.ComentarioDTO;
import es.rtur.pruebas.recipes.application.usecase.CreateComentarioUseCase;
import es.rtur.pruebas.recipes.application.usecase.ListComentariosUseCase;
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
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
class ComentarioResourceTest {

    private CreateComentarioUseCase createComentarioUseCase;
    private ListComentariosUseCase listComentariosUseCase;

    @BeforeEach
    void setupMocks() {
        createComentarioUseCase = Mockito.mock(CreateComentarioUseCase.class);
        listComentariosUseCase = Mockito.mock(ListComentariosUseCase.class);
        QuarkusMock.installMockForType(createComentarioUseCase, CreateComentarioUseCase.class);
        QuarkusMock.installMockForType(listComentariosUseCase, ListComentariosUseCase.class);
    }

    @TestSecurity(user = "10", roles = {"user"})
    @Test
    void createShouldAttachRecipeAndUser() {
        ComentarioDTO created = comentario(90, 42, 10, "¡Genial!");
        Mockito.when(createComentarioUseCase.execute(Mockito.any(ComentarioDTO.class), Mockito.eq(10)))
                .thenReturn(created);

        given()
                .contentType(ContentType.JSON)
                .body(Map.of("contenido", "¡Genial!"))
                .when()
                .post("/api/recipes/42/comments")
                .then()
                .statusCode(201)
                .body("id", equalTo(90))
                .body("idReceta", equalTo(42))
                .body("idAutor", equalTo(10));

        ArgumentCaptor<ComentarioDTO> dtoCaptor = ArgumentCaptor.forClass(ComentarioDTO.class);
        Mockito.verify(createComentarioUseCase).execute(dtoCaptor.capture(), Mockito.eq(10));
        assertEquals(42, dtoCaptor.getValue().getIdReceta());
    }

    @Test
    void listShouldReturnComments() {
        Mockito.when(listComentariosUseCase.execute(42)).thenReturn(List.of(
                comentario(1, 42, 7, "Muy rica"),
                comentario(2, 42, 9, "Necesita más sal")
        ));

        given()
            .when()
            .get("/api/recipes/42/comments")
            .then()
            .statusCode(200)
            .body("size()", equalTo(2))
            .body("[0].contenido", equalTo("Muy rica"));
    }

    @Test
    void createShouldRejectWithoutSecurity() {
        given()
                .contentType(ContentType.JSON)
                .body(Map.of("contenido", "Sin token"))
                .when()
                .post("/api/recipes/42/comments")
                .then()
                .statusCode(401);
    }

    private ComentarioDTO comentario(int id, int recipeId, int authorId, String contenido) {
        ComentarioDTO dto = new ComentarioDTO();
        dto.setId(id);
        dto.setIdReceta(recipeId);
        dto.setIdAutor(authorId);
        dto.setContenido(contenido);
        dto.setEstado("activo");
        dto.setFCreacion(LocalDateTime.now());
        dto.setFModificacion(LocalDateTime.now());
        return dto;
    }
}
