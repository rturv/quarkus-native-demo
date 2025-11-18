package es.rtur.pruebas.recipes.application.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for Ingrediente entity.
 */
public class IngredienteDTO {

    private Integer id;

    @NotBlank(message = "Ingredient name cannot be blank")
    private String nombre;

    @NotBlank(message = "Ingredient type cannot be blank")
    private String tipo;

    private LocalDateTime fCreacion;
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
