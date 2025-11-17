package es.rtur.pruebas.recipes.application.usecase;

import es.rtur.pruebas.recipes.application.dto.RecipeDTO;
import es.rtur.pruebas.recipes.domain.entity.Recipe;
import es.rtur.pruebas.recipes.domain.repository.RecipeRepository;
import es.rtur.pruebas.recipes.domain.valueobject.RecipeId;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;

@ApplicationScoped
public class CreateRecipeUseCase {

    private final RecipeRepository recipeRepository;

    @Inject
    public CreateRecipeUseCase(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    public RecipeDTO execute(@Valid RecipeDTO recipeDTO) {
        RecipeId recipeId = RecipeId.generate();
        Recipe recipe = new Recipe(
                recipeId,
                recipeDTO.getName(),
                recipeDTO.getDescription(),
                recipeDTO.getPreparationTimeMinutes(),
                recipeDTO.getDifficulty()
        );

        if (recipeDTO.getIngredients() != null) {
            recipeDTO.getIngredients().forEach(recipe::addIngredient);
        }
        if (recipeDTO.getInstructions() != null) {
            recipeDTO.getInstructions().forEach(recipe::addInstruction);
        }

        Recipe savedRecipe = recipeRepository.save(recipe);

        return mapToDTO(savedRecipe);
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
