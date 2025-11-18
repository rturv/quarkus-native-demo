package es.rtur.pruebas.recipes.infrastructure.persistence;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad JPA para la tabla utensilios.
 * Representa un utensilio de cocina necesario para elaborar recetas.
 */
@Entity
@Table(name = "utensilios")
public class UtensilioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_utensilio", nullable = false)
    public Integer idUtensilio;

    @Column(name = "nombre", nullable = false, columnDefinition = "TEXT")
    public String nombre;

    @Column(name = "tipo", nullable = false, columnDefinition = "TEXT")
    public String tipo;

    @Column(name = "f_creacion", nullable = false, updatable = false)
    public LocalDateTime fCreacion;

    @Column(name = "f_modificacion", nullable = true)
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
