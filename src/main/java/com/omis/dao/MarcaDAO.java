package com.omis.dao;

import com.omis.config.DatabaseManager;
import com.omis.model.Marca;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object para la tabla 'marca'.
 */
public class MarcaDAO {

    public List<Marca> findAll() {
        List<Marca> lista = new ArrayList<>();
        String sql = "SELECT id_marca, nombre_marca FROM marca ORDER BY nombre_marca";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                lista.add(new Marca(rs.getInt("id_marca"), rs.getString("nombre_marca")));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar marcas: " + e.getMessage());
        }
        return lista;
    }

    public boolean create(Marca m) {
        String sql = "INSERT INTO marca (nombre_marca) VALUES (?)";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, m.getNombreMarca());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al crear marca: " + e.getMessage());
            return false;
        }
    }

    public boolean update(Marca m) {
        String sql = "UPDATE marca SET nombre_marca = ? WHERE id_marca = ?";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, m.getNombreMarca());
            pstmt.setInt(2, m.getIdMarca());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar marca: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM marca WHERE id_marca = ?";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar marca: " + e.getMessage());
            return false;
        }
    }
}
