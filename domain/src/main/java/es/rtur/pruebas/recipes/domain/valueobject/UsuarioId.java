package es.rtur.pruebas.recipes.domain.valueobject;

import java.util.Objects;

/**
 * Value object representing a Usuario ID.
 * Can be either an auto-generated database ID or a specific value.
 */
public class UsuarioId {

    private final Integer value;

    private UsuarioId(Integer value) {
        this.value = Objects.requireNonNull(value, "UsuarioId value cannot be null");
    }

    public static UsuarioId of(Integer value) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("UsuarioId value must be a positive integer");
        }
        return new UsuarioId(value);
    }

    public Integer getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UsuarioId usuarioId = (UsuarioId) o;
        return Objects.equals(value, usuarioId.value);
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
