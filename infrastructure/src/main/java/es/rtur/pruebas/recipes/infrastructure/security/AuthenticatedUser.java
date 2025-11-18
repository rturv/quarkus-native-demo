package es.rtur.pruebas.recipes.infrastructure.security;

import io.quarkus.security.identity.SecurityIdentity;

/**
 * Helper para extraer la identidad del usuario autenticado.
 */
public final class AuthenticatedUser {

    private final Integer id;
    private final boolean admin;

    private AuthenticatedUser(Integer id, boolean admin) {
        this.id = id;
        this.admin = admin;
    }

    public static AuthenticatedUser from(SecurityIdentity identity) {
        if (identity == null || identity.getPrincipal() == null || identity.getPrincipal().getName() == null) {
            throw new SecurityException("No hay identidad JWT válida para el usuario autenticado");
        }

        try {
            Integer userId = Integer.valueOf(identity.getPrincipal().getName());
            boolean isAdmin = identity.getRoles().contains("admin");
            return new AuthenticatedUser(userId, isAdmin);
        } catch (NumberFormatException ex) {
            throw new SecurityException("El identificador del usuario no es válido", ex);
        }
    }

    public Integer id() {
        return id;
    }

    public boolean isAdmin() {
        return admin;
    }
}