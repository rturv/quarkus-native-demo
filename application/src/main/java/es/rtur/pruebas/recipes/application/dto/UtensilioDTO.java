package es.rtur.pruebas.recipes.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for Utensilio entity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
}
