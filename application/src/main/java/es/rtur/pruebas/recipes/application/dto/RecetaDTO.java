package es.rtur.pruebas.recipes.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for Receta entity.
 * Used to transfer recipe data between layers.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "Receta", description = "Representa una receta gestionada por el sistema")
public class RecetaDTO {

    @Schema(description = "Identificador único de la receta", example = "42")
    private Integer id;

    @NotBlank(message = "Recipe name cannot be blank")
    @Schema(description = "Nombre público de la receta", example = "Tortilla de patatas")
    private String nombre;

    @Positive(message = "Preparation time must be positive")
    @Schema(description = "Tiempo de preparación en minutos", example = "35")
    private Integer tiempo;

    @Positive(message = "Number of servings must be positive")
    @Schema(description = "Número de comensales para los que aplica la receta", example = "4")
    private Integer comensales;

    @Schema(description = "Nivel de dificultad percibido", example = "MEDIA")
    private String dificultad;
    
    @Schema(description = "Pasos resumidos para preparar la receta", example = "Batir huevos, freír patatas...")
    private String preparacion;
    
    @Schema(description = "Categoría culinaria", example = "Tradicional")
    private String categoria;
    
    @Schema(description = "Identificador del autor", example = "7")
    private Integer idAutor;
    
    @Schema(description = "Nombre del autor", example = "Laura Chef")
    private String nombreAutor; // For display purposes
    
    @Schema(description = "Fecha de creación", example = "2025-05-01T09:30:00")
    private LocalDateTime fCreacion;
    
    @Schema(description = "Última fecha de modificación", example = "2025-05-03T11:05:00")
    private LocalDateTime fModificacion;
}
