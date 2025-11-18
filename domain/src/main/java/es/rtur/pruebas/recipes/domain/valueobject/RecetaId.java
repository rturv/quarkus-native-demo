package es.rtur.pruebas.recipes.domain.valueobject;

import java.util.Objects;

/**
 * Value object representing a Receta ID.
 */
public class RecetaId {

    private final Integer value;

    private RecetaId(Integer value) {
        this.value = Objects.requireNonNull(value, "RecetaId value cannot be null");
    }

    public static RecetaId of(Integer value) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("RecetaId value must be a positive integer");
        }
        return new RecetaId(value);
    }

    public Integer getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RecetaId recetaId = (RecetaId) o;
        return Objects.equals(value, recetaId.value);
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
