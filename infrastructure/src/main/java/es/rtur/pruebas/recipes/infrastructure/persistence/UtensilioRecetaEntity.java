package es.rtur.pruebas.recipes.infrastructure.persistence;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad JPA para la tabla utensilios_receta.
 * Tabla intermedia que representa la relación muchos a muchos entre recetas y utensilios.
 */
@Entity
@Table(name = "utensilios_receta")
public class UtensilioRecetaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_utensilio_receta", nullable = false)
    public Integer idUtensilioReceta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_receta", nullable = false)
    public RecetaEntity receta;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_utensilio", nullable = false)
    public UtensilioEntity utensilio;

    @Column(name = "f_creacion", nullable = false, updatable = false)
    public LocalDateTime fCreacion;

    @Column(name = "f_modificacion", nullable = false)
    public LocalDateTime fModificacion;

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
