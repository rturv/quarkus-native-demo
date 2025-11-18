package es.rtur.pruebas.recipes.infrastructure.rest;

import es.rtur.pruebas.recipes.application.dto.UsuarioDTO;
import es.rtur.pruebas.recipes.application.usecase.ListUsuariosUseCase;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;

@Path("/api/users")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Usuarios", description = "Panel administrativo de usuarios")
public class UsuarioResource {

    private final ListUsuariosUseCase listUsuariosUseCase;

    @Inject
    public UsuarioResource(ListUsuariosUseCase listUsuariosUseCase) {
        this.listUsuariosUseCase = listUsuariosUseCase;
    }

    @GET
    @RolesAllowed("admin")
    @Operation(summary = "Listar usuarios", description = "Solo administradores pueden consultar la lista")
    public List<UsuarioDTO> list() {
        return listUsuariosUseCase.execute();
    }
}