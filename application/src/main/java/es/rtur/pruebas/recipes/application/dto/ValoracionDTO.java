package es.rtur.pruebas.recipes.application.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for Valoracion entity.
 */
public class ValoracionDTO {

    private Integer id;

    @NotNull(message = "Recipe ID cannot be null")
    private Integer idReceta;

    private Integer idUsuario;
    private String nombreUsuario; // For display purposes

    @NotNull(message = "Valoracion type cannot be null")
    private String tipo; // "like" or "dislike"

    private LocalDateTime fCreacion;
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
