package es.rtur.pruebas.recipes.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for Ingrediente entity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
}
