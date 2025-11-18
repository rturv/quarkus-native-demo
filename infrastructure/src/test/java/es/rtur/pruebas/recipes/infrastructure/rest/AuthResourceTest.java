package es.rtur.pruebas.recipes.infrastructure.rest;

import es.rtur.pruebas.recipes.application.dto.UsuarioDTO;
import es.rtur.pruebas.recipes.application.usecase.LoginUserUseCase;
import es.rtur.pruebas.recipes.application.usecase.RefreshTokenUseCase;
import es.rtur.pruebas.recipes.application.usecase.RegisterUserUseCase;
import es.rtur.pruebas.recipes.infrastructure.security.JwtTokenService;
import io.quarkus.test.junit.QuarkusMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;

@QuarkusTest
class AuthResourceTest {

        private RegisterUserUseCase registerUserUseCase;
        private LoginUserUseCase loginUserUseCase;
        private RefreshTokenUseCase refreshTokenUseCase;
        private JwtTokenService jwtTokenService;

        @BeforeEach
        void setupMocks() {
                registerUserUseCase = Mockito.mock(RegisterUserUseCase.class);
                loginUserUseCase = Mockito.mock(LoginUserUseCase.class);
                refreshTokenUseCase = Mockito.mock(RefreshTokenUseCase.class);
                jwtTokenService = Mockito.mock(JwtTokenService.class);
                QuarkusMock.installMockForType(registerUserUseCase, RegisterUserUseCase.class);
                QuarkusMock.installMockForType(loginUserUseCase, LoginUserUseCase.class);
                QuarkusMock.installMockForType(refreshTokenUseCase, RefreshTokenUseCase.class);
                QuarkusMock.installMockForType(jwtTokenService, JwtTokenService.class);
        }

    @Test
    void registerShouldReturnJwtAndMaskPassword() {
        UsuarioDTO stored = user(50, "Lucía", "lucia@example.com");
        Mockito.when(registerUserUseCase.execute(Mockito.any(UsuarioDTO.class))).thenReturn(stored);
        Mockito.when(jwtTokenService.generateToken(Mockito.any(UsuarioDTO.class)))
                .thenReturn(new JwtTokenService.JwtToken("jwt-test-token",
                        Instant.parse("2025-01-01T00:00:00Z"),
                        Instant.parse("2025-01-01T01:00:00Z")));

        given()
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "nombre", "Lucía",
                        "email", "lucia@example.com",
                        "claveAcceso", "P4ssword!"
                ))
                .when()
                .post("/api/auth/register")
                .then()
                .statusCode(201)
                .body("id", equalTo(50))
                .body("jwt", equalTo("jwt-test-token"))
                .body("claveAcceso", nullValue());
    }

    @Test
    void loginShouldReturnToken() {
        UsuarioDTO usuario = user(70, "Marco", "marco@example.com");
        Mockito.when(loginUserUseCase.execute("marco@example.com", "Secret1!"))
                .thenReturn(usuario);
        Mockito.when(jwtTokenService.generateToken(Mockito.any(UsuarioDTO.class)))
                .thenReturn(new JwtTokenService.JwtToken("jwt-login",
                        Instant.parse("2025-02-01T00:00:00Z"),
                        Instant.parse("2025-02-01T01:00:00Z")));

        given()
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "email", "marco@example.com",
                        "password", "Secret1!"
                ))
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(200)
                .body("id", equalTo(70))
                .body("jwt", equalTo("jwt-login"));
    }

    @Test
    void loginShouldReturn401OnInvalidCredentials() {
        Mockito.when(loginUserUseCase.execute("bad@example.com", "wrong"))
                .thenThrow(new IllegalArgumentException("Credenciales inválidas"));

        given()
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "email", "bad@example.com",
                        "password", "wrong"
                ))
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(401)
                .body("error", equalTo("Credenciales inválidas"));
    }

    @Test
    @TestSecurity(user = "33", roles = {"user"})
    void refreshShouldReturnNewJwt() {
        UsuarioDTO usuario = user(33, "Antonia", "antonia@example.com");
        Mockito.when(refreshTokenUseCase.execute(33)).thenReturn(usuario);
        Mockito.when(jwtTokenService.generateToken(Mockito.any(UsuarioDTO.class)))
                .thenReturn(new JwtTokenService.JwtToken("jwt-refresh",
                        Instant.parse("2025-03-01T00:00:00Z"),
                        Instant.parse("2025-03-01T01:00:00Z")));

        given()
                .contentType(ContentType.JSON)
                .when()
                .post("/api/auth/refresh")
                .then()
                .statusCode(200)
                .body("id", equalTo(33))
                .body("jwt", equalTo("jwt-refresh"));
    }

    @Test
    void refreshShouldRejectWithoutAuth() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .post("/api/auth/refresh")
                .then()
                .statusCode(401);
    }

    private UsuarioDTO user(int id, String nombre, String email) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(id);
        dto.setNombre(nombre);
        dto.setEmail(email);
        dto.setEsAdmin(Boolean.FALSE);
        return dto;
    }
}
