package com.omis.service;

import com.omis.dao.UsuarioDAO;
import com.omis.model.Usuario;
import com.omis.util.PasswordUtil;
import com.omis.util.SessionManager;

/**
 * Servicio de autenticación.
 * Valida credenciales y gestiona el inicio/cierre de sesión.
 */
public class AuthService {

    private final UsuarioDAO usuarioDAO;

    public AuthService() {
        this.usuarioDAO = new UsuarioDAO();
    }

    /**
     * Intenta autenticar a un usuario con sus credenciales.
     * @param login Nombre de usuario ingresado.
     * @param password Contraseña en texto plano.
     * @return Usuario autenticado, o null si las credenciales son incorrectas.
     */
    public Usuario autenticar(String login, String password) {
        if (login == null || login.trim().isEmpty()) {
            return null;
        }
        if (password == null || password.isEmpty()) {
            return null;
        }

        Usuario usuario = usuarioDAO.findByLogin(login.trim());

        if (usuario == null) {
            return null; // Usuario no existe
        }

        if (!PasswordUtil.verify(password, usuario.getPassword())) {
            return null; // Contraseña incorrecta
        }

        // Autenticación exitosa: registrar sesión
        SessionManager.getInstance().login(usuario);
        return usuario;
    }

    /**
     * Cierra la sesión del usuario activo.
     */
    public void cerrarSesion() {
        SessionManager.getInstance().logout();
    }

    /**
     * Verifica si el usuario admin por defecto necesita ser creado.
     * Se invoca al iniciar la app por primera vez.
     */
    public void asegurarUsuarioAdmin() {
        if (usuarioDAO.count() == 0) {
            Usuario admin = new Usuario();
            admin.setNombreCompleto("Administrador");
            admin.setUsuarioLogin("admin");
            admin.setPassword(PasswordUtil.hash("admin123"));
            admin.setRol("Jefe");
            usuarioDAO.create(admin);
            System.out.println("Usuario administrador creado (login: admin, password: admin123)");
        }
    }
}
