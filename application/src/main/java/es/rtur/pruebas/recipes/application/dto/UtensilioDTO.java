package es.rtur.pruebas.recipes.application.dto;

import jakarta.validation.constraints.NotBlank;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for Utensilio entity.
 */
@Schema(name = "Utensilio", description = "Utensilio de cocina requerido por ciertas recetas")
public class UtensilioDTO {

    @Schema(description = "Identificador del utensilio", example = "5")
    private Integer id;

    @NotBlank(message = "Utensil name cannot be blank")
    @Schema(description = "Nombre del utensilio", example = "Sartén antiadherente")
    private String nombre;

    @NotBlank(message = "Utensil type cannot be blank")
    @Schema(description = "Tipo o uso principal", example = "Cocción")
    private String tipo;

    @Schema(description = "Fecha de creación", example = "2025-03-02T09:00:00")
    private LocalDateTime fCreacion;
    @Schema(description = "Última modificación", example = "2025-03-05T10:00:00")
    private LocalDateTime fModificacion;

    public UtensilioDTO() {
    }

    public UtensilioDTO(Integer id, String nombre, String tipo,
                       LocalDateTime fCreacion, LocalDateTime fModificacion) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.fCreacion = fCreacion;
        this.fModificacion = fModificacion;
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public LocalDateTime getFCreacion() { return fCreacion; }
    public void setFCreacion(LocalDateTime fCreacion) { this.fCreacion = fCreacion; }

    public LocalDateTime getFModificacion() { return fModificacion; }
    public void setFModificacion(LocalDateTime fModificacion) { this.fModificacion = fModificacion; }
}
