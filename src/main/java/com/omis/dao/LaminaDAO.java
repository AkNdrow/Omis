package com.omis.dao;

import com.omis.config.DatabaseManager;
import com.omis.model.Lamina;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object para la tabla 'lamina' (subentidad 1:1 de producto).
 */
public class LaminaDAO {

    /**
     * Busca la lámina asociada a un producto.
     */
    public Lamina findByProductoId(int idProducto) {
        String sql = """
                SELECT l.id_producto, l.id_marca, l.numero_serie, l.tema, l.materia,
                       m.nombre_marca
                FROM lamina l
                LEFT JOIN marca m ON l.id_marca = m.id_marca
                WHERE l.id_producto = ?
                """;

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idProducto);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Lamina lam = new Lamina(
                        rs.getInt("id_producto"),
                        rs.getObject("id_marca") != null ? rs.getInt("id_marca") : null,
                        rs.getString("numero_serie"),
                        rs.getString("tema"),
                        rs.getString("materia")
                );
                lam.setNombreMarca(rs.getString("nombre_marca"));
                return lam;
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar lámina: " + e.getMessage());
        }
        return null;
    }

    /**
     * Crea una lámina asociada a un producto existente.
     */
    public boolean create(Lamina lam) {
        String sql = "INSERT INTO lamina (id_producto, id_marca, numero_serie, tema, materia) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, lam.getIdProducto());
            if (lam.getIdMarca() != null) {
                pstmt.setInt(2, lam.getIdMarca());
            } else {
                pstmt.setNull(2, Types.INTEGER);
            }
            pstmt.setString(3, lam.getNumeroSerie());
            pstmt.setString(4, lam.getTema());
            pstmt.setString(5, lam.getMateria());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al crear lámina: " + e.getMessage());
            return false;
        }
    }

    /**
     * Actualiza los datos de una lámina existente.
     */
    public boolean update(Lamina lam) {
        String sql = "UPDATE lamina SET id_marca = ?, numero_serie = ?, tema = ?, materia = ? WHERE id_producto = ?";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (lam.getIdMarca() != null) {
                pstmt.setInt(1, lam.getIdMarca());
            } else {
                pstmt.setNull(1, Types.INTEGER);
            }
            pstmt.setString(2, lam.getNumeroSerie());
            pstmt.setString(3, lam.getTema());
            pstmt.setString(4, lam.getMateria());
            pstmt.setInt(5, lam.getIdProducto());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar lámina: " + e.getMessage());
            return false;
        }
    }

    /**
     * Elimina la lámina asociada a un producto.
     */
    public boolean delete(int idProducto) {
        String sql = "DELETE FROM lamina WHERE id_producto = ?";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idProducto);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar lámina: " + e.getMessage());
            return false;
        }
    }

    /**
     * Búsqueda avanzada de láminas con JOIN a producto y marca.
     */
    public List<Lamina> buscarAvanzado(String temaOCve, String materia, String serie) {
        List<Lamina> lista = new ArrayList<>();
        String sql = """
                SELECT l.id_producto, l.id_marca, l.numero_serie, l.tema, l.materia,
                       m.nombre_marca, p.nombre AS nombre_producto, p.precio_venta, p.stock_actual
                FROM lamina l
                JOIN producto p ON l.id_producto = p.id_producto
                LEFT JOIN marca m ON l.id_marca = m.id_marca
                WHERE (l.tema LIKE ? OR p.nombre LIKE ?)
                  AND l.materia LIKE ?
                  AND l.numero_serie LIKE ?
                ORDER BY p.nombre
                """;

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String termClave = "%" + (temaOCve != null ? temaOCve : "") + "%";
            pstmt.setString(1, termClave);
            pstmt.setString(2, termClave);
            pstmt.setString(3, "%" + (materia != null ? materia : "") + "%");
            pstmt.setString(4, "%" + (serie != null ? serie : "") + "%");

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Lamina lam = new Lamina(
                        rs.getInt("id_producto"),
                        rs.getObject("id_marca") != null ? rs.getInt("id_marca") : null,
                        rs.getString("numero_serie"),
                        rs.getString("tema"),
                        rs.getString("materia")
                );
                lam.setNombreMarca(rs.getString("nombre_marca"));
                lam.setNombreProducto(rs.getString("nombre_producto"));
                lam.setPrecioVenta(rs.getDouble("precio_venta"));
                lam.setStockActual(rs.getInt("stock_actual"));
                lista.add(lam);
            }
        } catch (SQLException e) {
            System.err.println("Error en búsqueda avanzada de láminas: " + e.getMessage());
        }
        return lista;
    }
}
