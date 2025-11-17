package com.quarkus.recipes.application.usecase;

import com.quarkus.recipes.application.dto.RecipeDTO;
import com.quarkus.recipes.domain.entity.Recipe;
import com.quarkus.recipes.domain.repository.RecipeRepository;
import com.quarkus.recipes.domain.valueobject.RecipeId;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;

/**
 * Use Case: Create a new recipe
 * This class orchestrates the creation of a new recipe in the system
 */
@ApplicationScoped
public class CreateRecipeUseCase {

    private final RecipeRepository recipeRepository;

    @Inject
    public CreateRecipeUseCase(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    public RecipeDTO execute(@Valid RecipeDTO recipeDTO) {
        // Create domain entity
        RecipeId recipeId = RecipeId.generate();
        Recipe recipe = new Recipe(
                recipeId,
                recipeDTO.getName(),
                recipeDTO.getDescription(),
                recipeDTO.getPreparationTimeMinutes(),
                recipeDTO.getDifficulty()
        );

        // Add ingredients and instructions
        if (recipeDTO.getIngredients() != null) {
            recipeDTO.getIngredients().forEach(recipe::addIngredient);
        }
        if (recipeDTO.getInstructions() != null) {
            recipeDTO.getInstructions().forEach(recipe::addInstruction);
        }

        // Persist
        Recipe savedRecipe = recipeRepository.save(recipe);

        // Convert back to DTO
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
