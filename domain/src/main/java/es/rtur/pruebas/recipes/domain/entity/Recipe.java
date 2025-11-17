package es.rtur.pruebas.recipes.domain.entity;

import es.rtur.pruebas.recipes.domain.valueobject.RecipeId;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Recipe {

    private final RecipeId id;
    @NotBlank(message = "Recipe name cannot be blank")
    private String name;
    private String description;
    @NotNull(message = "Preparation time must be specified")
    @Positive(message = "Preparation time must be positive")
    private Integer preparationTimeMinutes;
    @NotNull(message = "Difficulty level must be specified")
    private DifficultyLevel difficulty;
    private final List<String> ingredients;
    private final List<String> instructions;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Recipe(RecipeId id, String name, String description,
                  Integer preparationTimeMinutes, DifficultyLevel difficulty) {
        this.id = Objects.requireNonNull(id, "Recipe ID cannot be null");
        this.name = Objects.requireNonNull(name, "Recipe name cannot be null");
        this.description = description;
        this.preparationTimeMinutes = preparationTimeMinutes;
        this.difficulty = Objects.requireNonNull(difficulty, "Difficulty level cannot be null");
        this.ingredients = new ArrayList<>();
        this.instructions = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void updateDetails(String name, String description, Integer preparationTime, DifficultyLevel difficulty) {
        if (name != null && !name.isBlank()) {
            this.name = name;
        }
        if (description != null) {
            this.description = description;
        }
        if (preparationTime != null && preparationTime > 0) {
            this.preparationTimeMinutes = preparationTime;
        }
        if (difficulty != null) {
            this.difficulty = difficulty;
        }
        this.updatedAt = LocalDateTime.now();
    }

    public void addIngredient(String ingredient) {
        if (ingredient != null && !ingredient.isBlank()) {
            this.ingredients.add(ingredient);
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void removeIngredient(String ingredient) {
        this.ingredients.remove(ingredient);
        this.updatedAt = LocalDateTime.now();
    }

    public void addInstruction(String instruction) {
        if (instruction != null && !instruction.isBlank()) {
            this.instructions.add(instruction);
            this.updatedAt = LocalDateTime.now();
        }
    }

    public boolean isQuickRecipe() {
        return this.preparationTimeMinutes != null && this.preparationTimeMinutes <= 30;
    }

    public RecipeId getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Integer getPreparationTimeMinutes() { return preparationTimeMinutes; }
    public DifficultyLevel getDifficulty() { return difficulty; }
    public List<String> getIngredients() { return Collections.unmodifiableList(ingredients); }
    public List<String> getInstructions() { return Collections.unmodifiableList(instructions); }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Recipe recipe = (Recipe) o;
        return Objects.equals(id, recipe.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return "Recipe{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", difficulty=" + difficulty +
                ", preparationTimeMinutes=" + preparationTimeMinutes +
                '}';
    }
}
