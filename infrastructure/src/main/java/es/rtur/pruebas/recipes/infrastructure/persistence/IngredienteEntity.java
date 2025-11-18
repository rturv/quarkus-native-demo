package es.rtur.pruebas.recipes.infrastructure.persistence;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad JPA para la tabla ingredientes.
 * Representa un ingrediente disponible en el catálogo del sistema.
 */
@Entity
@Table(name = "ingredientes")
public class IngredienteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ingrediente", nullable = false)
    public Integer idIngrediente;

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
