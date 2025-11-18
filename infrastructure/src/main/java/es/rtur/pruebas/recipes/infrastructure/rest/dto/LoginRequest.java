package es.rtur.pruebas.recipes.infrastructure.rest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * DTO que representa el cuerpo de la petición de login.
 */
@Schema(name = "LoginRequest", description = "Credenciales para autenticar a un usuario")
public class LoginRequest {

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email must be valid")
    @Schema(description = "Correo electrónico del usuario", example = "laura@example.com")
    private String email;

    @NotBlank(message = "Password cannot be blank")
    @Schema(description = "Contraseña del usuario", example = "Str0ngP@ssw0rd")
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}