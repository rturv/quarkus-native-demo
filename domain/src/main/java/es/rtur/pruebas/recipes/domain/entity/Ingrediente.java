package es.rtur.pruebas.recipes.domain.entity;

import es.rtur.pruebas.recipes.domain.valueobject.IngredienteId;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Domain entity representing an Ingrediente (Ingredient).
 * Part of the catalog that can be managed by admins (RF-06).
 */
@Getter
@ToString
public class Ingrediente {

    private final IngredienteId id;
    
    @NotBlank(message = "Ingredient name cannot be blank")
    private String nombre;
    
    @NotBlank(message = "Ingredient type cannot be blank")
    private String tipo;
    
    private final LocalDateTime fCreacion;
    private LocalDateTime fModificacion;

    /**
     * Constructor for creating a new Ingrediente (ID may be null for new entities before persistence).
     */
    public Ingrediente(IngredienteId id, String nombre, String tipo) {
        this.id = id; // Allow null for new entities before persistence
        this.nombre = Objects.requireNonNull(nombre, "Ingredient name cannot be null");
        this.tipo = Objects.requireNonNull(tipo, "Ingredient type cannot be null");
        this.fCreacion = LocalDateTime.now();
        this.fModificacion = LocalDateTime.now();
    }

    /**
     * Constructor for loading existing Ingrediente from repository.
     */
    public Ingrediente(IngredienteId id, String nombre, String tipo,
                      LocalDateTime fCreacion, LocalDateTime fModificacion) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.fCreacion = fCreacion;
        this.fModificacion = fModificacion;
    }

    /**
     * Updates ingredient details.
     */
    public void updateDetails(String nombre, String tipo) {
        if (nombre != null && !nombre.isBlank()) {
            this.nombre = nombre;
        }
        if (tipo != null && !tipo.isBlank()) {
            this.tipo = tipo;
        }
        this.fModificacion = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ingrediente that = (Ingrediente) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
