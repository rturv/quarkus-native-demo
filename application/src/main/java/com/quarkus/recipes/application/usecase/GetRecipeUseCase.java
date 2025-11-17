package com.quarkus.recipes.application.usecase;

import com.quarkus.recipes.application.dto.RecipeDTO;
import com.quarkus.recipes.domain.entity.Recipe;
import com.quarkus.recipes.domain.repository.RecipeRepository;
import com.quarkus.recipes.domain.valueobject.RecipeId;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Optional;

/**
 * Use Case: Get a recipe by ID
 */
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
