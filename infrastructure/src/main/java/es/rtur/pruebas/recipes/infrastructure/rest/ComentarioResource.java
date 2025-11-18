package es.rtur.pruebas.recipes.infrastructure.rest;

import es.rtur.pruebas.recipes.application.dto.ComentarioDTO;
import es.rtur.pruebas.recipes.application.usecase.CreateComentarioUseCase;
import es.rtur.pruebas.recipes.application.usecase.ListComentariosUseCase;
import es.rtur.pruebas.recipes.infrastructure.security.AuthenticatedUser;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;

@Path("/api/recipes/{recipeId}/comments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Comentarios", description = "Comentarios en recetas")
public class ComentarioResource {

    private final CreateComentarioUseCase createComentarioUseCase;
    private final ListComentariosUseCase listComentariosUseCase;
    private final SecurityIdentity securityIdentity;

    @Inject
    public ComentarioResource(CreateComentarioUseCase createComentarioUseCase,
                              ListComentariosUseCase listComentariosUseCase,
                              SecurityIdentity securityIdentity) {
        this.createComentarioUseCase = createComentarioUseCase;
        this.listComentariosUseCase = listComentariosUseCase;
        this.securityIdentity = securityIdentity;
    }

    @GET
    @PermitAll
    @Operation(summary = "Listar comentarios", description = "Recupera todos los comentarios activos de una receta")
    @APIResponse(responseCode = "200", description = "Comentarios recuperados",
            content = @Content(mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = ComentarioDTO.class, type = SchemaType.ARRAY)))
    public List<ComentarioDTO> list(@Parameter(description = "Identificador de la receta", required = true, example = "42")
                                    @PathParam("recipeId") Integer recipeId) {
        return listComentariosUseCase.execute(recipeId);
    }

    @POST
    @RolesAllowed({"user", "admin"})
    @Operation(summary = "Crear comentario", description = "Agrega un comentario para el usuario autenticado")
    @APIResponse(responseCode = "201", description = "Comentario creado",
            content = @Content(mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = ComentarioDTO.class)))
    public Response create(@Parameter(description = "Identificador de la receta", example = "42")
                           @PathParam("recipeId") Integer recipeId,
                           @RequestBody(description = "Contenido del comentario", required = true,
                                   content = @Content(schema = @Schema(implementation = ComentarioDTO.class)))
                           @Valid ComentarioDTO dto) {
        AuthenticatedUser user = AuthenticatedUser.from(securityIdentity);
        dto.setIdReceta(recipeId);
        ComentarioDTO created = createComentarioUseCase.execute(dto, user.id());
        return Response.status(Response.Status.CREATED).entity(created).build();
    }
}