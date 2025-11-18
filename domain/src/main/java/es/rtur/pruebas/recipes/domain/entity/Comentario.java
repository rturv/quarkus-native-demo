package es.rtur.pruebas.recipes.domain.entity;

import es.rtur.pruebas.recipes.domain.valueobject.ComentarioId;
import es.rtur.pruebas.recipes.domain.valueobject.RecetaId;
import es.rtur.pruebas.recipes.domain.valueobject.UsuarioId;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Domain entity representing a Comentario (Comment) on a recipe.
 * RF-03: Users can comment on recipes.
 * RN-08: Comments can only be created by logged-in users.
 */
public class Comentario {

    private final ComentarioId id;
    
    @NotNull(message = "Recipe ID cannot be null")
    private final RecetaId idReceta;
    
    @NotNull(message = "Author ID cannot be null")
    private final UsuarioId idAutor;
    
    @NotBlank(message = "Comment content cannot be blank")
    private String contenido;
    
    private String estado;
    private final LocalDateTime fCreacion;
    private LocalDateTime fModificacion;

    /**
     * Constructor for creating a new Comentario.
     */
    public Comentario(ComentarioId id, RecetaId idReceta, UsuarioId idAutor, String contenido) {
        this.id = Objects.requireNonNull(id, "Comentario ID cannot be null");
        this.idReceta = Objects.requireNonNull(idReceta, "Recipe ID cannot be null");
        this.idAutor = Objects.requireNonNull(idAutor, "Author ID cannot be null");
        this.contenido = Objects.requireNonNull(contenido, "Comment content cannot be null");
        this.estado = "activo";
        this.fCreacion = LocalDateTime.now();
        this.fModificacion = LocalDateTime.now();
    }

    /**
     * Constructor for loading existing Comentario from repository.
     */
    public Comentario(ComentarioId id, RecetaId idReceta, UsuarioId idAutor, String contenido,
                     String estado, LocalDateTime fCreacion, LocalDateTime fModificacion) {
        this.id = id;
        this.idReceta = idReceta;
        this.idAutor = idAutor;
        this.contenido = contenido;
        this.estado = estado;
        this.fCreacion = fCreacion;
        this.fModificacion = fModificacion;
    }

    /**
     * Updates comment content.
     */
    public void updateContent(String contenido) {
        if (contenido != null && !contenido.isBlank()) {
            this.contenido = contenido;
            this.fModificacion = LocalDateTime.now();
        }
    }

    /**
     * Marks comment as inactive (soft delete).
     */
    public void deactivate() {
        this.estado = "inactivo";
        this.fModificacion = LocalDateTime.now();
    }

    /**
     * Checks if comment is active.
     */
    public boolean isActive() {
        return "activo".equals(this.estado);
    }

    /**
     * Checks if a given user is the author of this comment.
     */
    public boolean isAuthor(UsuarioId usuarioId) {
        return this.idAutor.equals(usuarioId);
    }

    // Getters
    public ComentarioId getId() { return id; }
    public RecetaId getIdReceta() { return idReceta; }
    public UsuarioId getIdAutor() { return idAutor; }
    public String getContenido() { return contenido; }
    public String getEstado() { return estado; }
    public LocalDateTime getFCreacion() { return fCreacion; }
    public LocalDateTime getFModificacion() { return fModificacion; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comentario that = (Comentario) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Comentario{" +
                "id=" + id +
                ", idReceta=" + idReceta +
                ", idAutor=" + idAutor +
                ", estado='" + estado + '\'' +
                '}';
    }
}
