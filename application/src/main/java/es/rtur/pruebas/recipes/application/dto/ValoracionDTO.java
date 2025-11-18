package es.rtur.pruebas.recipes.application.dto;

import jakarta.validation.constraints.NotNull;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for Valoracion entity.
 */
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

    public ValoracionDTO() {
    }

    public ValoracionDTO(Integer id, Integer idReceta, Integer idUsuario, String tipo,
                        LocalDateTime fCreacion, LocalDateTime fEliminacion) {
        this.id = id;
        this.idReceta = idReceta;
        this.idUsuario = idUsuario;
        this.tipo = tipo;
        this.fCreacion = fCreacion;
        this.fEliminacion = fEliminacion;
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getIdReceta() { return idReceta; }
    public void setIdReceta(Integer idReceta) { this.idReceta = idReceta; }

    public Integer getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; }

    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public LocalDateTime getFCreacion() { return fCreacion; }
    public void setFCreacion(LocalDateTime fCreacion) { this.fCreacion = fCreacion; }

    public LocalDateTime getFEliminacion() { return fEliminacion; }
    public void setFEliminacion(LocalDateTime fEliminacion) { this.fEliminacion = fEliminacion; }
}
