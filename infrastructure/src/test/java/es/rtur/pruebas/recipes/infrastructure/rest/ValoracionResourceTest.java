package es.rtur.pruebas.recipes.infrastructure.rest;

import es.rtur.pruebas.recipes.application.dto.ValoracionDTO;
import es.rtur.pruebas.recipes.application.usecase.CreateValoracionUseCase;
import es.rtur.pruebas.recipes.application.usecase.GetValoracionesUseCase;
import io.quarkus.test.junit.QuarkusMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@QuarkusTest
class ValoracionResourceTest {

    private CreateValoracionUseCase createValoracionUseCase;
    private GetValoracionesUseCase getValoracionesUseCase;

    @BeforeEach
    void setupMocks() {
        createValoracionUseCase = Mockito.mock(CreateValoracionUseCase.class);
        getValoracionesUseCase = Mockito.mock(GetValoracionesUseCase.class);
        QuarkusMock.installMockForType(createValoracionUseCase, CreateValoracionUseCase.class);
        QuarkusMock.installMockForType(getValoracionesUseCase, GetValoracionesUseCase.class);
    }

    @Test
    void summaryShouldExposeLikeCounts() {
        Mockito.when(getValoracionesUseCase.execute(42))
                .thenReturn(new GetValoracionesUseCase.ValoracionSummary(7L, 3L));

        given()
                .pathParam("recipeId", 42)
                .when()
                .get("/api/recipes/{recipeId}/ratings")
                .then()
                .statusCode(200)
                .body("likes", equalTo(7))
                .body("dislikes", equalTo(3))
                .body("total", equalTo(10));
    }

    @Test
    @TestSecurity(user = "8", roles = {"user"})
    void rateShouldAssignAuthenticatedUser() {
        ValoracionDTO persisted = valoracion(11, 42, 8, "like");
        Mockito.when(createValoracionUseCase.execute(Mockito.any(ValoracionDTO.class), Mockito.eq(8)))
                .thenReturn(persisted);

        given()
            .contentType(ContentType.JSON)
            .body(Map.of("idReceta", 42, "tipo", "like"))
                .when()
                .post("/api/recipes/42/ratings")
                .then()
                .statusCode(201)
                .body("id", equalTo(11))
                .body("idUsuario", equalTo(8));

        ArgumentCaptor<ValoracionDTO> captor = ArgumentCaptor.forClass(ValoracionDTO.class);
        Mockito.verify(createValoracionUseCase).execute(captor.capture(), Mockito.eq(8));
        ValoracionDTO captured = captor.getValue();
        org.junit.jupiter.api.Assertions.assertEquals(42, captured.getIdReceta());
        org.junit.jupiter.api.Assertions.assertEquals("like", captured.getTipo());
    }

    @Test
    void rateShouldRejectWithoutAuthentication() {
        given()
            .contentType(ContentType.JSON)
            .body(Map.of("idReceta", 42, "tipo", "dislike"))
                .when()
                .post("/api/recipes/42/ratings")
                .then()
                .statusCode(401);
    }

    private ValoracionDTO valoracion(Integer id, Integer recetaId, Integer usuarioId, String tipo) {
        ValoracionDTO dto = new ValoracionDTO();
        dto.setId(id);
        dto.setIdReceta(recetaId);
        dto.setIdUsuario(usuarioId);
        dto.setTipo(tipo);
        dto.setFCreacion(LocalDateTime.now());
        return dto;
    }
}