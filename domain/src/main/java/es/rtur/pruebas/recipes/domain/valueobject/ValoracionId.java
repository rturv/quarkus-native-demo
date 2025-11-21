package es.rtur.pruebas.recipes.domain.valueobject;

import lombok.Value;

/**
 * Value object representing a Valoracion ID.
 */
@Value(staticConstructor = "of")
public class ValoracionId {

    private final Integer value;

    private ValoracionId(Integer value) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("ValoracionId value must be a positive integer");
        }
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
