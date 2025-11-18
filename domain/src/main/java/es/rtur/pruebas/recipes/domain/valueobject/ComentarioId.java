package es.rtur.pruebas.recipes.domain.valueobject;

import java.util.Objects;

/**
 * Value object representing a Comentario ID.
 */
public class ComentarioId {

    private final Integer value;

    private ComentarioId(Integer value) {
        this.value = Objects.requireNonNull(value, "ComentarioId value cannot be null");
    }

    public static ComentarioId of(Integer value) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("ComentarioId value must be a positive integer");
        }
        return new ComentarioId(value);
    }

    public Integer getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ComentarioId that = (ComentarioId) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
