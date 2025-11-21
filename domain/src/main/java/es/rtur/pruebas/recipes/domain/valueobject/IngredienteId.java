package es.rtur.pruebas.recipes.domain.valueobject;

import lombok.Value;

/**
 * Value object representing an Ingrediente ID.
 */
@Value(staticConstructor = "of")
public class IngredienteId {

    private final Integer value;

    private IngredienteId(Integer value) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("IngredienteId value must be a positive integer");
        }
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
