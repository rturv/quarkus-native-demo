package es.rtur.pruebas.recipes.domain.entity;

import es.rtur.pruebas.recipes.domain.valueobject.RecetaId;
import es.rtur.pruebas.recipes.domain.valueobject.UsuarioId;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Domain entity representing a Receta (Recipe).
 * Contains pure business logic without any framework dependencies.
 * RN-01: Only logged-in users can create, edit, or delete recipes.
 * RN-02: A user can only edit/delete their own recipes (except admin).
 */
@Getter
@ToString
public class Receta {

    private final RecetaId id;
    
    @NotBlank(message = "Recipe name cannot be blank")
    private String nombre;
    
    @Positive(message = "Preparation time must be positive")
    private Integer tiempo;
    
    @Positive(message = "Number of servings must be positive")
    private Integer comensales;
    
    private String dificultad;
    private String preparacion;
    private String categoria;
    
    @NotNull(message = "Author ID cannot be null")
    private final UsuarioId idAutor;
    
    private final LocalDateTime fCreacion;
    private LocalDateTime fModificacion;

    /**
     * Constructor for creating a new Receta (ID may be null for new entities before persistence).
     */
    public Receta(RecetaId id, String nombre, Integer tiempo, Integer comensales,
                  String dificultad, String preparacion, String categoria, UsuarioId idAutor) {
        this.id = id; // Allow null for new entities before persistence
        this.nombre = Objects.requireNonNull(nombre, "Recipe name cannot be null");
        this.tiempo = tiempo;
        this.comensales = comensales;
        this.dificultad = dificultad;
        this.preparacion = preparacion;
        this.categoria = categoria;
        this.idAutor = Objects.requireNonNull(idAutor, "Author ID cannot be null");
        this.fCreacion = LocalDateTime.now();
        this.fModificacion = LocalDateTime.now();
    }

    /**
     * Constructor for loading existing Receta from repository.
     */
    public Receta(RecetaId id, String nombre, Integer tiempo, Integer comensales,
                  String dificultad, String preparacion, String categoria, UsuarioId idAutor,
                  LocalDateTime fCreacion, LocalDateTime fModificacion) {
        this.id = id;
        this.nombre = nombre;
        this.tiempo = tiempo;
        this.comensales = comensales;
        this.dificultad = dificultad;
        this.preparacion = preparacion;
        this.categoria = categoria;
        this.idAutor = idAutor;
        this.fCreacion = fCreacion;
        this.fModificacion = fModificacion;
    }

    /**
     * Updates recipe details.
     */
    public void updateDetails(String nombre, Integer tiempo, Integer comensales,
                             String dificultad, String preparacion, String categoria) {
        if (nombre != null && !nombre.isBlank()) {
            this.nombre = nombre;
        }
        if (tiempo != null && tiempo > 0) {
            this.tiempo = tiempo;
        }
        if (comensales != null && comensales > 0) {
            this.comensales = comensales;
        }
        if (dificultad != null) {
            this.dificultad = dificultad;
        }
        if (preparacion != null) {
            this.preparacion = preparacion;
        }
        if (categoria != null) {
            this.categoria = categoria;
        }
        this.fModificacion = LocalDateTime.now();
    }

    /**
     * Checks if the recipe is a quick recipe (30 minutes or less).
     */
    public boolean isQuickRecipe() {
        return this.tiempo != null && this.tiempo <= 30;
    }

    /**
     * Checks if a given user is the author of this recipe.
     * RN-02: Used to verify if user can edit/delete.
     */
    public boolean isAuthor(UsuarioId usuarioId) {
        return this.idAutor.equals(usuarioId);
    }

    /**
     * Checks if a user can edit this recipe.
     * RN-02: Only the author or an admin can edit.
     */
    public boolean canEdit(UsuarioId usuarioId, boolean isAdmin) {
        return isAdmin || isAuthor(usuarioId);
    }

    /**
     * Checks if a user can delete this recipe.
     * RN-02: Only the author or an admin can delete.
     */
    public boolean canDelete(UsuarioId usuarioId, boolean isAdmin) {
        return isAdmin || isAuthor(usuarioId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Receta receta = (Receta) o;
        return Objects.equals(id, receta.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
