package es.rtur.pruebas.recipes.infrastructure.rest;

import es.rtur.pruebas.recipes.application.dto.UtensilioDTO;
import es.rtur.pruebas.recipes.application.usecase.CreateUtensilioUseCase;
import es.rtur.pruebas.recipes.application.usecase.ListUtensiliosUseCase;
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

@Path("/api/utensils")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Utensilios", description = "Gestión de utensilios en cocina")
public class UtensilioResource {

    private final ListUtensiliosUseCase listUtensiliosUseCase;
    private final CreateUtensilioUseCase createUtensilioUseCase;
    private final SecurityIdentity securityIdentity;

    @Inject
    public UtensilioResource(ListUtensiliosUseCase listUtensiliosUseCase,
                             CreateUtensilioUseCase createUtensilioUseCase,
                             SecurityIdentity securityIdentity) {
        this.listUtensiliosUseCase = listUtensiliosUseCase;
        this.createUtensilioUseCase = createUtensilioUseCase;
        this.securityIdentity = securityIdentity;
    }

    @GET
    @PermitAll
    @Operation(summary = "Listar utensilios", description = "Devuelve la colección de utensilios disponibles")
    public List<UtensilioDTO> list() {
        return listUtensiliosUseCase.execute();
    }

    @POST
    @RolesAllowed("admin")
    @Operation(summary = "Crear utensilio", description = "Solo administradores pueden registrar utensilios")
    public Response create(@Valid UtensilioDTO dto) {
        AuthenticatedUser user = AuthenticatedUser.from(securityIdentity);
        UtensilioDTO created = createUtensilioUseCase.execute(dto, user.isAdmin());
        return Response.status(Response.Status.CREATED).entity(created).build();
    }
}