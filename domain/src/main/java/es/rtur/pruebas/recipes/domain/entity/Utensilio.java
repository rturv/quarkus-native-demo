package es.rtur.pruebas.recipes.domain.entity;

import es.rtur.pruebas.recipes.domain.valueobject.UtensilioId;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Domain entity representing a Utensilio (Utensil/Cooking tool).
 * Part of the catalog that can be managed by admins (RF-06).
 */
@Getter
@ToString
public class Utensilio {

    private final UtensilioId id;
    
    @NotBlank(message = "Utensil name cannot be blank")
    private String nombre;
    
    @NotBlank(message = "Utensil type cannot be blank")
    private String tipo;
    
    private final LocalDateTime fCreacion;
    private LocalDateTime fModificacion;

    /**
     * Constructor for creating a new Utensilio (ID may be null for new entities before persistence).
     */
    public Utensilio(UtensilioId id, String nombre, String tipo) {
        this.id = id; // Allow null for new entities before persistence
        this.nombre = Objects.requireNonNull(nombre, "Utensil name cannot be null");
        this.tipo = Objects.requireNonNull(tipo, "Utensil type cannot be null");
        this.fCreacion = LocalDateTime.now();
        this.fModificacion = LocalDateTime.now();
    }

    /**
     * Constructor for loading existing Utensilio from repository.
     */
    public Utensilio(UtensilioId id, String nombre, String tipo,
                    LocalDateTime fCreacion, LocalDateTime fModificacion) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.fCreacion = fCreacion;
        this.fModificacion = fModificacion;
    }

    /**
     * Updates utensil details.
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
        Utensilio utensilio = (Utensilio) o;
        return Objects.equals(id, utensilio.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
