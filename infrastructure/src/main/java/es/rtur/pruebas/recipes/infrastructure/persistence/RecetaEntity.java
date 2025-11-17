package es.rtur.pruebas.recipes.infrastructure.persistence;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad JPA para la tabla recetas.
 * Representa una receta publicada por un usuario con sus ingredientes, utensilios y valoraciones.
 */
@Entity
@Table(name = "recetas")
public class RecetaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_receta", nullable = false)
    public Integer idReceta;

    @Column(name = "nombre", nullable = false, columnDefinition = "TEXT")
    public String nombre;

    @Column(name = "tiempo")
    public Integer tiempo;

    @Column(name = "comensales")
    public Integer comensales;

    @Column(name = "dificultad", columnDefinition = "TEXT")
    public String dificultad;

    @Column(name = "preparacion", columnDefinition = "TEXT")
    public String preparacion;

    @Column(name = "categoria", columnDefinition = "TEXT")
    public String categoria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_autor", nullable = false)
    public UsuarioEntity autor;

    @Column(name = "f_creacion", nullable = false, updatable = false)
    public LocalDateTime fCreacion;

    @Column(name = "f_modificacion", nullable = false)
    public LocalDateTime fModificacion;

    // Relaciones con ingredientes y utensilios
    @OneToMany(mappedBy = "receta", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<IngredienteRecetaEntity> ingredientesReceta = new ArrayList<>();

    @OneToMany(mappedBy = "receta", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<UtensilioRecetaEntity> utensiliosReceta = new ArrayList<>();

    @OneToMany(mappedBy = "receta", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<ComentarioEntity> comentarios = new ArrayList<>();

    @OneToMany(mappedBy = "receta", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<ValoracionEntity> valoraciones = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (fCreacion == null) {
            fCreacion = LocalDateTime.now();
        }
        if (fModificacion == null) {
            fModificacion = LocalDateTime.now();
        }
    }

    @PreUpdate
    public void preUpdate() {
        fModificacion = LocalDateTime.now();
    }
}
