package es.rtur.pruebas.recipes.domain.repository;

import es.rtur.pruebas.recipes.domain.entity.Recipe;
import es.rtur.pruebas.recipes.domain.valueobject.RecipeId;

import java.util.List;
import java.util.Optional;

public interface RecipeRepository {

    Recipe save(Recipe recipe);

    Optional<Recipe> findById(RecipeId id);

    List<Recipe> findAll();

    List<Recipe> findByNameContaining(String name);

    void deleteById(RecipeId id);

    boolean existsById(RecipeId id);
}
