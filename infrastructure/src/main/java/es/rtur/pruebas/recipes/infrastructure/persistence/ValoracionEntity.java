package es.rtur.pruebas.recipes.infrastructure.persistence;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad JPA para la tabla valoraciones.
 * Representa un like o dislike de un usuario sobre una receta.
 * RN-04: Un usuario solo puede dar un like/dislike por receta.
 */
@Entity
@Table(name = "valoraciones", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"id_receta", "id_usuario"}))
public class ValoracionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_valoracion", nullable = false)
    public Integer idValoracion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_receta", nullable = false)
    public RecetaEntity receta;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_usuario", nullable = false)
    public UsuarioEntity usuario;

    /**
     * Tipo de valoración: 'like' o 'dislike'
     */
    @Column(name = "tipo", nullable = false, length = 20)
    public String tipo;

    @Column(name = "f_creacion", nullable = false, updatable = false)
    public LocalDateTime fCreacion;

    @Column(name = "f_eliminacion")
    public LocalDateTime fEliminacion;

    @PrePersist
    public void prePersist() {
        if (fCreacion == null) {
            fCreacion = LocalDateTime.now();
        }
    }

    /**
     * Marca la valoración como eliminada (soft delete).
     */
    public void eliminar() {
        this.fEliminacion = LocalDateTime.now();
    }

    /**
     * Verifica si la valoración está activa (no eliminada).
     */
    public boolean estaActiva() {
        return fEliminacion == null;
    }
}
