package es.rtur.pruebas.recipes.infrastructure.rest;

import es.rtur.pruebas.recipes.application.dto.UsuarioDTO;
import es.rtur.pruebas.recipes.application.usecase.LoginUserUseCase;
import es.rtur.pruebas.recipes.application.usecase.RefreshTokenUseCase;
import es.rtur.pruebas.recipes.application.usecase.RegisterUserUseCase;
import es.rtur.pruebas.recipes.infrastructure.rest.dto.LoginRequest;
import es.rtur.pruebas.recipes.infrastructure.security.AuthenticatedUser;
import es.rtur.pruebas.recipes.infrastructure.security.JwtTokenService;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Autenticación", description = "Registro y login con JWT simétrico")
public class AuthResource {

    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUserUseCase loginUserUseCase;
    private final SecurityIdentity securityIdentity;
    private final JwtTokenService jwtTokenService;
    private final RefreshTokenUseCase refreshTokenUseCase;

    @Inject
    public AuthResource(RegisterUserUseCase registerUserUseCase,
                        LoginUserUseCase loginUserUseCase,
                        SecurityIdentity securityIdentity,
                        JwtTokenService jwtTokenService,
                        RefreshTokenUseCase refreshTokenUseCase) {
        this.registerUserUseCase = registerUserUseCase;
        this.loginUserUseCase = loginUserUseCase;
        this.securityIdentity = securityIdentity;
        this.jwtTokenService = jwtTokenService;
        this.refreshTokenUseCase = refreshTokenUseCase;
    }

    @POST
    @Path("/register")
    @PermitAll
    @Operation(summary = "Registrar usuario", description = "Crea un nuevo usuario y devuelve el token JWT")
        @APIResponses(value = {
            @APIResponse(responseCode = "201", description = "Usuario registrado",
                content = @Content(mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = UsuarioDTO.class))),
            @APIResponse(responseCode = "400", description = "Datos inválidos")
        })
        public Response register(@RequestBody(required = true, description = "Datos del nuevo usuario",
            content = @Content(schema = @Schema(implementation = UsuarioDTO.class,
                requiredProperties = {"nombre", "email", "claveAcceso"})))
                   @Valid UsuarioDTO dto) {
        try {
            UsuarioDTO created = registerUserUseCase.execute(dto);
            created.setClaveAcceso(null);
            created.setJwt(jwtTokenService.generateToken(created).token());
            return Response.status(Response.Status.CREATED).entity(created).build();
        } catch (IllegalArgumentException ex) {
            return buildError(Response.Status.BAD_REQUEST, ex.getMessage());
        }
    }

    @POST
    @Path("/login")
    @PermitAll
    @Operation(summary = "Login", description = "Valida credenciales y retorna un JWT válido")
        @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Login exitoso",
                content = @Content(mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = UsuarioDTO.class))),
            @APIResponse(responseCode = "401", description = "Credenciales inválidas")
        })
        public Response login(@RequestBody(required = true, description = "Credenciales del usuario",
            content = @Content(schema = @Schema(implementation = LoginRequest.class)))
                  @Valid LoginRequest request) {
        try {
            UsuarioDTO user = loginUserUseCase.execute(request.getEmail(), request.getPassword());
            user.setClaveAcceso(null);
            user.setJwt(jwtTokenService.generateToken(user).token());
            return Response.ok(user).build();
        } catch (IllegalArgumentException ex) {
            return buildError(Response.Status.UNAUTHORIZED, ex.getMessage());
        }
    }

        @POST
        @Path("/refresh")
        @RolesAllowed({"user", "admin"})
        @Operation(summary = "Renovar token JWT", description = "Genera un nuevo JWT para el usuario autenticado")
        @APIResponses(value = {
                @APIResponse(responseCode = "200", description = "Token refrescado",
                        content = @Content(mediaType = MediaType.APPLICATION_JSON,
                                schema = @Schema(implementation = UsuarioDTO.class))),
                @APIResponse(responseCode = "401", description = "Token inválido o expirado")
        })
        public Response refreshToken() {
            AuthenticatedUser user = AuthenticatedUser.from(securityIdentity);
            UsuarioDTO refreshed = refreshTokenUseCase.execute(user.id());
            refreshed.setClaveAcceso(null);
            refreshed.setJwt(jwtTokenService.generateToken(refreshed).token());
            return Response.ok(refreshed).build();
        }

    private Response buildError(Response.Status status, String message) {
        return Response.status(status)
                .entity(new ErrorMessage(message))
                .build();
    }

    public static class ErrorMessage {
        private final String error;

        public ErrorMessage(String message) {
            this.error = message;
        }

        public String getError() {
            return error;
        }
    }
}