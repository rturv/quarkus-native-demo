package es.rtur.pruebas.recipes.domain.valueobject;

import lombok.Value;

/**
 * Value object representing a Comentario ID.
 */
@Value(staticConstructor = "of")
public class ComentarioId {

    private final Integer value;

    private ComentarioId(Integer value) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("ComentarioId value must be a positive integer");
        }
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
