package es.rtur.pruebas.recipes.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for Usuario entity.
 * Used to transfer user data between layers.
 */
@Schema(name = "Usuario", description = "Usuario registrado en el portal de recetas")
public class UsuarioDTO {

    @Schema(description = "Identificador del usuario", example = "7")
    private Integer id;

    @NotBlank(message = "Name cannot be blank")
    @Size(max = 150, message = "Name cannot exceed 150 characters")
    @Schema(description = "Nombre completo", example = "Laura Chef")
    private String nombre;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email must be valid")
    @Size(max = 150, message = "Email cannot exceed 150 characters")
    @Schema(description = "Correo electrónico de acceso", example = "laura@example.com")
    private String email;

    // Password field - only used for registration/updates, not for read operations
    @Schema(description = "Contraseña en texto plano solo para operaciones de alta", example = "Str0ngP@ssw0rd", writeOnly = true)
    private String claveAcceso;

    @Schema(description = "Token JWT emitido tras el login", example = "eyJhbGciOiJIUzI1NiIs...")
    private String jwt;

    @Schema(description = "Indica si el usuario es administrador", example = "true")
    private Boolean esAdmin;
    @Schema(description = "Estado actual de la cuenta", example = "activo")
    private String estado;
    @Schema(description = "Fecha de creación", example = "2025-01-01T08:00:00")
    private LocalDateTime fCreacion;
    @Schema(description = "Última modificación", example = "2025-01-10T08:00:00")
    private LocalDateTime fModificacion;

    public UsuarioDTO() {
    }

    public UsuarioDTO(Integer id, String nombre, String email, Boolean esAdmin, String estado,
                     LocalDateTime fCreacion, LocalDateTime fModificacion) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.esAdmin = esAdmin;
        this.estado = estado;
        this.fCreacion = fCreacion;
        this.fModificacion = fModificacion;
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getClaveAcceso() { return claveAcceso; }
    public void setClaveAcceso(String claveAcceso) { this.claveAcceso = claveAcceso; }

    public String getJwt() { return jwt; }
    public void setJwt(String jwt) { this.jwt = jwt; }

    public Boolean getEsAdmin() { return esAdmin; }
    public void setEsAdmin(Boolean esAdmin) { this.esAdmin = esAdmin; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public LocalDateTime getFCreacion() { return fCreacion; }
    public void setFCreacion(LocalDateTime fCreacion) { this.fCreacion = fCreacion; }

    public LocalDateTime getFModificacion() { return fModificacion; }
    public void setFModificacion(LocalDateTime fModificacion) { this.fModificacion = fModificacion; }
}
