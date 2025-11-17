package es.rtur.pruebas.recipes.domain.entity;

import es.rtur.pruebas.recipes.domain.valueobject.RecipeId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RecipeTest {

    private Recipe recipe;
    private RecipeId recipeId;

    @BeforeEach
    void setUp() {
        recipeId = RecipeId.generate();
        recipe = new Recipe(
                recipeId,
                "Pasta Carbonara",
                "Classic Italian dish",
                25,
                DifficultyLevel.MEDIUM
        );
    }

    @Test
    void shouldCreateRecipeWithValidData() {
        assertNotNull(recipe);
        assertEquals("Pasta Carbonara", recipe.getName());
        assertEquals(25, recipe.getPreparationTimeMinutes());
        assertEquals(DifficultyLevel.MEDIUM, recipe.getDifficulty());
    }

    @Test
    void shouldIdentifyQuickRecipe() {
        Recipe quickRecipe = new Recipe(
                RecipeId.generate(),
                "Quick Salad",
                "Fast and healthy",
                15,
                DifficultyLevel.EASY
        );

        assertTrue(quickRecipe.isQuickRecipe(), "Recipe under 30 minutes should be quick");
    }

    @Test
    void shouldNotIdentifySlowRecipeAsQuick() {
        Recipe slowRecipe = new Recipe(
                RecipeId.generate(),
                "Slow Cooked Stew",
                "Takes time but worth it",
                120,
                DifficultyLevel.HARD
        );

        assertFalse(slowRecipe.isQuickRecipe(), "Recipe over 30 minutes should not be quick");
    }

    @Test
    void shouldAddIngredient() {
        recipe.addIngredient("Pasta");
        recipe.addIngredient("Eggs");

        assertEquals(2, recipe.getIngredients().size());
        assertTrue(recipe.getIngredients().contains("Pasta"));
        assertTrue(recipe.getIngredients().contains("Eggs"));
    }

    @Test
    void shouldNotAddBlankIngredient() {
        recipe.addIngredient("");
        recipe.addIngredient("   ");
        recipe.addIngredient(null);

        assertEquals(0, recipe.getIngredients().size(), "Blank ingredients should not be added");
    }

    @Test
    void shouldRemoveIngredient() {
        recipe.addIngredient("Pasta");
        recipe.addIngredient("Eggs");
        recipe.removeIngredient("Pasta");

        assertEquals(1, recipe.getIngredients().size());
        assertFalse(recipe.getIngredients().contains("Pasta"));
        assertTrue(recipe.getIngredients().contains("Eggs"));
    }

    @Test
    void shouldAddInstruction() {
        recipe.addInstruction("Boil water");
        recipe.addInstruction("Cook pasta");

        assertEquals(2, recipe.getInstructions().size());
        assertEquals("Boil water", recipe.getInstructions().get(0));
        assertEquals("Cook pasta", recipe.getInstructions().get(1));
    }

    @Test
    void shouldUpdateDetails() {
        recipe.updateDetails(
                "Spaghetti Carbonara",
                "Updated description",
                30,
                DifficultyLevel.EASY
        );

        assertEquals("Spaghetti Carbonara", recipe.getName());
        assertEquals("Updated description", recipe.getDescription());
        assertEquals(30, recipe.getPreparationTimeMinutes());
        assertEquals(DifficultyLevel.EASY, recipe.getDifficulty());
    }

    @Test
    void shouldNotUpdateWithInvalidData() {
        String originalName = recipe.getName();

        recipe.updateDetails("", null, -5, null);

        assertEquals(originalName, recipe.getName());
    }

    @Test
    void shouldReturnImmutableIngredientsList() {
        recipe.addIngredient("Pasta");

        assertThrows(UnsupportedOperationException.class, () -> {
            recipe.getIngredients().add("Should not work");
        }, "Should not be able to modify ingredients list directly");
    }

    @Test
    void shouldReturnImmutableInstructionsList() {
        recipe.addInstruction("Step 1");

        assertThrows(UnsupportedOperationException.class, () -> {
            recipe.getInstructions().add("Should not work");
        }, "Should not be able to modify instructions list directly");
    }

    @Test
    void shouldHaveEqualityBasedOnId() {
        Recipe recipe1 = new Recipe(recipeId, "Recipe 1", "Desc", 10, DifficultyLevel.EASY);
        Recipe recipe2 = new Recipe(recipeId, "Recipe 2", "Different", 20, DifficultyLevel.HARD);

        assertEquals(recipe1, recipe2, "Recipes with same ID should be equal");
        assertEquals(recipe1.hashCode(), recipe2.hashCode(), "Recipes with same ID should have same hash");
    }

    @Test
    void shouldThrowExceptionForNullId() {
        assertThrows(NullPointerException.class, () -> {
            new Recipe(null, "Name", "Desc", 10, DifficultyLevel.EASY);
        }, "Should not allow null ID");
    }

    @Test
    void shouldThrowExceptionForNullName() {
        assertThrows(NullPointerException.class, () -> {
            new Recipe(RecipeId.generate(), null, "Desc", 10, DifficultyLevel.EASY);
        }, "Should not allow null name");
    }

    @Test
    void shouldThrowExceptionForNullDifficulty() {
        assertThrows(NullPointerException.class, () -> {
            new Recipe(RecipeId.generate(), "Name", "Desc", 10, null);
        }, "Should not allow null difficulty");
    }
}
