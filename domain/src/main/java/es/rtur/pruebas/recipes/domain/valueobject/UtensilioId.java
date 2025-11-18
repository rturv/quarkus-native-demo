package es.rtur.pruebas.recipes.domain.valueobject;

import java.util.Objects;

/**
 * Value object representing a Utensilio ID.
 */
public class UtensilioId {

    private final Integer value;

    private UtensilioId(Integer value) {
        this.value = Objects.requireNonNull(value, "UtensilioId value cannot be null");
    }

    public static UtensilioId of(Integer value) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("UtensilioId value must be a positive integer");
        }
        return new UtensilioId(value);
    }

    public Integer getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UtensilioId that = (UtensilioId) o;
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
