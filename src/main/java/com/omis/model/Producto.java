package com.omis.model;

/**
 * Modelo que representa un producto del inventario.
 * Corresponde a la tabla 'producto' en la base de datos.
 * Puede tener extensiones: Lamina (1:1) o Perecedero (1:1).
 */
public class Producto {

    private int idProducto;
    private String nombre;
    private double costoAdquisicion;
    private double precioVenta;
    private double margenUtilidad;
    private int stockActual;
    private Integer idCategoria;
    private String ubicacionFisica;

    // Campo auxiliar (no persistido, para mostrar en tabla)
    private String nombreCategoria;

    public Producto() {
    }

    public Producto(int idProducto, String nombre, double costoAdquisicion, double precioVenta,
                    double margenUtilidad, int stockActual, Integer idCategoria, String ubicacionFisica) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.costoAdquisicion = costoAdquisicion;
        this.precioVenta = precioVenta;
        this.margenUtilidad = margenUtilidad;
        this.stockActual = stockActual;
        this.idCategoria = idCategoria;
        this.ubicacionFisica = ubicacionFisica;
    }

    // --- Getters y Setters ---

    public int getIdProducto() { return idProducto; }
    public void setIdProducto(int idProducto) { this.idProducto = idProducto; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public double getCostoAdquisicion() { return costoAdquisicion; }
    public void setCostoAdquisicion(double costoAdquisicion) { this.costoAdquisicion = costoAdquisicion; }

    public double getPrecioVenta() { return precioVenta; }
    public void setPrecioVenta(double precioVenta) { this.precioVenta = precioVenta; }

    public double getMargenUtilidad() { return margenUtilidad; }
    public void setMargenUtilidad(double margenUtilidad) { this.margenUtilidad = margenUtilidad; }

    public int getStockActual() { return stockActual; }
    public void setStockActual(int stockActual) { this.stockActual = stockActual; }

    public Integer getIdCategoria() { return idCategoria; }
    public void setIdCategoria(Integer idCategoria) { this.idCategoria = idCategoria; }

    public String getUbicacionFisica() { return ubicacionFisica; }
    public void setUbicacionFisica(String ubicacionFisica) { this.ubicacionFisica = ubicacionFisica; }

    public String getNombreCategoria() { return nombreCategoria; }
    public void setNombreCategoria(String nombreCategoria) { this.nombreCategoria = nombreCategoria; }

    @Override
    public String toString() {
        return nombre;
    }
}
