package com.quarkus.recipes.infrastructure.persistence;

import com.quarkus.recipes.domain.entity.Recipe;
import com.quarkus.recipes.domain.repository.RecipeRepository;
import com.quarkus.recipes.domain.valueobject.RecipeId;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of RecipeRepository using JPA/Panache
 * This adapter connects the domain to the database
 */
@ApplicationScoped
public class RecipeRepositoryImpl implements RecipeRepository {

    @Override
    @Transactional
    public Recipe save(Recipe recipe) {
        RecipeEntity entity = toEntity(recipe);
        entity.persist();
        return toDomain(entity);
    }

    @Override
    public Optional<Recipe> findById(RecipeId id) {
        RecipeEntity entity = RecipeEntity.findById(id.getValue());
        return Optional.ofNullable(entity).map(this::toDomain);
    }

    @Override
    public List<Recipe> findAll() {
        return RecipeEntity.<RecipeEntity>listAll()
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Recipe> findByNameContaining(String name) {
        return RecipeEntity.<RecipeEntity>list("LOWER(name) LIKE LOWER(?1)", "%" + name + "%")
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteById(RecipeId id) {
        RecipeEntity.deleteById(id.getValue());
    }

    @Override
    public boolean existsById(RecipeId id) {
        return RecipeEntity.findById(id.getValue()) != null;
    }

    // Mapping methods
    private RecipeEntity toEntity(Recipe recipe) {
        RecipeEntity entity = new RecipeEntity();
        entity.id = recipe.getId().getValue();
        entity.name = recipe.getName();
        entity.description = recipe.getDescription();
        entity.preparationTimeMinutes = recipe.getPreparationTimeMinutes();
        entity.difficulty = recipe.getDifficulty();
        entity.ingredients = recipe.getIngredients();
        entity.instructions = recipe.getInstructions();
        entity.createdAt = recipe.getCreatedAt();
        entity.updatedAt = recipe.getUpdatedAt();
        return entity;
    }

    private Recipe toDomain(RecipeEntity entity) {
        Recipe recipe = new Recipe(
                RecipeId.of(entity.id),
                entity.name,
                entity.description,
                entity.preparationTimeMinutes,
                entity.difficulty
        );
        
        // Add ingredients and instructions
        if (entity.ingredients != null) {
            entity.ingredients.forEach(recipe::addIngredient);
        }
        if (entity.instructions != null) {
            entity.instructions.forEach(recipe::addInstruction);
        }
        
        return recipe;
    }
}
