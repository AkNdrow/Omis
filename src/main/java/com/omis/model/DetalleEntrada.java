package com.omis.model;

/**
 * Modelo que representa un renglón de detalle de entrada de mercancía.
 * Corresponde a la tabla 'detalle_entrada' en la base de datos.
 */
public class DetalleEntrada {

    private int idDetalleEnt;
    private int idEntrada;
    private int idProducto;
    private int cantidadRecibida;
    private double costoUnitario;
    private double precioCalculado;

    // Campos auxiliares para display en tabla
    private String nombreProducto;

    public DetalleEntrada() {
    }

    public int getIdDetalleEnt() { return idDetalleEnt; }
    public void setIdDetalleEnt(int idDetalleEnt) { this.idDetalleEnt = idDetalleEnt; }

    public int getIdEntrada() { return idEntrada; }
    public void setIdEntrada(int idEntrada) { this.idEntrada = idEntrada; }

    public int getIdProducto() { return idProducto; }
    public void setIdProducto(int idProducto) { this.idProducto = idProducto; }

    public int getCantidadRecibida() { return cantidadRecibida; }
    public void setCantidadRecibida(int cantidadRecibida) { this.cantidadRecibida = cantidadRecibida; }

    public double getCostoUnitario() { return costoUnitario; }
    public void setCostoUnitario(double costoUnitario) { this.costoUnitario = costoUnitario; }

    public double getPrecioCalculado() { return precioCalculado; }
    public void setPrecioCalculado(double precioCalculado) { this.precioCalculado = precioCalculado; }

    public String getNombreProducto() { return nombreProducto; }
    public void setNombreProducto(String nombreProducto) { this.nombreProducto = nombreProducto; }

    /**
     * Calcula el subtotal del renglón (costo × cantidad).
     */
    public double getSubtotal() {
        return costoUnitario * cantidadRecibida;
    }
}
