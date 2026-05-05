package com.omis.dao;

import com.omis.config.DatabaseManager;
import com.omis.model.Producto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object para la tabla 'producto'.
 * Incluye consultas con JOIN a categoría para mostrar el nombre.
 */
public class ProductoDAO {

    /**
     * Obtiene todos los productos con el nombre de su categoría.
     */
    public List<Producto> findAll() {
        List<Producto> lista = new ArrayList<>();
        String sql = """
                SELECT p.id_producto, p.nombre, p.costo_adquisicion, p.precio_venta,
                       p.margen_utilidad, p.stock_actual, p.id_categoria, p.ubicacion_fisica,
                       c.nombre_categoria
                FROM producto p
                LEFT JOIN categoria c ON p.id_categoria = c.id_categoria
                ORDER BY p.nombre
                """;

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Producto p = mapResultSet(rs);
                p.setNombreCategoria(rs.getString("nombre_categoria"));
                lista.add(p);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar productos: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Busca productos por nombre (búsqueda parcial).
     */
    public List<Producto> buscarPorNombre(String termino) {
        List<Producto> lista = new ArrayList<>();
        String sql = """
                SELECT p.id_producto, p.nombre, p.costo_adquisicion, p.precio_venta,
                       p.margen_utilidad, p.stock_actual, p.id_categoria, p.ubicacion_fisica,
                       c.nombre_categoria
                FROM producto p
                LEFT JOIN categoria c ON p.id_categoria = c.id_categoria
                WHERE p.nombre LIKE ?
                ORDER BY p.nombre
                """;

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + termino + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Producto p = mapResultSet(rs);
                p.setNombreCategoria(rs.getString("nombre_categoria"));
                lista.add(p);
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar productos: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Busca un producto por su ID.
     */
    public Producto findById(int id) {
        String sql = """
                SELECT p.id_producto, p.nombre, p.costo_adquisicion, p.precio_venta,
                       p.margen_utilidad, p.stock_actual, p.id_categoria, p.ubicacion_fisica,
                       c.nombre_categoria
                FROM producto p
                LEFT JOIN categoria c ON p.id_categoria = c.id_categoria
                WHERE p.id_producto = ?
                """;

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Producto p = mapResultSet(rs);
                p.setNombreCategoria(rs.getString("nombre_categoria"));
                return p;
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar producto por ID: " + e.getMessage());
        }
        return null;
    }

    /**
     * Crea un nuevo producto y retorna el ID generado.
     * @return ID del producto creado, o -1 si falló.
     */
    public int create(Producto p) {
        String sql = "INSERT INTO producto (nombre, costo_adquisicion, precio_venta, margen_utilidad, stock_actual, id_categoria, ubicacion_fisica) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, p.getNombre());
            pstmt.setDouble(2, p.getCostoAdquisicion());
            pstmt.setDouble(3, p.getPrecioVenta());
            pstmt.setDouble(4, p.getMargenUtilidad());
            pstmt.setInt(5, p.getStockActual());
            if (p.getIdCategoria() != null) {
                pstmt.setInt(6, p.getIdCategoria());
            } else {
                pstmt.setNull(6, Types.INTEGER);
            }
            pstmt.setString(7, p.getUbicacionFisica());

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                ResultSet keys = pstmt.getGeneratedKeys();
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al crear producto: " + e.getMessage());
        }
        return -1;
    }

    /**
     * Actualiza un producto existente.
     */
    public boolean update(Producto p) {
        String sql = "UPDATE producto SET nombre = ?, id_categoria = ?, ubicacion_fisica = ? WHERE id_producto = ?";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, p.getNombre());
            if (p.getIdCategoria() != null) {
                pstmt.setInt(2, p.getIdCategoria());
            } else {
                pstmt.setNull(2, Types.INTEGER);
            }
            pstmt.setString(3, p.getUbicacionFisica());
            pstmt.setInt(4, p.getIdProducto());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar producto: " + e.getMessage());
            return false;
        }
    }

    /**
     * Elimina un producto por su ID.
     * Las subentidades (lamina, perecedero) se eliminan automáticamente por CASCADE.
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM producto WHERE id_producto = ?";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar producto: " + e.getMessage());
            return false;
        }
    }

    private Producto mapResultSet(ResultSet rs) throws SQLException {
        return new Producto(
                rs.getInt("id_producto"),
                rs.getString("nombre"),
                rs.getDouble("costo_adquisicion"),
                rs.getDouble("precio_venta"),
                rs.getDouble("margen_utilidad"),
                rs.getInt("stock_actual"),
                rs.getObject("id_categoria") != null ? rs.getInt("id_categoria") : null,
                rs.getString("ubicacion_fisica")
        );
    }
}
