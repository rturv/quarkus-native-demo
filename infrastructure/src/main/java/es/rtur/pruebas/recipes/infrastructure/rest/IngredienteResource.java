package es.rtur.pruebas.recipes.infrastructure.rest;

import es.rtur.pruebas.recipes.application.dto.IngredienteDTO;
import es.rtur.pruebas.recipes.application.usecase.CreateIngredienteUseCase;
import es.rtur.pruebas.recipes.application.usecase.ListIngredientesUseCase;
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
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;

@Path("/api/ingredients")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Ingredientes", description = "Gestión de ingredientes")
public class IngredienteResource {

    private final ListIngredientesUseCase listIngredientesUseCase;
    private final CreateIngredienteUseCase createIngredienteUseCase;
    private final SecurityIdentity securityIdentity;

    @Inject
    public IngredienteResource(ListIngredientesUseCase listIngredientesUseCase,
                               CreateIngredienteUseCase createIngredienteUseCase,
                               SecurityIdentity securityIdentity) {
        this.listIngredientesUseCase = listIngredientesUseCase;
        this.createIngredienteUseCase = createIngredienteUseCase;
        this.securityIdentity = securityIdentity;
    }

    @GET
    @PermitAll
    @Operation(summary = "Listar ingredientes", description = "Devuelve todos los ingredientes registrados")
    public List<IngredienteDTO> list() {
        return listIngredientesUseCase.execute();
    }

    @POST
    @RolesAllowed("admin")
    @Operation(summary = "Crear ingrediente", description = "Solo administradores pueden agregar ingredientes")
    public Response create(@Valid IngredienteDTO dto) {
        AuthenticatedUser user = AuthenticatedUser.from(securityIdentity);
        IngredienteDTO created = createIngredienteUseCase.execute(dto, user.isAdmin());
        return Response.status(Response.Status.CREATED).entity(created).build();
    }
}