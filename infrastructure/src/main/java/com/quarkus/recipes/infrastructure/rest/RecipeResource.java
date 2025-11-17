package com.quarkus.recipes.infrastructure.rest;

import com.quarkus.recipes.application.dto.RecipeDTO;
import com.quarkus.recipes.application.usecase.CreateRecipeUseCase;
import com.quarkus.recipes.application.usecase.GetRecipeUseCase;
import com.quarkus.recipes.application.usecase.ListRecipesUseCase;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;

/**
 * REST Resource for Recipe operations
 * This is the entry point for HTTP requests
 */
@Path("/api/recipes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Recipes", description = "Recipe management operations")
public class RecipeResource {

    private final CreateRecipeUseCase createRecipeUseCase;
    private final GetRecipeUseCase getRecipeUseCase;
    private final ListRecipesUseCase listRecipesUseCase;

    @Inject
    public RecipeResource(CreateRecipeUseCase createRecipeUseCase,
                          GetRecipeUseCase getRecipeUseCase,
                          ListRecipesUseCase listRecipesUseCase) {
        this.createRecipeUseCase = createRecipeUseCase;
        this.getRecipeUseCase = getRecipeUseCase;
        this.listRecipesUseCase = listRecipesUseCase;
    }

    @POST
    @Operation(summary = "Create a new recipe", description = "Creates a new recipe in the database")
    public Response createRecipe(@Valid RecipeDTO recipeDTO) {
        RecipeDTO created = createRecipeUseCase.execute(recipeDTO);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @GET
    @Operation(summary = "List all recipes", description = "Returns a list of all recipes")
    public List<RecipeDTO> listRecipes() {
        return listRecipesUseCase.execute();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Get recipe by ID", description = "Returns a single recipe by its ID")
    public Response getRecipe(@PathParam("id") String id) {
        return getRecipeUseCase.execute(id)
                .map(recipe -> Response.ok(recipe).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }
}
