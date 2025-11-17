package com.quarkus.recipes.application.usecase;

import com.quarkus.recipes.application.dto.RecipeDTO;
import com.quarkus.recipes.domain.entity.Recipe;
import com.quarkus.recipes.domain.repository.RecipeRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Use Case: List all recipes
 */
@ApplicationScoped
public class ListRecipesUseCase {

    private final RecipeRepository recipeRepository;

    @Inject
    public ListRecipesUseCase(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    public List<RecipeDTO> execute() {
        return recipeRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
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
