package es.rtur.pruebas.recipes.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for Usuario entity.
 * Used to transfer user data between layers.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
}
