package es.rtur.pruebas.recipes.domain.valueobject;

import java.util.Objects;

/**
 * Value object representing an Ingrediente ID.
 */
public class IngredienteId {

    private final Integer value;

    private IngredienteId(Integer value) {
        this.value = Objects.requireNonNull(value, "IngredienteId value cannot be null");
    }

    public static IngredienteId of(Integer value) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("IngredienteId value must be a positive integer");
        }
        return new IngredienteId(value);
    }

    public Integer getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IngredienteId that = (IngredienteId) o;
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
