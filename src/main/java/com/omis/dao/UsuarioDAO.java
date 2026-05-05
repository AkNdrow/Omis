package com.omis.dao;

import com.omis.config.DatabaseManager;
import com.omis.model.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object para la tabla 'usuario'.
 * Gestiona todas las operaciones CRUD de usuarios.
 */
public class UsuarioDAO {

    /**
     * Busca un usuario por su nombre de login.
     * @return Usuario encontrado, o null si no existe.
     */
    public Usuario findByLogin(String login) {
        String sql = "SELECT id_usuario, nombre_completo, usuario_login, password, rol FROM usuario WHERE usuario_login = ?";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, login);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar usuario por login: " + e.getMessage());
        }
        return null;
    }

    /**
     * Obtiene todos los usuarios registrados.
     */
    public List<Usuario> findAll() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT id_usuario, nombre_completo, usuario_login, password, rol FROM usuario ORDER BY nombre_completo";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                usuarios.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar usuarios: " + e.getMessage());
        }
        return usuarios;
    }

    /**
     * Crea un nuevo usuario en la base de datos.
     * La contraseña debe llegar ya hasheada con BCrypt.
     * @return true si se insertó correctamente.
     */
    public boolean create(Usuario usuario) {
        String sql = "INSERT INTO usuario (nombre_completo, usuario_login, password, rol) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, usuario.getNombreCompleto());
            pstmt.setString(2, usuario.getUsuarioLogin());
            pstmt.setString(3, usuario.getPassword());
            pstmt.setString(4, usuario.getRol());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al crear usuario: " + e.getMessage());
            return false;
        }
    }

    /**
     * Actualiza los datos de un usuario existente.
     * Si el password viene vacío o null, no lo modifica.
     */
    public boolean update(Usuario usuario) {
        String sql;
        boolean updatePassword = usuario.getPassword() != null && !usuario.getPassword().isEmpty();

        if (updatePassword) {
            sql = "UPDATE usuario SET nombre_completo = ?, usuario_login = ?, password = ?, rol = ? WHERE id_usuario = ?";
        } else {
            sql = "UPDATE usuario SET nombre_completo = ?, usuario_login = ?, rol = ? WHERE id_usuario = ?";
        }

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, usuario.getNombreCompleto());
            pstmt.setString(2, usuario.getUsuarioLogin());

            if (updatePassword) {
                pstmt.setString(3, usuario.getPassword());
                pstmt.setString(4, usuario.getRol());
                pstmt.setInt(5, usuario.getIdUsuario());
            } else {
                pstmt.setString(3, usuario.getRol());
                pstmt.setInt(4, usuario.getIdUsuario());
            }

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar usuario: " + e.getMessage());
            return false;
        }
    }

    /**
     * Elimina un usuario por su ID.
     */
    public boolean delete(int idUsuario) {
        String sql = "DELETE FROM usuario WHERE id_usuario = ?";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idUsuario);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar usuario: " + e.getMessage());
            return false;
        }
    }

    /**
     * Cuenta el número total de usuarios registrados.
     */
    public int count() {
        String sql = "SELECT COUNT(*) FROM usuario";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error al contar usuarios: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Mapea una fila del ResultSet a un objeto Usuario.
     */
    private Usuario mapResultSet(ResultSet rs) throws SQLException {
        return new Usuario(
                rs.getInt("id_usuario"),
                rs.getString("nombre_completo"),
                rs.getString("usuario_login"),
                rs.getString("password"),
                rs.getString("rol")
        );
    }
}
