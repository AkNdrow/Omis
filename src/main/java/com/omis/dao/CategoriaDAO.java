package com.omis.dao;

import com.omis.config.DatabaseManager;
import com.omis.model.Categoria;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object para la tabla 'categoria'.
 */
public class CategoriaDAO {

    public List<Categoria> findAll() {
        List<Categoria> lista = new ArrayList<>();
        String sql = "SELECT id_categoria, nombre_categoria FROM categoria ORDER BY nombre_categoria";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                lista.add(new Categoria(rs.getInt("id_categoria"), rs.getString("nombre_categoria")));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar categorías: " + e.getMessage());
        }
        return lista;
    }

    public boolean create(Categoria c) {
        String sql = "INSERT INTO categoria (nombre_categoria) VALUES (?)";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, c.getNombreCategoria());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al crear categoría: " + e.getMessage());
            return false;
        }
    }

    public boolean update(Categoria c) {
        String sql = "UPDATE categoria SET nombre_categoria = ? WHERE id_categoria = ?";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, c.getNombreCategoria());
            pstmt.setInt(2, c.getIdCategoria());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar categoría: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM categoria WHERE id_categoria = ?";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar categoría: " + e.getMessage());
            return false;
        }
    }
}
