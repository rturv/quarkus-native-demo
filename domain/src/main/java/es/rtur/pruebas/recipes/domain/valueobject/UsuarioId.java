package es.rtur.pruebas.recipes.domain.valueobject;

import lombok.Value;

/**
 * Value object representing a Usuario ID.
 * Can be either an auto-generated database ID or a specific value.
 */
@Value(staticConstructor = "of")
public class UsuarioId {

    private final Integer value;

    private UsuarioId(Integer value) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("UsuarioId value must be a positive integer");
        }
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
