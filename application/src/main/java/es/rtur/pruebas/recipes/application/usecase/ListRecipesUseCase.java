package es.rtur.pruebas.recipes.application.usecase;

import es.rtur.pruebas.recipes.application.dto.RecipeDTO;
import es.rtur.pruebas.recipes.domain.entity.Recipe;
import es.rtur.pruebas.recipes.domain.repository.RecipeRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.stream.Collectors;

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
