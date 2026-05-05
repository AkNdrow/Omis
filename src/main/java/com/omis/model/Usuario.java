package com.omis.model;

/**
 * Modelo que representa un usuario del sistema.
 * Corresponde a la tabla 'usuario' en la base de datos.
 */
public class Usuario {

    private int idUsuario;
    private String nombreCompleto;
    private String usuarioLogin;
    private String password;
    private String rol; // "Jefe" o "Empleado"

    public Usuario() {
    }

    public Usuario(int idUsuario, String nombreCompleto, String usuarioLogin, String password, String rol) {
        this.idUsuario = idUsuario;
        this.nombreCompleto = nombreCompleto;
        this.usuarioLogin = usuarioLogin;
        this.password = password;
        this.rol = rol;
    }

    // --- Getters y Setters ---

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getUsuarioLogin() {
        return usuarioLogin;
    }

    public void setUsuarioLogin(String usuarioLogin) {
        this.usuarioLogin = usuarioLogin;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    /**
     * Verifica si el usuario tiene rol de Jefe (administrador).
     */
    public boolean esJefe() {
        return "Jefe".equals(this.rol);
    }

    @Override
    public String toString() {
        return nombreCompleto + " (" + rol + ")";
    }
}
