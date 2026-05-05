package com.omis.dao;

import com.omis.config.DatabaseManager;
import com.omis.model.Proveedor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object para la tabla 'proveedor'.
 */
public class ProveedorDAO {

    public List<Proveedor> findAll() {
        List<Proveedor> lista = new ArrayList<>();
        String sql = "SELECT id_proveedor, nombre_empresa, contacto, frecuencia_entrega FROM proveedor ORDER BY nombre_empresa";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                lista.add(new Proveedor(
                        rs.getInt("id_proveedor"),
                        rs.getString("nombre_empresa"),
                        rs.getString("contacto"),
                        rs.getString("frecuencia_entrega")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar proveedores: " + e.getMessage());
        }
        return lista;
    }

    public boolean create(Proveedor p) {
        String sql = "INSERT INTO proveedor (nombre_empresa, contacto, frecuencia_entrega) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, p.getNombreEmpresa());
            pstmt.setString(2, p.getContacto());
            pstmt.setString(3, p.getFrecuenciaEntrega());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al crear proveedor: " + e.getMessage());
            return false;
        }
    }

    public boolean update(Proveedor p) {
        String sql = "UPDATE proveedor SET nombre_empresa = ?, contacto = ?, frecuencia_entrega = ? WHERE id_proveedor = ?";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, p.getNombreEmpresa());
            pstmt.setString(2, p.getContacto());
            pstmt.setString(3, p.getFrecuenciaEntrega());
            pstmt.setInt(4, p.getIdProveedor());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar proveedor: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM proveedor WHERE id_proveedor = ?";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar proveedor: " + e.getMessage());
            return false;
        }
    }
}
