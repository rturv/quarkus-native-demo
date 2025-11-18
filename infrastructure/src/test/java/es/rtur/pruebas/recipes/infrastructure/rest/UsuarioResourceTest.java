package es.rtur.pruebas.recipes.infrastructure.rest;

import es.rtur.pruebas.recipes.application.dto.UsuarioDTO;
import es.rtur.pruebas.recipes.application.usecase.ListUsuariosUseCase;
import io.quarkus.test.junit.QuarkusMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@QuarkusTest
class UsuarioResourceTest {

    private ListUsuariosUseCase listUsuariosUseCase;

    @BeforeEach
    void setupMocks() {
        listUsuariosUseCase = Mockito.mock(ListUsuariosUseCase.class);
        QuarkusMock.installMockForType(listUsuariosUseCase, ListUsuariosUseCase.class);
    }

    @Test
    @TestSecurity(user = "1", roles = {"admin"})
    void listShouldReturnUsersForAdmins() {
        Mockito.when(listUsuariosUseCase.execute()).thenReturn(List.of(usuario(1, "Ana")));

        given()
                .when()
                .get("/api/users")
                .then()
                .statusCode(200)
                .body("size()", equalTo(1))
                .body("[0].nombre", equalTo("Ana"));
    }

    @Test
    @TestSecurity(user = "2", roles = {"user"})
    void listShouldBeForbiddenForNonAdmin() {
        given()
                .when()
                .get("/api/users")
                .then()
                .statusCode(403);
    }

    private UsuarioDTO usuario(Integer id, String nombre) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(id);
        dto.setNombre(nombre);
        dto.setEmail(nombre.toLowerCase() + "@example.com");
        dto.setEsAdmin(Boolean.TRUE);
        dto.setEstado("activo");
        dto.setFCreacion(LocalDateTime.now());
        dto.setFModificacion(LocalDateTime.now());
        return dto;
    }
}