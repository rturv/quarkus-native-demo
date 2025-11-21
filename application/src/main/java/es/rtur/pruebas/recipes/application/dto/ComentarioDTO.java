package es.rtur.pruebas.recipes.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for Comentario entity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "Comentario", description = "Comentario realizado por un usuario sobre una receta")
public class ComentarioDTO {

    @Schema(description = "Identificador del comentario", example = "12")
    private Integer id;

    @Schema(description = "Identificador de la receta comentada", example = "42")
    private Integer idReceta;

    @Schema(description = "Identificador del autor del comentario", example = "7")
    private Integer idAutor;
    
    @Schema(description = "Nombre visible del autor", example = "Laura Chef")
    private String nombreAutor; // For display purposes

    @NotBlank(message = "Comment content cannot be blank")
    @Schema(description = "Contenido textual del comentario", example = "¡Me encanta esta receta!")
    private String contenido;

    @Schema(description = "Estado del comentario", example = "activo")
    private String estado;
    
    @Schema(description = "Fecha de creación", example = "2025-02-14T10:15:00")
    private LocalDateTime fCreacion;
    
    @Schema(description = "Última modificación", example = "2025-02-14T10:20:00")
    private LocalDateTime fModificacion;
}
