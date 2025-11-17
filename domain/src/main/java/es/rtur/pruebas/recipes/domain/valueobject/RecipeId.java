package es.rtur.pruebas.recipes.domain.valueobject;

import java.util.Objects;
import java.util.UUID;

public class RecipeId {

    private final String value;

    private RecipeId(String value) {
        this.value = Objects.requireNonNull(value, "RecipeId value cannot be null");
    }

    public static RecipeId of(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("RecipeId value cannot be null or blank");
        }
        return new RecipeId(value);
    }

    public static RecipeId generate() {
        return new RecipeId(UUID.randomUUID().toString());
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RecipeId recipeId = (RecipeId) o;
        return Objects.equals(value, recipeId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
