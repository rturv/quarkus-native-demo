package es.rtur.pruebas.recipes.application.usecase;

import es.rtur.pruebas.recipes.application.dto.RecipeDTO;
import es.rtur.pruebas.recipes.domain.entity.Recipe;
import es.rtur.pruebas.recipes.domain.repository.RecipeRepository;
import es.rtur.pruebas.recipes.domain.valueobject.RecipeId;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Optional;

@ApplicationScoped
public class GetRecipeUseCase {

    private final RecipeRepository recipeRepository;

    @Inject
    public GetRecipeUseCase(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    public Optional<RecipeDTO> execute(String recipeId) {
        RecipeId id = RecipeId.of(recipeId);
        return recipeRepository.findById(id)
                .map(this::mapToDTO);
    }

    private RecipeDTO mapToDTO(Recipe recipe) {
        return new RecipeDTO(
                recipe.getId().getValue(),
                recipe.getName(),
                recipe.getDescription(),
                recipe.getPreparationTimeMinutes(),
                recipe.getDifficulty(),
                recipe.getIngredients(),
                recipe.getInstructions(),
                recipe.getCreatedAt(),
                recipe.getUpdatedAt()
        );
    }
}
