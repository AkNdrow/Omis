package com.omis.dao;

import com.omis.config.DatabaseManager;
import com.omis.model.DetalleVenta;
import com.omis.model.Venta;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object para las tablas 'venta' y 'detalle_venta'.
 * Implementa la transacción atómica del Punto de Venta (POS).
 */
public class VentaDAO {

    /**
     * Registra una venta completa en una transacción atómica:
     * 1. Inserta la cabecera (venta)
     * 2. Inserta cada renglón (detalle_venta)
     * 3. Reduce el stock de cada producto vendido
     *
     * @return ID de la venta creada, o -1 si falló.
     */
    public int registrarVenta(Venta venta, List<DetalleVenta> detalles) {
        Connection conn = null;
        try {
            conn = DatabaseManager.getInstance().getConnection();
            conn.setAutoCommit(false); // Iniciar transacción

            // 1. Insertar cabecera de la venta
            String sqlVenta = "INSERT INTO venta (monto_total, id_usuario) VALUES (?, ?)";
            int idVenta;
            try (PreparedStatement pstmt = conn.prepareStatement(sqlVenta, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setDouble(1, venta.getMontoTotal());
                pstmt.setInt(2, venta.getIdUsuario());
                pstmt.executeUpdate();

                ResultSet keys = pstmt.getGeneratedKeys();
                if (keys.next()) {
                    idVenta = keys.getInt(1);
                } else {
                    conn.rollback();
                    return -1;
                }
            }

            // 2. Insertar detalles y reducir stock
            String sqlDetalle = "INSERT INTO detalle_venta (id_venta, id_producto, cantidad, precio_unitario) VALUES (?, ?, ?, ?)";
            String sqlStock = "UPDATE producto SET stock_actual = stock_actual - ? WHERE id_producto = ?";

            try (PreparedStatement pstmtDet = conn.prepareStatement(sqlDetalle);
                 PreparedStatement pstmtStock = conn.prepareStatement(sqlStock)) {

                for (DetalleVenta det : detalles) {
                    // Insertar renglón del carrito
                    pstmtDet.setInt(1, idVenta);
                    pstmtDet.setInt(2, det.getIdProducto());
                    pstmtDet.setInt(3, det.getCantidad());
                    pstmtDet.setDouble(4, det.getPrecioUnitario());
                    pstmtDet.executeUpdate();

                    // Descontar stock del inventario
                    pstmtStock.setInt(1, det.getCantidad());
                    pstmtStock.setInt(2, det.getIdProducto());
                    pstmtStock.executeUpdate();
                }
            }

            conn.commit(); // Confirmar transacción
            return idVenta;

        } catch (SQLException e) {
            System.err.println("Error al registrar la venta (POS): " + e.getMessage());
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            return -1;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); } catch (SQLException ex) { ex.printStackTrace(); }
            }
        }
    }

    /**
     * Obtiene el historial de ventas registradas.
     */
    public List<Venta> findAll() {
        List<Venta> lista = new ArrayList<>();
        String sql = """
                SELECT v.id_venta, v.fecha_hora, v.monto_total, v.id_usuario,
                       u.nombre_completo
                FROM venta v
                JOIN usuario u ON v.id_usuario = u.id_usuario
                ORDER BY v.fecha_hora DESC
                """;

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Venta v = new Venta();
                v.setIdVenta(rs.getInt("id_venta"));
                v.setFechaHora(rs.getString("fecha_hora"));
                v.setMontoTotal(rs.getDouble("monto_total"));
                v.setIdUsuario(rs.getInt("id_usuario"));
                v.setNombreUsuario(rs.getString("nombre_completo"));
                lista.add(v);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar ventas: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Obtiene el historial de ventas registradas en un rango de fechas.
     * @param fechaInicio Inicio del rango (incluyente), ej. "2026-05-01 00:00:00"
     * @param fechaFin Fin del rango (incluyente), ej. "2026-05-31 23:59:59"
     */
    public List<Venta> findByDateRange(String fechaInicio, String fechaFin) {
        List<Venta> lista = new ArrayList<>();
        String sql = """
                SELECT v.id_venta, v.fecha_hora, v.monto_total, v.id_usuario,
                       u.nombre_completo
                FROM venta v
                JOIN usuario u ON v.id_usuario = u.id_usuario
                WHERE v.fecha_hora >= ? AND v.fecha_hora <= ?
                ORDER BY v.fecha_hora ASC
                """;

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, fechaInicio);
            pstmt.setString(2, fechaFin);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Venta v = new Venta();
                    v.setIdVenta(rs.getInt("id_venta"));
                    v.setFechaHora(rs.getString("fecha_hora"));
                    v.setMontoTotal(rs.getDouble("monto_total"));
                    v.setIdUsuario(rs.getInt("id_usuario"));
                    v.setNombreUsuario(rs.getString("nombre_completo"));
                    lista.add(v);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al listar ventas por rango: " + e.getMessage());
        }
        return lista;
    }
}
