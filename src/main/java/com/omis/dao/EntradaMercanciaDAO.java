package com.omis.dao;

import com.omis.config.DatabaseManager;
import com.omis.model.DetalleEntrada;
import com.omis.model.EntradaMercancia;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object para las tablas 'entrada_mercancia' y 'detalle_entrada'.
 * Maneja transacciones atómicas para registrar entrada + detalles + actualizar stock.
 */
public class EntradaMercanciaDAO {

    /**
     * Registra una entrada completa de mercancía en una transacción atómica:
     * 1. Inserta la cabecera (entrada_mercancia)
     * 2. Inserta cada detalle (detalle_entrada)
     * 3. Actualiza el stock y precio de cada producto
     *
     * @return ID de la entrada creada, o -1 si falló.
     */
    public int registrarEntrada(EntradaMercancia entrada, List<DetalleEntrada> detalles) {
        Connection conn = null;
        try {
            conn = DatabaseManager.getInstance().getConnection();
            conn.setAutoCommit(false); // Iniciar transacción

            // 1. Insertar cabecera
            String sqlCab = "INSERT INTO entrada_mercancia (observaciones, id_proveedor, id_usuario) VALUES (?, ?, ?)";
            int idEntrada;
            try (PreparedStatement pstmt = conn.prepareStatement(sqlCab, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, entrada.getObservaciones());
                pstmt.setInt(2, entrada.getIdProveedor());
                pstmt.setInt(3, entrada.getIdUsuario());
                pstmt.executeUpdate();

                ResultSet keys = pstmt.getGeneratedKeys();
                if (keys.next()) {
                    idEntrada = keys.getInt(1);
                } else {
                    conn.rollback();
                    return -1;
                }
            }

            // 2. Insertar detalles y actualizar stock/precio de cada producto
            String sqlDet = "INSERT INTO detalle_entrada (id_entrada, id_producto, cantidad_recibida, costo_unitario, precio_calculado) VALUES (?, ?, ?, ?, ?)";
            String sqlStock = "UPDATE producto SET stock_actual = stock_actual + ?, costo_adquisicion = ?, precio_venta = ?, margen_utilidad = ? WHERE id_producto = ?";

            try (PreparedStatement pstmtDet = conn.prepareStatement(sqlDet);
                 PreparedStatement pstmtStock = conn.prepareStatement(sqlStock)) {

                for (DetalleEntrada det : detalles) {
                    // Insertar detalle
                    pstmtDet.setInt(1, idEntrada);
                    pstmtDet.setInt(2, det.getIdProducto());
                    pstmtDet.setInt(3, det.getCantidadRecibida());
                    pstmtDet.setDouble(4, det.getCostoUnitario());
                    pstmtDet.setDouble(5, det.getPrecioCalculado());
                    pstmtDet.executeUpdate();

                    // Actualizar stock y precio del producto
                    pstmtStock.setInt(1, det.getCantidadRecibida());
                    pstmtStock.setDouble(2, det.getCostoUnitario());
                    pstmtStock.setDouble(3, det.getPrecioCalculado());
                    pstmtStock.setDouble(4, 0); // margen se calcula dinámicamente
                    pstmtStock.setInt(5, det.getIdProducto());
                    pstmtStock.executeUpdate();
                }
            }

            conn.commit(); // Confirmar transacción
            return idEntrada;

        } catch (SQLException e) {
            System.err.println("Error al registrar entrada: " + e.getMessage());
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
     * Obtiene todas las entradas con nombres de proveedor y usuario.
     */
    public List<EntradaMercancia> findAll() {
        List<EntradaMercancia> lista = new ArrayList<>();
        String sql = """
                SELECT e.id_entrada, e.fecha_recepcion, e.observaciones, e.id_proveedor, e.id_usuario,
                       p.nombre_empresa, u.nombre_completo
                FROM entrada_mercancia e
                LEFT JOIN proveedor p ON e.id_proveedor = p.id_proveedor
                LEFT JOIN usuario u ON e.id_usuario = u.id_usuario
                ORDER BY e.fecha_recepcion DESC
                """;

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                EntradaMercancia em = new EntradaMercancia();
                em.setIdEntrada(rs.getInt("id_entrada"));
                em.setFechaRecepcion(rs.getString("fecha_recepcion"));
                em.setObservaciones(rs.getString("observaciones"));
                em.setIdProveedor(rs.getInt("id_proveedor"));
                em.setIdUsuario(rs.getInt("id_usuario"));
                em.setNombreProveedor(rs.getString("nombre_empresa"));
                em.setNombreUsuario(rs.getString("nombre_completo"));
                lista.add(em);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar entradas: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Obtiene los detalles de una entrada específica.
     */
    public List<DetalleEntrada> findDetalles(int idEntrada) {
        List<DetalleEntrada> lista = new ArrayList<>();
        String sql = """
                SELECT d.id_detalle_ent, d.id_entrada, d.id_producto, d.cantidad_recibida,
                       d.costo_unitario, d.precio_calculado, p.nombre
                FROM detalle_entrada d
                LEFT JOIN producto p ON d.id_producto = p.id_producto
                WHERE d.id_entrada = ?
                """;

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idEntrada);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                DetalleEntrada det = new DetalleEntrada();
                det.setIdDetalleEnt(rs.getInt("id_detalle_ent"));
                det.setIdEntrada(rs.getInt("id_entrada"));
                det.setIdProducto(rs.getInt("id_producto"));
                det.setCantidadRecibida(rs.getInt("cantidad_recibida"));
                det.setCostoUnitario(rs.getDouble("costo_unitario"));
                det.setPrecioCalculado(rs.getDouble("precio_calculado"));
                det.setNombreProducto(rs.getString("nombre"));
                lista.add(det);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener detalles de entrada: " + e.getMessage());
        }
        return lista;
    }
}
