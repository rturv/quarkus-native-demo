package es.rtur.pruebas.recipes.infrastructure.persistence;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad JPA para la tabla usuarios.
 * Representa un usuario del sistema que puede crear recetas, comentar y valorar.
 */
@Entity
@Table(name = "usuarios")
public class UsuarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario", nullable = false)
    public Integer idUsuario;

    @Column(name = "nombre", nullable = false, length = 150)
    public String nombre;

    @Column(name = "clave_acceso", nullable = false, length = 150)
    public String claveAcceso;

    @Column(name = "email", nullable = false, length = 150, unique = true)
    public String email;

    @Column(name = "f_creacion", nullable = false, updatable = false)
    public LocalDateTime fCreacion;

    @Column(name = "f_modificacion", nullable = false)
    public LocalDateTime fModificacion;

    @Column(name = "es_admin", nullable = false)
    public Boolean esAdmin = false;

    @Column(name = "estado", length = 20)
    public String estado = "activo";

    // Relaciones inversas (opcionales para navegación)
    @OneToMany(mappedBy = "autor", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<RecetaEntity> recetas = new ArrayList<>();

    @OneToMany(mappedBy = "autor", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<ComentarioEntity> comentarios = new ArrayList<>();

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<ValoracionEntity> valoraciones = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (fCreacion == null) {
            fCreacion = LocalDateTime.now();
        }
        if (fModificacion == null) {
            fModificacion = LocalDateTime.now();
        }
        if (esAdmin == null) {
            esAdmin = false;
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
