package es.rtur.pruebas.recipes.domain.valueobject;

import lombok.Value;

/**
 * Value object representing a Utensilio ID.
 */
@Value(staticConstructor = "of")
public class UtensilioId {

    private final Integer value;

    private UtensilioId(Integer value) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("UtensilioId value must be a positive integer");
        }
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
