package es.rtur.pruebas.recipes.infrastructure.persistence;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad JPA para la tabla comentarios.
 * Representa un comentario de un usuario sobre una receta.
 */
@Entity
@Table(name = "comentarios")
public class ComentarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_comentario", nullable = false)
    public Integer idComentario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_receta", nullable = false)
    public RecetaEntity receta;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_autor", nullable = false)
    public UsuarioEntity autor;

    @Column(name = "contenido", nullable = false, columnDefinition = "TEXT")
    public String contenido;

    @Column(name = "estado", length = 20)
    public String estado = "activo";

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
        if (estado == null) {
            estado = "activo";
        }
    }

    @PreUpdate
    public void preUpdate() {
        fModificacion = LocalDateTime.now();
    }
}
