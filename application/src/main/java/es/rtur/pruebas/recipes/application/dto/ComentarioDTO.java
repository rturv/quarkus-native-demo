package es.rtur.pruebas.recipes.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for Comentario entity.
 */
public class ComentarioDTO {

    private Integer id;

    @NotNull(message = "Recipe ID cannot be null")
    private Integer idReceta;

    private Integer idAutor;
    private String nombreAutor; // For display purposes

    @NotBlank(message = "Comment content cannot be blank")
    private String contenido;

    private String estado;
    private LocalDateTime fCreacion;
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
