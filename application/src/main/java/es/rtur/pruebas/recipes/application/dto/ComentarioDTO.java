package es.rtur.pruebas.recipes.application.dto;

import jakarta.validation.constraints.NotBlank;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for Comentario entity.
 */
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

    public ComentarioDTO() {
    }

    public ComentarioDTO(Integer id, Integer idReceta, Integer idAutor, String contenido,
                        String estado, LocalDateTime fCreacion, LocalDateTime fModificacion) {
        this.id = id;
        this.idReceta = idReceta;
        this.idAutor = idAutor;
        this.contenido = contenido;
        this.estado = estado;
        this.fCreacion = fCreacion;
        this.fModificacion = fModificacion;
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getIdReceta() { return idReceta; }
    public void setIdReceta(Integer idReceta) { this.idReceta = idReceta; }

    public Integer getIdAutor() { return idAutor; }
    public void setIdAutor(Integer idAutor) { this.idAutor = idAutor; }

    public String getNombreAutor() { return nombreAutor; }
    public void setNombreAutor(String nombreAutor) { this.nombreAutor = nombreAutor; }

    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public LocalDateTime getFCreacion() { return fCreacion; }
    public void setFCreacion(LocalDateTime fCreacion) { this.fCreacion = fCreacion; }

    public LocalDateTime getFModificacion() { return fModificacion; }
    public void setFModificacion(LocalDateTime fModificacion) { this.fModificacion = fModificacion; }
}
