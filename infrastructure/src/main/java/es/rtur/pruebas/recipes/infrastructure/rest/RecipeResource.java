package es.rtur.pruebas.recipes.infrastructure.rest;

import es.rtur.pruebas.recipes.application.dto.RecetaDTO;
import es.rtur.pruebas.recipes.application.usecase.CreateRecetaUseCase;
import es.rtur.pruebas.recipes.application.usecase.DeleteRecetaUseCase;
import es.rtur.pruebas.recipes.application.usecase.FilterRecetasUseCase;
import es.rtur.pruebas.recipes.application.usecase.GetRecetaUseCase;
import es.rtur.pruebas.recipes.application.usecase.ListRecetasUseCase;
import es.rtur.pruebas.recipes.application.usecase.SearchRecetasUseCase;
import es.rtur.pruebas.recipes.application.usecase.UpdateRecetaUseCase;
import es.rtur.pruebas.recipes.infrastructure.security.AuthenticatedUser;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.ExampleObject;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

@Path("/api/recipes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Recetas", description = "Gestión de recetas, búsquedas y filtros")
public class RecipeResource {

    private final ListRecetasUseCase listRecetasUseCase;
    private final GetRecetaUseCase getRecetaUseCase;
    private final SearchRecetasUseCase searchRecetasUseCase;
    private final FilterRecetasUseCase filterRecetasUseCase;
    private final CreateRecetaUseCase createRecetaUseCase;
    private final UpdateRecetaUseCase updateRecetaUseCase;
    private final DeleteRecetaUseCase deleteRecetaUseCase;
    private final SecurityIdentity securityIdentity;

    @Inject
    public RecipeResource(ListRecetasUseCase listRecetasUseCase,
                          GetRecetaUseCase getRecetaUseCase,
                          SearchRecetasUseCase searchRecetasUseCase,
                          FilterRecetasUseCase filterRecetasUseCase,
                          CreateRecetaUseCase createRecetaUseCase,
                          UpdateRecetaUseCase updateRecetaUseCase,
                          DeleteRecetaUseCase deleteRecetaUseCase,
                          SecurityIdentity securityIdentity) {
        this.listRecetasUseCase = listRecetasUseCase;
        this.getRecetaUseCase = getRecetaUseCase;
        this.searchRecetasUseCase = searchRecetasUseCase;
        this.filterRecetasUseCase = filterRecetasUseCase;
        this.createRecetaUseCase = createRecetaUseCase;
        this.updateRecetaUseCase = updateRecetaUseCase;
        this.deleteRecetaUseCase = deleteRecetaUseCase;
        this.securityIdentity = securityIdentity;
    }

    @GET
    @Operation(summary = "Listar todas las recetas", description = "Devuelve todas las recetas registradas")
        @APIResponses(value = {
                @APIResponse(responseCode = "200", description = "Listado recuperado",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON,
                        schema = @Schema(implementation = RecetaDTO.class, type = SchemaType.ARRAY),
                        examples = @ExampleObject(name = "recetas",
                        value = "[{\"id\":42,\"nombre\":\"Tortilla\",\"tiempo\":35}]")))
        })
    public List<RecetaDTO> list() {
        return listRecetasUseCase.execute();
    }

    @GET
    @Path("/{id}")
        @Operation(summary = "Obtener receta", description = "Devuelve los datos de una receta por su identificador")
        @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Receta encontrada",
                content = @Content(mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = RecetaDTO.class))),
            @APIResponse(responseCode = "404", description = "Receta no encontrada" )
        })
        public Response get(@Parameter(description = "Identificador de la receta", required = true, example = "42")
                @PathParam("id") Integer id) {
        try {
            RecetaDTO receta = getRecetaUseCase.execute(id);
            return Response.ok(receta).build();
        } catch (IllegalArgumentException ex) {
            return buildNotFoundResponse(ex);
        }
    }

    @GET
    @Path("/search")
    @Operation(summary = "Buscar recetas", description = "Busca recetas cuyo nombre contenga el término indicado")
        @APIResponse(responseCode = "200", description = "Resultados de búsqueda",
            content = @Content(mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = RecetaDTO.class, type = SchemaType.ARRAY)))
    public List<RecetaDTO> search(@Parameter(description = "Texto a buscar", example = "tortilla")
                                  @QueryParam("q") String term) {
        return searchRecetasUseCase.execute(term);
    }

    @GET
    @Path("/filter")
    @Operation(summary = "Filtrar recetas", description = "Filtra recetas por dificultad y/o categoría")
        @APIResponse(responseCode = "200", description = "Resultados filtrados",
            content = @Content(mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = RecetaDTO.class, type = SchemaType.ARRAY)))
    public List<RecetaDTO> filter(@Parameter(description = "Dificultad exacta", example = "MEDIA")
                                  @QueryParam("difficulty") String difficulty,
                                  @Parameter(description = "Categoría exacta", example = "Tradicional")
                                  @QueryParam("category") String category) {
        LinkedHashSet<RecetaDTO> filtered = new LinkedHashSet<>();
        if (difficulty != null && !difficulty.isBlank()) {
            filtered.addAll(filterRecetasUseCase.executeByDificultad(difficulty));
        }
        if (category != null && !category.isBlank()) {
            filtered.addAll(filterRecetasUseCase.executeByCategoria(category));
        }
        return new ArrayList<>(filtered);
    }

    @POST
    @RolesAllowed({"user", "admin"})
    @Operation(summary = "Crear receta", description = "Crea una nueva receta para el usuario autenticado")
        @APIResponses(value = {
            @APIResponse(responseCode = "201", description = "Receta creada",
                content = @Content(mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = RecetaDTO.class))),
            @APIResponse(responseCode = "400", description = "Datos inválidos"),
            @APIResponse(responseCode = "401", description = "Usuario no autenticado")
        })
        public Response create(@RequestBody(required = true, description = "Datos de la receta", content = @Content(
            schema = @Schema(implementation = RecetaDTO.class),
            example = "{\"nombre\":\"Tortilla\",\"tiempo\":35,\"comensales\":4}"))
                   @Valid RecetaDTO dto) {
        AuthenticatedUser user = AuthenticatedUser.from(securityIdentity);
        RecetaDTO created = createRecetaUseCase.execute(dto, user.id());
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({"user", "admin"})
    @Operation(summary = "Actualizar receta", description = "Actualiza la receta si el usuario es autor o administrador")
        @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Receta actualizada",
                content = @Content(mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = RecetaDTO.class))),
            @APIResponse(responseCode = "400", description = "Datos inválidos"),
            @APIResponse(responseCode = "401", description = "Usuario no autenticado"),
            @APIResponse(responseCode = "404", description = "Receta no encontrada")
        })
        public Response update(@Parameter(description = "Identificador de la receta", example = "42")
                   @PathParam("id") Integer id,
                   @RequestBody(required = true, description = "Datos a actualizar", content = @Content(
                       schema = @Schema(implementation = RecetaDTO.class)))
                   @Valid RecetaDTO dto) {
        try {
            AuthenticatedUser user = AuthenticatedUser.from(securityIdentity);
            RecetaDTO updated = updateRecetaUseCase.execute(id, dto, user.id(), user.isAdmin());
            return Response.ok(updated).build();
        } catch (IllegalArgumentException ex) {
            return buildNotFoundResponse(ex);
        }
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({"user", "admin"})
    @Operation(summary = "Eliminar receta", description = "Elimina la receta si el usuario tiene permiso necesario")
    @APIResponses(value = {
            @APIResponse(responseCode = "204", description = "Receta eliminada"),
            @APIResponse(responseCode = "401", description = "Usuario no autenticado"),
            @APIResponse(responseCode = "404", description = "Receta no encontrada")
    })
    public Response delete(@Parameter(description = "Identificador de la receta", example = "42")
                           @PathParam("id") Integer id) {
        try {
            AuthenticatedUser user = AuthenticatedUser.from(securityIdentity);
            deleteRecetaUseCase.execute(id, user.id(), user.isAdmin());
            return Response.noContent().build();
        } catch (IllegalArgumentException ex) {
            return buildNotFoundResponse(ex);
        }
    }

    private Response buildNotFoundResponse(IllegalArgumentException ex) {
        return Response.status(Response.Status.NOT_FOUND)
                .entity(Map.of("error", "not_found", "message", ex.getMessage()))
                .build();
    }
}
