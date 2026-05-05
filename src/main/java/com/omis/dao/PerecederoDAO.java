package com.omis.dao;

import com.omis.config.DatabaseManager;
import com.omis.model.Perecedero;

import java.sql.*;

/**
 * Data Access Object para la tabla 'perecedero' (subentidad 1:1 de producto).
 */
public class PerecederoDAO {

    public Perecedero findByProductoId(int idProducto) {
        String sql = "SELECT id_producto, refrigeracion, fecha_caducidad FROM perecedero WHERE id_producto = ?";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idProducto);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Perecedero(
                        rs.getInt("id_producto"),
                        rs.getString("refrigeracion"),
                        rs.getString("fecha_caducidad")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar perecedero: " + e.getMessage());
        }
        return null;
    }

    public boolean create(Perecedero per) {
        String sql = "INSERT INTO perecedero (id_producto, refrigeracion, fecha_caducidad) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, per.getIdProducto());
            pstmt.setString(2, per.getRefrigeracion());
            pstmt.setString(3, per.getFechaCaducidad());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al crear perecedero: " + e.getMessage());
            return false;
        }
    }

    public boolean update(Perecedero per) {
        String sql = "UPDATE perecedero SET refrigeracion = ?, fecha_caducidad = ? WHERE id_producto = ?";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, per.getRefrigeracion());
            pstmt.setString(2, per.getFechaCaducidad());
            pstmt.setInt(3, per.getIdProducto());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar perecedero: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(int idProducto) {
        String sql = "DELETE FROM perecedero WHERE id_producto = ?";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idProducto);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar perecedero: " + e.getMessage());
            return false;
        }
    }
}
