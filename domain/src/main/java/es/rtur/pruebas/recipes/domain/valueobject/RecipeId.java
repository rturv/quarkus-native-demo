package es.rtur.pruebas.recipes.domain.valueobject;

import lombok.Value;

import java.util.UUID;

@Value(staticConstructor = "of")
public class RecipeId {

    private final String value;

    private RecipeId(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("RecipeId value cannot be null or blank");
        }
        this.value = value;
    }

    public static RecipeId generate() {
        return new RecipeId(UUID.randomUUID().toString());
    }

    @Override
    public String toString() {
        return value;
    }
}
