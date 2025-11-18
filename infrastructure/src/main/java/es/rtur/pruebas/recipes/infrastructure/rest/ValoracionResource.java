package es.rtur.pruebas.recipes.infrastructure.rest;

import es.rtur.pruebas.recipes.application.dto.ValoracionDTO;
import es.rtur.pruebas.recipes.application.usecase.CreateValoracionUseCase;
import es.rtur.pruebas.recipes.application.usecase.GetValoracionesUseCase;
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
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api/recipes/{recipeId}/ratings")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Valoraciones", description = "Likes y dislikes de recetas")
public class ValoracionResource {

    private final CreateValoracionUseCase createValoracionUseCase;
    private final GetValoracionesUseCase getValoracionesUseCase;
    private final SecurityIdentity securityIdentity;

    @Inject
    public ValoracionResource(CreateValoracionUseCase createValoracionUseCase,
                              GetValoracionesUseCase getValoracionesUseCase,
                              SecurityIdentity securityIdentity) {
        this.createValoracionUseCase = createValoracionUseCase;
        this.getValoracionesUseCase = getValoracionesUseCase;
        this.securityIdentity = securityIdentity;
    }

    @GET
    @PermitAll
    @Operation(summary = "Resumen de valoraciones", description = "Devuelve el total de likes y dislikes")
    public ValoracionSummaryResponse summary(@PathParam("recipeId") Integer recipeId) {
        GetValoracionesUseCase.ValoracionSummary summary = getValoracionesUseCase.execute(recipeId);
        return new ValoracionSummaryResponse(summary.getLikes(), summary.getDislikes(), summary.getTotal());
    }

    @POST
    @RolesAllowed({"user", "admin"})
    @Operation(summary = "Valorar receta", description = "Permite dar me gusta o no me gusta a la receta autenticada")
    public Response rate(@PathParam("recipeId") Integer recipeId, @Valid ValoracionDTO dto) {
        AuthenticatedUser user = AuthenticatedUser.from(securityIdentity);
        dto.setIdReceta(recipeId);
        dto.setIdUsuario(user.id());
        ValoracionDTO persisted = createValoracionUseCase.execute(dto, user.id());
        return Response.status(Response.Status.CREATED).entity(persisted).build();
    }

    public static final class ValoracionSummaryResponse {
        private final long likes;
        private final long dislikes;
        private final long total;

        public ValoracionSummaryResponse(long likes, long dislikes, long total) {
            this.likes = likes;
            this.dislikes = dislikes;
            this.total = total;
        }

        public long getLikes() {
            return likes;
        }

        public long getDislikes() {
            return dislikes;
        }

        public long getTotal() {
            return total;
        }
    }
}