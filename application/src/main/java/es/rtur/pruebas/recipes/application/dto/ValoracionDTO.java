package es.rtur.pruebas.recipes.application.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for Valoracion entity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "Valoracion", description = "Valoración tipo like/dislike hecha por un usuario")
public class ValoracionDTO {

    @Schema(description = "Identificador de la valoración", example = "99")
    private Integer id;

    @NotNull(message = "Recipe ID cannot be null")
    @Schema(description = "Identificador de la receta valorada", example = "42")
    private Integer idReceta;

    @Schema(description = "Identificador del usuario que valora", example = "7")
    private Integer idUsuario;
    
    @Schema(description = "Nombre del usuario", example = "Laura Chef")
    private String nombreUsuario; // For display purposes

    @NotNull(message = "Valoracion type cannot be null")
    @Schema(description = "Tipo de valoración", example = "like")
    private String tipo; // "like" or "dislike"

    @Schema(description = "Fecha de creación", example = "2025-06-01T12:00:00")
    private LocalDateTime fCreacion;
    
    @Schema(description = "Fecha de eliminación lógica si aplica", example = "2025-06-02T13:00:00")
    private LocalDateTime fEliminacion;
}
