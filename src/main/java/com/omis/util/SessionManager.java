package com.omis.util;

import com.omis.model.Usuario;

/**
 * Gestiona la sesión del usuario activo en memoria.
 * Singleton que almacena quién está logueado actualmente.
 */
public class SessionManager {

    private static SessionManager instance;
    private Usuario usuarioActivo;

    private SessionManager() {
    }

    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    /**
     * Registra el usuario que inició sesión.
     */
    public void login(Usuario usuario) {
        this.usuarioActivo = usuario;
    }

    /**
     * Cierra la sesión del usuario activo.
     */
    public void logout() {
        this.usuarioActivo = null;
    }

    /**
     * Obtiene el usuario actualmente logueado.
     * @return Usuario activo, o null si no hay sesión.
     */
    public Usuario getUsuarioActivo() {
        return usuarioActivo;
    }

    /**
     * Verifica si hay una sesión activa.
     */
    public boolean haySesion() {
        return usuarioActivo != null;
    }

    /**
     * Verifica si el usuario activo es Jefe.
     */
    public boolean esJefe() {
        return usuarioActivo != null && usuarioActivo.esJefe();
    }
}
