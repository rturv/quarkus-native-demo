package com.quarkus.recipes.domain.repository;

import com.quarkus.recipes.domain.entity.Recipe;
import com.quarkus.recipes.domain.valueobject.RecipeId;

import java.util.List;
import java.util.Optional;

/**
 * Domain Repository Interface
 * Defines the contract for Recipe persistence without any implementation details.
 * This follows the Dependency Inversion Principle - domain defines the interface,
 * infrastructure provides the implementation.
 */
public interface RecipeRepository {

    /**
     * Save a recipe (create or update)
     */
    Recipe save(Recipe recipe);

    /**
     * Find a recipe by its ID
     */
    Optional<Recipe> findById(RecipeId id);

    /**
     * Find all recipes
     */
    List<Recipe> findAll();

    /**
     * Find recipes by name (partial match)
     */
    List<Recipe> findByNameContaining(String name);

    /**
     * Delete a recipe by its ID
     */
    void deleteById(RecipeId id);

    /**
     * Check if a recipe exists by ID
     */
    boolean existsById(RecipeId id);
}
