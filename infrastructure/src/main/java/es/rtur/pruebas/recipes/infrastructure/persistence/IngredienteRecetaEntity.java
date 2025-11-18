package es.rtur.pruebas.recipes.infrastructure.persistence;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad JPA para la tabla ingredientes_receta.
 * Tabla intermedia que representa la relación muchos a muchos entre recetas e ingredientes,
 * con información adicional como cantidad y unidad de medida.
 */
@Entity
@Table(name = "ingredientes_receta")
public class IngredienteRecetaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ingrediente_receta", nullable = false)
    public Integer idIngredienteReceta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_receta", nullable = false)
    public RecetaEntity receta;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_ingrediente", nullable = false)
    public IngredienteEntity ingrediente;

    @Column(name = "cantidad", precision = 10, scale = 2)
    public BigDecimal cantidad;

    @Column(name = "unidad_medida", columnDefinition = "TEXT")
    public String unidadMedida;

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
