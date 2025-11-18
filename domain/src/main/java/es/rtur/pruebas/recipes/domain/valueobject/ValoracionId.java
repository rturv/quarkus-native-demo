package es.rtur.pruebas.recipes.domain.valueobject;

import java.util.Objects;

/**
 * Value object representing a Valoracion ID.
 */
public class ValoracionId {

    private final Integer value;

    private ValoracionId(Integer value) {
        this.value = Objects.requireNonNull(value, "ValoracionId value cannot be null");
    }

    public static ValoracionId of(Integer value) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("ValoracionId value must be a positive integer");
        }
        return new ValoracionId(value);
    }

    public Integer getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ValoracionId that = (ValoracionId) o;
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
