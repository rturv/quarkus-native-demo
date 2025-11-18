package es.rtur.pruebas.recipes.application.dto;

import jakarta.validation.constraints.NotBlank;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for Ingrediente entity.
 */
@Schema(name = "Ingrediente", description = "Ingrediente disponible para asociar a recetas")
public class IngredienteDTO {

    @Schema(description = "Identificador del ingrediente", example = "15")
    private Integer id;

    @NotBlank(message = "Ingredient name cannot be blank")
    @Schema(description = "Nombre del ingrediente", example = "Tomate")
    private String nombre;

    @NotBlank(message = "Ingredient type cannot be blank")
    @Schema(description = "Tipo o categoría", example = "Verdura")
    private String tipo;

    @Schema(description = "Fecha de creación", example = "2025-01-10T08:00:00")
    private LocalDateTime fCreacion;
    @Schema(description = "Última modificación", example = "2025-01-12T08:00:00")
    private LocalDateTime fModificacion;

    public IngredienteDTO() {
    }

    public IngredienteDTO(Integer id, String nombre, String tipo,
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
