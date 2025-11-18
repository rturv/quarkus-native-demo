package es.rtur.pruebas.recipes.domain.entity;

import es.rtur.pruebas.recipes.domain.valueobject.UsuarioId;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Domain entity representing a Usuario (User).
 * Contains pure business logic without any framework dependencies.
 * RN-06: Field es_admin defines administration permissions.
 */
public class Usuario {

    private final UsuarioId id;
    
    @NotBlank(message = "Name cannot be blank")
    @Size(max = 150, message = "Name cannot exceed 150 characters")
    private String nombre;
    
    @NotBlank(message = "Password cannot be blank")
    @Size(max = 150, message = "Password cannot exceed 150 characters")
    private String claveAcceso;
    
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email must be valid")
    @Size(max = 150, message = "Email cannot exceed 150 characters")
    private String email;
    
    private final LocalDateTime fCreacion;
    private LocalDateTime fModificacion;
    private Boolean esAdmin;
    private String estado;

    /**
     * Constructor for creating a new Usuario (ID may be null for new entities before persistence).
     */
    public Usuario(UsuarioId id, String nombre, String claveAcceso, String email, Boolean esAdmin) {
        this.id = id; // Allow null for new entities before persistence
        this.nombre = Objects.requireNonNull(nombre, "Name cannot be null");
        this.claveAcceso = Objects.requireNonNull(claveAcceso, "Password cannot be null");
        this.email = Objects.requireNonNull(email, "Email cannot be null");
        this.esAdmin = esAdmin != null ? esAdmin : false;
        this.estado = "activo";
        this.fCreacion = LocalDateTime.now();
        this.fModificacion = LocalDateTime.now();
    }

    /**
     * Constructor for loading existing Usuario from repository.
     */
    public Usuario(UsuarioId id, String nombre, String claveAcceso, String email, 
                   Boolean esAdmin, String estado, LocalDateTime fCreacion, LocalDateTime fModificacion) {
        this.id = Objects.requireNonNull(id, "Usuario ID cannot be null");
        this.nombre = nombre;
        this.claveAcceso = claveAcceso;
        this.email = email;
        this.esAdmin = esAdmin;
        this.estado = estado;
        this.fCreacion = fCreacion;
        this.fModificacion = fModificacion;
    }

    /**
     * Updates usuario details.
     */
    public void updateDetails(String nombre, String email) {
        if (nombre != null && !nombre.isBlank()) {
            this.nombre = nombre;
        }
        if (email != null && !email.isBlank()) {
            this.email = email;
        }
        this.fModificacion = LocalDateTime.now();
    }

    /**
     * Updates the password (should be hashed before calling this method).
     */
    public void updatePassword(String newPassword) {
        if (newPassword != null && !newPassword.isBlank()) {
            this.claveAcceso = newPassword;
            this.fModificacion = LocalDateTime.now();
        }
    }

    /**
     * Marks usuario as inactive.
     */
    public void deactivate() {
        this.estado = "inactivo";
        this.fModificacion = LocalDateTime.now();
    }

    /**
     * Marks usuario as active.
     */
    public void activate() {
        this.estado = "activo";
        this.fModificacion = LocalDateTime.now();
    }

    /**
     * Checks if usuario is active.
     */
    public boolean isActive() {
        return "activo".equals(this.estado);
    }

    /**
     * Checks if usuario is an administrator.
     */
    public boolean isAdmin() {
        return Boolean.TRUE.equals(this.esAdmin);
    }

    /**
     * Grants admin permissions.
     */
    public void makeAdmin() {
        this.esAdmin = true;
        this.fModificacion = LocalDateTime.now();
    }

    /**
     * Revokes admin permissions.
     */
    public void revokeAdmin() {
        this.esAdmin = false;
        this.fModificacion = LocalDateTime.now();
    }

    // Getters
    public UsuarioId getId() { return id; }
    public String getNombre() { return nombre; }
    public String getClaveAcceso() { return claveAcceso; }
    public String getEmail() { return email; }
    public LocalDateTime getFCreacion() { return fCreacion; }
    public LocalDateTime getFModificacion() { return fModificacion; }
    public Boolean getEsAdmin() { return esAdmin; }
    public String getEstado() { return estado; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return Objects.equals(id, usuario.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", email='" + email + '\'' +
                ", esAdmin=" + esAdmin +
                ", estado='" + estado + '\'' +
                '}';
    }
}
