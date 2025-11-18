package es.rtur.pruebas.recipes.domain.entity;

import es.rtur.pruebas.recipes.domain.valueobject.ValoracionId;
import es.rtur.pruebas.recipes.domain.valueobject.RecetaId;
import es.rtur.pruebas.recipes.domain.valueobject.UsuarioId;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Domain entity representing a Valoracion (Like/Dislike) on a recipe.
 * RF-04: Users can rate recipes with likes/dislikes.
 * RN-04: A user can only give one like/dislike per recipe.
 */
public class Valoracion {

    private final ValoracionId id;
    
    @NotNull(message = "Recipe ID cannot be null")
    private final RecetaId idReceta;
    
    @NotNull(message = "User ID cannot be null")
    private final UsuarioId idUsuario;
    
    @NotNull(message = "Valoracion type cannot be null")
    private String tipo; // "like" or "dislike"
    
    private final LocalDateTime fCreacion;
    private LocalDateTime fEliminacion;

    /**
     * Constructor for creating a new Valoracion (ID may be null for new entities before persistence).
     */
    public Valoracion(ValoracionId id, RecetaId idReceta, UsuarioId idUsuario, String tipo) {
        this.id = id; // Allow null for new entities before persistence
        this.idReceta = Objects.requireNonNull(idReceta, "Recipe ID cannot be null");
        this.idUsuario = Objects.requireNonNull(idUsuario, "User ID cannot be null");
        validateTipo(tipo);
        this.tipo = tipo;
        this.fCreacion = LocalDateTime.now();
        this.fEliminacion = null;
    }

    /**
     * Constructor for loading existing Valoracion from repository.
     */
    public Valoracion(ValoracionId id, RecetaId idReceta, UsuarioId idUsuario, String tipo,
                     LocalDateTime fCreacion, LocalDateTime fEliminacion) {
        this.id = id;
        this.idReceta = idReceta;
        this.idUsuario = idUsuario;
        this.tipo = tipo;
        this.fCreacion = fCreacion;
        this.fEliminacion = fEliminacion;
    }

    /**
     * Validates the tipo (type) of valoracion.
     */
    private void validateTipo(String tipo) {
        if (tipo == null || (!tipo.equals("like") && !tipo.equals("dislike"))) {
            throw new IllegalArgumentException("Valoracion tipo must be 'like' or 'dislike'");
        }
    }

    /**
     * Changes the valoracion type (toggle between like and dislike).
     */
    public void changeTipo(String newTipo) {
        validateTipo(newTipo);
        this.tipo = newTipo;
    }

    /**
     * Marks the valoracion as deleted (soft delete).
     */
    public void eliminar() {
        this.fEliminacion = LocalDateTime.now();
    }

    /**
     * Checks if the valoracion is active (not deleted).
     */
    public boolean isActive() {
        return this.fEliminacion == null;
    }

    /**
     * Checks if this is a like.
     */
    public boolean isLike() {
        return "like".equals(this.tipo);
    }

    /**
     * Checks if this is a dislike.
     */
    public boolean isDislike() {
        return "dislike".equals(this.tipo);
    }

    // Getters
    public ValoracionId getId() { return id; }
    public RecetaId getIdReceta() { return idReceta; }
    public UsuarioId getIdUsuario() { return idUsuario; }
    public String getTipo() { return tipo; }
    public LocalDateTime getFCreacion() { return fCreacion; }
    public LocalDateTime getFEliminacion() { return fEliminacion; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Valoracion that = (Valoracion) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Valoracion{" +
                "id=" + id +
                ", idReceta=" + idReceta +
                ", idUsuario=" + idUsuario +
                ", tipo='" + tipo + '\'' +
                ", active=" + isActive() +
                '}';
    }
}
