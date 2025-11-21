package es.rtur.pruebas.recipes.domain.valueobject;

import lombok.AccessLevel;
import lombok.Value;

/**
 * Value object representing a Receta ID.
 */
@Value(staticConstructor = "of")
public class RecetaId {

    private final Integer value;

    private RecetaId(Integer value) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("RecetaId value must be a positive integer");
        }
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
